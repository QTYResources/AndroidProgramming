package com.aptl.sampleapi;

import com.aptl.sampleapi.CustomData;

/**
 * @author Erik Hellman
 */
interface ApiInterfaceV2 {
    /**
     * Stores the CustomData object and return true on success.
     */
    boolean storeData(in CustomData data);
}
