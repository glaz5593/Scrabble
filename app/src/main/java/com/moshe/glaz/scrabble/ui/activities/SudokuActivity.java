package com.moshe.glaz.scrabble.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.databinding.ActivitySudokuBinding;
import com.moshe.glaz.scrabble.enteties.Position;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.enteties.sudoku.DataSource;
import com.moshe.glaz.scrabble.enteties.sudoku.Game;
import com.moshe.glaz.scrabble.enteties.sudoku.Player;
import com.moshe.glaz.scrabble.enteties.sudoku.SelectedCell;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.LogicManager;
import com.moshe.glaz.scrabble.managers.SudokuManager;
import com.moshe.glaz.scrabble.infrastructure.*;
import com.moshe.glaz.scrabble.ui.views.FontFitTextView;

import java.util.ArrayList;
import java.util.Date;

public class SudokuActivity extends AppCompatActivity {
ActivitySudokuBinding binding;
    User user1;
    User user2;
    Game game;
    Player myPlayer;
    Player otherPlayer;
    int myPlayerColor;
    int otherPlayerColor;
    int baseColor;
boolean isMyPlayerUser1;

    FontFitTextView[][] views;
    TextView[] buttons;

    DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySudokuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UIUtils.setStatusBarColor(this);
        buttons = new TextView[]{
                binding.btn1,
                binding.btn2,
                binding.btn3,
                binding.btn4,
                binding.btn5,
                binding.btn6,
                binding.btn7,
                binding.btn8,
                binding.btn9
        };
        for(TextView tv:buttons){
            tv.setTag(Utils.getInt(tv.getText().toString()));
            tv.setOnClickListener(v->onNumberButtonClick(v));
        }
        baseColor=UIUtils.getColor(R.color.gray_dark);
    }

    @Override
    protected void onResume() {
        super.onResume();
        game = SudokuManager.getInstance().getActiveGame();
        if (game == null) {
            finish();
            return;
        }
        dataSource=DataSourceManager.getInstance().getSudokuDataSource(game.dataSourceId);

        if(LogicManager.getInstance().getUser().uid.equals(game.user1.uid)){
            otherPlayer=game.user2;
            myPlayer =  game.user1;
            isMyPlayerUser1=true;
        }else{
            otherPlayer=game.user1;
            myPlayer = game.user2;
            isMyPlayerUser1=false;
        }

        if(views==null){
            createTextViewBoardLayout();
        }
        initUsersUi();
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

        if(isMyPlayerUser1){
            myPlayerColor = getPlayerColor(user1.avatar);
            otherPlayerColor = getPlayerColor(user2.avatar);
        }else{
            myPlayerColor = getPlayerColor(user2.avatar);
            otherPlayerColor = getPlayerColor(user1.avatar);
        }
    }

    private int getPlayerColor(int avatar) {
        Drawable drawable = getDrawable(DataSourceManager.getInstance().getAvatarResId(avatar));

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        int x = bitmap.getWidth() / 20;
        int y = bitmap.getHeight() / 2;
        int pixel = bitmap.getPixel(x, y);
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);

        while (redValue + blueValue + greenValue > 250) {
            redValue = (int) (redValue * 0.9f);
            blueValue = (int) (blueValue * 0.9f);
            greenValue = (int) (greenValue * 0.9f);
        }

        return Color.rgb(redValue, blueValue, greenValue);
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

    public void createTextViewBoardLayout() {
        views = new FontFitTextView[9][9];

        for (int y = 0; y < 9; y++) {
            LinearLayout layout = getNewLayout();
            for (int x = 0; x < 9; x++) {
                Position o = new Position(x, y);
                FontFitTextView tv = getNewTextView(o);

                if (dataSource.baseValues.get(o) > 0) {
                    tv.setText(dataSource.baseValues.get(o) +"");
                    tv.setTextColor(UIUtils.getColor(R.color.sudoku_text_color_base_value));
                }

                layout.addView(tv);
                views[x][y] = tv;

                if (x == 2 || x == 5) {
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

    private String getSuggestionHtmlText(ArrayList<Integer> values) {
        if (values.size()==0){
            return ("");
        }

        //if (values.size()==1){
        //    return TextUtils.getHTMLText_green(values.get(0)+"");
        //}

        StringBuilder builder=new StringBuilder();
        for(int i=1;i<10;i++){
            if(values.contains(i)){
                builder.append(TextUtils.getHTMLText_green(i+""));
            }else{
                builder.append(TextUtils.getHTMLText_white(i+""));
            }

            if(i==3||i==6){
                builder.append(TextUtils.getHTMLEnter());
             }else{
                if(i!=9) {
                    builder.append("  ");
                }
            }
        }

        return  builder.toString();
    }

    void initViews(){
        Log.i("initViews","start");
         int rectNumSelected=0;

        // בודק אם השחקן שלי תפס משבצת
        if(myPlayer.selectedCell != null){
            // שומר את המיקום של המשבצת
            rectNumSelected=getRectNumber(myPlayer.selectedCell.position.x, myPlayer.selectedCell.position.y);
        }

        //עובר בלולאה על כל הפקדים כדי להגדיר את הנתונים שלהם
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                FontFitTextView tv = views[x][y];
                boolean isSelected = myPlayer.selectedCell != null && myPlayer.selectedCell.position.equals(x, y);

                //
                // setBackground
                //
                // בודק אם מדובר במשבצת ריקה שנבחרה על ידי השחקן שלי
                if (game.getBoardValue(x, y) == 0 && isSelected) {
                    tv.setBackgroundResource(R.drawable.sudoku_background_focus);
                    // בודק אם מדובר במשבצת שנבחרה על ידי השחקן השני
                } else if (otherPlayer.selectedCell != null && otherPlayer.selectedCell.position.equals(x, y)) {
                    tv.setBackgroundResource(R.drawable.sudoku_background_other_player);
                } else {
                    tv.setBackgroundResource(R.drawable.sudoku_background);
                }

                //
                // setText
                //
                ArrayList<Integer> values = myPlayer.suggestionBoard.asValues(x, y);


                if (myPlayer.board.get(x, y) > 0) {
                    tv.setText(myPlayer.board.get(x, y) + "");
                    tv.setTextColor(myPlayerColor);
                } else if (otherPlayer.board.get(x, y) > 0) {
                    tv.setText(otherPlayer.board.get(x, y) + "");
                    tv.setTextColor(otherPlayerColor);
                } else if (dataSource.baseValues.get(x, y) > 0) {
                    tv.setText(dataSource.baseValues.get(x, y) + "");
                    tv.setTextColor(baseColor);
                } else {
                      tv.setText(Html.fromHtml(getSuggestionHtmlText(values),HtmlCompat.FROM_HTML_MODE_LEGACY));
                 }

                //
                // set state
                //
                if (myPlayer.selectedCell != null) {
                    int rectNum = getRectNumber(x, y);
                    boolean isSameRect = rectNum == rectNumSelected;
                    boolean isSameRow=myPlayer.selectedCell.position.y==y;
                    boolean isSameLine=myPlayer.selectedCell.position.x==x;
                    tv.setSelected(isSameRow || isSameLine || isSameRect);
                    int value = game.getBoardValue(x, y);
                    if(game.getBoardValue(x, y)>0){
                        values.add(game.getBoardValue(x, y));
                    }
                    tv.setActivated(isSelected || values.contains(value));
                }else{
                    tv.setSelected(false);
                    tv.setActivated(false);
                }
            }
        }

        Log.i("initViews","finish");
    }
    void initViewsOld(){
        Log.i("initViews","start");
        int rectNumSelected=0;

        // בודק אם השחקן שלי תפס משבצת
        if(myPlayer.selectedCell != null){
            // שומר את המיקום של המשבצת
            rectNumSelected=getRectNumber(myPlayer.selectedCell.position.x, myPlayer.selectedCell.position.y);
        }

        //עובר בלולאה על כל הפקדים כדי להגדיר את הנתונים שלהם
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                FontFitTextView tv = views[x][y];
                boolean isSelected = myPlayer.selectedCell != null && myPlayer.selectedCell.position.equals(x, y);

                //
                // setBackground
                //
                // בודק אם מדובר במשבצת ריקה שנבחרה על ידי השחקן שלי
                if (game.getBoardValue(x, y) == 0 && isSelected) {
                    tv.setBackgroundResource(R.drawable.sudoku_background_focus);
                    // בודק אם מדובר במשבצת שנבחרה על ידי השחקן השני
                } else if (otherPlayer.selectedCell != null && otherPlayer.selectedCell.position.equals(x, y)) {
                    tv.setBackgroundResource(R.drawable.sudoku_background_other_player);
                } else {
                    tv.setBackgroundResource(R.drawable.sudoku_background);
                }

                //
                // setText
                //
                ArrayList<Integer> values = myPlayer.suggestionBoard.asValues(x, y);


                if (myPlayer.board.get(x, y) > 0) {
                    tv.setText(myPlayer.board.get(x, y) + "");
                    tv.setTextColor(myPlayerColor);
                } else if (otherPlayer.board.get(x, y) > 0) {
                    tv.setText(otherPlayer.board.get(x, y) + "");
                    tv.setTextColor(otherPlayerColor);
                } else if (dataSource.baseValues.get(x, y) > 0) {
                    tv.setText(dataSource.baseValues.get(x, y) + "");
                    tv.setTextColor(baseColor);
                } else {
                    tv.setText(Html.fromHtml(getSuggestionHtmlText(values),HtmlCompat.FROM_HTML_MODE_LEGACY));
                }

                //
                // set state
                //
                if (myPlayer.selectedCell != null) {
                    int rectNum = getRectNumber(x, y);
                    boolean isSameRect = rectNum == rectNumSelected;
                    boolean isSameRow=myPlayer.selectedCell.position.y==y;
                    boolean isSameLine=myPlayer.selectedCell.position.x==x;
                    tv.setSelected(isSameRow || isSameLine || isSameRect);
                    int value = game.getBoardValue(x, y);
                    tv.setActivated(values.contains(value));
                }else{
                    tv.setSelected(false);
                    tv.setActivated(false);
                }
            }
        }

        Log.i("initViews","finish");
    }

    private FontFitTextView getNewTextView(Position position) {
        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
        FontFitTextView tv = new FontFitTextView(getApplicationContext());
        tv.setTag(position);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.sudoku_background);
        tv.setLayoutParams(viewParam);
        tv.setFocusable(true);
        tv.setSelected(false);
        tv.setActivated(false);
        tv.setOnClickListener(v->{
            onCellClick((Position)v.getTag());
        });
        return tv;
    }

    private void onCellClick(Position position) {
        if(myPlayer.selectedCell != null) {
            if (myPlayer.selectedCell.position.equals(position)) {
                ArrayList<Integer> values = myPlayer.suggestionBoard. asValues(position);
                if (values.size() == 1) {
                    int value=values.get(0);
                    if(dataSource.values.get(position)==value) {
                        myPlayer.addAction(position, value);
                    }else{
                        myPlayer.addBadAction(position, value);
                    }
                    initViews();
                    initButtons();
                }
                return;
            }
        }

        myPlayer.selectedCell = new SelectedCell();
        myPlayer.selectedCell.position=position;
        myPlayer.selectedCell.time=new Date().getTime();

        initViews();
        initButtons();
    }

    private void initButtons() {
        if (myPlayer.selectedCell == null) {
            for (TextView tv : buttons) {
                tv.setSelected(false);
            }
            return;
        }

        ArrayList<Integer> values = myPlayer.suggestionBoard.asValues(myPlayer.selectedCell.position);
        for (TextView tv : buttons) {
            tv.setSelected(values.contains(tv.getTag()));
        }
    }

    private void onNumberButtonClick(View view) {
        int value = (Integer) view.getTag();
        if (myPlayer.selectedCell == null) {
            return;
        }
        if (myPlayer.suggestionBoard.has(myPlayer.selectedCell.position, value)) {
            myPlayer.suggestionBoard.remove(myPlayer.selectedCell.position, value);
            view.setSelected(false);
        } else {
            myPlayer.suggestionBoard.add(myPlayer.selectedCell.position, value);
            view.setSelected(true);
        }
        initViews();
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