package com.apptl.writingautomatedtests;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
    public static final String ACTION_START_BACKGROUND_JOB
            = "startBackgroundJob";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startBackgroundJob(View view) {
        Intent backgroundJob = new Intent(ACTION_START_BACKGROUND_JOB);
        startService(backgroundJob);
    }
}
