package org.projectc.simulation.randomactivity;

public class ExecutionResult {
    private final MarketCycle marketCycle;

    public ExecutionResult(MarketCycle marketCycle) {
        this.marketCycle = marketCycle;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "marketCycle=" + marketCycle +
                '}';
    }

}
