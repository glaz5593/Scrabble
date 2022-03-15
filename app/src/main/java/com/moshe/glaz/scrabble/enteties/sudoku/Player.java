package com.moshe.glaz.scrabble.enteties.sudoku;


import com.moshe.glaz.scrabble.enteties.Position;

import java.util.ArrayList;

public class Player {
    public Player(){
        actions=new ArrayList<>();
        badActions=new ArrayList<>();
        suggestionBoard=new SuggestionBoard();
    }

    public String uid;
    public SuggestionBoard suggestionBoard;
    public ArrayList<Action> actions;
    public ArrayList<Action> badActions;
    public SelectedCell selectedCell;
    public Board board=new Board();

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
        if(selectedCell==null){
            return 0;
        }

        return selectedCell.score;
    }


    public void addAction(Position position, int value,int score) {
        Action action=new Action();
        action.value = value;
        action.position = position;
        action.score=score;

        suggestionBoard.clear(position);

        board.set(position, value);
    }


}
