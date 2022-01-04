package org.projectc.simulation.dex.events;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class TransactionEventImpl implements TransactionEvent {

    private final boolean isBuy;
    private final BigInteger eth;
    private final BigInteger token;

    public TransactionEventImpl(boolean isBuy,
                                BigInteger eth,
                                BigInteger token) {
        this.isBuy = isBuy;
        this.eth = eth;
        this.token = token;
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
    public BigDecimal getPriceIncludingTax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getMediumPriceBefore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getMediumPriceAfter() {
        throw new UnsupportedOperationException();
    }

}
