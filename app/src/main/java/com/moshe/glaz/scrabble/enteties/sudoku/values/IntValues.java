package com.moshe.glaz.scrabble.enteties.sudoku.values;

import java.util.ArrayList;

public class IntValues {
    public ArrayList<BooleanVal> values;
    public IntValues() {
        clear();
    }

    public void clear(){
        values = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            values.add(new BooleanVal());
        }
    }
}
