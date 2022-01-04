package org.projectc.simulation.dex;

import org.projectc.simulation.LiquidityPool;
import org.projectc.simulation.dex.events.TransactionEventImpl;
import org.projectc.simulation.dex.events.TransactionEventListener;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Simple in-memory impl for the simulation.
 *
 */
public class JavaDexImpl implements JavaDex {

    protected final LiquidityPool lp;
    protected final TransactionEventListener eventListener;

    public JavaDexImpl(LiquidityPool lp, TransactionEventListener eventListener) {
        this.lp = lp;
        this.eventListener = eventListener;
    }

    @Override
    public LiquidityPool getLp() {
        return lp;
    }


    @Override
    public BigDecimal askTaxPercentForBuyToken(BigInteger eth) {
        return new BigDecimal("0");
    }

    @Override
    public BigDecimal askTaxPercentForSellToken(BigInteger token) {
        return new BigDecimal("0");
    }

    /**
     * Buys tokens for the given amount of eth.
     * @return token
     */
    @Override
    public BigInteger buyToken(BigInteger eth) {
        BigDecimal tokenToGet = lp.howManyTokenForMyEth(eth);
        BigInteger tokenToHandOut = tokenToGet.toBigInteger();

        BigInteger newTokenInLp = lp.getToken().subtract(tokenToHandOut);
        BigInteger newEthInLp = lp.getEth().add(eth);

        lp.setToken(newTokenInLp);
        lp.setEth(newEthInLp);

        eventListener.event(new TransactionEventImpl(true, eth, tokenToHandOut));

        return tokenToHandOut;
    }

    /**
     * Sells the given amount of tokens, receives eth.
     * @return eth
     */
    @Override
    public BigInteger sellToken(BigInteger token) {
        BigDecimal ethToGet = lp.howManyEthForMyToken(token);
        BigInteger ethToHandOut = ethToGet.toBigInteger();

        BigInteger newEthInLp = lp.getEth().subtract(ethToHandOut);
        BigInteger newTokenInLp = lp.getToken().add(token);

        lp.setToken(newTokenInLp);
        lp.setEth(newEthInLp);

        eventListener.event(new TransactionEventImpl(false, ethToHandOut, token));

        return ethToHandOut;
    }

}
