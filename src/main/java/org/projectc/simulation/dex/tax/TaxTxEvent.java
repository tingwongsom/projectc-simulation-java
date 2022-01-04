package org.projectc.simulation.dex.tax;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A buy or sell transaction in the {@link DynamicallyTaxedDex} as reported to a {@link TaxTxEventListener}.
 */
public interface TaxTxEvent {

    /**
     * true for buy token for ETH transaction.
     * false for sell token for ETH transaction.
     */
    boolean isBuy();

    BigInteger getEth();

    BigInteger getToken();

    BigDecimal computeEffectivePrice();

    BigDecimal getMeanPriceWithoutTax();

    BigDecimal getTax();
    BigDecimal getWithTaxApplied();

    BigDecimal getPriceIncludingTax();

    BigDecimal getMediumPriceBefore();
    BigDecimal getMediumPriceAfter();

    BigDecimal getMeanPriceBefore();

    BigDecimal getMeanPriceAfter();
}
