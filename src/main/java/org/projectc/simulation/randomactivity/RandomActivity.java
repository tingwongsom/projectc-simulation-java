package org.projectc.simulation.randomactivity;

import org.projectc.simulation.Ecosystem;
import org.projectc.simulation.Wallet;
import org.projectc.simulation.dex.JavaDex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomActivity {

    private final Ecosystem ecosystem;
    private final JavaDex dex;

    public RandomActivity(Ecosystem ecosystem, JavaDex dex) {
        this.ecosystem = ecosystem;
        this.dex = dex;
    }

    public ExecutionResult perform(int iteration) {
        MarketCycle marketCycle = MarketCycle.random(ecosystem.getLp());
        return execute(marketCycle);
    }


    /**
     * Attempts to. It happens that not as much can be traded, either because of insufficient
     * funds, or because the tax is considered to be too high.
     */
    private ExecutionResult execute(MarketCycle marketCycle) {
        int loopCounter = 0;
        while (loopCounter <= 250 && marketCycle.hasOutstanding()) {
            loopCounter++;

            if (new Random().nextBoolean()) {
                //buy
                BigInteger fillNow = computeFillNow(marketCycle.getBuyOrder());
                if (isAmountWorthTrading(fillNow)) {
                    informPlotter();
                    fillBuyOrder(fillNow, marketCycle);
                }
            } else {
                //sell
                BigInteger fillNow = computeFillNow(marketCycle.getSellOrder());
                if (isAmountWorthTrading(fillNow)) {
                    informPlotter();
                    fillSellOrder(fillNow, marketCycle);
                }
            }
        }

        return new ExecutionResult(marketCycle);
    }

    /**
     * Below this number (remember: 18 digits after the dot) it makes no sense to execute an order.
     */
    private boolean isAmountWorthTrading(BigInteger fillNow) {
        return fillNow.compareTo(new BigInteger("99999999")) > 0;
    }

    private void informPlotter() {
        BigDecimal mean = ecosystem.getLp().computeCurrentTheoreticalMeanPrice();
        BigDecimal buyTax = dex.askTaxPercentForBuyToken(new BigInteger("10000000000"));
        BigDecimal sellTax = dex.askTaxPercentForSellToken(new BigInteger("10000000000"));
        ecosystem.getPlotter().add(mean, buyTax, sellTax);
    }


    private BigInteger computeFillNow(FillOrder fillOrder) {
        BigInteger toFillTotal = fillOrder.getTotal();

        //buy/sell at most this many % of total buy/sell volume in a single transaction
        int percentOfTransaction;
        if (toFillTotal.longValue() >= 1000) {
            percentOfTransaction = new Random().nextInt(5) + 1;
        } else if (toFillTotal.longValue() >= 50) {
            percentOfTransaction = new Random().nextInt(10) + 1;
        } else if (toFillTotal.longValue() >= 10) {
            percentOfTransaction = new Random().nextInt(25) + 3;
        } else if (toFillTotal.longValue() >= 5) {
            percentOfTransaction = new Random().nextInt(40) + 4;
        } else {
            percentOfTransaction = new Random().nextInt(60) + 7;
        }

        BigInteger fillNow = toFillTotal.multiply(BigInteger.valueOf(percentOfTransaction)).divide(BigInteger.valueOf(100));

        BigInteger outstanding = fillOrder.getOutstanding();
        if (fillNow.compareTo(outstanding) > 0) {
            fillNow = outstanding;
        } else if (new BigDecimal(fillNow).multiply(new BigDecimal("1.1")).compareTo(new BigDecimal(outstanding)) > 0) {
            //would not leave enough for next, let's do all now.
            fillNow = outstanding;
        }
        return fillNow;
    }


    private void fillBuyOrder(BigInteger buyNowInEth, MarketCycle marketCycle) {
        BigInteger filled = new BigInteger("0");
        BigInteger rejected = new BigInteger("0");

        List<Wallet> wallets = findWalletsWithEth();
        for (Wallet randomWallet : wallets) {
            BigInteger buyFromThisWalletInEth = buyNowInEth.subtract(filled).subtract(rejected);
            if (buyFromThisWalletInEth.compareTo(new BigInteger("999999"))<0) {
                //too small remaining.
                break;
            }

            if (randomWallet.getEth().compareTo(buyFromThisWalletInEth) < 0) {
                buyFromThisWalletInEth = randomWallet.getEth();
            }

            BigDecimal tax = dex.askTaxPercentForBuyToken(buyFromThisWalletInEth);
            if (!isBuyTaxAcceptable(tax, marketCycle)) {
//                System.out.println("- Buying tax rejected: "+tax);
                rejected = rejected.add(buyFromThisWalletInEth);
                continue;
            }

            randomWallet.takeEth(buyFromThisWalletInEth);
            BigInteger tokensReceived = dex.buyToken(buyFromThisWalletInEth);
            randomWallet.addToken(tokensReceived);
            filled = filled.add(buyFromThisWalletInEth);

            if (filled.compareTo(buyNowInEth) >= 0) break;
        }

        marketCycle.getBuyOrder().addFilled(filled);
        marketCycle.getBuyOrder().addRejected(rejected);
    }

    private void fillSellOrder(BigInteger sellNowInEth, MarketCycle marketCycle) {
        BigInteger filled = new BigInteger("0");
        BigInteger rejected = new BigInteger("0");

//        BigInteger tokensToSell = dex.getLp().howManyTokensToSellToGetThisManyEth(sellNowInEth).toBigInteger();
//        List<Wallet> wallets = findWalletsWithUpToToken(tokensToSell);
        List<Wallet> wallets = findWalletsWithTokens();
        for (Wallet randomWallet : wallets) {
            BigInteger sellFromThisWalletInEth = sellNowInEth.subtract(filled).subtract(rejected);
            if (sellFromThisWalletInEth.compareTo(new BigInteger("999999"))<0) {
                //too small remaining.
                break;
            }

            BigInteger tokensToSellFromThisWallet;
            BigInteger ethThatWouldGet = dex.getLp().howManyEthForMyToken(randomWallet.getToken()).toBigInteger();
            if (ethThatWouldGet.compareTo(sellFromThisWalletInEth) <= 0) {
                tokensToSellFromThisWallet = randomWallet.getToken();
            } else {
                tokensToSellFromThisWallet = dex.getLp().howManyTokensToSellToGetThisManyEth(sellFromThisWalletInEth).toBigInteger();
            }

            if (tokensToSellFromThisWallet.compareTo(BigInteger.valueOf(0)) > 0) {
                if (randomWallet.getToken().compareTo(tokensToSellFromThisWallet) < 0) {
                    tokensToSellFromThisWallet = randomWallet.getToken();
                }

                BigDecimal tax = dex.askTaxPercentForSellToken(tokensToSellFromThisWallet);
                if (!isSellTaxAcceptable(tax, marketCycle)) {
//                    System.out.println("- Selling tax denied: "+tax);
                    rejected = rejected.add(sellFromThisWalletInEth);
                    continue;
                }

                randomWallet.takeToken(tokensToSellFromThisWallet);
                BigInteger ethReceived = dex.sellToken(tokensToSellFromThisWallet);
                randomWallet.addEth(ethReceived);
                filled = filled.add(ethReceived);

                if (filled.compareTo(sellNowInEth) >= 0) break;
            }
        }

        marketCycle.getSellOrder().addFilled(filled);
        marketCycle.getSellOrder().addRejected(rejected);
    }


    private boolean isBuyTaxAcceptable(BigDecimal tax, MarketCycle marketCycle) {
        return marketCycle.getSentiment().isBuyTaxAcceptable(tax);
    }

    private boolean isSellTaxAcceptable(BigDecimal tax, MarketCycle marketCycle) {
        return marketCycle.getSentiment().isSellTaxAcceptable(tax);
    }






    //    private List<Wallet> findWalletsWithUpToToken(BigInteger tokens) {
//        //try to find one that fills it all. but don't loop all wallets, else we'd always pick the same.
//        for (int i = 0; i < 10; i++) {
//            Wallet randomWallet = bank.getRandomWallet();
//            if (randomWallet.getToken().compareTo(tokens) >= 0) {
//                return Collections.singletonList(randomWallet);
//            }
//        }
//
//        //else we take multiple combined.
//        List<Wallet> wallets = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Wallet randomWallet = bank.getRandomWallet();
//            if (randomWallet.getToken().compareTo(tokens) >= 0) {
//                if (randomWallet.hasToken()) {
//                    wallets.add(randomWallet);
//                }
//            }
//        }
//        return wallets;
//    }
    private List<Wallet> findWalletsWithTokens() {
        List<Wallet> wallets = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Wallet randomWallet = ecosystem.getBank().getRandomWallet();
            if (randomWallet.hasToken() && !wallets.contains(randomWallet)) {
                wallets.add(randomWallet);
            }
        }
        return wallets;
    }
    private List<Wallet> findWalletsWithEth() {
        List<Wallet> wallets = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Wallet randomWallet = ecosystem.getBank().getRandomWallet();
            if (randomWallet.hasEth() && !wallets.contains(randomWallet)) {
                wallets.add(randomWallet);
            }
        }
        return wallets;
    }

}
