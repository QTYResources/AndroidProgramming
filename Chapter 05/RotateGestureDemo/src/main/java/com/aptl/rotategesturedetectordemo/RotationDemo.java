package com.aptl.rotategesturedetectordemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

public class RotationDemo extends Activity implements View.OnTouchListener {

    private static final String TAG = "RotationDemo";
    private RotationDemoView mRotationView;
    private MotionEvent.PointerCoords mPrevOne;
    private MotionEvent.PointerCoords mPrevTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRotationView = (RotationDemoView) findViewById(R.id.rotation_demo);
        mRotationView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getPointerCount() >= 2) {
            MotionEvent.PointerCoords pointOne = new MotionEvent.PointerCoords();
            MotionEvent.PointerCoords pointTwo = new MotionEvent.PointerCoords();
            motionEvent.getPointerCoords(0, pointOne);
            motionEvent.getPointerCoords(1, pointTwo);

            pointOne.x *= -1;
            pointTwo.x *= -1;

            if(mPrevOne == null && mPrevTwo == null) {
                mPrevOne = pointOne;
                mPrevTwo = pointTwo;
            } else {
                float[] vecOne = new float[] {mPrevTwo.x - mPrevOne.x, mPrevTwo.y - mPrevOne.y};
                float[] vecTwo = new float[] {pointTwo.x -pointOne.x, pointTwo.y - pointOne.y};
                Math.atan2()
                Log.d(TAG, "Angle: " + Math.toDegrees(angle));
                mPrevOne = pointOne;
                mPrevTwo = pointTwo;
            }
        } else {
            mPrevOne = null;
            mPrevTwo = null;
        }
        return true;
    }
}
