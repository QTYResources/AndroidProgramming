package com.apptl.chapter2.allocations;

/**
 * @author Erik Hellman
 */
public class AllocationDemo {
    public void bestObjectAllocationExample(int[] pairs) {
        if(pairs.length % 2 != 0) throw new IllegalArgumentException ("Bad array size!");

        for (int i = 0; i < pairs.length; i+=2) {
            Pair pair = Pair.obtain();
            pair.firstValue = pairs[i];
            pair.secondValue = pairs[i+1];
            sendPair(pair);
            pair.recycle();
        }
    }

    private void sendPair(Pair pair) {
        // TODO: Send Pair object over network...
    }
}
