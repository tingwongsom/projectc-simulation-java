package org.projectc.simulation.randomactivity;

import org.projectc.simulation.LiquidityPool;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A randomly created {@link MarketCycle}, with a market {@link Sentiment},
 * and an amount of tokens to buy and to sell if possible.
 *
 * For example in a FOMO sentiment, more market participants want to buy than to sell.
 *
 * The buy/sell volume is in relation to the LP. The more money in ETH is in already,
 * the higher the volumes.
 */
public class MarketCycle {

    /**
     * Returns a randomly created {@link MarketCycle}.
     */
    public static MarketCycle random(LiquidityPool liquidityPool) {
        Sentiment sentiment = randomSentiment();

        BigInteger ethInPool = liquidityPool.getEth();

        FillOrder buyOrder = sentiment.computeBuyVolume(ethInPool);
        FillOrder sellOrder = sentiment.computeSellVolume(ethInPool);

        return new MarketCycle(sentiment, buyOrder, sellOrder);
    }



    private static Sentiment randomSentiment() {
        List<Sentiment> randomList = new ArrayList<>();

        //the chance of being selected is equal per entry. if oyu want to give one type
        //a higher chance, use it multiple times.
//        randomList.add(Sentiment.FOMO);
//        randomList.add(Sentiment.BULL);
//        randomList.add(Sentiment.BULL);
//        randomList.add(Sentiment.AVERAGE);
//        randomList.add(Sentiment.AVERAGE);
//        randomList.add(Sentiment.BEAR);
//        randomList.add(Sentiment.BEAR);
//        randomList.add(Sentiment.BEAR);
//        randomList.add(Sentiment.CRASH);
//        randomList.add(Sentiment.CRASH);
//        randomList.add(Sentiment.CRASH);

        randomList.add(Sentiment.FOMO);
        randomList.add(Sentiment.FOMO);
        randomList.add(Sentiment.BULL);
        randomList.add(Sentiment.BULL);
        randomList.add(Sentiment.BULL);
        randomList.add(Sentiment.BULL);
        randomList.add(Sentiment.BULL);
        randomList.add(Sentiment.AVERAGE);
        randomList.add(Sentiment.AVERAGE);
        randomList.add(Sentiment.AVERAGE);
        randomList.add(Sentiment.AVERAGE);
        randomList.add(Sentiment.BEAR);
        randomList.add(Sentiment.BEAR);
        randomList.add(Sentiment.CRASH);

        Random rand = new Random();
        return randomList.get(rand.nextInt(randomList.size()));
    }


    private final Sentiment sentiment;
    private final FillOrder buyOrder;
    private final FillOrder sellOrder;

    public MarketCycle(Sentiment sentiment, FillOrder buyOrder, FillOrder sellOrder) {
        this.sentiment = sentiment;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
    }

    public boolean hasOutstanding() {
        return buyOrder.hasOutstanding() || sellOrder.hasOutstanding();
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public FillOrder getBuyOrder() {
        return buyOrder;
    }

    public FillOrder getSellOrder() {
        return sellOrder;
    }

    @Override
    public String toString() {
        return "MarketCycle{" +
                "sentiment=" + sentiment +
                ", buyOrder=" + buyOrder +
                ", sellOrder=" + sellOrder +
                '}';
    }

}
