package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemListExport {
    private Context _context = null;
    private boolean _isAuto = false;
    private LinearProgressIndicator _progressBar = null;
    private File _folder = null;
    private String _fullExportPath;
    private boolean _isResultOk = false;
    private ContainerDbHelper _dbHelper = null;
    private ProgressDialog _progressDialog;


    public String getFullExportPath() {
        return _fullExportPath;
    }

    public ItemListExport(
            Context context,
            boolean isAuto,
            LinearProgressIndicator progressBar) {
        _context = context;
        _isAuto = isAuto;
        _progressBar = progressBar;
        _dbHelper = new ContainerDbHelper(_context);

    }

    public void exportToFile() {
        try {
            if (isExportFolderExist(_context)) {
                if(_progressBar != null) {
                    _progressBar.setVisibility(View.VISIBLE);
                }
                new ItemListExport.ExportAsyncTask().execute();
            }


        } catch(Exception ex) {
            if(_isAuto) {
                throw ex;
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
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
    }

    public boolean isExportFolderExist(Context context) {
        _folder = getExportRootFolder(context);
        //_folder = context.getExternalFilesDir(null);

        boolean isFolderExist = true;
        if (!_folder.exists()) {
            //Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            isFolderExist = _folder.mkdir();
        }
        if (!isFolderExist && !_isAuto) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
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

    private File getExportRootFolder(Context context) {
        return context.getExternalFilesDir(null);
    }

    private Cursor getVwCaddyCursor() {

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

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

    public class ExportAsyncTask extends AsyncTask<Void, Integer, Boolean> {
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
            if(_progressBar != null) {
                _progressBar.setVisibility(View.GONE);
            }

            if(_progressDialog != null) {
                _progressDialog.dismiss();
            }

            if(_isAuto) {
                _isResultOk = result;
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
                if (result) {
                    builder1.setTitle("Export byl dokončen");
                    builder1.setMessage("Data byla uložena do souboru " + _fullExportPath);
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
        }
        @Override
        protected void onPreExecute() {
            if(_progressBar != null) {
                _progressBar.setProgress(0);
            }
            /*if(!_isAuto) {
                _progressDialog = ProgressDialog.show(_context, "Generování souboru", "Probíhá generování souboru ...", false, false);
            }*/
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if(_progressBar != null) {
                _progressBar.setProgress(values[0]);
            }
        }

        public void exportThread() throws IOException, InterruptedException {

            try {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss", Locale.US);
                String strDate = dateFormat.format(date);

                fileName = "VwCaddyFull" + strDate + ".csv";
                if(_folder == null) {
                    _folder = getExportRootFolder(_context);
                }
                File exportFile = new File(_folder, fileName);
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
                    if(!_isAuto) {
                        publishProgress(iRowIndex);
                        iRowIndex++;
                    }
                }
                myOutWriter.close();
                fOut.close();

                _fullExportPath = _folder.getAbsolutePath() + "/" + fileName;
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
}
