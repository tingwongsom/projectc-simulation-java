package org.projectc.simulation;

import org.projectc.simulation.participants.MarketParticipant;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contains customers, and customers have wallets.
 */
public class Bank {

    private List<MarketParticipant> customers = new ArrayList<>();

    public void addCustomer(MarketParticipant customer) {
        customers.add(customer);
    }

    /**
     * Returns the first wallet of the first customer.
     */
    public Wallet getFirstWallet() {
        return customers.get(0).getFirstWallet();
    }

    /**
     * @return The sum of all ETH in all wallets combined.
     */
    public BigInteger getTotalEth() {
        BigInteger total = new BigInteger("0");
        for (MarketParticipant customer : customers) {
            for (Wallet wallet : customer.getWallets()) {
                total = total.add(wallet.getEth());
            }
        }
        return total;
    }

    /**
     * @return The sum of all tokens in all wallets combined.
     */
    public BigInteger getTotalToken() {
        BigInteger total = new BigInteger("0");
        for (MarketParticipant customer : customers) {
            for (Wallet wallet : customer.getWallets()) {
                total = total.add(wallet.getToken());
            }
        }
        return total;
    }

    /**
     * @return a randomly selected wallet from a randomly selected customer.
     */
    public Wallet getRandomWallet() {
        Random rand = new Random();
        MarketParticipant customer = customers.get(rand.nextInt(customers.size()));
        List<Wallet> wallets = customer.getWallets();
        return wallets.get(rand.nextInt(wallets.size()));
    }

    @Override
    public String toString() {
        return "Bank{" +
                "wallets=" + customers.size() +
                ", total-eth=" + new BigDecimal(getTotalEth()).divide(new BigDecimal("1000000000000000000"), 10, RoundingMode.HALF_UP) +
                ", total-token=" + new BigDecimal(getTotalToken()).divide(new BigDecimal("1000000000000000000"), 10, RoundingMode.HALF_UP) +
                '}';
    }


}
