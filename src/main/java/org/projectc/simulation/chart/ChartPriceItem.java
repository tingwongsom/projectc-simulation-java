package org.projectc.simulation.chart;

/**
 * example:
 * 	"2018-09-05": {
 * 		"open": "228.99",
 * 		"close": "226.87",
 * 		"high": "229.67",
 * 		"low": "225.10",
 * 		"volume": "33332960"
 *  }
 */
public class ChartPriceItem {

    private final String open;
    private final String close;
    private final String high;
    private final String low;
    private final String volume;

    public ChartPriceItem(String open, String close, String high, String low, String volume) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }
    public ChartPriceItem(String price, String volume) {
        this.open = price;
        this.close = price;
        this.high = price;
        this.low = price;
        this.volume = volume;
    }


    public String getOpen() {
        return open;
    }

    public String getClose() {
        return close;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getVolume() {
        return volume;
    }
}
