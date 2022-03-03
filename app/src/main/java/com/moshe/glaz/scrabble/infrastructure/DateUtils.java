package com.moshe.glaz.scrabble.infrastructure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static SimpleDateFormat format_dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public static SimpleDateFormat format_dd_MM_HH_mm = new SimpleDateFormat("dd/MM HH:mm", Locale.ENGLISH);
    public static SimpleDateFormat format_HH_mm = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    public static SimpleDateFormat format_HH_mm_ss = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    public static String getTime(Date date) {
        if (date == null)
            return "";

        return format_HH_mm.format(date);
    }
    public static String getFullTime(Date date) {
        if (date == null)
            return "";

        return format_HH_mm_ss.format(date);
    }
    public static Date getCurrentDate() {
        return new Date();
    }public static String getCurrentTime() {
        return getTime(getCurrentDate());
    }
    public static String getTimeOrDateTime(Date date) {
        if (date == null) {
            return "";
        }

        String currentDate = getDate(getCurrentDate());
        String dDate = getDate(date);

        if (TextUtils.equals(currentDate, dDate)) {
            return format_HH_mm.format(date);
        }

        return format_dd_MM_HH_mm.format(date);
    }
    public static String getTimeOrDate(Date date) {
        if (date == null) {
            return "";
        }

        String currentDate = getDate(getCurrentDate());
        String dDate = getDate(date);

        if (TextUtils.equals(currentDate, dDate)) {
            return format_HH_mm.format(date);
        }

        return format_dd_MM_yyyy.format(date);
    }
    public static String getDate(Date date) {
        if (date == null)
            return "";

        return format_dd_MM_yyyy.format(date);
    }
    public static Date getDate(String date, String format) {
        if (date == null) {
            return null;
        }

        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Exception ex) {
        }

        return null;
    }
}
