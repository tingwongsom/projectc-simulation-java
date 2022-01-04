package org.projectc.simulation;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A wallet contains assets, in this simplified impl it can have ETH and tokens.
 */
public class Wallet {

    private static final BigInteger eighteenDigitsInteger = new BigInteger("1000000000000000000");


    private BigInteger eth;
    private BigInteger token;

    public Wallet(int eth, int token) {
        this.eth = BigInteger.valueOf(eth).multiply(eighteenDigitsInteger);
        this.token = BigInteger.valueOf(token).multiply(eighteenDigitsInteger);;
    }

    public BigInteger getEth() {
        return eth;
    }

    public BigInteger getToken() {
        return token;
    }

    public boolean hasEth() {
        return eth.compareTo(BigInteger.valueOf(0)) > 0;
    }
    public boolean hasToken() {
        return token.compareTo(BigInteger.valueOf(0)) > 0;
    }


    public BigInteger takeEth(int amount) {
        BigInteger take = BigInteger.valueOf(amount).multiply(eighteenDigitsInteger);
        return takeEth(take);
    }
    
    public BigInteger takeEth(BigInteger take) {
        if (take.compareTo(this.eth) > 0) {
            throw new RuntimeException();
        }
        this.eth = this.eth.subtract(take);
        return take;
    }

    public BigInteger takeEthPercent(int percent) {
        BigInteger take = eth.multiply(BigInteger.valueOf(percent)).divide(BigInteger.valueOf(100));
        return takeEth(take);
    }

    public void addEth(BigInteger addEth) {
        this.eth = this.eth.add(addEth);
    }

    public void addToken(BigInteger addToken) {
        this.token = this.token.add(addToken);
    }


    public BigInteger takeToken(BigInteger take) {
        if (take.compareTo(this.token) > 0) {
            throw new RuntimeException();
        }
        this.token = this.token.subtract(take);
        return take;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "token=" + new BigDecimal(token).divide(new BigDecimal("1000000000000000000"), 18, RoundingMode.HALF_UP) +
                ", eth=" + new BigDecimal(eth).divide(new BigDecimal("1000000000000000000"), 18, RoundingMode.HALF_UP) +
                '}';
    }


}
