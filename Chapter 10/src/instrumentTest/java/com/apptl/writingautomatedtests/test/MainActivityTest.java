package com.apptl.writingautomatedtests.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.view.View;
import com.apptl.writingautomatedtests.MainActivity;
import com.apptl.writingautomatedtests.R;

/**
 * @author Erik Hellman
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    private Intent mServiceIntent;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testIfButtonHasClickListener() {
        startActivity(new Intent(Intent.ACTION_MAIN), null, null);
        View testButton = getActivity().
                findViewById(R.id.background_job_btn);
        assertTrue("Button is missing onClick listener!",
                testButton.hasOnClickListeners());
    }

    public void testIfClickListenerStartsServiceCorrectly() {
        setActivityContext(new MyMockContext(getInstrumentation().
                getTargetContext()));
        startActivity(new Intent(Intent.ACTION_MAIN), null, null);
        View testButton = getActivity().
                findViewById(R.id.background_job_btn);
        TouchUtils.clickView(this, testButton);
        assertEquals("Wrong Intent action for starting service!",
                "startBackgroundJob", mServiceIntent.getAction());
    }

    public class MyMockContext extends ContextWrapper {
        public MyMockContext(Context base) {
            super(base);
        }

        @Override
        public ComponentName startService(Intent serviceIntent) {
            mServiceIntent = serviceIntent;
            return new ComponentName("com.aptl.writingautomatedtests", "NetworkService");
        }
    }
}
