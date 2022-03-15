package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;

import java.util.ArrayList;

public class SuggestionBoard {
    public SuggestionBoard() {
        values = new boolean[9][9][10];
    }

    private boolean[][][] values;

    public boolean has(int x, int y, int value) {
        return values[x][y][value];
    }

    public void add(int x, int y, int value) {
        values[x][y][value] = true;
    }

    public void remove(int x, int y, int value) {
        values[x][y][value] = false;
    }

    public void clear(int x, int y) {
        values[x][y] = new boolean[10];
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
        for(boolean b : values[x][y]) {
            if (b) res.add(i);
            i++;
        }

        return res;
    }
}