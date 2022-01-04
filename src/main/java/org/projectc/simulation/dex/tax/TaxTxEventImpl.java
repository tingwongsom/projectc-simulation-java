package org.projectc.simulation.dex.tax;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class TaxTxEventImpl implements TaxTxEvent {

    private static final BigInteger eighteenDigitsInteger = new BigInteger("1000000000000000000");
    private static final BigDecimal eighteenDigitsDecimal = new BigDecimal("1000000000000000000");


    private final boolean isBuy;
    private final BigDecimal meanPriceWithoutTax;
    private final BigDecimal tax;
    private final BigInteger eth;
    private final BigInteger token;
    private final BigDecimal meanPriceBefore;
    private final BigDecimal meanPriceAfter;

    public TaxTxEventImpl(boolean isBuy,
                          BigDecimal meanPriceWithoutTax,
                          BigDecimal tax,
                          BigInteger eth,
                          BigInteger token,

                          BigDecimal meanPriceBefore, BigDecimal meanPriceAfter) {
        this.isBuy = isBuy;
        this.meanPriceWithoutTax = meanPriceWithoutTax;
        this.tax = tax;
        this.eth = eth;
        this.token = token;
        this.meanPriceBefore = meanPriceBefore;
        this.meanPriceAfter = meanPriceAfter;
    }

    @Override
    public boolean isBuy() {
        return isBuy;
    }

    @Override
    public BigInteger getEth() {
        return eth;
    }

    @Override
    public BigInteger getToken() {
        return token;
    }

    /**
     * Effective price includes all acquisition costs including taxes and gas and all.
     */
    @Override
    public BigDecimal computeEffectivePrice() {
        try {
            BigDecimal result = new BigDecimal(eth).divide(new BigDecimal(token), 18, RoundingMode.HALF_UP);
            if  (result.equals(new BigDecimal("0"))) {
                throw new RuntimeException();
            }
            if  (result.toPlainString().equals("0.000000000000000000")) {
                throw new RuntimeException();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public BigDecimal getMeanPriceWithoutTax() {
        return meanPriceWithoutTax;
    }

    @Override
    public BigDecimal getTax() {
        return tax;
    }

    @Override
    public BigDecimal getWithTaxApplied() {
        if (isBuy) {
            return meanPriceWithoutTax.add(tax.divide(eighteenDigitsDecimal, 10, RoundingMode.HALF_UP));
        } else {
            return meanPriceWithoutTax.subtract(tax.divide(eighteenDigitsDecimal, 10, RoundingMode.HALF_UP));
        }
    }

    @Override
    public BigDecimal getPriceIncludingTax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getMediumPriceBefore() {
        return meanPriceBefore;
    }

    @Override
    public BigDecimal getMediumPriceAfter() {
        return meanPriceAfter;
    }

    @Override
    public BigDecimal getMeanPriceBefore() {
        return meanPriceBefore;
    }

    @Override
    public BigDecimal getMeanPriceAfter() {
        return meanPriceAfter;
    }
}
