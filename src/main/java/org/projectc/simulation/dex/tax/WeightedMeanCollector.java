package org.projectc.simulation.dex.tax;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class WeightedMeanCollector {

    private final double minIncl;
    private final double maxIncl;
    private final List<Double> values;
    private final List<Double> weights;

    public static WeightedMeanCollector unbounded() {
        return new WeightedMeanCollector(-1.7976931348623157E308D, 1.7976931348623157E308D);
    }

    public WeightedMeanCollector(double minIncl, double maxIncl) {
        this.values = new ArrayList();
        this.weights = new ArrayList();
        if (Double.isNaN(minIncl)) {
            throw new IllegalArgumentException("Min. boundary can not be NaN.");
        } else if (Double.isNaN(maxIncl)) {
            throw new IllegalArgumentException("Max. boundary can not be NaN.");
        } else {
            this.minIncl = minIncl;
            this.maxIncl = maxIncl;
        }
    }

    
    public WeightedMeanCollector add(double value, double weight) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("Value can not be NaN.");
        } else if (Double.isNaN(weight)) {
            throw new IllegalArgumentException("Weight can not be NaN.");
        } else if (weight <= 0.0D) {
            throw new IllegalArgumentException("Weight must be > 0 but was " + weight + "!");
        } else {
            value = (Double)OutOfBoundsStrategy.CUTROUNDINGERROR.apply(value, this.minIncl, this.maxIncl);
            this.values.add(value);
            this.weights.add(weight);
            return this;
        }
    }

    public double getMean() {
        if (this.values.isEmpty()) {
            throw new IllegalStateException("The add() method was never called!");
        } else if (this.values.size() == 1) {
            return (Double)this.values.get(0);
        } else if (areAllValuesIdentical(this.values)) {
            return (Double)this.values.get(0);
        } else {
            double totalWeight = Sum.sumDouble(this.weights);
            double ret = 0.0D;

            for(int i = 0; i < this.values.size(); ++i) {
                double value = (Double)this.values.get(i);
                double weight = (Double)this.weights.get(i);
                double weightRatio = weight / totalWeight;
                double weightedValue = value * weightRatio;
                ret += weightedValue;
            }

            return (Double)OutOfBoundsStrategy.CUTROUNDINGERROR.apply(ret, this.minIncl, this.maxIncl);
        }
    }

    private static boolean areAllValuesIdentical(List<Double> values) {
        if (values.size() < 2) {
            return true;
        } else {
            for(int i = 0; i < values.size(); ++i) {
                if (i > 0 && (Double)values.get(i) != (Double)values.get(i - 1)) {
                    return false;
                }
            }

            return true;
        }
    }

    public double getMeanOr(double fallback) {
        if (Double.isNaN(fallback)) {
            throw new IllegalArgumentException("Fallback value can not be NaN.");
        } else {
            return this.values.isEmpty() ? fallback : this.getMean();
        }
    }


    public String toString() {
        String ret = "WeightedMeanCollector{";
        if (this.values.isEmpty()) {
            ret = ret + "empty";
        } else if (this.values.size() == 1) {
            ret = ret + "mean=" + this.getMean();
            ret = ret + ", 1 item";
        } else {
            ret = ret + "mean=" + this.getMean();
            ret = ret + ", " + this.values.size() + " item(s)";
        }

        return ret + "}";
    }

}


enum OutOfBoundsStrategy {
    THROW {
        public <T extends Number> T apply(T value, T minIncl, T maxIncl) {
            if (GenericAlgebra.isLess(value, minIncl)) {
                throw new IllegalArgumentException("New value of " + value + " is below min value " + minIncl + "!");
            } else if (GenericAlgebra.isMore(value, maxIncl)) {
                throw new IllegalArgumentException("New value of " + value + " is above max value " + maxIncl + "!");
            } else {
                return value;
            }
        }
    },
    CUTROUNDINGERROR {
        public <T extends Number> T apply(T value, T minIncl, T maxIncl) {
            if (value instanceof Double) {
                double diff;
                if ((Double)value < (Double)minIncl) {
                    diff = (Double)minIncl - (Double)value;
                    if (diff > 1.0E-6D) {
                        throw new IllegalArgumentException("New value of " + value + " is below min value " + minIncl + "!");
                    } else {
                        return minIncl;
                    }
                } else if ((Double)value > (Double)maxIncl) {
                    diff = (Double)value - (Double)maxIncl;
                    if (diff > 1.0E-6D) {
                        throw new IllegalArgumentException("New value of " + value + " is above max value " + maxIncl + "!");
                    } else {
                        return maxIncl;
                    }
                } else {
                    return value;
                }
            } else {
                return THROW.apply(value, minIncl, maxIncl);
            }
        }
    };

    public abstract <T extends Number> T apply(T var1, T var2, T var3);

}



class Sum {

    public static double sumDouble(Iterable<Double> in) {
        if (in == null) {
            return 0.0D;
        } else {
            double ret = 0.0D;
            Iterator var3 = in.iterator();

            while(var3.hasNext()) {
                Double val = (Double)var3.next();
                if (val != null) {
                    ret += val;
                }
            }

            return ret;
        }
    }

}



/**
 */
class GenericAlgebra {

    public static <T extends Number> boolean isLess( T thisHere,  T thanThis) {
        if (thisHere instanceof Integer) {
            //noinspection unchecked
            return (Integer)thisHere < (Integer)thanThis;
        } else if (thisHere instanceof Long) {
            //noinspection unchecked
            return (Long)thisHere < (Long)thanThis;
        } else if (thisHere instanceof Float) {
            //noinspection unchecked
            return (Float)thisHere < (Float)thanThis;
        } else if (thisHere instanceof Double) {
            //noinspection unchecked
            return (Double)thisHere < (Double)thanThis;
        } else if (thisHere instanceof Byte) {
            throw new IllegalArgumentException("Byte type not supported!");
        } else if (thisHere instanceof Short) {
            throw new IllegalArgumentException("Short type not supported!");
        } else {
            throw new IllegalArgumentException("Unsupported type: "+thisHere.getClass().getName());
        }
    }

    public static <T extends Number> boolean isMore(T thisHere, T thanThis) {
        if (thisHere instanceof Integer) {
            //noinspection unchecked
            return (Integer)thisHere > (Integer)thanThis;
        } else if (thisHere instanceof Long) {
            //noinspection unchecked
            return (Long)thisHere > (Long)thanThis;
        } else if (thisHere instanceof Float) {
            //noinspection unchecked
            return (Float)thisHere > (Float)thanThis;
        } else if (thisHere instanceof Double) {
            //noinspection unchecked
            return (Double)thisHere > (Double)thanThis;
        } else if (thisHere instanceof Byte) {
            throw new IllegalArgumentException("Byte type not supported!");
        } else if (thisHere instanceof Short) {
            throw new IllegalArgumentException("Short type not supported!");
        } else {
            throw new IllegalArgumentException("Unsupported type: "+thisHere.getClass().getName());
        }
    }

}
