package com.aptl.sampleapi;

import com.aptl.sampleapi.CustomData;
import com.aptl.sampleapi.AidlCallback;

interface ApiInterfaceV1 {
    /**
     * Simple remote method that take a long and return true if it is a prime number.
     */
    boolean isPrime(long value);

    /**
     * Retrieve all CustomData objects since timestamp.
     * Will get at most result.length objects.
     */
    void getAllDataSince(long timestamp, out CustomData[] result);

    /**
     * Stores the CustomData object.
     */
    void storeData(in CustomData data);

    void setCallback(in AidlCallback callback);
}
