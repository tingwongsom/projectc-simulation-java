package org.projectc.simulation.randomactivity;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * An instruction to transact (buy or sell) for the given amount, specified in ETH.
 *
 * Whether the amount is for buy or sell is not specified here, it's known by context.
 *
 * The amount is executed amongst multiple (up to all) market participants, until filled.
 * It does not always get fully filled, reasons are:
 *  - not enough funds available to trade this much
 *  - tax too high and therefore market participant is not interested
 *
 */
public class FillOrder {

    private static final BigInteger eighteenDigitsInteger = new BigInteger("1000000000000000000");
    private static final BigDecimal eighteenDigitsDecimal = new BigDecimal("1000000000000000000");


    private final BigInteger total;
    private BigInteger filled = new BigInteger("0");
    private BigInteger rejected = new BigInteger("0");

    public FillOrder(BigInteger total) {
        this.total = total;
    }
    public FillOrder(int randomBuyEth) {
        this(BigInteger.valueOf(randomBuyEth).multiply(eighteenDigitsInteger));
    }

    public BigInteger getTotal() {
        return total;
    }

    public BigInteger getOutstanding() {
        BigInteger remaining = total.subtract(filled).subtract(rejected);
        if (remaining.compareTo(BigInteger.valueOf(0))>0)return remaining;
        return BigInteger.valueOf(0);
    }

    public boolean hasOutstanding() {
        return getOutstanding().compareTo(new BigInteger("0")) >0;
    }

    public void addFilled(BigInteger filled) {
        this.filled = this.filled.add(filled);
    }

    public void addRejected(BigInteger rejected) {
        this.rejected = this.rejected.add(rejected);
    }



    @Override
    public String toString() {
        return "FillOrder{" +
                "total=" + forPrint(total)  +
                ", filled=" + forPrint(filled) +
                ", rejected=" + forPrint(rejected) +
                '}';
    }

    
    private BigDecimal forPrint(BigInteger val) {
        return new BigDecimal(val).divide(eighteenDigitsDecimal, 2, RoundingMode.HALF_UP);
    }

}
