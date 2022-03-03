package com.moshe.glaz.scrabble.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.app.AppBase;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.TextUtils;
import com.moshe.glaz.scrabble.managers.DataSourceManager;
import com.moshe.glaz.scrabble.managers.LogicManager;

public class SuggestionGamesAdapter extends ArrayAdapter<SuggestionGame> {
    SuggestionGamesListener listener;

    public enum e_actionType {
        Accept,
        Start,
        Cancel,
        Next
    }

    public interface SuggestionGamesListener {
        void onSuggestionGamesAction(SuggestionGame suggestionGame, e_actionType type);
    }

    public SuggestionGamesAdapter(SuggestionGame[] arr, SuggestionGamesListener listener) {
        super(AppBase.getContext(), R.layout.item_suggestion_game, arr);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SuggestionGame suggestionGame = getItem(position);

        if(!suggestionGame.canceled) {
            convertView = new ViewModel(parent, suggestionGame).root;
        }else{
            convertView = new ViewModelCanceled(parent, suggestionGame).root;
        }
        return convertView;
    }

    class ViewModel {
        ImageView iv_icon1;
        TextView tv_nickName1;
        TextView tv_description1;
        ImageView iv_icon2;
        TextView tv_nickName2;
        TextView tv_description2;
        TextView tv_title;
        View btn_accept;
        View pb_wait;
        View tv_search;
        View btn_start;
        View btn_cancel;
        View root;
        SuggestionGame suggestionGame;

        ViewModel(ViewGroup parent, SuggestionGame suggestionGame) {
            this.suggestionGame = suggestionGame;
            this.root = LayoutInflater.from(AppBase.getContext()).inflate(R.layout.item_suggestion_game, parent, false);

            iv_icon1 = root.findViewById(R.id.iv_icon1);
            tv_nickName1 = root.findViewById(R.id.tv_nickName1);
            tv_description1 = root.findViewById(R.id.tv_description1);
            iv_icon2 = root.findViewById(R.id.iv_icon2);
            tv_nickName2 = root.findViewById(R.id.tv_nickName2);
            tv_description2 = root.findViewById(R.id.tv_description2);
            tv_title = root.findViewById(R.id.tv_title);
            btn_accept = root.findViewById(R.id.btn_accept);
            pb_wait = root.findViewById(R.id.pb_wait);
            tv_search = root.findViewById(R.id.tv_search);
            btn_start = root.findViewById(R.id.btn_start);
            btn_cancel = root.findViewById(R.id.btn_cancel);

            updateUi();
        }

        private void updateUi() {
            User user1=DataSourceManager.getInstance().getUser(suggestionGame.user1);
            User user2=DataSourceManager.getInstance().getUser(suggestionGame.user2);

            initUserUi(user1,iv_icon1,tv_nickName1,tv_description1);
            initUserUi(user2,iv_icon2,tv_nickName2,tv_description2);

            boolean isMySuggestion=LogicManager.getInstance().thisIsMe(user1.uid);

            if(isMySuggestion) {
                if (user2 == null) {
                    btn_accept.setVisibility(View.GONE);
                    btn_start.setVisibility(View.GONE);

                    tv_title.setText(TextUtils.getStringResorce(R.string.new_your_suggestion));
                    root.setBackgroundResource(R.drawable.suggestion_background_green);
                    setOnClick(btn_cancel, e_actionType.Cancel);
                    return;
                }

                if (suggestionGame.gameUid == null) {
                    btn_accept.setVisibility(View.GONE);
                    pb_wait.setVisibility(View.GONE);
                    tv_search.setVisibility(View.GONE);

                    if (user2.gender == 2) {
                        tv_title.setText(TextUtils.getStringResorce(R.string.has_suggestion_female));
                    } else {
                        tv_title.setText(TextUtils.getStringResorce(R.string.has_suggestion_male));
                    }
                    setOnClick(btn_cancel, e_actionType.Cancel);
                    setOnClick(btn_start, e_actionType.Start);
                    root.setBackgroundResource(R.drawable.suggestion_background_blue);
                    return;
                }
            }

            if(suggestionGame.gameUid!=null && suggestionGame.gameUid.length()>0) {
                btn_accept.setVisibility(View.GONE);
                pb_wait.setVisibility(View.GONE);
                tv_search.setVisibility(View.GONE);
                btn_start.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);

                root.setBackgroundResource(R.drawable.suggestion_background_yellow);
                tv_title.setText(TextUtils.getStringResorce(R.string.active_game));
                setOnClick(root, e_actionType.Next);
                return;
            }

            if(user2 == null){
                btn_cancel.setVisibility(View.GONE);
                pb_wait.setVisibility(View.GONE);
                tv_search.setVisibility(View.GONE);
                btn_start.setVisibility(View.GONE);

                root.setBackgroundResource(R.drawable.suggestion_background_green);
                tv_title.setText(TextUtils.getStringResorce(R.string.new_suggestion));
                setOnClick(btn_accept,e_actionType.Accept);
                return;
            }

            btn_accept.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
            pb_wait.setVisibility(View.GONE);
            tv_search.setVisibility(View.GONE);
            btn_start.setVisibility(View.GONE);

            root.setBackgroundResource(R.drawable.suggestion_background_blue);
            tv_title.setText(TextUtils.getStringResorce(R.string.wait_for_start));
        }

        private void setOnClick(View view, e_actionType action) {
            view.setTag(suggestionGame);
            view.setOnClickListener(v -> {
                SuggestionGame game = (SuggestionGame) view.getTag();
                listener.onSuggestionGamesAction(game, action);
            });
        }

        private void initUserUi(User user, ImageView iv_icon, TextView tv_nickName, TextView tv_description) {
            if(user==null){
                iv_icon.setVisibility(View.INVISIBLE);
                tv_nickName.setVisibility(View.INVISIBLE);
                tv_description.setVisibility(View.INVISIBLE);
                return;
            }

            tv_nickName.setText(user.nickName);
            iv_icon.setImageResource(DataSourceManager.getInstance().getAvatarResId(user.avatar));
            tv_description.setText(user.status);
        }
    }
    class ViewModelCanceled {
        ImageView iv_icon1;
        TextView tv_nickName1;
        ImageView iv_icon2;
        TextView tv_nickName2;
        TextView tv_title;
        View root;
        SuggestionGame suggestionGame;

        ViewModelCanceled(ViewGroup parent, SuggestionGame suggestionGame) {
            this.suggestionGame = suggestionGame;
            this.root = LayoutInflater.from(AppBase.getContext()).inflate(R.layout.item_suggestion_game_canceled, parent, false);


            iv_icon1 = root.findViewById(R.id.iv_icon1);
            tv_nickName1 = root.findViewById(R.id.tv_nickName1);
            iv_icon2 = root.findViewById(R.id.iv_icon2);
            tv_nickName2 = root.findViewById(R.id.tv_nickName2);
            tv_title = root.findViewById(R.id.tv_title);


            updateUi();
        }

        private void updateUi() {
            User user1=DataSourceManager.getInstance().getUser(suggestionGame.user1);
            User user2=DataSourceManager.getInstance().getUser(suggestionGame.user2);

            initUserUi(user1,iv_icon1,tv_nickName1);
            initUserUi(user2,iv_icon2,tv_nickName2);

            tv_title.setText(TextUtils.getStringResorce(R.string.canceled_game));
        }

        private void setOnClick(View view, e_actionType action) {
            view.setTag(suggestionGame);
            view.setOnClickListener(v -> {
                SuggestionGame game = (SuggestionGame) view.getTag();
                listener.onSuggestionGamesAction(game, action);
            });
        }

        private void initUserUi(User user, ImageView iv_icon, TextView tv_nickName) {
            if(user==null){
                iv_icon.setVisibility(View.INVISIBLE);
                tv_nickName.setVisibility(View.INVISIBLE);
                return;
            }

            tv_nickName.setText(user.nickName);
            iv_icon.setImageResource(DataSourceManager.getInstance().getAvatarResId(user.avatar));
        }
    }
}
