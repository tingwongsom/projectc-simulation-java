package org.projectc.simulation.randomactivity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

/**
 * The overall sentiment of the market.
 *
 * This decides on
 *  - the (random) amounts of buy and sell to execute for that market cycle.
 *    eg in FOMO sentiment market participants want to buy a lot more than sell.
 *  - the (random) max acceptable buy or sell tax
 *    eg in FOMO sentiment market participants accept higher buy taxes, and lower sell taxes.
 */
public enum Sentiment {

    FOMO {
        public boolean isBuyTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 40d, 4);
        }
        public boolean isSellTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 6d, 4);
        }

        @Override
        public FillOrder computeBuyVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 15, 30);
        }
        @Override
        public FillOrder computeSellVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 1, 5);
        }
    },
    BULL {
        public boolean isBuyTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 25d, 5);
        }
        public boolean isSellTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 4d, 3);
        }
        @Override
        public FillOrder computeBuyVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 8, 20);
        }
        @Override
        public FillOrder computeSellVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 3, 7);
        }
    },
    AVERAGE {
        public boolean isBuyTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 15d, 4);
        }
        public boolean isSellTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 15d, 4);
        }
        @Override
        public FillOrder computeBuyVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 5, 10);
        }
        @Override
        public FillOrder computeSellVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 5, 10);
        }
    },
    BEAR {
        public boolean isBuyTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 5d, 3);
        }
        public boolean isSellTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 30d, 4);
        }

        @Override
        public FillOrder computeBuyVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 3, 7);
        }
        @Override
        public FillOrder computeSellVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 8, 20);
        }
    },
    CRASH {
        public boolean isBuyTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 4d, 1);
        }
        public boolean isSellTaxAcceptable(BigDecimal tax) {
            return isTaxAcceptable(tax, 50d, 3);
        }

        @Override
        public FillOrder computeBuyVolume(BigInteger ethInPool) {
            return randomVolume(ethInPool, 1, 5);
        }
        @Override
        public FillOrder computeSellVolume(BigInteger ethInPool) {
            //this is not opposite to FOMO, because a crash is harder to the downside than a
            //fomo is to the upside.

            //the visual representation is:
            //  the bull runs up the stairs and the bear jumps out of the window.

            //hence this common chart pattern in the free market:
            //
            //                 ...........
            //                /           |
            //             ...            |
            //           /                |
            //        ..                  |
            //      /                     |

            return randomVolume(ethInPool, 15, 50);
        }
    },
    ;

    public abstract boolean isBuyTaxAcceptable(BigDecimal tax);
    public abstract boolean isSellTaxAcceptable(BigDecimal tax);

    private static boolean isTaxAcceptable(BigDecimal tax, double maxPercent, int iterations) {
        double maxAcceptable3 = computeMaxAcceptableTaxPercent(maxPercent, iterations);
        return (tax.doubleValue() <= maxAcceptable3);
    }

    /**
     * Example with maxPercent=80 and iterations=3 could go:
     * random 0-80 choose 72
     * random 0-72 choose 15
     * random 0-15 choose 8
     * result 8.
     * Any number from 0 to 80 could be the end result in this case, higher numbers less likely
     * than lower numbers.
     *
     * The higher the number of iterations, the more likely the output number goes towards zero.
     *
     * This is a simple method to compute a non-linear, curved number, where high values are
     * only occasionally accepted.
     */
    private static double computeMaxAcceptableTaxPercent(double maxPercent, int iterations) {
        if (iterations<1) throw new IllegalArgumentException();

        double result = maxPercent;
        for (int i=1; i<iterations; i++) {
            result = _randomUpTo(result);
        }
        return result;
    }

    private static double _randomUpTo(double maxPercent) {
        double rangeMin = 0d;
        double rangeMax = maxPercent;
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public abstract FillOrder computeBuyVolume(BigInteger ethInPool);

    public abstract FillOrder computeSellVolume(BigInteger ethInPool);


    /**
     * Computes a random percent from minPercent to maxPercent (incl or excl, don't know) and then
     * selects that amount from the ethInPool.
     *
     * Example:
     * ethInPool = 200
     * minPercent = 5
     * maxPercent = 15
     * random number can be anywhere 5-15, where a 8 would mean 16 because 8% of 200 is 16.
     */
    private static FillOrder randomVolume(BigInteger ethInPool, int minPercent, int maxPercent) {
        int randomPercent = new Random().nextInt(maxPercent-minPercent) + minPercent;
        BigInteger transactPercentOfEthInPool = ethInPool.multiply(BigInteger.valueOf(randomPercent)).divide(new BigInteger("100"));
        return new FillOrder(transactPercentOfEthInPool);
    }

}
