package com.moshe.glaz.scrabble.enteties.scrabble;

import com.moshe.glaz.scrabble.enteties.Cell;

import java.util.ArrayList;
import java.util.Date;

public class Action {
    public Date startDate;
    public ArrayList<Cell> filledCells;
    public int multipleScore;
    public Cell doubleCell;
    public Cell tripleCell;
    public int score;

    public void calcScore() {
        score = 0;
        if (filledCells.size() == 0) {
            return;
        }

        int simpleScore=0;
        for (Cell c : filledCells) {
            int multipleCell = 1;
            if (c.equals(tripleCell)) {
                multipleCell = 3;
            } else if (c.equals(doubleCell)) {
                multipleCell = 2;
            }

            score += (c.card.score * multipleCell);
            simpleScore+=c.card.score;
        }

        score+=(simpleScore * (multipleScore-1));
    }
}
