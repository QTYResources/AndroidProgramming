package com.aptl;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * @author Erik Hellman
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(),
                R.xml.preference_screen, false);
        addPreferencesFromResource(R.xml.preference_screen);
    }
}
