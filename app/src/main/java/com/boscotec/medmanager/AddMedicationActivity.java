package com.boscotec.medmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.boscotec.medmanager.database.DbHelper;
import com.boscotec.medmanager.model.MedicineInfo;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedicationActivity extends AppCompatActivity implements View.OnClickListener {
    private Calendar mCalendar = Calendar.getInstance();
    private EditText mName, mDescription, mStartDate, mEndDate;
    private NumberPicker mInterval;
    int mYear, mMonth, mDay, mHour, mMinute;
    String name, description, startDate, endDate;
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mName = findViewById(R.id.name);
        mDescription = findViewById(R.id.description);
        mInterval = findViewById(R.id.interval);
        mStartDate = findViewById(R.id.startDate);
        mEndDate = findViewById(R.id.endDate);

        findViewById(R.id.btnStartDate).setOnClickListener(this);
        findViewById(R.id.btnStopDate).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStartDate:
                pickDate();

                // Launch Date Picker Dialog
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        mStartDate.setText(sdf.format(mCalendar.getTime()));
                    }
                }, mYear, mMonth, mDay).show();
                break;
            case R.id.btnStopDate:
                pickDate();

                // Launch Date Picker Dialog
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        mEndDate.setText(sdf.format(mCalendar.getTime()));
                    }
                }, mYear, mMonth, mDay).show();
                break;
            case R.id.save:
                saveToDatabase();
                break;
        }
    }

    private void pickDate(){
        // Get Current Date and Time
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        // new TimePickerDialog(this, this, mHour, mMinute, false).show();
    }

    private void saveToDatabase(){
        if(!validated()) return;

        Toast.makeText(this, "Sucessful", Toast.LENGTH_SHORT).show();


        /*DbHelper db = new DbHelper(this);

        Date sDate = new Date(startDate);
        mCalendar.setTime(sDate);
        int startYear = mCalendar.get(Calendar.YEAR);
        int startMonth = mCalendar.get(Calendar.MONTH);
        int startDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        Date eDate = new Date(endDate);
        mCalendar.setTime(eDate);
        int endYear = mCalendar.get(Calendar.YEAR);
        int endMonth = mCalendar.get(Calendar.MONTH);
        int endDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        MedicineInfo info = new MedicineInfo();
        info.setName(name);
        info.setDescription(description);
        info.setDrugPix(null);
        info.setInterval(num);

        info.setStartDay(startDay);
        info.setStartMonth(startMonth);
        info.setStartYear(startYear);

        info.setEndDay(endDay);
        info.setEndMonth(endMonth);
        info.setEndYear(endYear);

        if(db.insert(info) > 0){
            Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show();
            mName.setText("");
            mDescription.setText("");
            mStartDate.setText("");
            mEndDate.setText("");
        }else{
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        db.close();
        */
    }

    private boolean validated(){
        num = mInterval.getValue();
        name = mName.getText().toString();
        description = mDescription.getText().toString();
        startDate = mStartDate.getText().toString();
        endDate = mEndDate.getText().toString();
/*
        if(TextUtils.isEmpty(name)){
            mName.setError(getString(R.string.empty_name));
            return false;
        }else{
            mName.setError(null);
        }

        if(TextUtils.isEmpty(description)){
            mDescription.setError(getString(R.string.empty_description));
            return false;
        }else{
            mDescription.setError(null);
        }

        if(TextUtils.isEmpty(startDate)){
            mStartDate.setError(getString(R.string.empty_field));
            return false;
        } else{
            mStartDate.setError(null);
        }

        if(TextUtils.isEmpty(endDate)){
            mEndDate.setError(getString(R.string.empty_field));
            return false;
        } else{
            mEndDate.setError(null);
        }
*/

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String formattedDateString = formatter.format(currentDate);
        //mTodayDate.setText(formattedDateString);
        Toast.makeText(this, formattedDateString, Toast.LENGTH_SHORT).show();


        // Date startTime = new Date(startDate);
      //  Toast.makeText(this, startTime.toString(), Toast.LENGTH_SHORT).show();

      //  Date endTime = Calendar.getInstance().getTime();
      //  Toast.makeText(this, endTime.toString(), Toast.LENGTH_SHORT).show();

      //  Date currentTime = Calendar.getInstance().getTime();
//        long d = Calendar.getInstance().getTimeInMillis();
      //  Toast.makeText(this, currentTime.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, String.valueOf(currentTime.getTime()), Toast.LENGTH_SHORT).show();


        /* TODO
        if(sDate.before(nDate)){
            Toast.makeText(this, "Start Date can not be earlier than today", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(eDate.before(nDate)){
            Toast.makeText(this, "End Date can not be earlier than today", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(sDate.after(eDate)){
            Toast.makeText(this, "End date shouldn't be earlier than Start date", Toast.LENGTH_SHORT).show();
           return false;
        }
        */

        return true;
    }

}