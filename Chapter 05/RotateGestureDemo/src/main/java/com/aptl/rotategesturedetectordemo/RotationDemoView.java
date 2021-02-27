package com.aptl.rotategesturedetectordemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
* Created by erikhellman on 6/30/13.
*/
public class RotationDemoView extends View {

    public RotationDemoView(Context context) {
        super(context);
    }

    public RotationDemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationDemoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
