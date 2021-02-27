package com.aptl.sampleapi;

import com.aptl.sampleapi.CustomData;

oneway interface AidlCallback {
    void onDataUpdated(in CustomData[] data);
}
