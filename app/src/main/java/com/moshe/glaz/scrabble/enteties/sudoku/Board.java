package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;

public class Board {
    public Board(){
        values=new int[9][9];
    }
    private int[][] values;

    public int get(int x,int y){
        return values[x][y];
    }
    public int get(Position position){
        return values[position.x][position.y];
    }

    public void set(int x,int y,int value){
         values[x][y]=value;
    }
    public void get(Position position,int value){
         values[position.x][position.y]=value;
    }
}
