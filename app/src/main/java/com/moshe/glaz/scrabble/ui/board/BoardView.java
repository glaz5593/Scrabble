package com.moshe.glaz.scrabble.ui.board;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moshe.glaz.scrabble.ui.views.ScrollableLinearLayout;
import com.moshe.glaz.scrabble.ui.views.ZoomableLinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BoardView extends ScrollableLinearLayout implements ZoomableLinearLayout.ZoomListener {
    HashMap<Integer, CellViewModel> views;
    Context context;
    int minCellWidth = 46;
    int maxCellWidth = 120;

    int zoom = 0;

    public BoardView(Context context) {
        super(context);
        init(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        setOnZoomListener(this);
    }

    View pushView;
    public void setBoard(int width, int height, ArrayList<BoardCellData> dataSource) {
        contentLayout.removeAllViews();
        views = new HashMap<>();
        int screenPX = getScreenWidthInPX(context);
        int wPX = this.getMeasuredWidth();
        minCellWidth = wPX / width;
        maxCellWidth = minCellWidth * 3;
        Log.i("testW","screenPX:"+screenPX+" wPX:"+wPX+" minCellWidth:"+minCellWidth);

        int counter = 0;
        for (int h = 0; h < height; h++) {
            LinearLayout layout = new LinearLayout(context);
            LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(p);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for (int w = 0; w < width; w++) {
                TextView tv = new TextView(context);
                BoardCellData data = dataSource.get(counter);
                CellViewModel vm = new CellViewModel(tv);
                tv.setGravity(Gravity.CENTER);
                tv.setTag(data.id);

                counter++;

                layout.addView(tv);

                vm.textView = tv;
                vm.data = data;

                views.put(data.id, vm);
                vm.init();
            }

            contentLayout.addView(layout);
        }

        setZoom(0);

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int screenPX = getScreenWidthInPX(context);
                int wPX = getWidth();
                minCellWidth = wPX / width;
                maxCellWidth = minCellWidth * 3;
                Log.i("testW","screenPX:"+screenPX+" wPX:"+wPX+" minCellWidth:"+minCellWidth);

                setZoom(minCellWidth-100);
                removeOnLayoutChangeListener(this);
            }
        });
    }

    private void setZoom(int newZoomLevel) {
        int w =100;
        w+=newZoomLevel;
        if(w<minCellWidth){
            return;
        }
        if(w>maxCellWidth){
            return;
        }
        zoom=newZoomLevel;

        for(CellViewModel vm : views.values()){
             vm.setSize(w);
        }
    }

    @Override
    public void onStartZoom() {

    }

    @Override
    public void onEndZoom() {

    }

    @Override
    public void onZooming(int level) {
        int newZoom = zoom;
        newZoom += level;
        setZoom(newZoom);
    }

float pushX,pushY;
    long lastPushTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pushX = event.getX();
                pushY = event.getY();
                lastPushTime = new Date().getTime();
                break;
            case MotionEvent.ACTION_UP:
                long l = new Date().getTime();
                if (lastPushTime > 0 && l - lastPushTime < 300) {
                    getPushedView(pushX,pushY);
                    lastPushTime=0;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    CellViewModel  selectedView;
    private void getPushedView(float pushX, float pushY) {
        Log.i("test22","getPushedView");
        for(CellViewModel c : views.values()){
            if(c.containesPosition(pushX,pushY)){
                if(selectedView!=null){
                    selectedView.textView.setSelected(false);
                }
                c.textView.setSelected(true);
                selectedView=c;
            }
        }
    }

}
