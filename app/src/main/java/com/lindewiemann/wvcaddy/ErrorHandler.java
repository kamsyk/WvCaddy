package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ErrorHandler {
    private File _errorFolder = null;
    private Exception _exception = null;
    private boolean _isAuto = false;
    private Context _context = null;

    public void LogError(Context context, Exception ex) {
        try {
            if (ex == null) return;
            File rootFolder = getExportRootFolder(context);

            if (!rootFolder.exists()) {
                rootFolder.mkdir();
            }

            _errorFolder = new File(rootFolder.getAbsolutePath() + File.separator + "ErrorLog");
            if (!_errorFolder.exists()) {
                _errorFolder.mkdir();
            }

            _context = context;
            _exception = ex;
            new ErrorHandler.ExportAsyncTask().execute();
        } catch(Exception ex1) {
            if(!_isAuto) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
                builder1.setTitle("Došlo k chybě");
                builder1.setMessage("Došlo k chybě při ukládání ");
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

    private File getExportRootFolder(Context context) {
        return context.getExternalFilesDir(null);
    }


    public class ExportAsyncTask extends AsyncTask<Void, Integer, Boolean> {
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

        }

        public void exportThread() throws IOException, InterruptedException {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss", Locale.US);
            String strDate = dateFormat.format(date);

            String fileName = "VwCaddyError" + strDate + ".txt";

            File errorLogFile = new File(_errorFolder, fileName);
            errorLogFile.createNewFile();


            FileOutputStream fOutSummary = new FileOutputStream(errorLogFile);
            OutputStreamWriter myOutSummaryWriter = new OutputStreamWriter(fOutSummary, StandardCharsets.UTF_8);

            myOutSummaryWriter.write("\ufeff"); // Add BOM to UTF-8 necessary for displaying Czech chars in excel in windows


            myOutSummaryWriter.append(GetExceptionMessage(_exception, strDate));

            myOutSummaryWriter.close();
            fOutSummary.close();
        }

        private String GetExceptionMessage(Throwable ex, String strDateTime) {
            String strErrMsg = strDateTime + ": ";// + ex.toString();

            StringWriter writer = null;
            try {
                writer = new StringWriter();
                joinStackTrace(ex, writer);
                strErrMsg += writer.toString();
            }
            finally {
                if (writer != null)
                    try {
                        writer.close();
                    } catch (IOException e1) {
                        // ignore
                    }
            }

            return strErrMsg;
        }

        private void joinStackTrace(Throwable e, StringWriter writer) {
            PrintWriter printer = null;
            try {
                printer = new PrintWriter(writer);

                while (e != null) {

                    printer.println(e);
                    StackTraceElement[] trace = e.getStackTrace();
                    for (int i = 0; i < trace.length; i++)
                        printer.println("\tat " + trace[i]);

                    e = e.getCause();
                    if (e != null)
                        printer.println("Caused by:\r\n");
                }
            }
            finally {
                if (printer != null)
                    printer.close();
            }
        }
    }
}
