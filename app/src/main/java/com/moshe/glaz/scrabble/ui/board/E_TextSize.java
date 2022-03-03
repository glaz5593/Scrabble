package com.moshe.glaz.scrabble.ui.board;

public enum E_TextSize {
    Big,Medium,Small;

    public int getSize(int viewSize){
        int  ratio= 6;
        if(this==Big){
            ratio= 5;
        }

        if(this==Small){
            ratio= 8;
        }

        return viewSize / ratio;
    }
}
