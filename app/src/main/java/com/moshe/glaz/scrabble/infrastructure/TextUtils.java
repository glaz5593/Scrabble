package com.moshe.glaz.scrabble.infrastructure;

import com.moshe.glaz.scrabble.app.AppBase;

public class TextUtils {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean equals(String str1, String str2) {
        String s1 = str1 == null ? "" : str1;
        String s2 = str2 == null ? "" : str2;
        return s2.equals(s1);
    }

    public static String getHTMLText_green(String text) {
        return "<font color=\"#009400\">" + text + "</font>";
    }
    public static String getHTMLText_Header(String text,int size) {
        return "<H"+size+">" + text + "</H"+size+">";
    }
    public static String getHTMLText_red(String text) {
        return "<font color=\"#ba0c0c\">" + text + "</font>";
    }

    public static String getHTMLText_black(String text) {
        return "<font color=\"black\">" + text + "</font>";
    }

    public static String getHTMLText_blue(String text) {
        return "<font color=\"blue\">" + text + "</font>";
    }

    public static String getHTMLText_white(String text) {
        return "<font color=\"#ffffff\">" + text + "</font>";
    }
    public static String getHTMLEnter() {
        return "<BR>";
    }

    public static String getHTMLText_bold(String text) {
        return "<B>" + text + "</B>";
    }

    public static String getHTMLText_underline(String text) {
        return "<U>" + text + "</U>";
    }

    public static String getStringResorce(int resId) {
        return AppBase.getContext().getString(resId);
    }
}
