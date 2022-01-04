package org.projectc.simulation.math;



import java.math.BigDecimal;
import java.math.RoundingMode;

public class DexMath {

    /**
     * @return for example "3.2" when currentPrice is 3.2% above formerPrice.
     *         value can be above 100, eg if price went from 100 to 300, and
     *         value can be below 0, when price decreased, but not below -100.
     */
    
    public static BigDecimal computePerformanceInPercent(BigDecimal currentPrice, BigDecimal formerPrice) {
        BigDecimal performance = currentPrice.divide(formerPrice, RoundingMode.HALF_UP);
        BigDecimal performancePercent = performance.multiply(new BigDecimal("100")).subtract(new BigDecimal("100"));
        return performancePercent;
    }



}
