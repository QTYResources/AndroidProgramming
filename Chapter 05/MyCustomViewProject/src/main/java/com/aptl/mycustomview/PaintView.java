package com.aptl.mycustomview;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * @author Erik Hellman
 */
public class PaintView extends View {
    public static final int MAX_FINGERS = 5;
    private Path[] mFingerPaths = new Path[MAX_FINGERS];
    private Paint mFingerPaint;
    private ArrayList<Path> mCompletedPaths;
    private RectF mPathBounds = new RectF();

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCompletedPaths = new ArrayList<Path>();
        mFingerPaint = new Paint();
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setColor(Color.BLACK);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeWidth(6);
        mFingerPaint.setStrokeCap(Paint.Cap.BUTT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Path completedPath : mCompletedPaths) {
            canvas.drawPath(completedPath, mFingerPaint);
        }

        for (Path fingerPath : mFingerPaths) {
            if (fingerPath != null) {
                canvas.drawPath(fingerPath, mFingerPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int id = event.getPointerId(actionIndex);

        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id < MAX_FINGERS) {
            mFingerPaths[id] = new Path();
            mFingerPaths[id].moveTo(event.getX(actionIndex), event.getY(actionIndex));
        } else if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) && id < MAX_FINGERS) {
            mFingerPaths[id].setLastPoint(event.getX(actionIndex), event.getY(actionIndex));
            mCompletedPaths.add(mFingerPaths[id]);
            mFingerPaths[id].computeBounds(mPathBounds, true);
            invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                    (int) mPathBounds.right, (int) mPathBounds.bottom);
            mFingerPaths[id] = null;
        }

        for(int i = 0; i < cappedPointerCount; i++) {
            if(mFingerPaths[i] != null) {
                int index = event.findPointerIndex(i);
                mFingerPaths[i].lineTo(event.getX(index), event.getY(index));
                mFingerPaths[i].computeBounds(mPathBounds, true);
                invalidate((int) mPathBounds.left, (int) mPathBounds.top,
                        (int) mPathBounds.right, (int) mPathBounds.bottom);
            }
        }

        return true;
    }


}
