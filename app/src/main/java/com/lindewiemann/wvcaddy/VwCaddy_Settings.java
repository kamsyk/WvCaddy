package com.lindewiemann.wvcaddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VwCaddy_Settings extends AppCompatActivity {
    private final String WORKER_TAG = "MailWorkerTag";

    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
    boolean _isAuthorized = false;
    String _pwd = null;
    private int _origHour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_vw_caddy_settings);
        init();
    }

    private void init() {
        Cursor cursor = getVwCaddyCursor();
        int iHour = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ((EditText) findViewById(R.id.txtSender)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER)));
            ((EditText) findViewById(R.id.txtRecipients)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS)));
            //((EditText) findViewById(R.id.txtMailjetApiKey)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY)));
            //((EditText) findViewById(R.id.txtMailjetSecretKey)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY)));
            ((EditText) findViewById(R.id.txtGMailAppPwd)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_GMAILPASSWORD)));
            _pwd = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_PASSWORD));
            iHour = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR));
            _origHour = iHour;
            //((TextView) findViewById(R.id.txtLastAutoCheckDate)).setText("Poslední kontrola automatického odeslání: " + cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE)));
            ((TextView) findViewById(R.id.txtLastAutomMailStatus)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_AUTO_MAIL_STATUS)));
        }

        if (_pwd == null || _pwd.trim().length() == 0) {
            LinearLayout llPwd = findViewById(R.id.llPwd);
            llPwd.setVisibility(View.GONE);
            LinearLayout llOldPwd = findViewById(R.id.llOldPwd);
            llOldPwd.setVisibility(View.GONE);
            TextView tvOldPwdLabel = findViewById(R.id.tvOldPwdLabel);
            tvOldPwdLabel.setVisibility(View.GONE);
        } else {
            LinearLayout llSetPwd = findViewById(R.id.llSetPwd);
            LinearLayout llPwd = findViewById(R.id.llPwd);
            LinearLayout llContent = findViewById(R.id.llContent);

            llSetPwd.setVisibility(View.GONE);

        }

        List<String> hours = new ArrayList<String>();
        hours.add("Neodesílat");
        for (int i = 1; i < 25; i++) {
            hours.add(String.valueOf(i) + " hod.");
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hours);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinHour);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(iHour);
    }

    public void login(View view) {
        EditText txtPassword = findViewById(R.id.txtPassword);
        String enteredPwd = txtPassword.getText().toString();
        if(_pwd.equals(enteredPwd)) {
            LinearLayout llSetPwd = findViewById(R.id.llSetPwd);
            LinearLayout llPwd = findViewById(R.id.llPwd);
            LinearLayout llContent = findViewById(R.id.llContent);

            llSetPwd.setVisibility(View.GONE);
            llPwd.setVisibility(View.GONE);
            llContent.setVisibility(View.VISIBLE);

            return;
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Přístup byl zamítnut");
            builder1.setMessage("Chybně zadané heslo");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();

            return;
        }
    }

    public void changePassword(View view) {
        LinearLayout llSetPwd = findViewById(R.id.llSetPwd);
        LinearLayout llPwd = findViewById(R.id.llPwd);
        LinearLayout llContent = findViewById(R.id.llContent);

        llSetPwd.setVisibility(View.VISIBLE);
        llPwd.setVisibility(View.GONE);
        llContent.setVisibility(View.GONE);
    }

    public void setNewPassword(View view) {
        EditText txtNewPassword = findViewById(R.id.txtNewPassword);
        EditText txtNewPasswordConfirm = findViewById(R.id.txtNewPasswordConfirm);

        String strPwd = txtNewPassword.getText().toString();
        String strPwd1 = txtNewPasswordConfirm.getText().toString();

        if(_pwd != null) {
            EditText txtOldPassword = findViewById(R.id.txtOldPassword);
            String origPwd = txtOldPassword.getText().toString();
            if (!_pwd.equals(origPwd)) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Nastavení nebylo uloženo");
                builder1.setMessage("Chybně zadané aktuální heslo");
                builder1.setCancelable(true);
                builder1.setNeutralButton("Zavřít",
                        (DialogInterface dialog, int id) ->
                                dialog.cancel()
                );

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return;
            }
        }

        if (!strPwd.equals(strPwd1)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Nastavení nebylo uloženo");
            builder1.setMessage("Zadaná hesla se neshodují");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();

            return;
        }

        if (strPwd.trim().length() < 4) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Nastavení nebylo uloženo");
            builder1.setMessage("Minimální délka hesla jsou 4 znaky");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();

            return;
        }


        _pwd = ((EditText)findViewById(R.id.txtNewPassword)).getText().toString();

        Cursor cursor = getVwCaddyCursor();
        if (cursor.getCount() == 0) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_PASSWORD, _pwd);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, null, values);
            //db.close();
        } else {
            cursor.moveToFirst();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            // on below line we are passing all values
            // along with its key and value pair.
            values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_PASSWORD, _pwd);

            // on below line we are calling a update method to update our database and passing our values.
            // and we are comparing it with name of our course which is stored in original name variable.
            db.update(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, values, null, null);
            //db.close();
        }

        Toast.makeText(
                getApplicationContext(),
                "Data byla uložena",
                Toast.LENGTH_SHORT).show();

        LinearLayout llSetPwd = findViewById(R.id.llSetPwd);
        LinearLayout llPwd = findViewById(R.id.llPwd);
        LinearLayout llContent = findViewById(R.id.llContent);

        llSetPwd.setVisibility(View.GONE);
        llPwd.setVisibility(View.GONE);
        llContent.setVisibility(View.VISIBLE);
    }

    public void save(View view) {
        try {
            String strSender = ((EditText) findViewById(R.id.txtSender)).getText().toString();
            String strRecipients = ((EditText) findViewById(R.id.txtRecipients)).getText().toString();
            //String strMailjetApiKey = ((EditText) findViewById(R.id.txtMailjetApiKey)).getText().toString();
            //String strMailjetSecretKey = ((EditText) findViewById(R.id.txtMailjetSecretKey)).getText().toString();
            String strGMailAppPwd = ((EditText) findViewById(R.id.txtGMailAppPwd)).getText().toString();
            final Spinner spinner = (Spinner) findViewById(R.id.spinHour);
            int iHour = spinner.getSelectedItemPosition();

            Cursor cursor = getVwCaddyCursor();
            if (cursor.getCount() == 0) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER, strSender);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS, strRecipients);
                //values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY, strMailjetApiKey);
                //values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY, strMailjetSecretKey);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY, strGMailAppPwd);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR, iHour);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, null, values);
                //db.close();
            } else {
                cursor.moveToFirst();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                String strDateNull = null;
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER, strSender);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS, strRecipients);
                //values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY, strMailjetApiKey);
                //values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY, strMailjetSecretKey);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_GMAILPASSWORD, strGMailAppPwd);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR, iHour);
                if(iHour != _origHour) {
                    values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE, strDateNull);
                }

                // on below line we are calling a update method to update our database and passing our values.
                // and we are comparing it with name of our course which is stored in original name variable.
                db.update(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, values, null, null);
                //db.close();
            }

            if(iHour > 0) {
                //startMailAlert(iHour);
                setMailWorker();
            } else {
                stopMailWorker();
            }

            /*if(iHour > 0)  {
                if(_origHour == 0) {
                    setMailWorker();
                }
            } else {
                stopMailWorker();
            }*/
            _origHour = iHour;

            Toast.makeText(
                    getApplicationContext(),
                    "Data byla uložena",
                    Toast.LENGTH_SHORT).show();

        } catch(Exception ex) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
            builder1.setTitle("Nastavení nebylo uloženo");
            builder1.setMessage("Při ukládání došo k chybě");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private Cursor getVwCaddyCursor() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(
                LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                null,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null);                               // The sort order

    }

    public void displayHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setMailWorker() {
        PeriodicWorkRequest mailWorkRequest = new PeriodicWorkRequest.Builder(
                MailWorker.class,
                15*60*1000L, //15 mins is minimum
                TimeUnit.MILLISECONDS)
                .addTag(WORKER_TAG)
                .build();

        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, mailWorkRequest);

        clearCheckStamp();

    }

    private void clearCheckStamp() {
        ContainerDbHelper dbHelper = new ContainerDbHelper(this);

        Cursor cursor = getVwCaddyCursor();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String clearDate = null;
        values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE, clearDate);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            db.update(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, values, null, null);
        }
    }

    private void stopMailWorker() {
        WorkManager.getInstance(this).cancelAllWorkByTag(WORKER_TAG);
        WorkManager.getInstance(this).cancelAllWorkByTag(MailBroadcastReceiver.WORKER_TAG);

    }

    public void startMailAlert(int iHour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, iHour);
        calendar.set(Calendar.MINUTE, 00);

        Intent intent = new Intent(this, MailBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        /*PendingIntent pi = PendingIntent.getService(context, 0,
                new Intent(context, MyClass.class),PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);*/

    }
}