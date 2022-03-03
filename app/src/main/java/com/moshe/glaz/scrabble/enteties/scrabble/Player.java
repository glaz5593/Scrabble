package com.moshe.glaz.scrabble.enteties.scrabble;

import com.moshe.glaz.scrabble.enteties.Card;

import java.util.ArrayList;

public class Player {
    public String uid;
    public ArrayList<Action> actions;
    public ArrayList<Card> availableCards;
    public Action suggestionAction;
}
