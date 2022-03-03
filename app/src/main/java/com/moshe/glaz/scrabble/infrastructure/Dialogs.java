package com.moshe.glaz.scrabble.infrastructure;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.moshe.glaz.scrabble.R;
import com.moshe.glaz.scrabble.ui.dialog.ColorDialog;

public class Dialogs {
    public interface OnDialogMessageClick {
        void onClick(int index);
    }

    public static void showDialogMessage(Activity activity, String title, String message, String[] buttons, OnDialogMessageClick listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(buttons.length == 0);

        if (buttons.length > 0) {
            builder.setPositiveButton(buttons[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(0);
                }
            });
        }

        if (buttons.length > 1) {
            builder.setNegativeButton(buttons[1], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(1);
                }
            });
        }

        if (buttons.length > 2) {
            builder.setNeutralButton(buttons[2], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(2);
                }
            });
        }

        builder.create().show();
    }

    public interface ListDialogListener {
        void onSelectValue(String value, int index);

        void onClickButton(int index);
    }

    public static void showListDialog(Activity activity, String title, String[] buttons, final String[] array, final ListDialogListener listener) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_singlechoice, array);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setAdapter(adapter, (dialog, index) -> {
            listener.onSelectValue(array[index], index);
        });

        if (buttons.length > 0) {
            builder.setPositiveButton(buttons[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClickButton(0);
                }
            });
        }

        if (buttons.length > 1) {
            builder.setNegativeButton(buttons[1], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClickButton(1);
                }
            });
        }

        if (buttons.length > 2) {
            builder.setNeutralButton(buttons[2], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClickButton(2);
                }
            });
        }

        builder.create().show();
    }

    public interface OnDialogInputTextChange {
        void onTextChange(String text);
    }

    public static void showInputTextDialog(Activity activity, String title, String text, boolean isNumeric, OnDialogInputTextChange listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setCancelable(true);

        final EditText input = new EditText(activity);
        input.setText(text);
        input.setInputType(isNumeric ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            listener.onTextChange(input.getText().toString());
        });
        builder.setNeutralButton(R.string.clear, (dialog, which) -> {
            listener.onTextChange("");
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    //examples

    public static void showColorDialog(FragmentActivity activity) {
        ColorDialog.show(activity, new ColorDialog.ColorDialogListener() {
            @Override
            public void onColorSelect(int red, int green, int blue) {
                // do something...
            }
        });
    }

    public static void showMessageDialog(FragmentActivity activity) {
        Dialogs.showDialogMessage(
                activity,
                "כותרת",
                "הודעה ארוכה",
                new String[]{"אפשרות 1", "אפשרות 0"},
                new Dialogs.OnDialogMessageClick() {
                    @Override
                    public void onClick(int index) {
                        switch (index) {
                            case 0: {
                                // do something...
                                break;
                            }
                            case 1: {
                                // do something...
                                break;
                            }
                        }
                    }
                }
        );

    }

    public static void showInputTextDialog(FragmentActivity activity, String name) {

        String[] buttons = new String[]{
                "אישור",
                "ביטול"
        };
        Dialogs.showInputTextDialog(activity, "תיאור התוכן", name, false, new Dialogs.OnDialogInputTextChange() {
            public void onTextChange(String text) {
                // Do something...
            }
        });
    }

    public static void showListDialog(FragmentActivity activity) {
        String[] array = new String[]{
                "שילת",
                "אלישבע",
                "שרי",
                "הודיה",
                "ציפי",
                "צופיה"
        };
        String[] buttons = new String[]{
                "אפשרות 0",
                "אפשרות 1",
                "אפשרות2"
        };
        Dialogs.showListDialog(activity, "בחר מתכנתת", buttons, array, new Dialogs.ListDialogListener() {
            @Override
            public void onSelectValue(String value, int index) {
                // Do something...
            }

            @Override
            public void onClickButton(int index) {
                switch (index) {
                    case 0: {
                        // Do something...
                        break;
                    }
                    case 1: {
                        // Do something...
                        break;
                    }
                    case 2: {
                        // Do something...
                        break;
                    }
                }
            }
        });
    }
}
