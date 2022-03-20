package com.moshe.glaz.scrabble.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.moshe.glaz.scrabble.databinding.ActivitySudoku2Binding;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.enteties.Position;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;
import com.moshe.glaz.scrabble.ui.views.FontFitTextView;

import java.util.ArrayList;

public class SudokuActivity2 extends AppCompatActivity {
    private ActivitySudoku2Binding $;
    ArrayList<FontFitTextView> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        $ = ActivitySudoku2Binding.inflate(getLayoutInflater());
        setContentView($.getRoot());

        initBoardViews();
    }

    private void initBoardViews() {
        int size = 9;
        views = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            FontFitTextView view = new FontFitTextView(this);
            view.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));
            view.setId(View.generateViewId());
            view.setTag(new Position(i % size, i / size));
            view.setGravity(Gravity.CENTER);
            view.setBackgroundResource(R.drawable.sudoku_background);
            view.setFocusable(true);
            view.setSelected(false);
            view.setActivated(false);
            view.setText(i + "");
            view.setOnClickListener(v -> {
                onCellClick((Position) v.getTag());
                v.setActivated(true);
            });
            $.clBoard.addView(view, 0);
            views.add(view);
        }

        for (int i = 0; i < size * size; i++) {
            Log.i("init2", "" + i);
            FontFitTextView view = views.get(i);
            ConstraintSet set = new ConstraintSet();
            set.clone($.clBoard);

            int marginTop= (i / size ==3 || i / size==6) ? 2 : 1;
            int marginBottom=(i / size ==2 || i / size==5) ? 2 : 1;
            int marginStart=(i % size ==3 || i % size==6) ? 2 : 1;
            int marginEnd=(i % size ==2 || i % size==5) ? 2 : 1;

            if (i < size) {
                set.connect(view.getId(), ConstraintSet.TOP, $.clBoard.getId(), ConstraintSet.TOP, marginTop);
            } else {
                set.connect(view.getId(), ConstraintSet.TOP, views.get(i - size).getId(), ConstraintSet.BOTTOM, marginTop);
            }
            if (i >= (size * size) - size) {
                set.connect(view.getId(), ConstraintSet.BOTTOM, $.clBoard.getId(), ConstraintSet.BOTTOM, marginBottom);
            } else {
                set.connect(view.getId(), ConstraintSet.BOTTOM, views.get(i + size).getId(), ConstraintSet.TOP, marginBottom);
            }
            if (i % size == 0) {
                set.connect(view.getId(), ConstraintSet.START, $.clBoard.getId(), ConstraintSet.START, marginStart);
            } else {
                set.connect(view.getId(), ConstraintSet.START, views.get(i - 1).getId(), ConstraintSet.END, marginStart);
            }
            if ((i + 1) % size == 0) {
                set.connect(view.getId(), ConstraintSet.END, $.clBoard.getId(), ConstraintSet.END, marginEnd);
            } else {
                set.connect(view.getId(), ConstraintSet.END, views.get(i + 1).getId(), ConstraintSet.START, marginEnd);
            }
            set.applyTo($.clBoard);
        }
    }

    private void onCellClick(Position position) {

    }
}