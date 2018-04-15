package com.boscotec.medmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.boscotec.medmanager.TimeUtils;
import com.boscotec.medmanager.interfaces.RecyclerItem;
import com.boscotec.medmanager.model.MedicineInfo;
import com.boscotec.medmanager.model.Month;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Johnbosco on 23-Mar-18.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
    private Context context;
    private static final String DATABASE_NAME = "MedManagerDB.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate...");
        db.execSQL(CREATE_MED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationName = String.format(Locale.getDefault(), "from_%d_to_%d.sql", i, (i + 1));
            Log.d(TAG, "Looking for migration file: " + migrationName);
            readAndExecuteSQLScript(db, context, migrationName);
        }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open("database"+ File.separator+fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }
    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static final String MED_TABLE_NAME = "table_med";
    private static final String COLUMN_ID = "_id";

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_INTERVAL = "interval";

    private static final String COLUMN_START_DATE_DAY = "start_date_day";
    private static final String COLUMN_START_DATE_MONTH = "start_date_month";
    private static final String COLUMN_START_DATE_YEAR = "start_date_year";

    private static final String COLUMN_END_DATE_DAY = "end_date_day";
    private static final String COLUMN_END_DATE_MONTH = "end_date_month";
    private static final String COLUMN_END_DATE_YEAR = "end_date_year";

    private static final String COLUMN_TIME_HOUR = "time_hour";
    private static final String COLUMN_TIME_MINUTES = "time_minute";

    private static final String CREATE_MED_TABLE = "create table "
            + MED_TABLE_NAME + " ("
            + COLUMN_ID + " integer primary key autoincrement , "

            + COLUMN_NAME + " text not null , "
            + COLUMN_DESCRIPTION + " text not null , "
            + COLUMN_IMAGE + " text null , "
            + COLUMN_INTERVAL + " integer not null , "

            + COLUMN_START_DATE_DAY + " integer not null , "
            + COLUMN_START_DATE_MONTH + " integer not null , "
            + COLUMN_START_DATE_YEAR + " integer not null , "

            + COLUMN_END_DATE_DAY + " integer not null , "
            + COLUMN_END_DATE_MONTH + " integer not null , "
            + COLUMN_END_DATE_YEAR + " integer not null , "

            + COLUMN_TIME_HOUR + " integer not null , "
            + COLUMN_TIME_MINUTES + " integer not null);";

    public long insert(MedicineInfo info){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, info.getName());
        values.put(COLUMN_DESCRIPTION, info.getDescription());
        values.put(COLUMN_IMAGE, info.getDrugPix());
        values.put(COLUMN_INTERVAL, info.getInterval());

        values.put(COLUMN_START_DATE_DAY, info.getStartDay());
        values.put(COLUMN_START_DATE_MONTH, info.getStartMonth());
        values.put(COLUMN_START_DATE_YEAR, info.getStartYear());

        values.put(COLUMN_END_DATE_DAY, info.getEndDay());
        values.put(COLUMN_END_DATE_MONTH, info.getEndMonth());
        values.put(COLUMN_END_DATE_YEAR, info.getEndYear());

        values.put(COLUMN_TIME_HOUR, info.getTimeHour());
        values.put(COLUMN_TIME_MINUTES, info.getTimeMinute());

        return db.insert(MED_TABLE_NAME, null, values);
    }

    public List<RecyclerItem> read(){
        SQLiteDatabase db = this.getWritableDatabase();

        List<RecyclerItem> medicineInfos = new ArrayList<>();
        int lastMonth = -1;
        Cursor cursor = db.query(MED_TABLE_NAME, null, null, null, null, null, COLUMN_START_DATE_MONTH);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                int thisMonth = cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_MONTH));

                MedicineInfo info = new MedicineInfo();
                info.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                info.setInterval(cursor.getInt(cursor.getColumnIndex(COLUMN_INTERVAL)));
                info.setDrugPix(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));

                info.setStartDay(cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_DAY)));
                info.setStartMonth(cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_MONTH)));
                info.setStartYear(cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_YEAR)));

                info.setEndDay(cursor.getInt(cursor.getColumnIndex(COLUMN_END_DATE_DAY)));
                info.setEndMonth(cursor.getInt(cursor.getColumnIndex(COLUMN_END_DATE_MONTH)));
                info.setEndYear(cursor.getInt(cursor.getColumnIndex(COLUMN_END_DATE_YEAR)));

                info.setTimeHour(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_HOUR)));
                info.setTimeMinute(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_MINUTES)));

                if(lastMonth != thisMonth){
                    Month month = new Month();
                    month.setName(TimeUtils.getMonthString(thisMonth));
                    medicineInfos.add(month);
                    lastMonth = thisMonth;
                }

                medicineInfos.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return medicineInfos;
    }

    public MedicineInfo read(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MED_TABLE_NAME, null, COLUMN_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        MedicineInfo info = new MedicineInfo();
        info.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        info.setInterval(cursor.getInt(cursor.getColumnIndex(COLUMN_INTERVAL)));
        info.setDrugPix(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));

        info.setStartDay(cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_DAY)));
        info.setStartMonth(cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_MONTH)));
        info.setStartYear(cursor.getInt(cursor.getColumnIndex(COLUMN_START_DATE_YEAR)));

        info.setEndDay(cursor.getInt(cursor.getColumnIndex(COLUMN_END_DATE_DAY)));
        info.setEndMonth(cursor.getInt(cursor.getColumnIndex(COLUMN_END_DATE_MONTH)));
        info.setEndYear(cursor.getInt(cursor.getColumnIndex(COLUMN_END_DATE_YEAR)));

        info.setTimeHour(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_HOUR)));
        info.setTimeMinute(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_MINUTES)));

        return info;
    }

    public int getMedicationCount(){
        String countQuery = "SELECT * FROM " + MED_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        cursor.close();
        return cursor.getCount();
    }

    public int update(MedicineInfo info){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, info.getName());
        values.put(COLUMN_DESCRIPTION, info.getDescription());
        values.put(COLUMN_IMAGE, info.getDrugPix());
        values.put(COLUMN_INTERVAL, info.getInterval());

        values.put(COLUMN_START_DATE_DAY, info.getStartDay());
        values.put(COLUMN_START_DATE_MONTH, info.getStartMonth());
        values.put(COLUMN_START_DATE_YEAR, info.getStartYear());

        values.put(COLUMN_END_DATE_DAY, info.getEndDay());
        values.put(COLUMN_END_DATE_MONTH, info.getEndMonth());
        values.put(COLUMN_END_DATE_YEAR, info.getEndYear());

        values.put(COLUMN_TIME_HOUR, info.getTimeHour());
        values.put(COLUMN_TIME_MINUTES, info.getTimeMinute());

        return db.update(MED_TABLE_NAME, values, COLUMN_ID +"=?", new String[]{String.valueOf(info.getId())});
    }

    public void delete(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(MED_TABLE_NAME, COLUMN_ID+"=?", new String[]{String.valueOf(id)});
        db.close();
    }
}