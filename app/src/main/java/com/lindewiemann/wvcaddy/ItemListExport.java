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
import com.lindewiemann.wvcaddy.adapters.VwCaddyItemsAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemListExport {
    private Context _context = null;
    private boolean _isAuto = false;
    private LinearProgressIndicator _progressBar = null;
    private File _folder = null;
    private String _fullExportPath;
    private String _fullSummaryExportPath;
    private boolean _isResultOk = false;
    private ContainerDbHelper _dbHelper = null;
    private ProgressDialog _progressDialog;


    public String getFullExportPath() {
        return _fullExportPath;
    }
    public String getFullSummaryExportPath() {
        return _fullSummaryExportPath;
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

    public void exportToFile(GregorianCalendar startDate, GregorianCalendar endDate) {
        try {
            if (isExportFolderExist()) {
                if(_progressBar != null) {
                    _progressBar.setVisibility(View.VISIBLE);
                }
                new ItemListExport.ExportAsyncTask(startDate, endDate).execute();
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

    public boolean isExportFolderExist() {
        _folder = getExportRootFolder(_context);
        //_folder = context.getExternalFilesDir(null);

        boolean isFolderExist = true;
        if (!_folder.exists()) {
            //Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            isFolderExist = _folder.mkdir();
        }
        if (!isFolderExist) {
            if(!_isAuto) {
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
            }
            return false;
        }

        return true;
    }

    private File getExportRootFolder(Context context) {
        return context.getExternalFilesDir(null);
    }

    private Cursor getVwCaddyCursor(GregorianCalendar startDate, GregorianCalendar endDate) {

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        String strFilter = null;
        if(startDate != null && endDate != null) {
            /*GregorianCalendar startDate = new GregorianCalendar();
            startDate.add(Calendar.MONTH, -1);
            startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);

            GregorianCalendar endDate = new GregorianCalendar();
            endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);
            endDate.add(Calendar.MONTH, 1);*/

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
    }

    private Cursor getVwCaddySubcodeCursor(GregorianCalendar startDate, GregorianCalendar endDate) {

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        String strFilter = null;
        if(startDate != null && endDate != null) {
            /*GregorianCalendar startDate = new GregorianCalendar();
            startDate.add(Calendar.MONTH, -1);
            startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);

            GregorianCalendar endDate = new GregorianCalendar();
            endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), 1, 0, 0);
            endDate.add(Calendar.MONTH, 1);*/

            long intStart = startDate.getTime().getTime();
            long intEnd = endDate.getTime().getTime();

            strFilter = LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE_INT + ">" + intStart + " AND " + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE_INT + "<" + intEnd;

            /*year[0] = startDate.get(Calendar.YEAR);
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            String monthName = month_date.format(startDate.getTime());
            month[0] = monthName;*/
        }

        return db.query(
                LwVwCaddyDbDict.WvCaddySubcodeEntry.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                strFilter,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                "rowid DESC");
    }

    public class ExportAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        String fileName = null;
        String fileSummaryName = null;
        GregorianCalendar _startDate = null;
        GregorianCalendar _endDate = null;

        public ExportAsyncTask(GregorianCalendar startDate, GregorianCalendar endDate) {
            super();
            _startDate = startDate;
            _endDate = endDate;
            /*_startDate.add(Calendar.MONTH, -1);
            _startDate.set(_startDate.get(Calendar.YEAR), _startDate.get(Calendar.MONTH), 1, 0, 0);
            _endDate.set(_startDate.get(Calendar.YEAR), _startDate.get(Calendar.MONTH), 1, 0, 0);
            _endDate.add(Calendar.MONTH, 1);*/
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                exportThread(_startDate, _endDate);
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
                    builder1.setMessage("Data byla uložena do souborů " + _fullExportPath + ", " + _fullSummaryExportPath);
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

        public void exportThread(GregorianCalendar startDate, GregorianCalendar endDate) throws IOException, InterruptedException {

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
                        + "Podsestava,"
                        + "Počet,"
                        + "Chyba"
                        + System.lineSeparator();
                myOutWriter.append(strHeader);

                Cursor cursor = getVwCaddyCursor(startDate, endDate);
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

                //File Summary
                fileSummaryName = "VwCaddySummary" + strDate + ".csv";
                if(_folder == null) {
                    _folder = getExportRootFolder(_context);
                }
                File exportFileSumary = new File(_folder, fileSummaryName);
                exportFileSumary.createNewFile();

                FileOutputStream fOutSummary = new FileOutputStream(exportFileSumary);
                OutputStreamWriter myOutSummaryWriter = new OutputStreamWriter(fOutSummary, StandardCharsets.UTF_8);

                myOutSummaryWriter.write("\ufeff"); // Add BOM to UTF-8 necessary for displaying Czech chars in excel in windows

                String strSummaryHeader = "Díly,"
                        + "Kusy"
                        + System.lineSeparator();
                myOutSummaryWriter.append(strSummaryHeader);

                int year = startDate.get(Calendar.YEAR);
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                String monthName = month_date.format(startDate.getTime());
                String month = monthName;
                Cursor cursorSummary = getVwCaddySubcodeCursor(startDate, endDate);
                iRowIndex = 0;

                Map<String, Integer> mapSummary = new HashMap<String, Integer>();
                mapSummary.put("810 2-4536B", 0);
                mapSummary.put("020 2-4530", 0);
                mapSummary.put("820 8-0398A", 0);
                mapSummary.put("020 2-4532", 0);
                mapSummary.put("820 8-0343", 0);
                mapSummary.put("81024534A", 0);
                mapSummary.put("810 2-4535", 0);
                mapSummary.put("020 2-4529", 0);
                mapSummary.put("810 2-4533A", 0);
                mapSummary.put("020 2-4531", 0);
                mapSummary.put("820 8-0263", 0);
                mapSummary.put("810 2-4537A", 0);

                while (cursorSummary.moveToNext()) {
                    String strSubCode = cursorSummary.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SUBCODE));
                    int iPcs = cursorSummary.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_PCS));
                    //mapSummary.computeIfPresent(strSubCode, (k, v) -> v + iPcs);
                    if(mapSummary.containsKey(strSubCode)) {
                        mapSummary.put(strSubCode, mapSummary.get(strSubCode) + iPcs);
                    }

                    if(!_isAuto) {
                        publishProgress(iRowIndex);
                        iRowIndex++;
                    }
                }

                myOutSummaryWriter.append("810 2-4536B," + mapSummary.get("810 2-4536B") + System.lineSeparator());
                myOutSummaryWriter.append("020 2-4530," + mapSummary.get("020 2-4530") + System.lineSeparator());
                myOutSummaryWriter.append("820 8-0398A," + mapSummary.get("820 8-0398A") + System.lineSeparator());
                myOutSummaryWriter.append("020 2-4532," + mapSummary.get("020 2-4532") + System.lineSeparator());
                myOutSummaryWriter.append("820 8-0343," + mapSummary.get("820 8-0343") + System.lineSeparator());
                myOutSummaryWriter.append("81024534A," + mapSummary.get("81024534A") + System.lineSeparator());
                myOutSummaryWriter.append("810 2-4535," + mapSummary.get("810 2-4535") + System.lineSeparator());
                myOutSummaryWriter.append("020 2-4529," + mapSummary.get("020 2-4529") + System.lineSeparator());
                myOutSummaryWriter.append("810 2-4533A," + mapSummary.get("810 2-4533A") + System.lineSeparator());
                myOutSummaryWriter.append("020 2-4531," + mapSummary.get("020 2-4531") + System.lineSeparator());
                myOutSummaryWriter.append("820 8-0263," + mapSummary.get("820 8-0263") + System.lineSeparator());
                myOutSummaryWriter.append("810 2-4537A," + mapSummary.get("810 2-4537A") + System.lineSeparator());

                if(startDate != null && endDate != null && year != 0 && monthName != null) {
                    myOutSummaryWriter.append(System.lineSeparator());
                    myOutSummaryWriter.append("Měsíc," + monthName + System.lineSeparator());
                    myOutSummaryWriter.append("Rok," + year + System.lineSeparator());
                }

                myOutSummaryWriter.close();
                fOutSummary.close();

                _fullSummaryExportPath = _folder.getAbsolutePath() + "/" + fileSummaryName;

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
            int iFailReason = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_FAIL_REASON));

            String strShift = LwVwCaddyDbDict.getShiftName(iShift);
            String strLr = LwVwCaddyDbDict.getLeftRightText(iLr);
            String strPcs = String.valueOf(iPcs);
            String strReason = VwCaddyItemsAdapter.getFailReason(iFailReason);
            strCode += " " + strLr;

            String exportLine = strDateTime + ","
                    + strShift + ","
                    + strCode + ","
                    + strPcs + ","
                    + strReason;
            exportLine += System.lineSeparator();

            return exportLine;
        }

        private String getExportSubcodeLine(Cursor cursor) {
            String strDateTime = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_DATE));
            int iShift = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_SHIFT));
            String strSubCode = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_SUBCODE));
            int iFailReason = cursor.getInt(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_FAIL_REASON));

            String strShift = LwVwCaddyDbDict.getShiftName(iShift);
            String strReason = VwCaddyItemsAdapter.getFailReason(iFailReason);

            String exportLine = strDateTime + ","
                    + strShift + ","
                    + strSubCode + ","
                    + strReason;
            exportLine += System.lineSeparator();

            return exportLine;
        }
    }
}
