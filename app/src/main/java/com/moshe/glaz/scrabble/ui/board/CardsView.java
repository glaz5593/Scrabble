package com.moshe.glaz.scrabble.ui.board;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.ui.views.DragLinearLayout;

public class CardsView extends LinearLayout {
    CellViewModel[] textViews;
    Context context;

    public CardsView(Context context) {
        super(context);
        this.context=context;
        init(context);
    }
    public CardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(context);
    }

    private void init(Context context) {
        View layout = inflate(context, R.layout.cards_view_layout, null);
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(layout);

        textViews=new CellViewModel[]{
                new CellViewModel(findViewById(R.id.tv_1)),
                new CellViewModel(findViewById(R.id.tv_2)),
                new CellViewModel(findViewById(R.id.tv_3)),
                new CellViewModel(findViewById(R.id.tv_4)),
                new CellViewModel(findViewById(R.id.tv_5)),
                new CellViewModel(findViewById(R.id.tv_6)),
                new CellViewModel(findViewById(R.id.tv_7))
        };

    }

    private boolean enabled;
    public void setEnabled(boolean enabled){
        this.enabled=enabled;
        initUi();
    }

    private void initUi() {
        char c = 'א';

        for(CellViewModel vm:textViews) {
            BoardCellData data = new BoardCellData();
            data.text = "" + c++;
            data.textColor =  !enabled ? E_TextColor.white : E_TextColor.green;
            data.background = !enabled ? E_cellBackground.card_disabled : E_cellBackground.card;
            vm.data = data;
            vm.init();
        }

        if(enabled){
            DragLinearLayout dragLinearLayout = (DragLinearLayout) findViewById(R.id.container);
            for(int i = 0; i < dragLinearLayout.getChildCount(); i++){
                View child = dragLinearLayout.getChildAt(i);
                dragLinearLayout.setViewDraggable(child, child);
            }
        }
    }
}
