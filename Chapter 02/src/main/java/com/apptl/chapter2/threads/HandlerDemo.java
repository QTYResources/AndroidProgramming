package com.apptl.chapter2.threads;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;
import com.apptl.chapter2.R;

public class HandlerDemo extends Activity implements Handler.Callback {
    private Handler mHandler;
    private Handler mUiHandler;

    public static final int BACKGROUND_OPERATION = 10;
    public static final int MAIN_THREAD_OPERATION = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Start a new thread with a Looper
        HandlerThread handlerThread = new HandlerThread("BackgroundThread");
        handlerThread.start();
        // Create your new Handler
        mHandler = new Handler(handlerThread.getLooper(), this);

        // This Handler will execute on the main thread
        mUiHandler = new Handler(getMainLooper(), this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down the Looper thread
        mHandler.getLooper().quit();
    }

    @Override
    public boolean handleMessage(Message message) {
        // Process incoming messages here...

        switch (message.what) {
            case BACKGROUND_OPERATION: // Perform background operation
                performBackgroundOoperation(message.obj);
                break;
            case MAIN_THREAD_OPERATION: // Update a UI element
                ((TextView) findViewById(R.id.status_text)).setText((String) message.obj);
                findViewById(R.id.status_text).invalidate();
                break;
        }

        // Recycle your message object
        message.recycle();
        return true;
    }

    private void performBackgroundOoperation(Object obj) {
        // TODO: Some background operation...
    }
}
