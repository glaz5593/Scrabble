package com.moshe.glaz.scrabble.infrastructure;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.app.AppBase;
import com.moshe.glaz.scrabble.ui.adapters.AvatarAdapter;
import com.moshe.glaz.scrabble.ui.board.BoardCellData;
import com.moshe.glaz.scrabble.ui.board.CellViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class UIUtils {

    public static int getScreenWidthInPX(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getDP(int px) {
        return (int) (px / AppBase.getContext().getResources().getDisplayMetrics().density);
    }

    public static void showToast(int message) {
        showToast(TextUtils.getStringResorce(message));
    }

    public static void showToast(String message) {
        Toast.makeText(AppBase.getContext(), message, Toast.LENGTH_SHORT).show();
    }




    public static AlertDialog showStatusDialog(Activity activity,int gender, StringDialogListener listener) {
        String[] array=null;
        if(gender == 2){
            array=AppBase.getContext().getResources().getStringArray(R.array.avatars_female);
        }else{
            array=AppBase.getContext().getResources().getStringArray(R.array.avatars_male);
        }

        return showStringGridDialog(activity,array,listener);
    }


    public interface StringDialogListener {
        void onSelectValue(String value);
    }

    public static AlertDialog showStringGridDialog(Activity activity,String[] array, StringDialogListener listener) {
        GridView gridView = new GridView(activity);
        gridView.setNumColumns(3);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(gridView);
        builder.setTitle(R.string.title_dialog_avatar);
        final AlertDialog avatarDialog = builder.show();
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(activity,R.layout.item_grid_string,  R.id.tv_text, array);
        gridView.setAdapter(itemsAdapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            avatarDialog.dismiss();
            listener.onSelectValue(array[position]);
        });
        return avatarDialog;
    }
    public interface AvatarDialogListener {
        void onSelectAvatar(int avatarId);
    }
    public static void showAvatarDialog(Activity activity, AvatarDialogListener listener) {
        GridView gridView = new GridView(activity);
        gridView.setNumColumns(3);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(gridView);
        builder.setTitle(R.string.title_dialog_avatar);
        final AlertDialog avatarDialog = builder.show();


        gridView.setAdapter(new AvatarAdapter(new AvatarAdapter.onSelectListener() {
            @Override
            public void onSelectAvatar(Integer avatarId) {
                avatarDialog.dismiss();
                listener.onSelectAvatar(avatarId);
            }
        }));
    }

    public interface runOnUIListener {
        void run();
    }

    public static void runOnUI(runOnUIListener listener) {
        runOnUI(0, listener);
    }

    public static void runOnUI(int timeout, runOnUIListener listener) {
        try {
            if (timeout > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.sleep(timeout);
                        runOnUI(0, listener);
                    }
                }).start();
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getColor(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return AppBase.getContext().getResources().getColor(resId, AppBase.getContext().getTheme());
        } else {
            return AppBase.getContext().getResources().getColor(resId);
        }
    }

    public static void showSnackbar(View view, int message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.close, v -> {

                })
                .setActionTextColor(AppBase.getContext().getResources().getColor(R.color.white))
                .show();
    }

    public static void showDialogResult_YesNo(int title, int message, int yesText, int NoText, boolean cancelable, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {
        Context context = AppBase.getContext();
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(title));
        alertDialog.setMessage("\n" + context.getString(message) + "\n");
        alertDialog.setCancelable(cancelable);
        alertDialog.setPositiveButton(context.getString(yesText), yesListener);
        alertDialog.setNegativeButton(context.getString(NoText), noListener);
        alertDialog.create().show();
    }


    public static void setStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.status_bar_color));
        }
    }
}
