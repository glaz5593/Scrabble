package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;

public class Action {
    public Action(){
        position=new Position();
    }
    public Position position;
    public int value;
    public long time;
    public int score;
}
