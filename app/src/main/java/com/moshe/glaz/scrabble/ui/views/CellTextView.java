package com.moshe.glaz.scrabble.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.CompoundButtonCompat;

public class CellTextView extends CompoundButton
{
    private static final float THRESHOLD = 0.5f;

    private enum Mode { Width, Height, Both, None }

    private int minTextSize = 1;
    private int maxTextSize = 1000;

    private Mode mode = Mode.None;
    private boolean inComputation;
    private int widthMeasureSpec;
    private int heightMeasureSpec;

    public CellTextView(Context context) {
        super(context);
        init();
    }

    public CellTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CellTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setAllCaps(false);
    }


    private void resizeText() {
        if (getWidth() <= 0 || getHeight() <= 0)
            return;
        if(mode == Mode.None)
            return;

        final int targetWidth = getWidth();
        final int targetHeight = getHeight();

        inComputation = true;
        float higherSize = maxTextSize;
        float lowerSize = minTextSize;
        float textSize = getTextSize();
        while(higherSize - lowerSize > THRESHOLD) {
            textSize = (higherSize + lowerSize) / 2;
            if (isTooBig(textSize, targetWidth, targetHeight)) {
                higherSize = textSize;
            } else {
                lowerSize = textSize;
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, lowerSize);
        measure(widthMeasureSpec, heightMeasureSpec);
        inComputation = false;
    }

    private boolean isTooBig(float textSize, int targetWidth, int targetHeight) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        measure(0, 0);
        if(mode == Mode.Both)
            return getMeasuredWidth() >= targetWidth || getMeasuredHeight() >= targetHeight;
        if(mode == Mode.Width)
            return getMeasuredWidth() >= targetWidth;
        else
            return getMeasuredHeight() >= targetHeight;
    }

    private Mode getMode(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY)
            return Mode.Both;
        if(widthMode == MeasureSpec.EXACTLY)
            return Mode.Width;
        if(heightMode == MeasureSpec.EXACTLY)
            return Mode.Height;
        return Mode.None;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!inComputation) {
            this.widthMeasureSpec = widthMeasureSpec;
            this.heightMeasureSpec = heightMeasureSpec;
            mode = getMode(widthMeasureSpec, heightMeasureSpec);
            resizeText();
        }
    }

    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        resizeText();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh)
            resizeText();
    }

    public int getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
        resizeText();
    }

    public int getMaxTextSize() {
        return maxTextSize;
    }

    public void setMaxTextSize(int maxTextSize) {
        this.maxTextSize = maxTextSize;
        resizeText();
    }
}
