package com.moshe.glaz.scrabble.managers;

import com.moshe.glaz.scrabble.enteties.scrabble.Game;

public class GameLogicManager {
    public  static GameLogicManager instance;
    public static GameLogicManager getInstance() {
        if(instance==null){
            instance=new GameLogicManager();
        }
        return instance;
    }

    private GameLogicManager(){

    }

    private Game activeGame;
    public Game getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(String gameUid,ActionListener listener) {
        if(activeGame != null && activeGame.uid.equals(gameUid)){
            listener.onResult(ActionResult.toSuccess(activeGame));
            return;
        }

        FirebaseManager.getInstance().getGame(gameUid,result -> {
            if(result.success){
                activeGame=(Game) result.result;
            }

            listener.onResult(result);
        });
    }

    public boolean hasActiveGame() {
        return false;
    }
}
