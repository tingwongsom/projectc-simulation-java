package org.projectc.simulation.dex.events;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A buy or sell transaction submitted to a {@link TransactionEventListener}.
 */
public interface TransactionEvent {

    /**
     * true for buy token for ETH transaction.
     * false for sell token for ETH transaction.
     */
    boolean isBuy();

    BigInteger getEth();

    BigInteger getToken();

    BigDecimal computeEffectivePrice();
    BigDecimal getPriceIncludingTax();

    BigDecimal getMediumPriceBefore();
    BigDecimal getMediumPriceAfter();

}
