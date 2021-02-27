package com.apptl.writingautomatedtests.test;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import com.apptl.writingautomatedtests.MyService;

/**
 * @author Erik Hellman
 */
public class MyServiceTest extends ServiceTestCase<MyService> {
    public MyServiceTest() {
        super(MyService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setupService();
    }

    public void testBinder() throws Exception {
        Intent serviceIntent = new Intent(getContext(), MyService.class);
        IBinder binder = bindService(serviceIntent);
        assertTrue(binder instanceof MyService.LocalBinder);
        MyService myService = ((MyService.LocalBinder) binder).getService();
        assertSame(myService, getService());
    }

    @Override
    public void tearDown() throws Exception {
        shutdownService();
        super.tearDown();
    }
}
