package com.moshe.glaz.scrabble.managers;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.enteties.sudoku.Game;
import com.moshe.glaz.scrabble.enteties.sudoku.Player;
import com.moshe.glaz.scrabble.enteties.RegistrationUser;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.infrastructure.TextUtils;

import java.util.ArrayList;
import java.util.Date;

public class LogicManager {
    public  static LogicManager instance;
    public static LogicManager getInstance() {
        if(instance==null){
            instance=new LogicManager();
        }
        return instance;
    }
    private User user;

    LogicManager(){
        user=PreferencesManager.getInstance().getUser();
    }

    public String getGenderDescription(int gender){
        return TextUtils.getStringResorce(gender==2 ? R.string.female :R.string.male) ;
    }

    // region users
    //
    //
    public User getUser() {
        return user;
    }
    public boolean hasUser() {
        return getUser()!=null;
    }
    public void login(String userName,String pass,ActionListener listener){
       try {
            FirebaseManager.getInstance().getRegistrationUserByUserName(userName, registrationResult->{
                if(!registrationResult.success){
                    listener.onResult(registrationResult);
                    return;
                }

                ArrayList<RegistrationUser> users = (ArrayList<RegistrationUser>) registrationResult.result;
                RegistrationUser user = users.get(0);
                 if(!user.password.equals(pass)){
                     listener.onResult(ActionResult.toError(TextUtils.getStringResorce(R.string.password_incorrect)));
                     return;
                 }

                getUserByUid(user.userUid, userResult->{
                    if(!userResult.success){
                        listener.onResult(userResult);
                        return;
                    }

                    LogicManager.this.user=(User)userResult.result;
                    PreferencesManager.getInstance().saveUser(LogicManager.this.user);
                    listener.onResult(userResult);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onResult(ActionResult.toError(e.getMessage()));
        }
    }

    private void getUserByUid(String userUid, ActionListener listener) {
        FirebaseManager.getInstance().getUserByKey(userUid, result->{
            listener.onResult(result);
        });
    }

    public void register(RegistrationUser registrationUser,User user, ActionListener listener){
        FirebaseManager.getInstance().getRegistrationUserByUserName(registrationUser.userName, registrationResult-> {
            if(registrationResult.success){
                listener.onResult(ActionResult.toError(TextUtils.getStringResorce(R.string.user_already_exist)));
                return;
            }

            FirebaseManager.getInstance().registration(registrationUser,user,  result -> {
                if(result.success){
                    PreferencesManager.getInstance().saveUser(user);
                    LogicManager.this.user=user;
                }

                listener.onResult(result);
            });
        });
    }

    public void updateUserAvatar(User user,int avatar, ActionListener listener){
        int lastAvatar=user.avatar;
        user.avatar = avatar;

        FirebaseManager.getInstance().updateUser(user, result->{
            if(!result.success){
                user.avatar=lastAvatar;
            }else{
                PreferencesManager.getInstance().saveUser(user);
            }

            listener.onResult(result);
        });
    }
    public boolean thisIsMe(String userUid) {
        if(user==null){
            return false;
        }
         User me=PreferencesManager.getInstance().getUser();
         return (userUid.equals(me.uid));
    }
    // endregion

    // region suggestion game
    //
    //

    public void addNewSuggestionGame(ActionListener listener) {
        SuggestionGame suggestionGame=new SuggestionGame();
        suggestionGame.startDate = new Date();
        suggestionGame.user1=user.uid;
        DataSourceManager.getInstance().addSuggestionGame(suggestionGame,listener);
    }

    public void acceptToSuggestion(SuggestionGame suggestionGame,ActionListener listener) {
        suggestionGame.user2 = LogicManager.getInstance().getUser().uid;
        FirebaseManager.getInstance().updateSuggestionGame(suggestionGame, result->{
            if(!result.success){
                suggestionGame.user2 = null;
            }

            listener.onResult(result);
        });
    }

    public void startSuggestion(SuggestionGame suggestionGame,ActionListener listener) {
        Game game = new Game();
        game.user1 = new Player();
        game.user2 = new Player();
        game.user1.uid = suggestionGame.user1;
        game.user2.uid = suggestionGame.user2;

        ActionListener suggestionListener = result -> {
            if (!result.success) {
                suggestionGame.gameUid ="";
            }
            listener.onResult(result);
        };

        ActionListener gameListener = result -> {
            if (!result.success) {
                listener.onResult(result);
            } else {
                suggestionGame.gameUid = game.uid;
                FirebaseManager.getInstance().updateSuggestionGame(suggestionGame, suggestionListener);
            }
        };

        FirebaseManager.getInstance().addGame(game, gameListener);
    }

    public void cancelSuggestion(SuggestionGame suggestionGame,ActionListener listener) {
        suggestionGame.canceled=true;
        FirebaseManager.getInstance().updateSuggestionGame(suggestionGame, result->{
            if(!result.success){
                suggestionGame.canceled=false;
            }

            listener.onResult(result);
        });
    }

    //end region
}
