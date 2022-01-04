package org.projectc.simulation.dex.tax;

import org.projectc.simulation.math.DexMath;

import java.math.BigDecimal;

/**
 * Computes the tax that would apply now for a given amount for a buy or a sell, in relation to one
 * moving average. Multiple of these results are then combined into an overall result.
 */
public class TaxScaler {

    public static class WeightedTax {
        private final BigDecimal taxPercent;
        private final double weight;

        /**
         * @param taxPercent can be negative, to cancel out or ease other weighted tax computations.
         */
        public WeightedTax(BigDecimal taxPercent, double weight) {
            this.taxPercent = taxPercent;
            this.weight = weight;
        }

        public BigDecimal getTaxPercent() {
            return taxPercent;
        }

        public double getWeight() {
            return weight;
        }
    }

    public WeightedTax computeTax(boolean isBuy, BigDecimal newPrice, BigDecimal formerPrice, BigDecimal desiredPerformance,
                                  double baseWeight) {
        return _computeTax(isBuy, newPrice, formerPrice, desiredPerformance, baseWeight);
    }

    private WeightedTax _computeTax(boolean isBuy, BigDecimal newPrice, BigDecimal formerPrice, BigDecimal desiredPerformance,
                                    double baseWeight) {
        BigDecimal actualPerformance = DexMath.computePerformanceInPercent(newPrice, formerPrice);
        //BigDecimal desiredPrice = formerPrice.multiply(desiredPerformance);

        if (actualPerformance.compareTo(desiredPerformance) > 0) {
            //outperformed.
            BigDecimal outperformanceGap = actualPerformance.subtract(desiredPerformance);
            if (isBuy) {
                //we don't want too much outperformance. so we need to tax to ease buying pressure.
                //TODO adjust weight based on how much it overperformed.
                return new WeightedTax(outperformanceGap, baseWeight);
            } else {
                //thanks for counter-steering
                BigDecimal negatedPart = outperformanceGap.divide(new BigDecimal(4)).negate();
                return new WeightedTax(negatedPart, baseWeight);
            }
        } else if (actualPerformance.compareTo(desiredPerformance) < 0) {
            //underperformed.
            BigDecimal underperformanceGap = desiredPerformance.subtract(actualPerformance);
            if (isBuy) {
                //since this is a buy, and we want more buys, we don't tax it.
                BigDecimal negatedPart = underperformanceGap.divide(new BigDecimal(4)).negate();
                return new WeightedTax(negatedPart, baseWeight);
            } else {
                //TODO adjust weight based on how much it underperformed.
                return new WeightedTax(underperformanceGap.multiply(new BigDecimal("2")), baseWeight);
            }
        } else {
            //exactly as desired. don't tax it.
            return new WeightedTax(new BigDecimal("0"), baseWeight);
        }
    }

}
