package com.moshe.glaz.scrabble.ui.board;

public class BoardCellData {
    public BoardCellData(){
        textColor=E_TextColor.white;
        background= E_cellBackground.empty;
        textSize=E_TextSize.Medium;
    }

    public E_cellBackground background;
    public E_TextColor textColor;
    public E_TextSize textSize;
    public boolean textBold;
    public int id;
    public String text;
    public int imageResId;

    public boolean hasText() {
        return text !=null && text.length() > 0;
    }

}
