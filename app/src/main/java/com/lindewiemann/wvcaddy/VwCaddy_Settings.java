package com.lindewiemann.wvcaddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VwCaddy_Settings extends AppCompatActivity {

    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
    boolean _isAuthorized = false;
    String _pwd = null;

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
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ((EditText) findViewById(R.id.txtSender)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER)));
            ((EditText) findViewById(R.id.txtRecipients)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS)));
            ((EditText) findViewById(R.id.txtMailjetApiKey)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY)));
            ((EditText) findViewById(R.id.txtMailjetSecretKey)).setText(cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY)));
            _pwd = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_PASSWORD));
        }

        if(_pwd == null || _pwd.trim().length()==0) {
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
            String strMailjetApiKey = ((EditText) findViewById(R.id.txtMailjetApiKey)).getText().toString();
            String strMailjetSecretKey = ((EditText) findViewById(R.id.txtMailjetSecretKey)).getText().toString();

            Cursor cursor = getVwCaddyCursor();
            if (cursor.getCount() == 0) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER, strSender);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS, strRecipients);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY, strMailjetApiKey);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY, strMailjetSecretKey);


                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, null, values);
                //db.close();
            } else {
                cursor.moveToFirst();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                // on below line we are passing all values
                // along with its key and value pair.
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER, strSender);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS, strRecipients);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY, strMailjetApiKey);
                values.put(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY, strMailjetSecretKey);

                // on below line we are calling a update method to update our database and passing our values.
                // and we are comparing it with name of our course which is stored in original name variable.
                db.update(LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, values, null, null);
                //db.close();
            }

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
}