package com.moshe.glaz.scrabble.managers;

import android.content.Context;


import com.moshe.glaz.scrabble.app.AppBase;
import com.moshe.glaz.scrabble.enteties.scrabble.Game;
import com.moshe.glaz.scrabble.enteties.SuggestionGame;
import com.moshe.glaz.scrabble.enteties.SuggestionGames;
import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.enteties.Users;
import com.moshe.glaz.scrabble.infrastructure.Json;

public class PreferencesManager extends PreferencesBase {
    private static PreferencesManager instance;
    public static PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager(AppBase.getContext(), "Pr9ef979");
        }

        return instance;
    }

    private PreferencesManager(Context context, String prefKey) {
        super(context, prefKey);
    }

    public Game getLastGame(){
        String j= get("LastGame","");
        if(j.length()==0){
            return null;
        }

        return Json.toObject(j,Game.class);
    }

    public void saveLastGame(Game game){
        put("LastGame",Json.toString(game));
    }

    public User getUser(){
        String j= get("User","");
        if(j.length()==0){
            return null;
        }

        return Json.toObject(j,User.class);
    }

    public void saveUser(User user){
        put("User",Json.toString(user));
    }


    public void saveActiveGame(Game activeGame){
        if(activeGame==null){
            put("ActiveGame","");
        }else {
            put("ActiveGame", Json.toString(activeGame));
        }
    }
    public Game getActiveGame() {
        String j= get("ActiveGame","");
        if(j.length()==0){
            return null;
        }

        return Json.toObject(j,Game.class);
    }

    public void saveSuggestionGames(SuggestionGames suggestionGames){
        if(suggestionGames==null || suggestionGames.size()==0){
            putAsync("SuggestionGames","");
        }else {
            putAsync("SuggestionGames", Json.toString(suggestionGames.asArray()));
        }
    }
    public SuggestionGames getSuggestionGames() {
        String j= get("SuggestionGames","");
        if(j.length()==0){
            return new SuggestionGames(null);
        }
        SuggestionGame[] res=Json.toObject(j,(new SuggestionGame[]{}).getClass() );

        return new SuggestionGames(res);
    }

    public void saveUsers(Users users){

        if(users==null || users.size()==0){
            putAsync("Users","");
        }else {
            putAsync("Users", Json.toString(users));
        }

    }

    public Users getUsers() {
        String j= get("Users","");
        if(j.length()==0){
            return new Users();
        }
        Users list=Json.toObject(j,(Users.class));

        return  list;
    }

}
