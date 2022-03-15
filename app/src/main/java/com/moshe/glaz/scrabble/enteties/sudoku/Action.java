package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;

import java.util.Date;

public class Action {
    public Action(){
        position=new Position();
        time=new Date().getTime();
    }
    public Position position;
    public int value;
    public long time;
    public int score;
}
