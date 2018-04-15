package com.boscotec.medmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.boscotec.medmanager.database.DbHelper;
import com.boscotec.medmanager.interfaces.RecyclerItem;
import com.boscotec.medmanager.model.MedicineInfo;

import java.util.Calendar;
import java.util.List;

import static com.boscotec.medmanager.TimeUtils.milHour;


public class BootReceiver extends BroadcastReceiver {

    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mReceivedID;
    private String mTitle;
    private String mDescription;
    private String mStartDate;
    private String mEndDate;
    private String mTime;
    private int mInterval;

    private Calendar mCalendar;
    private AlarmReceiver mAlarmReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            DbHelper db = new DbHelper(context);
            mCalendar = Calendar.getInstance();
            mAlarmReceiver = new AlarmReceiver();

            List<RecyclerItem> items = db.read();

            for (RecyclerItem rm : items) {
                MedicineInfo info = ((MedicineInfo) rm);

                mReceivedID = info.getId();
                mTitle = info.getName();
                mDescription = info.getDescription();
                mStartDate = info.getStartDate();
                mEndDate = info.getEndDate();
                //mTime =

                mHour = info.getTimeHour();
                mMinute = info.getTimeMinute();

                mInterval = info.getInterval() * (int) milHour;

                // mCalendar.set(Calendar.MONTH, mMonth);
                // mCalendar.set(Calendar.YEAR, mYear);
                // mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);

                // Cancel existing notification of the reminder by using its ID
                // mAlarmReceiver.cancelAlarm(context, (int) mReceivedID);

                // Create a new notification
                // mAlarmReceiver.setRepeatAlarm(context, mCalendar,(int) mReceivedID, mInterval);
                // mAlarmReceiver.setAlarm(context, mCalendar, (int) mReceivedID);
            }
        }
    }
}