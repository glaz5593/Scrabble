package com.moshe.glaz.scrabble.ui.views;


import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public  class ZoomableLinearLayout extends LinearLayout {
    private SparseArray<PointF> mActivePointers;
    ZoomListener listener;
boolean isZooming=false;
    public void setOnZoomListener(ZoomListener listener) {
        this.listener = listener;
    }

    public interface ZoomListener {
        void onStartZoom();

        void onEndZoom();

        void onZooming(int level);
    }

    public boolean isZooming() {
        return this.isZooming;
    }

    public ZoomableLinearLayout(Context context) {
        super(context);
        initView();
    }

    public ZoomableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mActivePointers = new SparseArray<PointF>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener == null) {
            return false;
        }

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);

                if (mActivePointers.size()==2) {
                    startZoom();
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }

                zooming();
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointers.remove(pointerId);
                if (this.isZooming && mActivePointers.size()==0) {
                    endZoom();
                }
                break;
            }
        }

        getDistance();
        return true;
    }

    private void startZoom() {
        this.isZooming=true;
        distance = getDistance();
        listener.onStartZoom();
    }
    private void endZoom() {
        this.isZooming=false;
        distance = getDistance();
        listener.onEndZoom();
    }

    private void zooming(){
        double d = getDistance();
        listener.onZooming((int)((distance-d)*-0.1f));
        distance=d;
    }

    double distance=0;
    private double getDistance() {
        if (mActivePointers.size() != 2) {
            return 0;
        }

        PointF point1 = mActivePointers.valueAt(0);
        PointF point2 = mActivePointers.valueAt(1);
        double distance = Math.sqrt((point2.y - point1.y) * (point2.y - point1.y) + (point2.x - point1.x) * (point2.x - point1.x));
        Log.i("MultitouchView", "distance:" + distance);

        return distance;
    }
}
