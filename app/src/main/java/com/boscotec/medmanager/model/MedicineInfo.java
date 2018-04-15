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
    private String drugPix, name, description;
    private int interval;
    private int startDay, startMonth, startYear;
    private int endDay, endMonth, endYear;
    private int timeHour, timeMinute;

    public MedicineInfo(){}

    //getter
    public long getId() {
        return id;
    }
    public String getDrugPix() { return drugPix; }
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
    public String getStartDate() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(startYear, startMonth, startDay);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(mCalendar.getTime());
    }
    public String getEndDate() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(endYear, endMonth, endDay);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(mCalendar.getTime());
    }

    //setter
    public void setId(long id) {
        this.id = id;
    }
    public void setDrugPix(String drugPix) {this.drugPix = drugPix;}
    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}
    public void setInterval(int interval) {this.interval = interval;}
    public void setStartDay(int day) {this.startDay = day;}
    public void setStartMonth(int month) {this.startMonth = month;}
    public void setStartYear(int year) {this.startYear = year;}
    public void setEndDay(int day) {this.endDay = day;}
    public void setEndMonth(int month) {this.endMonth = month;}
    public void setEndYear(int year) {this.endYear = year;}
    public void setTimeHour(int hour) {this.timeHour = hour;}
    public void setTimeMinute(int minute) {this.timeMinute = minute;}

    @Override
    public int getRecyclerItemType() {
        return RecyclerItem.TYPE_MED;
    }
}
