package com.moshe.glaz.scrabble.enteties;

public class Position {
   public int x, y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(){}
    public boolean equals(Position xy) {
        return xy!= null &&
                x== xy.x &&
                y == xy.y;
    }
    public boolean equals(int x, int y) {
       return this.x== x &&
                this.y == y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
