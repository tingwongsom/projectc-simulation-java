package org.projectc.simulation;


import org.projectc.simulation.dex.tax.PriceAndTaxPlotter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Represents the universe of this application, contains its state.
 */
public class Ecosystem {

    private final Bank bank;
    private final LiquidityPool lp;
    private final Wallet taxWallet;
    private final PriceAndTaxPlotter plotter;

    public Ecosystem(Bank bank, LiquidityPool lp, Wallet taxWallet, PriceAndTaxPlotter priceAndTaxPlotter) {
        this.bank = bank;
        this.lp = lp;
        this.taxWallet = taxWallet;
        this.plotter = priceAndTaxPlotter;
    }


    public Bank getBank() {
        return bank;
    }

    public LiquidityPool getLp() {
        return lp;
    }

    public Wallet getTaxWallet() {
        return taxWallet;
    }

    public PriceAndTaxPlotter getPlotter() {
        return plotter;
    }


    @Override
    public String toString() {
        BigInteger totalEth = bank.getTotalEth().add(taxWallet.getEth()).add(lp.getEth());
        BigInteger totalToken = bank.getTotalToken().add(taxWallet.getToken()).add(lp.getToken());

        return "Ecosystem{\n" +
                "  bank: " + bank + "\n" +
                "  lp: " + lp + "\n" +
                "  tax-wallet: " +taxWallet+ "\n" +
                "  total: eth=" +format(totalEth)+", token="+format(totalToken)+ "\n" +
                "}";
    }

    
    private BigDecimal format(BigInteger bankEth) {
        return new BigDecimal(bankEth).divide(new BigDecimal("1000000000000000000"), 10, RoundingMode.HALF_UP);
    }


}
