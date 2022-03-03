package com.moshe.glaz.scrabble.ui.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.app.AppBase;

public class ColorDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    View dialogView;
    ColorDialogListener listener;
    int red = 255, green = 255, blue = 255;

    SeekBar sb_red, sb_green, sb_blue;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_red: {
                red = progress;
                break;
            }
            case R.id.sb_green: {
                green = progress;
                break;
            }
            case R.id.sb_blue: {
                blue = progress;
                break;
            }
        }

        updateUi();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ColorDialogListener {
        void onColorSelect(int red, int green, int blue);
    }

    Color color;

    ColorDialog(ColorDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = LayoutInflater.from(AppBase.getContext()).inflate(R.layout.colored_dialog, null, false);
        setCancelable(true);

        dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            listener.onColorSelect(red, green, blue);
            dismiss();
        });
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
        });
        sb_red = dialogView.findViewById(R.id.sb_red);
        sb_green = dialogView.findViewById(R.id.sb_green);
        sb_blue = dialogView.findViewById(R.id.sb_blue);

        sb_red.setOnSeekBarChangeListener(this);
        sb_green.setOnSeekBarChangeListener(this);
        sb_blue.setOnSeekBarChangeListener(this);

        sb_red.setProgress(255);
        sb_green.setProgress(255);
        sb_blue.setProgress(255);


        getDialog().getWindow().setLayout(400, 400);

        return dialogView;
    }


    public static void show(FragmentActivity activity, ColorDialogListener listener) {
        ColorDialog dialog = new ColorDialog(listener);
        dialog.show(activity.getSupportFragmentManager(), "ColorDialog");
    }

    void updateUi() {
        ((TextView) dialogView.findViewById(R.id.tv_red)).setText(red + "");
        ((TextView) dialogView.findViewById(R.id.tv_green)).setText(green + "");
        ((TextView) dialogView.findViewById(R.id.tv_blue)).setText(blue + "");

        int color = Color.rgb(red, green, blue);
        dialogView.findViewById(R.id.v_selected_color).setBackgroundColor(color);
    }
}
