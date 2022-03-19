package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;
import com.moshe.glaz.scrabble.enteties.sudoku.values.IntValue;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
    int size = 9;
    public Board() {
        values = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                values.add(new IntValue());
            }
        }
    }

    public ArrayList<IntValue> values;

    public int get(int x, int y) {
        return values.get((y * size) + x).value;
    }

    public int get(Position position) {
        return get(position.x,position.y);
    }

    public void set(Position position, int value) {
        set(position.x,position.y,value);
    }

    public void set(int x, int y, int value) {
        values.get((y * size) + x).value = value;
    }

}
