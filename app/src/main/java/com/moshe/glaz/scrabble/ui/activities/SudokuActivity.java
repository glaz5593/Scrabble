package com.moshe.glaz.scrabble.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.databinding.ActivitySudokuBinding;
import com.moshe.glaz.scrabble.enteties.Position;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.enteties.sudoku.Action;
import com.moshe.glaz.scrabble.enteties.sudoku.DataSource;
import com.moshe.glaz.scrabble.enteties.sudoku.Game;
import com.moshe.glaz.scrabble.enteties.sudoku.Player;
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

    FontFitTextView[][] views;
    TextView[] buttons;

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

    }

    DataSource dataSource;

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
            myPlayer =  game.user1 ;
        }else{
            otherPlayer=game.user1;
            myPlayer = game.user2;
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
        StringBuilder builder=new StringBuilder();
        for(int i=1;i<10;i++){
            builder.append(getSuggestionHtmlNumberText(i,values));
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

    private String getSuggestionHtmlNumberText(int value,ArrayList<Integer> values){
        if(values.contains(value)){
            return value+"";
        }else{
            return TextUtils.getHTMLText_white(value+"");
        }
    }

    long isSuggestionActionEquals(){
        if(otherPlayer.suggestionAction.hasValue() && myPlayer.suggestionAction.hasValue()){
            if(otherPlayer.suggestionAction.position.equals(myPlayer.suggestionAction.position)){
                if(otherPlayer.suggestionAction.time > myPlayer.suggestionAction.time ){
                    return myPlayer.suggestionAction.time-otherPlayer.suggestionAction.time;
                }
            }
        }
        return 0;
    }

    void initViews(){
        // מגדיר את המשבצת שהשחקן שלי בחר בלוח (אם קיים)
        Position selectedPosition=null;
        // מגדיר את המספרים שהשחקן שלי בחר כדי להדגיש את שאר המספרים הזהים בלוח
        ArrayList<Integer> selectedNumbers=new ArrayList<>();
        int rectNumSelected=0;

        // בודק אם השחקן שלי תפס משבצת
        if(myPlayer.selectedCell != null){
            // שומר את המיקום של המשבצת
            selectedPosition=myPlayer.selectedCell.position;
            rectNumSelected=getRectNumber(selectedPosition.x, selectedPosition.y);

            // אם יש שם ערך זה אומר שמדובר בתא ישן שכבר שמו בו ערך
            if(myPlayer.selectedCell.value > 0){
                selectedNumbers.add(myPlayer.selectedCell.value);
                // אם לא, אז אני ארצה להדגיש גם מספרים שהשחקן שלי רשם כהצעה במשבצת שבחר
            }
        }

        if(myPlayer.suggestionAction!= null && myPlayer.suggestionAction.hasValue()){
            selectedNumbers.addAll(myPlayer.suggestionAction.values);
        }



        //עובר בלולאה על כל הפקדים כדי להגדיר את הנתונים שלהם
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                FontFitTextView tv = views[x][y];

                //
                // setBackground
                //
                // בודק אם מדובר במשבצת ריקה שנבחרה על ידי השחקן שלי
                if (game.board.get(x, y) == 0 && myPlayer.selectedCell != null && myPlayer.selectedCell.position.equals(x, y)) {
                    tv.setBackgroundResource(R.drawable.sudoku_background_focus);
                    // בודק אם מדובר במשבצת שנבחרה על ידי השחקן השני
                } else if (selectedPosition != null && selectedPosition.equals(x, y)) {
                    tv.setBackgroundResource(R.drawable.sudoku_background_other_player);
                } else {
                    tv.setBackgroundResource(R.drawable.sudoku_background);
                }

                //
                // setText
                //
                if (game.board.get(x, y) > 0) {
                    tv.setText(game.board.get(x, y) + "");
                    tv.setTextColor(UIUtils.getColor(R.color.sudoku_text_color_base_value));
                } else if (selectedPosition != null && selectedPosition.equals(x, y) && myPlayer.hasSuggestionValue()) {
                    tv.setText(Html.fromHtml(getSuggestionHtmlText(myPlayer.suggestionAction.values),HtmlCompat.FROM_HTML_MODE_LEGACY));
                    tv.setTextColor(UIUtils.getColor(R.color.sudoku_text_color_suggestion_value));
                } else {
                    tv.setText("");
                }

                //
                // set state
                //
                if (selectedPosition != null && !selectedPosition.equals(x, y)) {
                    int rectNum=getRectNumber(x, y);
                    boolean isSameRect = rectNum==rectNumSelected;
                    tv.setSelected(selectedPosition.x == x || selectedPosition.y == y || isSameRect);
                    int value=game.board.get(x,y);
                    tv.setActivated(selectedNumbers.contains(value));
                }

                //
                // set text color
                //
            }
        }
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
        myPlayer.selectedCell=new Action();
        myPlayer.selectedCell.position=position;
        myPlayer.selectedCell.time=new Date().getTime();
        myPlayer.selectedCell.value=game.board.get(position);

        initViews();
    }

    private void onNumberButtonClick(View view) {

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