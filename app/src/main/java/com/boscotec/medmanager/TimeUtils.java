package com.boscotec.medmanager;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TimeUtils {
    private static HashMap<Integer, String> monthMap;

    public static String formatDateString(String dateString) {
        return dateString.replace('-', '/');
    }

    public static String getCalenderDateStrings(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateString));
        return dateFormat.format(calendar.getTime());
    }

    public static Calendar getCalenderDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateFormat.parse(dateString);
            calendar.setTime(date);
        }catch(Exception e){
            e.printStackTrace();
        }
        return calendar;
    }

    public static String getDateStringFromLongDateInYYYYMMDDFormat(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateFormat.format(calendar.getTime());
    }

    public static long getLongDateFromDateString(String dateString) {
        long longDate = 0;
        if (dateString == null || dateString.equals(BuildConfig.FLAVOR)) return longDate;

        try {
            longDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return longDate;
    }

    public static String getDateStringFromLongDateInDDMMYYYYFormat(String dateString) {
        String dateValue = BuildConfig.FLAVOR;
        if (dateString == null || dateString.equalsIgnoreCase(BuildConfig.FLAVOR)) return dateValue;

        Long date = getLongDateFromDateString(dateString);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateFormat.format(calendar.getTime());
    }

    public static String getDateStringFromLongDateInDDMMYYYYFormat(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateFormat.format(calendar.getTime());
    }

    public static String formatDateFromString(String inputFormat, String outputFormat, String inputDate) {
        String outputDate = BuildConfig.FLAVOR;
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        try {
            outputDate = new SimpleDateFormat(outputFormat, Locale.getDefault()).format(df_input.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
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

    @SuppressLint({"UseSparseArrays"})
    public static HashMap<Integer, String> getMonthMap() {
        if (monthMap == null) { monthMap = new HashMap<>();}

        monthMap.put(0, "JAN");
        monthMap.put(1, "FEB");
        monthMap.put(2, "MAR");
        monthMap.put(3, "APR");
        monthMap.put(4, "MAY");
        monthMap.put(5, "JUN");
        monthMap.put(6, "JUL");
        monthMap.put(7, "AUG");
        monthMap.put(8, "SEP");
        monthMap.put(9, "OCT");
        monthMap.put(10, "NOV");
        monthMap.put(11, "DEC");

        return monthMap;
    }

}
