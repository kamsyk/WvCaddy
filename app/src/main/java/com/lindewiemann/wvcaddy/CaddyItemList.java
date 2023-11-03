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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CaddyItemList extends AppCompatActivity {
    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
    private LinearProgressIndicator progressBar = null;
    File folder = null;
    String fullExportPath;
    Context context = this;
    private ProgressDialog mProgressDialog;
    private boolean _isAllData = false;
    private Month _month;
    private Year _year;
    GregorianCalendar _startDate = null;
    GregorianCalendar _endDate = null;

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
        setMonths();
        setYears();
        loadItems();
    }

    public void displayHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loadItems() {
        if(!_isAllData) {
            try {
                _startDate = new GregorianCalendar();
                _endDate = new GregorianCalendar();
                int startMonth = _month.getId();
                int startYear = _year.getId();
                _startDate.set(startYear, startMonth - 1, 1, 0, 0);
                _endDate.set(_startDate.get(Calendar.YEAR), _startDate.get(Calendar.MONTH), 1, 0, 0);
                _endDate.add(Calendar.MONTH, 1);
            } catch(Exception ex) {
                throw ex;
            }
        }

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
        if(_startDate != null && _endDate != null) {
            //GregorianCalendar startDate = new GregorianCalendar();
            //startDate.add(Calendar.MONTH, -1);
            //startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);

            /*GregorianCalendar endDate = new GregorianCalendar();
            endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);
            endDate.add(Calendar.MONTH, 1);*/

            long intStart = _startDate.getTime().getTime();
            long intEnd = _endDate.getTime().getTime();

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
                    progressBar).exportToFile(_startDate, _endDate);
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
                        _startDate,
                        _endDate,
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
            new ErrorHandler().LogError(context, e);
        }
    }

    public void ckbAll(View v) {
        CheckBox ckbAll = (CheckBox)v;

        final Spinner spinnerMonth = (Spinner) findViewById(R.id.spinMonth);
        final Spinner spinnerYear = (Spinner) findViewById(R.id.spinYear);
        spinnerMonth.setEnabled(!ckbAll.isChecked());
        spinnerYear.setEnabled(!ckbAll.isChecked());

        _isAllData = (ckbAll.isChecked());
        if(ckbAll.isChecked()) {
            _startDate = null;
            _endDate = null;
        }
        loadItems();
    }

    private void setMonths() {
        List<Month> monthsList = new ArrayList<Month>();

        monthsList.add(new Month(1,"Leden"));
        monthsList.add(new Month(2, "Únor"));
        monthsList.add(new Month(3,"Březen"));
        monthsList.add(new Month(4,"Duben"));
        monthsList.add(new Month(5,"Květen"));
        monthsList.add(new Month(6,"Červen"));
        monthsList.add(new Month(7,"Červenec"));
        monthsList.add(new Month(8,"Srpen"));
        monthsList.add(new Month(9,"Září"));
        monthsList.add(new Month(10,"Říjen"));
        monthsList.add(new Month(11,"Listopad"));
        monthsList.add(new Month(12,"Prosinec"));

        GregorianCalendar nowDate = new GregorianCalendar();
        int iMonth = nowDate.get(Calendar.MONTH);
        for(Month m: monthsList) {
            if(m.getId() == (iMonth + 1)) {
                _month = m;
                break;
            }
        }

        // Creating adapter for spinner
        ArrayAdapter<Month> dataAdapter = new ArrayAdapter<Month>(this, android.R.layout.simple_spinner_item, monthsList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinMonth);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getPosition(_month));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _month = (Month) parent.getSelectedItem();
                loadItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setYears() {
        List<Year> yearsList = new ArrayList<Year>();

        GregorianCalendar nowDate = new GregorianCalendar();
        int iYear = nowDate.get(Calendar.YEAR);

        for(int i=2023; i<=iYear; i++) {
            yearsList.add(new Year(i, String.valueOf(i)));
        }


        for(Year y: yearsList) {
            if(y.getId() == iYear) {
                _year = y;
                break;
            }
        }

        // Creating adapter for spinner
        ArrayAdapter<Year> dataAdapter = new ArrayAdapter<Year>(this, android.R.layout.simple_spinner_item, yearsList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinYear);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getPosition(_year));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _year = (Year) parent.getSelectedItem();
                loadItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}