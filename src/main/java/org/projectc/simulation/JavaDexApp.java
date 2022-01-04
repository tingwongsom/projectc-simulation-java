package org.projectc.simulation;


import org.projectc.simulation.chart.JsonPriceFileWriter;
import org.projectc.simulation.participants.MarketParticipant;
import org.projectc.simulation.participants.MarketParticipantImpl;
import org.projectc.simulation.participants.ParticipantType;
import org.projectc.simulation.randomactivity.RandomActivity;
import org.projectc.simulation.dex.JavaDex;
import org.projectc.simulation.dex.JavaDexImpl;
import org.projectc.simulation.dex.events.TransactionEventListener;
import org.projectc.simulation.dex.pricedata.HistoricalData;
import org.projectc.simulation.dex.tax.DynamicallyTaxedDex;
import org.projectc.simulation.dex.tax.PriceAndTaxPlotter;
import org.projectc.simulation.dex.tax.TaxTxEvent;
import org.projectc.simulation.dex.tax.TaxTxEventListener;
import org.projectc.simulation.randomactivity.ExecutionResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * The main app to run. Edit the AppConfig class, then run this.
 */
public class JavaDexApp {

    private static final BigInteger eighteenDigitsInteger = new BigInteger("1000000000000000000");
    private static final BigDecimal eighteenDigitsDecimal = new BigDecimal("1000000000000000000");


    public static void main(String[] args) {
        new JavaDexApp();
    }


    private final LiquidityPool lp;
    private final JavaDex dex;

    public JavaDexApp() {
        AppConfig appConfig = new AppConfig();

        Bank bank = makeBank(appConfig.getNumWallets(), appConfig.getEthPerWallet());

        final HistoricalData historicalData = new HistoricalData();
        final JsonPriceFileWriter jsonPriceFileWriter = new JsonPriceFileWriter();

        lp = new LiquidityPool(
                appConfig.getNumTokens().multiply(eighteenDigitsInteger),
                bank.getFirstWallet().takeEth(appConfig.getNumEthIntoLiquidityPool())
        );
        JavaDex dex = new JavaDexImpl(lp, TransactionEventListener.nullImpl());

        DynamicallyTaxedDex taxedDex = new DynamicallyTaxedDex(
                dex,
                historicalData,
                appConfig.getTaxConfig(),
                appConfig.getMovingAvgTaxItems(),
                new TaxTxEventListener() {
                    @Override
                    public void event(TaxTxEvent event) {
                        historicalData.add(event);
                        jsonPriceFileWriter.add(event);
                    }
                }

        );
        this.dex = taxedDex;

        Ecosystem ecosystem = new Ecosystem(
                bank,
                lp,
                taxedDex.getTaxWallet(),
                new PriceAndTaxPlotter(appConfig.getPriceAndTaxPlotterFile())
        );

        printStatus();

        for (int i = 1; i <= appConfig.getNumIterationsInSimulation(); i++) {
            ExecutionResult execResult = new RandomActivity(ecosystem, this.dex).perform(i);
            System.out.println(execResult);
            printStatus();
        }
        jsonPriceFileWriter.write(appConfig.getJsonOutputFile());

        System.out.println("Tax wallet: "+taxedDex.getTaxWallet());
        System.out.println(ecosystem);
    }

    private void printStatus() {
        System.out.println("-------------------");
        System.out.println(lp);

        //not necessary to print, not useful when correct:
//        System.out.println(ecosystem);
    }



    
    private Bank makeBank(int numWallets, int ethPerWallet) {
        Bank bank = new Bank();
        for (int i = 0; i < numWallets; i++) {
            Wallet wallet = new Wallet(ethPerWallet, 0);
            MarketParticipant marketParticipant = new MarketParticipantImpl(wallet, ParticipantType.BUY_LOW_SELL_HIGH);
            bank.addCustomer(marketParticipant);
        }
        return bank;
    }



    private void valueOfEth(int i) {
        BigInteger input = BigInteger.valueOf(i).multiply(eighteenDigitsInteger);
        BigDecimal tokensToGet = dex.getLp().howManyTokenForMyEth(input);
        System.out.println(i + " eth gives " + tokensToGet.divide(eighteenDigitsDecimal, 10, RoundingMode.HALF_UP) + " tokens");
    }

    private void valueOfToken(int i) {
        BigInteger input = BigInteger.valueOf(i).multiply(eighteenDigitsInteger);
        BigDecimal ethToGet = dex.getLp().howManyEthForMyToken(input);
        System.out.println(i + " token gives " + ethToGet.divide(eighteenDigitsDecimal, 10, RoundingMode.HALF_UP) + " eth");
    }

}
