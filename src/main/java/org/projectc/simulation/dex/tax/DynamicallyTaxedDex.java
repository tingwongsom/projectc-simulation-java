package org.projectc.simulation.dex.tax;

import org.projectc.simulation.LiquidityPool;
import org.projectc.simulation.Wallet;
import org.projectc.simulation.dex.JavaDex;
import org.projectc.simulation.dex.pricedata.HistoricalData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

/**
 * Wraps the JavaDex to take the dynamic buy/sell tax from the market participant before
 * handing over the remaining amount to the JavaDex.
 *
 * The tax is put into the tax wallet.
 */
public class DynamicallyTaxedDex implements JavaDex {

    private final JavaDex dex;
    private final HistoricalData historicalData;
    private final Wallet taxWallet;
    private final TaxConfig taxConfig;
    private final List<MovingAvgTaxItem> movingAvgTaxItems;
    private final TaxTxEventListener taxTxEventListener;

    public DynamicallyTaxedDex(JavaDex dex,
                               HistoricalData historicalData,
                               TaxConfig taxConfig,
                               List<MovingAvgTaxItem> movingAvgTaxItems,
                               TaxTxEventListener taxTxEventListener) {
        this.dex = dex;
        this.historicalData = historicalData;
        this.taxWallet = new Wallet(0, 0);
        this.taxConfig = taxConfig;
        this.movingAvgTaxItems = movingAvgTaxItems;
        this.taxTxEventListener = taxTxEventListener;
    }

    @Override
    public LiquidityPool getLp() {
        return dex.getLp();
    }

    public Wallet getTaxWallet() {
        return taxWallet;
    }


    @Override
    public BigDecimal askTaxPercentForBuyToken(BigInteger eth) {
        BigDecimal price2 = computeBuyMeanPriceWithoutTax(eth);
        return _askTaxPercentForBuyToken( price2);
    }
    @Override
    public BigDecimal askTaxPercentForSellToken(BigInteger token) {
        BigDecimal price2 = computeSellMeanPriceWithoutTax(token);
        return _askTaxPercentForSellToken(price2);
    }

    
    private BigDecimal _askTaxPercentForBuyToken(BigDecimal price2) {
        WeightedMeanCollector mean = WeightedMeanCollector.unbounded();

        List<MovingAvgTaxItem> items = movingAvgTaxItems;
        for (MovingAvgTaxItem item : items) {
            if (historicalData.getTicksAvailable() >= item.getTicks()) {
                BigDecimal movingAvg = historicalData.computeMovingAverageIncluding(price2, item.getTicks());
                TaxScaler.WeightedTax weightedTax = new TaxScaler().computeTax(true, price2, movingAvg, item.getDesiredPerformance(), item.getBaseWeight());
                mean.add(weightedTax.getTaxPercent().doubleValue(), weightedTax.getWeight());
            }
        }

        double taxPercentDbl = mean.getMeanOr(taxConfig.getInitialTaxPercent());
        if (taxPercentDbl < 0d) taxPercentDbl = 0d;
        BigDecimal taxPercent = BigDecimal.valueOf(taxPercentDbl);

        taxPercent = taxConfig.applyBuyTaxRule(taxPercent);
        return taxPercent;
    }



    @Override
    public BigInteger buyToken(BigInteger eth) {
        BigDecimal meanPriceBefore = getLp().computeCurrentTheoreticalMeanPrice();

        BigDecimal meanPriceWithoutTax = computeBuyMeanPriceWithoutTax(eth);
        BigDecimal taxPercent = _askTaxPercentForBuyToken(meanPriceWithoutTax);

        BigInteger tax = new BigDecimal(eth).multiply(taxPercent).divide(new BigDecimal("100"), 18, RoundingMode.HALF_UP).toBigInteger();
        taxWallet.addEth(tax);

        BigInteger ethRemaining = eth.subtract(tax);
        BigInteger tokensToReturn = dex.buyToken(ethRemaining);

        BigDecimal meanPriceAfter = getLp().computeCurrentTheoreticalMeanPrice();

        taxTxEventListener.event(new TaxTxEventImpl(
                true,
                meanPriceWithoutTax,
                new BigDecimal(tax),
                ethRemaining,
                tokensToReturn,
                meanPriceBefore, meanPriceAfter
        ));

        return tokensToReturn;
    }

    @Override
    public BigInteger sellToken(BigInteger token) {
        BigDecimal meanPriceBefore = getLp().computeCurrentTheoreticalMeanPrice();

        //compute tax:
        BigDecimal meanPriceWithoutTax = computeSellMeanPriceWithoutTax(token);
        BigDecimal taxPercent = _askTaxPercentForSellToken(meanPriceWithoutTax);

        BigInteger tax = new BigDecimal(token).multiply(taxPercent).divide(new BigDecimal("100"), 18, RoundingMode.HALF_UP).toBigInteger();
        taxWallet.addToken(tax);

        BigInteger tokenRemaining = token.subtract(tax);
        BigInteger ethToReturn = dex.sellToken(tokenRemaining);

        BigDecimal meanPriceAfter = getLp().computeCurrentTheoreticalMeanPrice();

        taxTxEventListener.event(new TaxTxEventImpl(
                false,
                meanPriceWithoutTax,
                new BigDecimal(tax),
                ethToReturn,
                tokenRemaining,
                meanPriceBefore, meanPriceAfter
        ));

        return ethToReturn;
    }

    
    private BigDecimal _askTaxPercentForSellToken(BigDecimal price2) {
        WeightedMeanCollector mean = WeightedMeanCollector.unbounded();

        List<MovingAvgTaxItem> items = movingAvgTaxItems;
        for (MovingAvgTaxItem item : items) {
            if (historicalData.getTicksAvailable() >= item.getTicks()) {
                BigDecimal movingAvg = historicalData.computeMovingAverageIncluding(price2, item.getTicks());
                TaxScaler.WeightedTax weightedTax = new TaxScaler().computeTax(false, price2, movingAvg, item.getDesiredPerformance(), item.getBaseWeight());
                mean.add(weightedTax.getTaxPercent().doubleValue(), weightedTax.getWeight());
            }
        }

        double taxPercentDbl = mean.getMeanOr(3);
        if (taxPercentDbl < 0d) taxPercentDbl = 0d;
        BigDecimal taxPercent = BigDecimal.valueOf(taxPercentDbl);

        taxPercent = taxConfig.applySellTaxRule(taxPercent);
        return taxPercent;
    }



    
    private BigDecimal computeBuyMeanPriceWithoutTax(BigInteger ethToSpend) {
        LiquidityPool lp = getLp();

        BigDecimal tokenForEth = lp.howManyTokenForMyEth(ethToSpend);
        BigDecimal price2 = lp.computeTheoreticalMeanPrice(
                lp.getToken().subtract(tokenForEth.toBigInteger()),
                lp.getEth().add(ethToSpend),
                RoundingMode.HALF_UP
        );
        return price2;
    }

    
    private BigDecimal computeSellMeanPriceWithoutTax(BigInteger tokenToSell) {
        LiquidityPool lp = getLp();

        BigDecimal ethForToken = lp.howManyEthForMyToken(tokenToSell);
        BigDecimal price2 = lp.computeTheoreticalMeanPrice(
                lp.getToken().add(tokenToSell),
                lp.getEth().subtract(ethForToken.toBigInteger()),
                RoundingMode.HALF_UP
        );
        return price2;
    }


}
