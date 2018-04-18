package com.boscotec.medmanager.model;

import com.boscotec.medmanager.interfaces.RecyclerItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Johnbosco on 24-Mar-18.
 */

public class MedicineInfo implements RecyclerItem {
    private long id;
    private String email, name, description;
    private int interval;
    private int startDay, startMonth, startYear;
    private int endDay, endMonth, endYear;
    private int timeHour, timeMinute;

    public MedicineInfo(long id, String email, String name, String description, int interval,
                        int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, int hour, int minute ){
        this.id = id;
        this.email = email;
        this.name = name;
        this.description = description;
        this.interval = interval;

        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;

        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;

        this.timeHour = hour;
        this.timeMinute = minute;
    }

    //getter
    public long getId() {
        return id;
    }
    public String getEmail() { return email; }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getInterval() {
        return interval;
    }
    public int getStartDay() {
        return startDay;
    }
    public int getStartMonth() {
        return startMonth;
    }
    public int getStartYear() {
        return startYear;
    }
    public int getEndDay() {
        return endDay;
    }
    public int getEndMonth() {
        return endMonth;
    }
    public int getEndYear() {
        return endYear;
    }
    public int getTimeHour() {
        return timeHour;
    }
    public int getTimeMinute() {
        return timeMinute;
    }
    public String getStartDate() { return convert(startYear, startMonth, startDay);}
    public String getEndDate() { return convert(endYear, endMonth, endDay); }
    private String convert(int year, int month, int day){
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(mCalendar.getTime());
    }

    @Override
    public int getRecyclerItemType() {
        return RecyclerItem.TYPE_MED;
    }
}
