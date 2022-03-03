package com.moshe.glaz.scrabble.enteties.sudoku;


import java.util.ArrayList;

public class Player {
    public Player(){
        actions=new ArrayList<>();
        badActions=new ArrayList<>();
    }

    public String uid;
    public SuggestionAction suggestionAction;
    public ArrayList<Action> actions;
    public ArrayList<Action> badActions;

    public int getScore(){
        int res=0;

        for(Action a : actions){
            res+=a.score;
        }
        for(Action b : badActions){
            res+=b.score;
        }
        return res;
    }

    public int getActiveActionScore(){
        if(suggestionAction==null){
            return 0;
        }

        return suggestionAction.score;
    }
}
