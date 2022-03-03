package com.moshe.glaz.scrabble.managers;

import com.moshe.glaz.scrabble.enteties.User;
import com.moshe.glaz.scrabble.enteties.sudoku.Game;
import com.moshe.glaz.scrabble.enteties.sudoku.Player;

import java.util.Date;

public class SudokuManager {
    public  static SudokuManager instance;
    public static SudokuManager getInstance() {
        if(instance==null){
            instance=new SudokuManager();
        }
        return instance;
    }

    private SudokuManager(){

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

    public Game createNewGame(User user1, User user2,ActionListener listener){
        Game res=new Game();
        res.dataSource=DataSourceManager.getInstance().sudokuGamesDataSource.get(0);
        res.user1=new Player();
        res.user2=new Player();
        res.user1.uid=user1.uid;
        res.user2.uid=user2.uid;
        res.startDate=new Date();

        FirebaseManager.getInstance().addGame(res,listener);

        return res;
    }


}
