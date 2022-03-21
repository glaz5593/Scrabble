package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;
import com.moshe.glaz.scrabble.enteties.sudoku.values.BooleanVal;

import java.io.Serializable;
import java.util.ArrayList;

public class SuggestionBoard implements Serializable {
    public SuggestionBoard() {
        values = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                values.add(new IntValues());
            }
        }
    }

    public ArrayList<IntValues> values;

    public class IntValues {
        public ArrayList<BooleanVal> values;
        public IntValues() {
            clear();
        }

        void clear(){
            values = new ArrayList<>();
            for (int i = 0; i <= 9; i++) {
                values.add(new BooleanVal());
            }
        }
    }
 
    public boolean has(int x, int y, int value) {
        return values.get((y * 9) + x).values.get(value).value;
    }

    public void add(int x, int y, int value) {
        values.get((y * 9) + x).values.get(value).value = true;
    }

    public void remove(int x, int y, int value) {
        values.get((y * 9) + x).values.get(value).value = false;
    }

    public void clear(int x, int y) {
        values.get((y * 9) + x).clear();
    }

    public boolean has(Position position, int value) {
        if (position == null) return false;
        return has(position.x, position.y, value);
    }

    public void add(Position position, int value) {
        if (position == null) return;
        add(position.x, position.y, value);
    }

    public void remove(Position position, int value) {
        if (position == null) return;
        remove(position.x, position.y, value);
    }

    public void clear(Position position) {
        if (position == null) return;
        clear(position.x, position.y);
    }

    public ArrayList<Integer> asValues(Position position) {
        if (position == null) return new ArrayList<>();
        return asValues(position.x, position.y);
    }
    public ArrayList<Integer> asValues(int x,int y) {
        ArrayList<Integer> res=new ArrayList<>();

        int i=0;
        for(BooleanVal b : values.get((y * 9) + x).values) {
            if (b.value) res.add(i);
            i++;
        }

        return res;
    }

}