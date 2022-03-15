package com.moshe.glaz.scrabble.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.enteties.scrabble.Game;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.GameLogicManager;
import com.moshe.glaz.scrabble.managers.LogicManager;
import com.moshe.glaz.scrabble.ui.board.*;


public class GameActivity extends AppCompatActivity {

    RelativeLayout rl_board;
    UserViewModel myViews;
    UserViewModel playerViews;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        myViews=new UserViewModel();
        playerViews=new UserViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //game= GameLogicManager.getInstance().getActiveGame();

        if(game==null){
            finish();
            return;
        }

        boolean isMe = LogicManager.getInstance().thisIsMe(game.user1.uid);

        myViews.init(R.id.ll_user1, game.user1.uid, isMe);
        playerViews.init(R.id.ll_user2,game.user2.uid,isMe);
        playerViews.setCardEnabled(false);

        myViews.updateUi();
        playerViews.updateUi();
    }

    class UserViewModel {
        CardsView cv_user;
        ImageView iv_icon;
        TextView tv_nickName;
        TextView tv_description;
        User  user;
        boolean isMe;
        View rootView;

        void init(int rootResId, String userUid, boolean isMe) {
            this.rootView = findViewById(rootResId);
            this.isMe = isMe;
            cv_user = rootView.findViewById(R.id.cv_user);
            iv_icon = rootView.findViewById(R.id.iv_icon);
            tv_nickName = rootView.findViewById(R.id.tv_nickName);
            tv_description = rootView.findViewById(R.id.tv_description);
            user = DataSourceManager.getInstance().getUser(userUid);
        }

        public void setCardEnabled(boolean value){
            cv_user.setEnabled(value);
        }



        private void updateUi() {
            tv_nickName.setText(user.nickName);
            iv_icon.setImageResource(DataSourceManager.getInstance().getAvatarResId(user.avatar));
            tv_description.setText(user.status);
        }
    }
}