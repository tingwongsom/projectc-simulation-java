package org.projectc.simulation.dex.events;

public interface TransactionEventListener {

    /**
     * Gets informed on every buy and sell transaction on the DEX.
     */
    void event(TransactionEvent event);


    static TransactionEventListener nullImpl() {
        return new TransactionEventListener() {
            @Override
            public void event(TransactionEvent event) {
                //do nothing.
            }
        };
    }

}
