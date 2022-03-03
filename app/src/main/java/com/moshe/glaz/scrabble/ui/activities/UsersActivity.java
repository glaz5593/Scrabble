package com.moshe.glaz.scrabble.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.databinding.ActivityUsersBinding;
import com.moshe.glaz.scrabble.enteties.scrabble.Game;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.ui.adapters.UserAdapter;

public class UsersActivity extends AppCompatActivity implements DataSourceManager.UpdateListener, UserAdapter.onSelectListener {
    UserAdapter adapter;
    ActivityUsersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UIUtils.setStatusBarColor(this);

        setTitle(R.string.users);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initAdapter();
        DataSourceManager.getInstance().setUpdateListener(this);
        DataSourceManager.getInstance().refreshData();
    }

    private void initAdapter() {
        adapter=new UserAdapter(this);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsers.setAdapter(adapter);
    }

    @Override
    public void onGameUpdate(Game game) {

    }

    @Override
    public void onOnUserUpdate(User user) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onOnSuggestionGameUpdate(SuggestionGame suggestionGame) {

    }

    @Override
    public void onOnSuggestionGamesChange() {

    }

    @Override
    public void onOnUsersChange() {
        initAdapter();
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onSelectUser(User user) {
        UIUtils.showToast(user.nickName);
    }
}