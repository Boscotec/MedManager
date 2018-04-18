package com.boscotec.medmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boscotec.medmanager.database.DbHelper;
import com.boscotec.medmanager.model.MedicineInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.shawnlin.numberpicker.NumberPicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import static com.boscotec.medmanager.TimeUtils.milHour;

public class MedicationAddActivity extends AppCompatActivity {
    private EditText mTitleText, mDescriptionText;
    private TextView mStartDateText, mTimeText, mEndDateText;
    private NumberPicker mIntervalPicker;
    private Calendar startCalender, endCalender;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mTitle, mDescription, mStartDate, mCurrentDate, mTime, mEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mTitleText = findViewById(R.id.medication_title);
        mDescriptionText = findViewById(R.id.medication_description);
        mStartDateText = findViewById(R.id.set_start_date);
        mTimeText = findViewById(R.id.set_start_time);
        mEndDateText = findViewById(R.id.set_end_date);
        mIntervalPicker = findViewById(R.id.set_interval);
        Button mSaveButton = findViewById(R.id.save);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_medication);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUp();
    }

    private void setUp(){
        Calendar mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DATE);

        mCurrentDate = TimeUtils.getCalenderDateString(mYear, mMonth, mDay);
        if (mMinute < 10) {
            mTime = mHour + ":" + "0" + mMinute;
        } else {
            mTime = mHour + ":" + mMinute;
        }

        // Setup Medication Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Medication Description EditText
        mDescriptionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDescription = s.toString().trim();
                mDescriptionText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup TextViews using values
        mStartDateText.setText(mCurrentDate);
        mEndDateText.setText(mCurrentDate);
        mTimeText.setText(mTime);
    }

    // On clicking Date picker
    public void setStartDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year; mMonth = monthOfYear; mDay = dayOfMonth;
                        mStartDate = TimeUtils.getCalenderDateString(year, monthOfYear, dayOfMonth);
                        mStartDateText.setText(mStartDate);
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    // On clicking Date picker
    public void setEndDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        mEndDate = TimeUtils.getCalenderDateString(year, monthOfYear, dayOfMonth);
                        mEndDateText.setText(mEndDate);
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    // On clicking Time picker
    public void setStartTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        mHour = hourOfDay; mMinute = minute;
                        if (minute < 10) {
                            mTime = hourOfDay + ":" + "0" + minute;
                        } else {
                            mTime = hourOfDay + ":" + minute;
                        }
                        mTimeText.setText(mTime);
                    }
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    private boolean validated(){
        mStartDate = mStartDateText.getText().toString();
        mTime = mTimeText.getText().toString();
        mEndDate = mEndDateText.getText().toString();
        mTitle = mTitleText.getText().toString();
        if (TextUtils.isEmpty(mTitle)){
             mTitleText.setError(getString(R.string.empty_title));
             mTitleText.requestFocus();
             return false;
        }
        mDescription = mDescriptionText.getText().toString();
        if(TextUtils.isEmpty(mDescription)){
            mDescriptionText.setError(getString(R.string.empty_description));
            mDescriptionText.requestFocus();
            return false;
        }
        startCalender = TimeUtils.getCalender(mStartDate);
        endCalender = TimeUtils.getCalender(mEndDate);
        Calendar currentCalender = TimeUtils.getCalender(mCurrentDate);

        if(startCalender.before(currentCalender)){ Toast.makeText(this, "Start Date can not be earlier than today", Toast.LENGTH_SHORT).show(); return false;}
        if(endCalender.before(currentCalender)){ Toast.makeText(this, "End Date can not be earlier than today", Toast.LENGTH_SHORT).show(); return false; }
        if(startCalender.after(endCalender)){ Toast.makeText(this, "End date shouldn't be earlier than Start date", Toast.LENGTH_SHORT).show(); return false; }
        return true;
    }

    public void saveToDatabase(View v){
        if(!validated()) return;

        String mEmail = GoogleSignIn.getLastSignedInAccount(this).getEmail();
        DbHelper db = new DbHelper(this);
        MedicineInfo info = new MedicineInfo(0, mEmail, mTitle, mDescription, mIntervalPicker.getValue(),
                startCalender.get(Calendar.DAY_OF_MONTH), startCalender.get(Calendar.MONTH), startCalender.get(Calendar.YEAR),
                endCalender.get(Calendar.DAY_OF_MONTH), endCalender.get(Calendar.MONTH), endCalender.get(Calendar.YEAR),
                mHour, mMinute);

        long ID = db.insert(info);
        if(ID > 0){
            mTitleText.setText("");
            mDescriptionText.setText("");
            // Set up calender for creating the notification
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.MONTH, mMonth);
            mCalendar.set(Calendar.YEAR, mYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
            mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            mCalendar.set(Calendar.MINUTE, mMinute);
            mCalendar.set(Calendar.SECOND, 0);
            // Create a new notification
            new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, (int) ID, mIntervalPicker.getValue() * milHour);
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        }else{ Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show(); }
        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}