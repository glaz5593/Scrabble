package com.moshe.glaz.scrabble.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.ui.views.HScroll;
import com.moshe.glaz.scrabble.ui.views.VScroll;
import com.moshe.glaz.scrabble.ui.views.ZoomableLinearLayout;

public class ScrollableLinearLayout extends ZoomableLinearLayout {
    VScroll vScroll;
    HScroll hScroll;
    private float mx, my;
    private float curX, curY;

   protected LinearLayout contentLayout;

    public ScrollableLinearLayout(Context context) {
        super(context);
        init(context);
    }
    public ScrollableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View layout = inflate(context, R.layout.scrollable_linear_layout_view, null);
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(layout);

        vScroll =  findViewById(R.id.vScroll);
        hScroll =  findViewById(R.id.hScroll);
        contentLayout = findViewById(R.id.ll_content);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isZooming = isZooming();
        boolean zoomingRes=super.onTouchEvent(event);
        if (isZooming && zoomingRes) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                mx = curX;
                my = curY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                curX = event.getX();
                curY = event.getY();
                vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                break;
        }

        return true;
    }

    protected static int getScreenWidthInPX(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
