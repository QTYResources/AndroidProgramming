package com.aptl.chapter8;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * @author Erik Hellman
 */
public class RetainDataOnRestartDemo extends Activity {
    private SharedPreferences mPrefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void prefsDemo() {
    }


}