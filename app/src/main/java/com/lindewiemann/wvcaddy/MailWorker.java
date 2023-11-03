package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MailWorker extends Worker {
    ContainerDbHelper _dbHelper = null;

    private int _sendHour = 0;
    private GregorianCalendar _lastSentDate = null;
    private final String _dateFormat = "yyyy-MM-dd HH:mm:ss";

    public MailWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        _dbHelper = new ContainerDbHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            getSettings();

            GregorianCalendar nowDate = new GregorianCalendar();
            int year = nowDate.get(Calendar.YEAR);
            int month = nowDate.get(Calendar.MONTH);
            int day = nowDate.get(Calendar.DAY_OF_MONTH);

            GregorianCalendar refDate = new GregorianCalendar(year, month, 1, _sendHour, 0, 0);

            boolean isSend = true;
            if (day == 1 && _sendHour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                if (_lastSentDate != null) {
                    if (_lastSentDate.after(refDate)) {
                        isSend = false;
                    }
                }
            } else {
                if (_lastSentDate != null) {
                    GregorianCalendar dayCal = _lastSentDate;

                    if (dayCal.after(refDate)) {
                        isSend = false;
                    }
                }
            }

            if (isSend) {
                GregorianCalendar startDate = new GregorianCalendar();
                startDate.add(Calendar.MONTH, -1);
                startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);

                GregorianCalendar endDate = new GregorianCalendar();
                endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);
                endDate.add(Calendar.MONTH, 1);


                try {
                    new ItemListMailer(
                            getApplicationContext(),
                            true,
                            startDate,
                            endDate,
                            null
                    ).sendMail();
                    setCheckStamp();
                } catch (Exception e) {
                    setMailStatus(e.getMessage());
                    new ErrorHandler().LogError(getApplicationContext(), e);
                    return Result.failure();
                }

                setMailStatus("Odesláno úspěšně");
            }

        } catch(Exception e) {
            setMailStatus(e.getMessage());
            new ErrorHandler().LogError(getApplicationContext(), e);
        }
        return Result.success();
    }

    private void getSettings() {
        Cursor cursor = getVwCaddyCursor();
        int iHour = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            _sendHour = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR));
            String strSentDate = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE));
            if(strSentDate != null) {
                try {
                    _lastSentDate = stringToDate(strSentDate, _dateFormat);
                } catch(Exception e) {
                    _lastSentDate = null;
                }
            }
        }
    }

    private void setCheckStamp() {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat spf=new SimpleDateFormat(_dateFormat);
        spf= new SimpleDateFormat(_dateFormat);
        //String strDate = spf.format(date);
        String strDate
                = spf.format(
                gc.getTime());

        Cursor cursor = getVwCaddyCursor();
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE, strDate);
        if (cursor.getCount() == 0) {
            db.insert(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, null, values);
        } else {
            cursor.moveToFirst();
            db.update(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, values, null, null);
        }
    }

    private void setMailStatus(String strMsg) {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat spf=new SimpleDateFormat(_dateFormat);
        spf= new SimpleDateFormat(_dateFormat);
        //String strDate = spf.format(date);
        String strDate
                = spf.format(
                gc.getTime());

        Cursor cursor = getVwCaddyCursor();
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_AUTO_MAIL_STATUS, strDate + ": " + strMsg);
        if (cursor.getCount() == 0) {
            db.insert(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, null, values);
        } else {
            cursor.moveToFirst();
            db.update(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, values, null, null);
        }
    }

    private Cursor getVwCaddyCursor() {

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        return db.query(
                LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                null,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null);                               // The sort order

    }


    private GregorianCalendar stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        Calendar cal = new GregorianCalendar();
        cal.setTime(stringDate);
        return (GregorianCalendar)cal;

    }
}
