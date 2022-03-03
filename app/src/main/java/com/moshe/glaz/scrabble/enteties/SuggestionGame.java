package com.moshe.glaz.scrabble.enteties;

import java.util.Date;
import java.util.UUID;

public class SuggestionGame {
    public SuggestionGame(){

    }

    public String uid;
    public Date   startDate;
    public String user1;
    public String user2;
    public String gameUid;
    public boolean canceled;

    public void copy(SuggestionGame suggestionGame) {
       if(suggestionGame==null){
           return;
       }

       this.startDate = suggestionGame.startDate;
       this.user1 = suggestionGame.user1;
       this.user2 = suggestionGame.user2;
       this.gameUid = suggestionGame.gameUid;
       if(suggestionGame.canceled){
           this.canceled = true;
       }
    }
}
