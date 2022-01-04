package org.projectc.simulation.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.projectc.simulation.dex.tax.TaxTxEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonPriceFileWriter {

    private static final BigDecimal eighteenDigitsDecimal = new BigDecimal("1000000000000000000");

//    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Map<String, ChartPriceItem> map = new LinkedHashMap<>();
    private LocalDateTime lastTime;

    public JsonPriceFileWriter() {
//        lastTime = LocalDateTime.now();
        lastTime = LocalDateTime.of(1900, Month.JANUARY, 1, 1, 1, 1);
    }

    public void add(TaxTxEvent event) {
//        lastTime = lastTime.plus(Duration.of(1, ChronoUnit.MINUTES));
        lastTime = lastTime.plus(Duration.of(1, ChronoUnit.DAYS));
        Date tmfn = Date.from(lastTime.atZone(ZoneId.systemDefault()).toInstant());

        String timestamp = SIMPLE_DATE_FORMAT.format(tmfn);
        if (map.containsKey(timestamp)) {
            throw new RuntimeException("Duplicate timestamp!");
        }

        BigDecimal meanNoTax = event.getMeanPriceWithoutTax();
        BigDecimal open;
        BigDecimal close;
        BigDecimal high;
        BigDecimal low;
        if (event.isBuy()) {
            open = event.getMediumPriceBefore();
            close = event.getMediumPriceAfter();
            high = event.getWithTaxApplied();
            low = event.getMediumPriceBefore();
        } else {
            open = event.getMeanPriceBefore();
            close = event.getMeanPriceAfter();
            high = event.getMeanPriceBefore();
            low = event.getWithTaxApplied();
        }

        ChartPriceItem item = new ChartPriceItem(
                open.toPlainString(),
                close.toPlainString(),
                high.toPlainString(),
                low.toPlainString(),
                new BigDecimal(event.getEth()).divide(eighteenDigitsDecimal, 18, RoundingMode.HALF_UP).toPlainString()
        );
        map.put(timestamp, item);
    }

    public void write(String targetFile) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(map);
            Files.write(Paths.get(targetFile), Collections.singleton(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
