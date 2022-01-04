package org.projectc.simulation.dex;

import org.projectc.simulation.LiquidityPool;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A simple DEX using a {@link LiquidityPool} to buy and sell tokens.
 */
public interface JavaDex {
    LiquidityPool getLp();

//    BigInteger computeBuyTokenAmountForEth(BigInteger eth);

    /**
     * this is without the standard 0.3% dex tax.
     */
    BigDecimal askTaxPercentForBuyToken(BigInteger eth);

    /**
     * this is without the standard 0.3% dex tax.
     */
    BigDecimal askTaxPercentForSellToken(BigInteger token);

    BigInteger buyToken(BigInteger eth);

    BigInteger sellToken(BigInteger token);
}
