package org.projectc.simulation.participants;

/**
 * The personality of a {@link MarketParticipant}.
 *
 * TODO these types are not well defined, and not used yet.
 */
public enum ParticipantType {

    /**
     * Does not like to sell. Neither to take profits, nor to stop the loss.
     */
    DIE_HARD_HODLER,

    /**
     * Sells a part to cover the investment at some point, and then keeps taking profits on the
     * way up, if it goes up, else hodls.
     *
     */
    TAKE_OUT_INITIAL_THEN_PROFITS_ON_WAY_UP,

    /**
     * Sells a part to cover the investment at some point, then keeps the rest as moonbag and never sells.
     */
    TAKE_OUT_INITIAL_THEN_HOLD_MOONBAG,

    /**
     * Buys into asset, then sells all once he has doubled his investment.
     * Does not sell otherwise (holds to zero or to double).
     */
    BUY_THEN_SELL_ONCE_DOUBLED,

    /**
     * Likes to buy when it looks cheap, and takes profits.
     */
    BUY_LOW_SELL_HIGH,

    /**
     * Likes to dollar-cost-average into a position.
     */
    DCA_BUYER,

    /**
     * Sells all if he loses at least 50% of the investment, else hodls forever.
     */
    STOPLOSS_INVESTOR,

    ;

}
