package org.projectc.simulation.dex.tax;

import java.math.BigDecimal;

/**
 * TODO for now this only looks at n number of last transactions, and ignores time.
 */
public class MovingAvgTaxItem {

    private final int ticks;
    private final BigDecimal desiredPerformance;
    private final double baseWeight;

    public MovingAvgTaxItem(int ticks, BigDecimal desiredPerformance, double baseWeight) {
        this.ticks = ticks;
        this.desiredPerformance = desiredPerformance;
        this.baseWeight = baseWeight;
    }

    /**
     * How many of the last transactions to look at for this moving average.
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * How much gain on the price is desired for that time period, eg "1.02" means factor 1.02, therefore 2%.
     */
    public BigDecimal getDesiredPerformance() {
        return desiredPerformance;
    }

    /**
     * How important this item is in relation to the others (weighted mean).
     */
    public double getBaseWeight() {
        return baseWeight;
    }
}
