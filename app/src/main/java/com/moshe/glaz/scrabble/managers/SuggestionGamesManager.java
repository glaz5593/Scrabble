package com.moshe.glaz.scrabble.managers;

import com.moshe.glaz.scrabble.enteties.SuggestionGame;

import java.util.ArrayList;

public class SuggestionGamesManager {
    private static SuggestionGamesManager instance;
    public static SuggestionGamesManager getInstance() {
        if (instance == null) {
            instance = new SuggestionGamesManager();
        }

        return instance;
    }

    private SuggestionGamesManager(){
         suggestionGames=new ArrayList<>();
         loadSuggestionGames();
    }

    ArrayList<SuggestionGame> suggestionGames;
    interface OnSuggestionGameListener{ void onUpdate();}
    private OnSuggestionGameListener listener;
    public void setListener(OnSuggestionGameListener listener) {
        this.listener = listener;
    }

    private void loadSuggestionGames() {
        FirebaseManager.getInstance().getSuggestionGames(result -> {
            if(result.success) {
                suggestionGames = (ArrayList<SuggestionGame>) result.result;
                if(listener!=null){
                    listener.onUpdate();
                }
                registerToSuggestionGamesUpdate();
            }
        });
    }

    private void registerToSuggestionGamesUpdate() {
        FirebaseManager.getInstance().registerToSuggestionGameUpdate(result -> {
            if(result.success) {
                SuggestionGame suggestionGame = (SuggestionGame) result.result;
                updateSuggestionGame(suggestionGame);
                if(listener!=null){
                    listener.onUpdate();
                }
            }
        });
    }

    private void updateSuggestionGame(SuggestionGame suggestionGame) {
        SuggestionGame oldObject = suggestionGames.stream()
                .filter(o -> "uid".equals(suggestionGame.uid))
                .findAny()
                .orElse(null);

        if(oldObject != null){
            oldObject.copy(suggestionGame);
        }else{
            suggestionGames.add(suggestionGame);
        }
    }
}
