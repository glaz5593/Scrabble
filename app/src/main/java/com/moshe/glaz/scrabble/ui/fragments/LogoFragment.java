package com.moshe.glaz.scrabble.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.ui.board.BoardCellData;
import com.moshe.glaz.scrabble.ui.board.BoardView;
import com.moshe.glaz.scrabble.ui.board.E_TextColor;
import com.moshe.glaz.scrabble.ui.board.E_TextSize;
import com.moshe.glaz.scrabble.ui.board.E_cellBackground;

import java.util.ArrayList;

public class LogoFragment extends Fragment {
    BoardView boardView;
    public LogoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logo, container, false);

        boardView = root.findViewById(R.id.boardView);
        initTitleBoard();
        return root;
    }

    private void initTitleBoard(){
        BoardCellData cell;
        ArrayList<BoardCellData> data=new ArrayList<>();

        for (int i=0;i<(3*3);i++){
            cell=new BoardCellData();
            cell.background= E_cellBackground.empty;
            cell.id=i;

            if(i==1){
                cell.text="DL";
                cell.background = E_cellBackground.blue_sky;
                cell.textColor = E_TextColor.green;
            }

            if(i==8){
                cell.text="TW";
                cell.background = E_cellBackground.magenta;
                cell.textColor = E_TextColor.green;
            }

            if(i == 3){
                cell.text="ס";
                cell.background = E_cellBackground.start;
                cell.textSize = E_TextSize.Big;
                cell.textColor = E_TextColor.black;
                cell.textBold=true;
            }

            data.add(cell);
        }

       boardView.setBoard(3,3,data);
    }

}