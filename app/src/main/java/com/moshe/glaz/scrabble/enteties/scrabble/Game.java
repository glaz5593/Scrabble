package com.moshe.glaz.scrabble.enteties.scrabble;

import com.moshe.glaz.scrabble.enteties.Card;

import java.util.ArrayList;
import java.util.Date;

public class Game {
    public String uid;
    public Date startDate;
    public Player user1;
    public Player user2;
    public ArrayList<Card> freeCards;
    public String turnUserUid;
}
