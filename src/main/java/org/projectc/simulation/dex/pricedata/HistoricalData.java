package org.projectc.simulation.dex.pricedata;


import org.projectc.simulation.dex.tax.TaxTxEvent;
import org.projectc.simulation.dex.tax.DynamicallyTaxedDex;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * The historical data is used in the {@link DynamicallyTaxedDex} to compute various moving averages.
 * Every paid price (buy and sell) is recorded here through the event listener.
 */
public class HistoricalData {

    private static final BigDecimal eighteenDigitsDecimal = new BigDecimal("1000000000000000000");

//    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Map<String, TaxTxEvent> map = new LinkedHashMap<>();
    private LocalDateTime lastTime;

    public HistoricalData() {
//        lastTime = LocalDateTime.now();
        lastTime = LocalDateTime.of(2020, Month.JANUARY, 1, 1, 1, 1);
    }

    public void add(TaxTxEvent event) {
        incrementTime();
        Date tmfn = Date.from(lastTime.atZone(ZoneId.systemDefault()).toInstant());
        String timestamp = SIMPLE_DATE_FORMAT.format(tmfn);
        if (map.containsKey(timestamp)) {
            throw new RuntimeException("Duplicate timestamp!");
        }

        map.put(timestamp, event);
    }

    private void incrementTime() {
        //        lastTime = lastTime.plus(Duration.of(1, ChronoUnit.MINUTES));
        lastTime = lastTime.plus(Duration.of(1, ChronoUnit.DAYS));
    }

    public int getTicksAvailable() {
        return map.size();
    }

    /**
     */
    public BigDecimal computeMovingAverage(int ticks) {
        List<TaxTxEvent> sublist = getSublistForticks(ticks);
        BigDecimal total = collect(sublist);
        return total.divide(new BigDecimal(sublist.size()), 18, RoundingMode.HALF_UP);
    }

    /**
     * @param includingMyDesiredTransaction acts as if this transaction that someone wants to make was included
     *                                      in the price. because if it moves the market much, we need to tax
     *                                      based on that included, else things could be abused and manipulated.
     */
    public BigDecimal computeMovingAverageIncluding(BigDecimal includingMyDesiredTransaction, int ticks) {
        List<TaxTxEvent> sublist = getSublistForticks(ticks);
        BigDecimal total = collect(sublist);
        total = total.add(includingMyDesiredTransaction);
        return total.divide(new BigDecimal(sublist.size() + 1), 18, RoundingMode.HALF_UP);
    }

    
    private List<TaxTxEvent> getSublistForticks(int ticks) {
        List<TaxTxEvent> values = new ArrayList<>(map.values());
        return values.subList(values.size() - Math.min(values.size(), ticks), values.size());
    }

    
    private BigDecimal collect(List<TaxTxEvent> sublist) {
        BigDecimal total = new BigDecimal("0");
        for (TaxTxEvent transactionEvent : sublist) {
            total = total.add(transactionEvent.computeEffectivePrice());
        }
        return total;
    }

}
