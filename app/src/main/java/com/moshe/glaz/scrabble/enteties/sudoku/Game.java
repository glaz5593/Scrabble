package com.moshe.glaz.scrabble.enteties.sudoku;

import com.moshe.glaz.scrabble.enteties.Position;

import java.util.Date;

public class Game {
    public String uid;
    public Date startDate;
    public Player user1;
    public Player user2;
    public Board baseBoard;
    public int dataSourceId;

    public int getBoardValue(Position position) {
        if (position == null) {
            return 0;
        }
        return getBoardValue(position.x, position.y);
    }

    public int getBoardValue(int x, int y) {
        if (baseBoard.get(x, y) > 0) {
            return baseBoard.get(x, y);
        }

        if (user1.board.get(x, y) > 0) {
            return user1.board.get(x, y);
        }

        return user2.board.get(x, y);
    }

}
