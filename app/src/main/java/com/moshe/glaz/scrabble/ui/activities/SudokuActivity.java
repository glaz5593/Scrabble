package com.moshe.glaz.scrabble.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import com.moshe.glaz.scrabble.enteties.sudoku.values.BooleanVal;
import com.moshe.glaz.scrabble.enteties.sudoku.values.IntBoolValue;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.LogicManager;
import com.moshe.glaz.scrabble.managers.SudokuManager;
import com.moshe.glaz.scrabble.infrastructure.*;
import com.moshe.glaz.scrabble.ui.views.CellTextView;

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

    ArrayList<CellTextView> views;
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
            initBoardViews();
         }
        initUsersUi();
        initViews(true);
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

    private void initBoardViews() {
        int size = 9;
        views = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            CellTextView view = new CellTextView(this);
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
            binding.clBoard.addView(view, 0);
            views.add(view);
        }

        for (int i = 0; i < size * size; i++) {
            Log.i("init2", "" + i);
            CellTextView view = views.get(i);
            ConstraintSet set = new ConstraintSet();
            set.clone(binding.clBoard);

            int marginTop= (i / size ==3 || i / size==6) ? 2 : 1;
            int marginBottom=(i / size ==2 || i / size==5) ? 2 : 1;
            int marginStart=(i % size ==3 || i % size==6) ? 2 : 1;
            int marginEnd=(i % size ==2 || i % size==5) ? 2 : 1;

            if (i < size) {
                set.connect(view.getId(), ConstraintSet.TOP, binding.clBoard.getId(), ConstraintSet.TOP, marginTop);
            } else {
                set.connect(view.getId(), ConstraintSet.TOP, views.get(i - size).getId(), ConstraintSet.BOTTOM, marginTop);
            }
            if (i >= (size * size) - size) {
                set.connect(view.getId(), ConstraintSet.BOTTOM, binding.clBoard.getId(), ConstraintSet.BOTTOM, marginBottom);
            } else {
                set.connect(view.getId(), ConstraintSet.BOTTOM, views.get(i + size).getId(), ConstraintSet.TOP, marginBottom);
            }
            if (i % size == 0) {
                set.connect(view.getId(), ConstraintSet.START, binding.clBoard.getId(), ConstraintSet.START, marginStart);
            } else {
                set.connect(view.getId(), ConstraintSet.START, views.get(i - 1).getId(), ConstraintSet.END, marginStart);
            }
            if ((i + 1) % size == 0) {
                set.connect(view.getId(), ConstraintSet.END, binding.clBoard.getId(), ConstraintSet.END, marginEnd);
            } else {
                set.connect(view.getId(), ConstraintSet.END, views.get(i + 1).getId(), ConstraintSet.START, marginEnd);
            }
            set.applyTo(binding.clBoard);
        }
    }

    private String getSuggestionHtmlText(int x,int y) {
        ArrayList<Integer> values= myPlayer.suggestionBoard.asValues(x,y);
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

    void initViews(boolean setText){
        Log.i("initViews","start");
        int rectNumSelected=0;
        ArrayList<Integer> selectedValues=new ArrayList<>();
        // בודק אם השחקן שלי תפס משבצת
        if(myPlayer.selectedCell != null){
            int boardValue=game.getBoardValue(myPlayer.selectedCell.position.x,myPlayer.selectedCell.position.y);
            if (boardValue > 0){
                selectedValues.add(boardValue);
            }else {
                selectedValues = myPlayer.suggestionBoard.asValues(myPlayer.selectedCell.position);
            }
            rectNumSelected=getRectNumber(myPlayer.selectedCell.position.x, myPlayer.selectedCell.position.y);
        }

        //עובר בלולאה על כל הפקדים כדי להגדיר את הנתונים שלהם
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                CellTextView tv = views.get((y * 9) + x);
                boolean isSelected = myPlayer.selectedCell != null && myPlayer.selectedCell.position.equals(x, y);

                //if (otherPlayer.selectedCell != null && otherPlayer.selectedCell.position.equals(x, y)) {
                //         tv.setBackgroundResource(R.drawable.sudoku_background_other_player);
                //     }

                //
                // set state
                //
                ArrayList<Integer> badValues=new ArrayList<>();
                if (myPlayer.selectedCell != null) {
                    int rectNum = getRectNumber(x, y);
                    int value = game.getBoardValue(x, y);

                    boolean isSameRect = rectNum == rectNumSelected;
                    boolean isSameRow=myPlayer.selectedCell.position.y==y;
                    boolean isSameLine=myPlayer.selectedCell.position.x==x;
                    boolean isContains=selectedValues.contains(value);

                    tv.setSelected(isSameRow || isSameLine || isSameRect);
                    tv.setActivated(isSelected || isContains);
                    tv.setChecked(isSelected && game.getBoardValue(x, y) == 0);
                    if(setText && !isSelected && (isSameRow || isSameLine || isSameRect) && isContains){
                        badValues.add(value);
                    }
                }else{
                    tv.setChecked(false);
                    tv.setSelected(false);
                    tv.setActivated(false);
                }

                //
                // setText
                //
                initViewText(x,y);
            }
        }

        Log.i("initViews","finish");
    }
    void initViewText(int x,int y) {
        CellTextView tv = views.get((y * 9) + x);

        //
        // setText
        //
        if (myPlayer.board.get(x, y) > 0) {
            tv.setText(myPlayer.board.get(x, y) + "");
        } else if (otherPlayer.board.get(x, y) > 0) {
            tv.setText(otherPlayer.board.get(x, y) + "");
        } else if (dataSource.baseValues.get(x, y) > 0) {
            tv.setText(dataSource.baseValues.get(x, y) + "");
        } else {
            tv.setText(Html.fromHtml(getSuggestionHtmlText(x,y), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }


    private void onCellClick(Position position) {
        Log.i("pref3","1");
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
                    initViews(false);
                    initViewText(position.x ,position.y);
                    initButtons();
                }
                return;
            }
        }

        myPlayer.selectedCell = new SelectedCell();
        myPlayer.selectedCell.position=position;
        myPlayer.selectedCell.time=new Date().getTime();

        Log.i("pref3","7");
        initViews(false);
        initViewText(position.x ,position.y);

        Log.i("pref3","8");
        initButtons();
        Log.i("pref3","9");
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

    boolean lllllll=true;
    private void onNumberButtonClick(View view) {
        int value = (Integer) view.getTag();
        if (myPlayer.selectedCell == null) {
            return;
        }
        if (myPlayer.suggestionBoard.has(myPlayer.selectedCell.position, value)) {
            myPlayer.suggestionBoard.remove(myPlayer.selectedCell.position, value);
            view.setSelected(false);
        } else {
            lllllll=!lllllll;
            myPlayer.suggestionBoard.add(myPlayer.selectedCell.position, value);
            view.setSelected(true);
        }
        initViews(false);
        initViewText(myPlayer.selectedCell.position.x ,myPlayer.selectedCell.position.y);
    }

    private int getRectNumber(int x, int y) {
        int xx = x / 3;
        xx++;
        xx*=100;
        int yy=y/3;
        yy++;
        return  xx+yy;
    }
}