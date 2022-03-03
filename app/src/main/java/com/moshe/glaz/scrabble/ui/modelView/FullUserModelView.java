package com.moshe.glaz.scrabble.ui.modelView;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.text.HtmlCompat;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.DateUtils;
import com.moshe.glaz.scrabble.infrastructure.TextUtils;
import com.moshe.glaz.scrabble.infrastructure.UIUtils;
import com.moshe.glaz.scrabble.infrastructure.Utils;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.GoogleAPIManager;
import com.moshe.glaz.scrabble.managers.LogicManager;

public class FullUserModelView {
    public View root;
    TextView tv_nickName,tv_date, tv_status_1,tv_status_2,tv_status_3,
            tv_status_result_1,tv_status_result_2,tv_status_result_3,tv_description,tv_location;

    ImageView iv_icon;

    User user;

    public FullUserModelView(View root){
        this.root=root;

        tv_nickName = findViewById(R.id.tv_nickName);
        tv_date = findViewById(R.id.tv_date);
        tv_status_1 = findViewById(R.id.tv_status_1);
        tv_status_2 = findViewById(R.id.tv_status_2);
        tv_status_3 = findViewById(R.id.tv_status_3);
        tv_status_result_1 = findViewById(R.id.tv_status_result_1);
        tv_status_result_2 = findViewById(R.id.tv_status_result_2);
        tv_status_result_3 = findViewById(R.id.tv_status_result_3);
        tv_description= findViewById(R.id.tv_description);
        iv_icon   = findViewById(R.id.iv_icon);
        tv_location = findViewById(R.id.tv_location);
    }

    public FullUserModelView setUser(User user){
        this.user=user;
        return this;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return root.findViewById(id);
    }



    public void initUi() {
        tv_nickName.setText(user.nickName);
        tv_date.setText(Html.fromHtml( TextUtils.getStringResorce(R.string.connectedFrom) + " " + TextUtils.getHTMLText_bold(DateUtils.getTimeOrDate(user.registrationDate)), HtmlCompat.FROM_HTML_MODE_LEGACY));
        initAvatarUi();
        tv_description.setText(user.status);

        tv_status_result_1.setText(user.gamesPlayed+"");
        tv_status_result_2.setText(user.gamesWin+"");
        tv_status_result_3.setText(user.gamesLose+"");

        if(tv_location!=null){
            tv_location.setVisibility(View.GONE);
            if(user.lastKnowLocation!= null){
                 UIUtils.runOnUI(() -> {
                    tv_location.setText(GoogleAPIManager.getAddressString(user.lastKnowLocation.getLat(),user.lastKnowLocation.getLon()).toString());
                    tv_location.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    public void  initAvatarUi(){
        int avatarResId = DataSourceManager.getInstance().getAvatarResId(user.avatar);
        iv_icon.setImageResource(avatarResId);
    }

    public User getUser() {
        return user;
    }
}
