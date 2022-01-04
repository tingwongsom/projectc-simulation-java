package org.projectc.simulation.dex.tax;

import java.math.BigDecimal;

/**
 * Configuration for the dynamic tax.
 */
public class TaxConfig {

    public static class Builder {
        private double initialTaxPercent = 3d;

        private BigDecimal minBuyTaxPercent = new BigDecimal("0");
        private BigDecimal maxBuyTaxPercent = new BigDecimal("99");

        private BigDecimal minSellTaxPercent = new BigDecimal("0");
        private BigDecimal maxSellTaxPercent = new BigDecimal("99");

        /**
         * This tax is used for the first few transactions before any moving average is available.
         */
        public Builder initialTaxPercent(double d) {
            this.initialTaxPercent = d;
            return this;
        }

        public Builder minBuyTaxPercent(String s) {
            this.minBuyTaxPercent = new BigDecimal(s);
            return this;
        }

        public Builder maxBuyTaxPercent(String s) {
            this.maxBuyTaxPercent = new BigDecimal(s);
            return this;
        }

        public Builder minSellTaxPercent(String s) {
            this.minSellTaxPercent = new BigDecimal(s);
            return this;
        }

        public Builder maxSellTaxPercent(String s) {
            this.maxSellTaxPercent = new BigDecimal(s);
            return this;
        }

        public TaxConfig build() {
            return new TaxConfig(
                    initialTaxPercent,
                    minBuyTaxPercent,
                    maxBuyTaxPercent,
                    minSellTaxPercent,
                    maxSellTaxPercent
            );
        }
    }

    private final double initialTaxPercent;

    private final BigDecimal minBuyTaxPercent;
    private final BigDecimal maxBuyTaxPercent;

    private final BigDecimal minSellTaxPercent;
    private final BigDecimal maxSellTaxPercent;

    private TaxConfig(double initialTaxPercent,
                      BigDecimal minBuyTaxPercent, BigDecimal maxBuyTaxPercent,
                      BigDecimal minSellTaxPercent, BigDecimal maxSellTaxPercent) {
        this.initialTaxPercent = initialTaxPercent;
        this.minBuyTaxPercent = minBuyTaxPercent;
        this.maxBuyTaxPercent = maxBuyTaxPercent;
        this.minSellTaxPercent = minSellTaxPercent;
        this.maxSellTaxPercent = maxSellTaxPercent;
    }

    public double getInitialTaxPercent() {
        return initialTaxPercent;
    }


    public BigDecimal applyBuyTaxRule(BigDecimal computedTaxPercent) {
        if (computedTaxPercent.compareTo(minBuyTaxPercent) < 0) return minBuyTaxPercent;
        if (computedTaxPercent.compareTo(maxBuyTaxPercent) > 0) return maxBuyTaxPercent;
        return computedTaxPercent;
    }

    public BigDecimal applySellTaxRule(BigDecimal computedTaxPercent) {
        if (computedTaxPercent.compareTo(minSellTaxPercent) < 0) return minSellTaxPercent;
        if (computedTaxPercent.compareTo(maxSellTaxPercent) > 0) return maxSellTaxPercent;
        return computedTaxPercent;
    }


    @Override
    public String toString() {
        return "TaxConfig{" +
                "minBuyTaxPercent=" + minBuyTaxPercent +
                ", maxBuyTaxPercent=" + maxBuyTaxPercent +
                ", minSellTaxPercent=" + minSellTaxPercent +
                ", maxSellTaxPercent=" + maxSellTaxPercent +
                '}';
    }

}
