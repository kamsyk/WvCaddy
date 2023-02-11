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
import java.util.Date;
import java.util.Locale;

public class CaddyItemList extends AppCompatActivity {
    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
    private LinearProgressIndicator progressBar = null;
    File folder = null;
    String fullExportPath;
    Context context = this;
    private ProgressDialog mProgressDialog;

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

        return db.query(
                LwVwCaddyDbDict.WvCaddyEntry.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                null,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                "rowid DESC");
                //"datetime(" + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE + ") DESC" );                               // The sort order

    }


    public void exportToFile(View v) {
        try {
            progressBar = findViewById(R.id.pgbExport);
            /*if (isExportFolderExist(v.getContext())) {
                progressBar.setVisibility(View.VISIBLE);
                new ExportAsyncTask().execute();
            }*/
            new ItemListExport(v.getContext(), false).exportToFile();
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
            if (isExportFolderExist(v.getContext())) {
                progressBar = findViewById(R.id.pgbExport);
                new SendMailAsyncTask().execute();


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


    private boolean isExportFolderExist(Context context) {
        folder = context.getExternalFilesDir(null);

        boolean isFolderExist = true;
        if (!folder.exists()) {
            //Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            isFolderExist = folder.mkdir();
        }
        if (!isFolderExist) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Došlo k chybě");
            builder1.setMessage("Zkontrolujte zda má aplikace oprávnění pro zápis do úložiště");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    (DialogInterface dialog, int id) ->
                            dialog.cancel()
            );

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return false;
        }

        return true;
    }


    class ExportAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        String fileName = null;

        public ExportAsyncTask() {
            super();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                exportThread();
                return true;
            } catch (IOException | InterruptedException e) {
                return false;
            }

        }
        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            if(result) {
                builder1.setTitle("Export byl dokončen");
                builder1.setMessage("Data byla uložena do souboru " + fullExportPath);
                builder1.setCancelable(true);
                builder1.setNeutralButton("Zavřít",
                        (DialogInterface dialog, int id) ->
                                dialog.cancel()
                );

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {

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
        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            //Log.d("Progress", String.valueOf(values[0]));
            progressBar.setProgress(values[0]);
        }

        public void exportThread() throws IOException, InterruptedException {

            try {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss", Locale.US);
                String strDate = dateFormat.format(date);

                fileName = "VwCaddyFull" + strDate + ".csv";
                File exportFile = new File(folder, fileName);
                exportFile.createNewFile();

                FileOutputStream fOut = new FileOutputStream(exportFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, StandardCharsets.UTF_8);

                myOutWriter.write("\ufeff"); // Add BOM to UTF-8 necessary for displaying Czech chars in excel in windows

                String strHeader = "Datum,"
                        + "Směna,"
                        + "Kód,"
                        + "Levé / Pravé,"
                        + "Subkód,"
                        + "Počet kusů"
                        + System.lineSeparator();
                myOutWriter.append(strHeader);

                Cursor cursor = getVwCaddyCursor();
                int iRowIndex = 0;

                while (cursor.moveToNext()) {
                    String strLine = getExportLine(cursor);
                    myOutWriter.append(strLine);
                    publishProgress(iRowIndex);
                    iRowIndex++;

                }
                myOutWriter.close();
                fOut.close();

                fullExportPath = folder.getAbsolutePath() + "/" + fileName;
            } catch(Exception ex) {
                throw ex;
            }
        }

        private String getExportLine(Cursor cursor) {
            String strDateTime = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE));
            int iShift = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SHIFT));
            String strCode = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_CODE));
            int iLr = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_LR));
            String strSubCode = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SUBCODE));
            int iPcs = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_PCS));

            String strShift = LwVwCaddyDbDict.getShiftName(iShift);
            String strLr = LwVwCaddyDbDict.getLeftRightText(iLr);
            String strPcs = String.valueOf(iPcs);

            String exportLine = strDateTime + ","
                    + strShift + ","
                    + strCode + ","
                    + strLr + ","
                    + strSubCode + ","
                    + strPcs;
            exportLine += System.lineSeparator();

            return exportLine;

        }
    }

    class SendMailAsyncTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                new ExportAsyncTask().exportThread();
                //new LwMailJetClient(getApplicationContext()).sendMail(fullExportPath);

                //Send GMail
                new GMailApi(getApplicationContext()).sendGMail(fullExportPath);

                return null;
            } catch (IOException | InterruptedException e) {
                return "Generování dokumentu selhalo";
            } catch (Exception e) {
                return "Odesílání dokumentu selhalo";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            mProgressDialog.dismiss();

            if(result == null) {
                Toast.makeText(
                        getApplicationContext(),
                        "Mail byl odeslán",
                        Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Při odesílání mailu došlo k chybě");
                builder1.setMessage(result);
                builder1.setCancelable(true);
                builder1.setNeutralButton("Zavřít",
                        (DialogInterface dialog, int id) ->
                                dialog.cancel()
                );

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        @Override
        protected void onPreExecute() {

            progressBar.setProgress(0);
            mProgressDialog = ProgressDialog.show(context,"Odeslání mail", "Probíhá odesílání mailu ...",false,false);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            //Log.d("Progress", String.valueOf(values[0]));
            progressBar.setProgress(values[0]);
        }
    }
}