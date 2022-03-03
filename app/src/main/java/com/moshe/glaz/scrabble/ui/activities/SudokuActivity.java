package com.moshe.glaz.scrabble.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.databinding.ActivitySudokuBinding;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.enteties.sudoku.Game;
import com.moshe.glaz.scrabble.enteties.sudoku.Player;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.LogicManager;
import com.moshe.glaz.scrabble.managers.SudokuManager;
import com.moshe.glaz.scrabble.infrastructure.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SudokuActivity extends AppCompatActivity {
ActivitySudokuBinding binding;
    User user1;
    User user2;
    Game game;
    TextView[][] views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySudokuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UIUtils.setStatusBarColor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game = SudokuManager.getInstance().getActiveGame();
        if (game == null) {
            finish();
            return;
        }

        if(views==null){
            createTextViewBoardLayout();
        }
        initDashboardUi();
    }

    private void initUsersUi() {
        user1 = DataSourceManager.getInstance().getUser(game.user1.uid);
        user2 = DataSourceManager.getInstance().getUser(game.user2.uid);

        binding.tvData1.setText(user1.nickName);
        binding.ivIcon1.setImageResource(DataSourceManager.getInstance().getAvatarResId(user1.avatar));
        binding.tvDescription1.setText(user1.status);

        binding.tvData2.setText(user2.nickName);
        binding.ivIcon2.setImageResource(DataSourceManager.getInstance().getAvatarResId(user2.avatar));
        binding.tvDescription2.setText(user2.status);

        binding.tvScore1.setText(Html.fromHtml(getScoreHtmlText(game.user1), HtmlCompat.FROM_HTML_MODE_LEGACY));
        binding.tvScore2.setText(Html.fromHtml(getScoreHtmlText(game.user2), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private String getScoreHtmlText(Player user) {
        StringBuilder builder = new StringBuilder();

        builder.append("ניקוד:");
        builder.append(user.getScore());
        builder.append(" ");
        int score = user.getActiveActionScore();
        if (score > 0) {
            StringBuilder builder1 = new StringBuilder();
            builder1.append("(+");
            builder1.append(score);
            builder1.append(")");
            builder.append(TextUtils.getHTMLText_blue(builder1.toString()));
        }

        return builder.toString();
    }

    private void initDashboardUi() {
        initUsersUi();
    }

    public void createTextViewBoardLayout() {
        views = new TextView[9][9];

        for (int y = 0; y < 9; y++) {
            LinearLayout layout = getNewLayout();
            for (int x = 0; x < 9; x++) {
                ViewPosition o =new ViewPosition(x,y);
                TextView tv = getNewTextView(o);
                views[x][y]=tv;
                layout.addView(tv);

                if(x == 2 || x == 5){
                    layout.addView(getEmptyViewCell());
                }
            }

            binding.llBoard.addView(layout);

            if(y == 2 || y == 5){
                binding.llBoard.addView(getEmptyViewLine());
            }
        }

        binding.llBoard.post(() -> {
            while (binding.llBoard.getWidth()==0){
                Utils.sleep(200);
            }
            int width=binding.llBoard.getWidth();
            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width,1.0f);
            binding.llBoard.setLayoutParams(viewParam);
        });
    }

    class ViewPosition {
        int x, y;

        ViewPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private TextView getNewTextView(ViewPosition position) {
        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
        TextView tv = new TextView(getApplicationContext());
        tv.setTag(position);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.sudoku_background);
        tv.setLayoutParams(viewParam);
        tv.setFocusable(true);
        tv.setOnClickListener(v->{
            onCellClick((ViewPosition)v.getTag());
        });
        return tv;
    }

    private void onCellClick(ViewPosition viewPosition) {

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                boolean isSameRect=getRectNumber(x,y) == getRectNumber(viewPosition.x,viewPosition.y);
                views[x][y].setSelected(viewPosition.x==x || viewPosition.y==y || isSameRect);
                views[x][y].setActivated(viewPosition.x==x && viewPosition.y==y);
            }
        }
    }

    private int getRectNumber(int x, int y) {
        int xx = x / 3;
        xx++;
        xx*=100;
        int yy=y/3;
        yy++;
        return  xx+yy;
    }

    private LinearLayout getNewLayout() {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
        layout.setLayoutParams(p);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private View getEmptyViewCell() {
        View view = new View(getApplicationContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(4, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(p);
        view.setBackgroundColor(UIUtils.getColor(R.color.gray_dark));
        return view;
    }

    private View getEmptyViewLine() {
        View view = new View(getApplicationContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4);
        view.setLayoutParams(p);
        view.setBackgroundColor(UIUtils.getColor(R.color.gray_dark));
        return view;
    }
}