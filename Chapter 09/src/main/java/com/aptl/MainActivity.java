package com.aptl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void readUiPreferences() {
        SharedPreferences preferences
                = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultBackgroundColor = getResources().
                getColor(R.color.default_background);
        int backgroundColor = preferences.getInt(
                Constants.UI_BACKGROUND_COLOR,
                defaultBackgroundColor);
        View view = findViewById(R.id.background_view);
        view.setBackgroundColor(backgroundColor);
    }


    public void doToggleWifiOnlyPreference(View view) {
        SharedPreferences preferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        boolean currentValue = preferences.
                getBoolean(Constants.NETWORK_WIFI_ONLY, false);
        preferences.edit()
                .putBoolean(Constants.NETWORK_WIFI_ONLY, !currentValue)
                .apply();
    }


}
