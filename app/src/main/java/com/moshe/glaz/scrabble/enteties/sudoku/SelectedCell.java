package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;

import java.util.ArrayList;

public class SelectedCell {
    public SelectedCell(){
        position=new Position();
    }

    public Position position;
    public long time;
    public int score;

    public boolean hasValue() {
        return time > 0;
    }

}
