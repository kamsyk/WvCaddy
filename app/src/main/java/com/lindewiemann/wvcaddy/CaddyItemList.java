package com.lindewiemann.wvcaddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lindewiemann.wvcaddy.adapters.VwCaddyItemsAdapter;
import com.mailjet.client.errors.MailjetException;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CaddyItemList extends AppCompatActivity {
    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
    private LinearProgressIndicator progressBar = null;
    File folder = null;
    String fullExportPath;
    Context context = this;
    private ProgressDialog mProgressDialog;
    private boolean _isLastMonthOnly = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_caddy_item_list);
        loadItems();
    }

    public void displayHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loadItems() {
        Cursor cursor = getVwCaddyCursor();

        ListView lvItems = findViewById(R.id.lvItems);
        // Setup cursor adapter using cursor from last step
        VwCaddyItemsAdapter todoAdapter = new VwCaddyItemsAdapter(this, cursor);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);
        todoAdapter.changeCursor(cursor);
    }


    private Cursor getVwCaddyCursor() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String strFilter = null;
        if(_isLastMonthOnly) {
            GregorianCalendar startDate = new GregorianCalendar();
            startDate.add(Calendar.MONTH, -1);
            startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);

            GregorianCalendar endDate = new GregorianCalendar();
            endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);
            endDate.add(Calendar.MONTH, 1);

            long intStart = startDate.getTime().getTime();
            long intEnd = endDate.getTime().getTime();

            strFilter = LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE_INT + ">" + intStart + " AND " + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE_INT + "<" + intEnd;
        }

        return db.query(
                LwVwCaddyDbDict.WvCaddyEntry.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                strFilter,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                "rowid DESC");
                //"datetime(" + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE + ") DESC" );                               // The sort order

    }


    public void exportToFile(View v) {
        try {
            progressBar = findViewById(R.id.pgbExport);

            new ItemListExport(
                    v.getContext(),
                    false,
                    progressBar).exportToFile(_isLastMonthOnly);
        } catch(Exception ex) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Došlo k chybě");
            builder1.setMessage("Export dat selhal");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void sendMail(View v) {
        try {
            if (new ItemListExport(v.getContext(), false, null).isExportFolderExist()) {
                progressBar = findViewById(R.id.pgbExport);

                new ItemListMailer(
                        v.getContext(),
                        false,
                        _isLastMonthOnly,
                        progressBar).sendMail();

            }
        } catch (Exception e) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Došlo k chybě");
            builder1.setMessage("Odeslání mailu selhalo");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void ckbOnlyMonth(View v) {
        CheckBox ckbMonthOnly = (CheckBox)v;
        _isLastMonthOnly = (ckbMonthOnly.isChecked());
        loadItems();
    }
}