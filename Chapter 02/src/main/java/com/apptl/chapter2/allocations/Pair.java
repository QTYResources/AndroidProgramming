package com.apptl.chapter2.allocations;

/**
 * @author Erik Hellman
 */
public final class Pair {
    public int firstValue;
    public int secondValue;

    // Reference to next object in the pool
    private Pair next;

    // The lock used for synchronization
    private static final Object sPoolSync = new Object();

    // The first available object in the pool
    private static Pair sPool;

    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;

    /**
     * Only allow new objects from obtain()
     */
    private Pair() { }

    /**
     * Return recycled object or new if pool is empty
     */
    public static Pair obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Pair m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                return m;
            }
        }
        return new Pair();
    }

    /**
     * Recycle this object. You must release all references to
     * this instance after calling this method.
     */
    public void recycle() {
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
}
