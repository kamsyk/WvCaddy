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
import android.widget.Toast;

public class VwCaddy_Settings extends AppCompatActivity {

    ContainerDbHelper dbHelper = new ContainerDbHelper(this);

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

        }
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
                db.close();
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
                db.close();
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