package org.projectc.simulation;


import org.projectc.simulation.dex.tax.MovingAvgTaxItem;
import org.projectc.simulation.dex.tax.TaxConfig;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for the JavaDexApp. Edit this to your likings.
 *
 * Besides this class you can also customize the simulation runs in the MarketCycle class,
 * in the random() and randomSentiment() methods.
 *
 * Also the Sentiment class has variables to modify.
 *
 * Then there's some randomness programmed in RandomActivity.
 */
public class AppConfig {

    /**
     * Check out project https://github.com/tingwongsom/projectc-chart
     */
    public String getJsonOutputFile() {
        return "/path/to/projectc-chart/src/my-static-data.json";
    }

    public String getPriceAndTaxPlotterFile() {
        return "/wherever/you/want/priceprogress.csv";
    }


    public BigInteger getNumTokens() {
        return new BigInteger("100");
    }
    public int getNumEthIntoLiquidityPool() {
        return 30;
    }
    public int getNumWallets() {
        return 100;
    }
    public int getEthPerWallet() {
        return 200;
    }

    public int getNumIterationsInSimulation() {
        return 140;
    }

    public TaxConfig getTaxConfig() {
        return new TaxConfig.Builder()
                .initialTaxPercent(3d)
                .minBuyTaxPercent("0")
                .maxBuyTaxPercent("90")
                .minSellTaxPercent("0")
                .maxSellTaxPercent("90")
                .build();
    }


    
    public List<MovingAvgTaxItem> getMovingAvgTaxItems() {
        List<MovingAvgTaxItem> items = new ArrayList<>();

        //the moving average of the last 5 transactions.
        //TODO we should look at the time. currently it's a hack that we just use the number of last transactions to simulate time. it should probably be a combination.
        //the desired performance is 0.005% price gain.
        //the weight of this moving average, in relation to others, is 1d.
        items.add(new MovingAvgTaxItem(5, new BigDecimal("1.005"), 1d));

        //same logic as above, for different times.
        items.add(new MovingAvgTaxItem(30, new BigDecimal("1.02"), 1d));
        items.add(new MovingAvgTaxItem(100, new BigDecimal("1.1"), 1d));
        items.add(new MovingAvgTaxItem(300, new BigDecimal("1.3"), 1d));

        return Collections.unmodifiableList(items);
    }

}
