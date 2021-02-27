package com.apptl.chapter2.threads;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import com.apptl.chapter2.R;

/**
 * @author Erik Hellman
 */
public class MyAsyncTask extends AsyncTask<String, Integer, Integer> {
    private Activity mActivity;

    public MyAsyncTask(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // This will run on the main thread
        mActivity.findViewById(R.id.progressBar).
                setVisibility(View.VISIBLE);
        ((ProgressBar) mActivity.findViewById(R.id.progressBar)).
                setProgress(0);
    }

    @Override
    protected Integer doInBackground(String... inputs) {
        // This will run NOT run on the main thread
        int progress = 0;

        for (String input : inputs) {
            // Post the input to a server (fake it with a sleep)
            SystemClock.sleep(50);
            publishProgress(++progress, inputs.length);
        }
        return progress;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // This will run on the main thread
        ((ProgressBar) mActivity.findViewById(R.id.progressBar)).
                setMax(values[1]);
        ((ProgressBar) mActivity.findViewById(R.id.progressBar)).
                setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        // This will run on the main thread
        mActivity.findViewById(R.id.progressBar).
                setVisibility(View.INVISIBLE);
    }
}
