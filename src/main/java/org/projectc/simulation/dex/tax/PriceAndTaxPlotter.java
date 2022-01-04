package org.projectc.simulation.dex.tax;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;

/**
 * Writes a file with all paid prices, overwrites it each time.
 */
public class PriceAndTaxPlotter {

    private final String priceAndTaxPlotterFile;

    private String lastLine = "";

    public PriceAndTaxPlotter(String priceAndTaxPlotterFile) {
        this.priceAndTaxPlotterFile = priceAndTaxPlotterFile;

        new File(priceAndTaxPlotterFile).delete();

        try {
            new File(priceAndTaxPlotterFile).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(BigDecimal mean, BigDecimal buyTax, BigDecimal sellTax) {
        BigDecimal meanWithBuyTax = mean.add(mean.multiply(buyTax).divide(new BigDecimal("100")));
        BigDecimal meanWithSellTax = mean.subtract(mean.multiply(sellTax).divide(new BigDecimal("100")));

        StringJoiner sj = new StringJoiner("\t");
        sj.add(""+meanWithBuyTax);
        sj.add(""+meanWithSellTax);
        sj.add(""+mean); //as 3rd column because when making graphs in openoffice then prints over the other lines, better visibility.

        sj.add(""+buyTax);
        sj.add(""+sellTax);

        String s = sj.toString() + "\r\n";
        if (lastLine.equals(s)) {
            return;
        }
        lastLine = s;

        try {
            Files.write(Paths.get(priceAndTaxPlotterFile), s.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
