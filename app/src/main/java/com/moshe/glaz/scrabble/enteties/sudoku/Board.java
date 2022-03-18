package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;
import com.moshe.glaz.scrabble.enteties.sudoku.values.IntValue;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
    public Board() {
        values = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                values.add(new IntValue());
            }
        }
    }

    public ArrayList<IntValue> values;

    public int get(int x, int y) {
        return values.get((y * 9) + x).value;
    }

    public int get(Position position) {
        return get(position.x,position.y);
    }

    public void set(Position position, int value) {
        set(position.x,position.y,value);
    }

    public void set(int x, int y, int value) {
        values.get((y * 9) + x).value = value;
    }

}
