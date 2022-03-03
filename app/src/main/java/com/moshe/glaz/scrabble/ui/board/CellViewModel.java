package com.moshe.glaz.scrabble.ui.board;

import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.app.AppBase;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;

public class CellViewModel {
    public TextView textView;
    public BoardCellData data;

    public CellViewModel(TextView tv) {
        textView = tv;
    }

    public CellViewModel() {

    }

    public void init() {
        //
        // set text color
        //
        int colorResId = 0;
        switch (data.textColor) {
            case green:
                colorResId = R.color.board_cell_text_green;
                break;
            case white:
                colorResId = R.color.board_cell_text_white;
                break;
            case black:
                colorResId = R.color.board_cell_text_black;
                break;
        }
        textView.setTextColor(UIUtils.getColor(colorResId));

        //
        // set cell background
        //
        int drawableResId = R.drawable.cell_background_empty;
        switch (data.background) {
            case empty:
                drawableResId = R.drawable.cell_background_empty;
                break;
            case card_disabled:
                drawableResId = R.drawable.cell_background_card_disabled;
                break;
            case suggestion:
                drawableResId = R.drawable.cell_background_suggestion;
                break;
            case card:
                drawableResId = R.drawable.cell_background_card;
                break;
            case blue_sky:
                drawableResId = R.drawable.cell_background_blue_light;
                break;
            case blue:
                drawableResId = R.drawable.cell_background_blue;
                break;
            case start:
                drawableResId = R.drawable.cell_background_green;
                break;
            case orange:
                drawableResId = R.drawable.cell_background_orange;
                break;
            case magenta:
                drawableResId = R.drawable.cell_background_magenta;
                break;
        }
        textView.setBackgroundResource(drawableResId);

        //
        // set text
        //
        if (data.hasText()) {
            textView.setText(Html.fromHtml(data.text));
        } else {
            textView.setText("");
        }

        //
        // image
        //
        if (data.imageResId != 0) {
            textView.setCompoundDrawables(null, AppBase.getContext().getDrawable(data.imageResId), null, null);
        }

        //
        // textBold
        //
        textView.setTypeface(data.textBold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    public void setSize(int w) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, w);
        textView.setLayoutParams(p);
        textView.setTextSize(data.textSize.getSize(w));
    }

    public boolean containesPosition(float pushX, float pushY) {
        int s = textView.getHeight();
        int l = textView.getLeft();
        int t = textView.getTop();

        LinearLayout ll= (LinearLayout) textView.getParent();
        if (ll!=null){
            t+=ll.getTop();
        }

        if (pushX <= (l+s) && pushX >= l) {
            if (pushY  <= t +s && pushY >= t) {
                 return true;
            }
        }

        return false;
    }
}
