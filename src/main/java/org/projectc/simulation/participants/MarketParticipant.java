package org.projectc.simulation.participants;

import org.projectc.simulation.Wallet;

import java.math.BigInteger;
import java.util.List;

/**
 * A market participant has 1-n wallets, and has a certain {@link ParticipantType personality}.
 */
public interface MarketParticipant {

    Wallet getFirstWallet();
    List<Wallet> getWallets();

    ParticipantType getParticipantType();

    BigInteger interestedToSellNowForAmountToken();

    BigInteger interestedToBuyNowForAmountEth();

}
