package com.moshe.glaz.scrabble.ui.board;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class ScrabbleBoardView11 extends BoardView {

    public ScrabbleBoardView11(Context context) {
        super(context);
        init();
    }
    public ScrabbleBoardView11(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        BoardCellData cell;
        ArrayList<BoardCellData> data=new ArrayList<>();
        int[] dl      =new int[]{2,8,22,32,48,50};
        int[] tl      =new int[]{15,17,45,53};
        int[] dw      =new int[]{12,20,25,29,35,41};
        int[] tw      =new int[]{0,5,10,55};

        int[][] specialCell= new int[][]{dl, tl,dw,tw    };
        String[] specialCellText= new String[]{"DL", "TL","DW","TW"    };
        E_cellBackground[] types=new E_cellBackground[]{E_cellBackground.blue_sky, E_cellBackground.blue, E_cellBackground.orange, E_cellBackground.magenta};

        for (int i=0;i<(11*11);i++){
            cell=new BoardCellData();
            cell.background= E_cellBackground.empty;
            cell.id=i;

            for(int a=0;a<types.length;a++){
                int[] array = specialCell[a];
                for(int id:array){
                    if(cell.id==id ||cell.id==120-id){
                        cell.background = types[a];
                        cell.text=specialCellText[a];
                    }
                }
            }

            if(i==60){
                cell.text="★";
                cell.background = E_cellBackground.start;
            }

            data.add(cell);
        }

        setBoard(11,11,data);
    }
}
