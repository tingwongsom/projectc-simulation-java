package org.projectc.simulation.participants;

import org.projectc.simulation.Wallet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MarketParticipantImpl implements MarketParticipant {

    private final Wallet wallet;
    private final ParticipantType participantType;

    public MarketParticipantImpl(Wallet wallet, ParticipantType participantType) {
        this.wallet = wallet;
        this.participantType = participantType;
    }


    @Override
    public Wallet getFirstWallet() {
        return wallet;
    }

    @Override
    public List<Wallet> getWallets() {
        List<Wallet> list = new ArrayList<>();
        list.add(wallet);
        return list;
    }


    @Override
    public ParticipantType getParticipantType() {
        return participantType;
    }

    @Override
    public BigInteger interestedToSellNowForAmountToken() {
        return null;
    }

    @Override
    public BigInteger interestedToBuyNowForAmountEth() {
        return null;
    }

    @Override
    public String toString() {
        return "MarketParticipantImpl{" +
                "wallet=" + wallet +
                ", participantType=" + participantType +
                '}';
    }
}
