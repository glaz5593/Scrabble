package com.moshe.glaz.scrabble.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.enteties.scrabble.Game;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;
import com.moshe.glaz.scrabble.managers.ActionListener;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.GameLogicManager;
import com.moshe.glaz.scrabble.managers.SudokuManager;
import com.moshe.glaz.scrabble.managers.TrackingManager;
import com.moshe.glaz.scrabble.managers.LogicManager;
import com.moshe.glaz.scrabble.managers.PreferencesManager;
import com.moshe.glaz.scrabble.server.WiktionaryTask;
import com.moshe.glaz.scrabble.ui.adapters.SuggestionGamesAdapter;
import com.moshe.glaz.scrabble.ui.modelView.FullUserModelView;

public class MainActivity extends AppCompatActivity implements SuggestionGamesAdapter.SuggestionGamesListener {

    ListView lv_games;
    SuggestionGamesAdapter adapter;
    FullUserModelView modelView;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

        modelView=new FullUserModelView(findViewById(R.id.ll_main));
        UIUtils.setStatusBarColor(this);

        lv_games = findViewById(R.id.lv_games);
      }

    @Override
    protected void onResume() {
        super.onResume();

        user = LogicManager.getInstance().getUser();


        if (user == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;
        }

        modelView.setUser(user);
        modelView.initUi();
        TrackingManager.getInstance().startTracking();

        DataSourceManager.getInstance().setUpdateListener(onDataSourceUpdate());
        DataSourceManager.getInstance().refreshData();
        initGamesAdapter();
    }

    private DataSourceManager.UpdateListener onDataSourceUpdate() {
        return new DataSourceManager.UpdateListener() {
            @Override
            public void onGameUpdate(Game game) {

            }

            @Override
            public void onOnUserUpdate(User user) {
                if(adapter!= null){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onOnSuggestionGameUpdate(SuggestionGame suggestionGame) {
                if(adapter!= null){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onOnSuggestionGamesChange() {
                initGamesAdapter();
            }

            @Override
            public void onOnUsersChange() {
                if(adapter!= null){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {

            }
        };
    }

    private void initGamesAdapter() {
        adapter=new SuggestionGamesAdapter(DataSourceManager.getInstance().getSuggestionGames(),this);
        lv_games.setAdapter(adapter);
    }

    public void onAvatarClick(View view) {
        UIUtils.showAvatarDialog(this, avatar->{
            modelView.initAvatarUi();
            LogicManager.getInstance().updateUserAvatar(user, avatar, result->{
                if(result.success){
                    user=PreferencesManager.getInstance().getUser();
                }else{
                    user=PreferencesManager.getInstance().getUser();
                    modelView.initUi();
                }
            });
        });
    }

    public void onHistoryClick(View view) {

    }

    String[] words=new String[]{"ולקחתם","לקיחה","שלום","אמית","אמת","קוודש","קודש","טיפש","בילבול","בולבול","טקע"};
    int iii=0;

    public void onAboutClick(View view) {
        String word=words[iii++];
        if(iii==words.length)iii=0;

        Log.i("WiktionaryTask","test:"+word);
        WiktionaryTask task=new WiktionaryTask(word, new WiktionaryTask.WiktionaryResult() {
            @Override
            public void onResult(boolean valid) {
                Log.i("WiktionaryTask","valid:"+valid);
            }

            @Override
            public void onError() {
                Log.i("WiktionaryTask","onError");
            }
        });
        task.execute();
    }

    public void onUsersClick(View view) {
        startActivity(new Intent(getApplicationContext(),UsersActivity.class));
    }

    @Override
    public void onSuggestionGamesAction(SuggestionGame suggestionGame, SuggestionGamesAdapter.e_actionType type) {

        ActionListener listener= result->{
            if(!result.success){
                UIUtils.showToast(result.error);
            }

            adapter.notifyDataSetChanged();
        };


        switch (type){
            case Accept:
                LogicManager.getInstance().acceptToSuggestion(suggestionGame,listener);
                adapter.notifyDataSetChanged();
                break;
            case Start:
                LogicManager.getInstance().startSuggestion(suggestionGame,listener);
                adapter.notifyDataSetChanged();
                break;
            case Cancel:
                LogicManager.getInstance().cancelSuggestion(suggestionGame,listener);
                initGamesAdapter();
                break;
            case Next:
                SudokuManager.getInstance().setActiveGame(suggestionGame.gameUid, result ->{
                    if(result.success){
                        startActivity(new Intent(getApplicationContext(),SudokuActivity.class));
                    }else{
                        UIUtils.showToast(result.error);
                    }
                });
                break;
        }
    }

    public void onAddClick(View view) {
        LogicManager.getInstance().addNewSuggestionGame(result->{
            if(result.success){
                initGamesAdapter();
            }else{
                UIUtils.showToast(result.error);
            }
        });
    }
}