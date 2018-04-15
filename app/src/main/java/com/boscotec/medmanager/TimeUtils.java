package com.boscotec.medmanager;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TimeUtils {

    public static String formatDateString(String dateString) {
        return dateString.replace('-', '/');
    }

    public static long getStartOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    @SuppressLint("DefaultLocale")
    public static String formatTime(float sec) {
        int minutes = (int) (sec / 60);
        int seconds = (int) (sec % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    static String getCalenderDateString(int year, int month, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return dateFormat.format(calendar.getTime());
    }

    static Calendar getCalender(String dateString) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(dateString);
        calendar.setTime(date);
        return calendar;
    }

    public static String getMonthString(int month){
        switch(month){
            case 0: return "JANUARY";
            case 1: return "FEBRUARY";
            case 2: return "MARCH";
            case 3: return "APRIL";
            case 4: return "MAY";
            case 5: return "JUNE";
            case 6: return "JULY";
            case 7: return "AUGUST";
            case 8: return "SEPTEMBER";
            case 9: return "OCTOBER";
            case 10: return "NOVEMBER";
            case 11: return "DECEMBER";
        }
        return "";
    }

}
