package org.projectc.simulation;


import org.projectc.simulation.dex.JavaDex;
import org.projectc.simulation.math.DexMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * The liquidity pool is used in the {@link JavaDex} as the trading pair.
 *
 * This impl is not thread safe. (Nothing in this demo uses multi-threading.)
 *
 */
public class LiquidityPool {

    protected BigInteger token;
    protected BigInteger eth;

    protected BigDecimal initialPriceInEthForToken;
//    protected BigInteger k;

    public LiquidityPool(BigInteger token, BigInteger eth) {
        this.token = token;
        this.eth = eth;
//        this.k = token.multiply(eth);

        this.initialPriceInEthForToken = computeTheoreticalMeanPrice(token, eth, RoundingMode.HALF_UP);
    }

    
    public BigDecimal computeTheoreticalMeanPrice(BigInteger token, BigInteger eth, RoundingMode rounding) {
        return new BigDecimal(eth).divide(new BigDecimal(token), 18, rounding);
    }

    
    public BigDecimal computeCurrentTheoreticalMeanPrice() {
        return computeTheoreticalMeanPrice(token, eth, RoundingMode.HALF_UP);
    }

    /**
     * @return How many tokens the pool contains.
     */
    public BigInteger getToken() {
        return token;
    }

    /**
     * @return How many ETH the pool contains.
     */
    public BigInteger getEth() {
        return eth;
    }

    /**
     * Changes the amount of tokens in this LP.
     */
    public void setToken(BigInteger token) {
        this.token = token;
    }

    /**
     * Changes the amount of ETH in this LP.
     */
    public void setEth(BigInteger eth) {
        this.eth = eth;
    }

    /**
     * Tells what the price was before trading started, after adding the initial liquidity.
     */
    public BigDecimal getInitialPrice() {
        return initialPriceInEthForToken;
    }

    /**
     * Tells how well the token performed since the trading start after adding the initial liquidity.
     */
    public BigDecimal getPerformance() {
        BigDecimal currentPrice = computeCurrentTheoreticalMeanPrice();
        return DexMath.computePerformanceInPercent(currentPrice, initialPriceInEthForToken);
    }


    /**
     * @return How many tokens one would get if he'd trade in the given amount of ETH right now.
     *         Calculation is with the usual 0.3% fee for the liquidity provider.
     */
    public BigDecimal howManyTokenForMyEth(BigInteger ethToSpend) {
        BigInteger ethReserve = eth;
        BigInteger tokenReserve = token;

        BigInteger spendWithFee = ethToSpend.multiply(new BigInteger("997"));
        BigInteger numerator = spendWithFee.multiply(tokenReserve);
        BigInteger denominator = ethReserve.multiply(new BigInteger("1000")).add(spendWithFee);
        BigDecimal tokenToGet = computeTheoreticalMeanPrice(denominator, numerator, RoundingMode.DOWN);
        return tokenToGet;
    }

    /**
     * @return How many ETH one would get if he'd trade in the given amount of tokens right now.
     *         Calculation is with the usual 0.3% fee for the liquidity provider.
     */
    public BigDecimal howManyEthForMyToken(BigInteger tokenToSpend) {
        BigInteger ethReserve = eth;
        BigInteger tokenReserve = token;

        BigInteger spendWithFee = tokenToSpend.multiply(new BigInteger("997"));
        BigInteger numerator = spendWithFee.multiply(ethReserve);
        BigInteger denominator = tokenReserve.multiply(new BigInteger("1000")).add(spendWithFee);
        BigDecimal ethToGet = computeTheoreticalMeanPrice(denominator, numerator, RoundingMode.DOWN);
        return ethToGet;
    }


    /**
     * TODO this is not exact, i have some mistake somewhere don't know where.
     */
    public BigDecimal howManyTokensToSellToGetThisManyEth(BigInteger ethToGet) {
        if (ethToGet.compareTo(eth) > 0) {
            throw new RuntimeException("Trying to get "+ethToGet + " only having "+eth);
        }

        BigInteger ethReserve = eth;
        BigInteger tokenReserve = token;

        BigInteger getWithFee = ethToGet.multiply(new BigInteger("997"));
        BigInteger numerator = getWithFee.multiply(tokenReserve);
        BigInteger denominator = ethReserve.multiply(new BigInteger("1000")).subtract(getWithFee);
        BigDecimal tokenToGet = computeTheoreticalMeanPrice(denominator, numerator, RoundingMode.DOWN);
        return tokenToGet;
    }



    @Override
    public String toString() {
        BigDecimal tokensHumanReadable = new BigDecimal(token).divide(new BigDecimal("1000000000000000000"), 10, RoundingMode.HALF_UP);
        BigDecimal ethHumanReadable = new BigDecimal(eth).divide(new BigDecimal("1000000000000000000"), 10, RoundingMode.HALF_UP);
        BigDecimal price = computeTheoreticalMeanPrice(token, eth, RoundingMode.HALF_UP);
        return "LiquidityPool{" +
                "token=" + tokensHumanReadable.setScale(5, RoundingMode.HALF_UP) +
                ", eth=" + ethHumanReadable.setScale(5, RoundingMode.HALF_UP) +
                ", price=" + price.setScale(5, RoundingMode.HALF_UP) +
                ", performance=" + getPerformance().setScale(2, RoundingMode.HALF_UP)+"%" +
                '}';
    }


}
