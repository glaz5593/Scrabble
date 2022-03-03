package com.moshe.glaz.scrabble.ui.board;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class ScrabbleBoardView15 extends BoardView {

    public ScrabbleBoardView15(Context context) {
        super(context);
        init();
    }
    public ScrabbleBoardView15(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        BoardCellData cell;
        ArrayList<BoardCellData> data=new ArrayList<>();
        int[] dl      =new int[]{3,11,36,38,45,52,59,92,96,98,102,108};
        int[] tl      =new int[]{20,24,76,80,84,88};
        int[] dw      =new int[]{16,28,32,42,48,56,64,70};
        int[] tw      =new int[]{0,7,14,105};

        int[][] specialCell= new int[][]{dl, tl,dw,tw    };
        String[] specialCellText= new String[]{"DL", "TL","DW","TW"    };
        E_cellBackground[] types=new E_cellBackground[]{E_cellBackground.blue_sky, E_cellBackground.blue, E_cellBackground.orange, E_cellBackground.magenta};

        for (int i=0;i<(15*15);i++){
            cell=new BoardCellData();
            cell.background= E_cellBackground.empty;
            cell.id=i;

            for(int a=0;a<types.length;a++){
                int[] array = specialCell[a];
                for(int id:array){
                    if(cell.id==id ||cell.id==224-id){
                        cell.background = types[a];
                        cell.text=specialCellText[a];
                    }
                }
            }

            if(i==112){
                cell.text="★";
                cell.background = E_cellBackground.start;
            }

            data.add(cell);
        }

        setBoard(15,15,data);
    }
}
