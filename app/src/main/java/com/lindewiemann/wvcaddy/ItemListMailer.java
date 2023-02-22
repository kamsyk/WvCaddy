package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.IOException;

public class ItemListMailer {
    private Context _context = null;
    private boolean _isAuto = false;
    private LinearProgressIndicator _progressBar = null;
    private ProgressDialog _progressDialog;
    private boolean _isResultOk = false;

    public ItemListMailer(
            Context context,
            boolean isAuto,
            LinearProgressIndicator progressBar) {
        _context = context;
        _isAuto = isAuto;
        _progressBar = progressBar;

    }

    public void sendMail() throws Exception {
        try {
            ItemListExport itemListExport = new ItemListExport(
                    _context,
                    _isAuto,
                    _progressBar);
            if (itemListExport.isExportFolderExist()) {
                if(_isAuto) {
                    ItemListExport.ExportAsyncTask exportAsyncTask = itemListExport.new ExportAsyncTask();
                    exportAsyncTask.exportThread();
                    new GMailApi(_context).sendGMail(itemListExport.getFullExportPath());
                } else {
                    new ItemListMailer.SendMailAsyncTask().execute();
                }
            } else {
                if(_isAuto) {
                    throw new Exception("Export folder does not exist");
                }
            }
        } catch (Exception e) {
            if(_isAuto) {
                throw e;
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
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
    }

    class SendMailAsyncTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ItemListExport itemListExport = new ItemListExport(
                        _context,
                        _isAuto,
                        _progressBar);

                //ItemListExport.ExportAsyncTask().exportThread();
                ItemListExport.ExportAsyncTask exportAsyncTask = itemListExport.new ExportAsyncTask();
                exportAsyncTask.exportThread();

                //Send GMail
                new GMailApi(_context).sendGMail(itemListExport.getFullExportPath());

                return null;
            } catch (IOException | InterruptedException e) {

                    return "Generování dokumentu selhalo";

            } catch (Exception e) {

                    return "Odesílání dokumentu selhalo";

            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(_progressBar != null) {
                _progressBar.setVisibility(View.GONE);
            }
            if(_progressDialog != null) {
                _progressDialog.dismiss();
            }

            if(result == null) {
                if(!_isAuto) {
                    Toast.makeText(
                            _context,
                            "Mail byl odeslán",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                if(_isAuto) {
                    _isResultOk = result == null || result.isEmpty();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);
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
        }
        @Override
        protected void onPreExecute() {
            if(_progressBar != null) {
                _progressBar.setProgress(0);
            }
            if(!_isAuto) {
                _progressDialog = ProgressDialog.show(_context, "Odeslání mail", "Probíhá odesílání mailu ...", false, false);
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if(_progressBar != null) {
                _progressBar.setProgress(values[0]);
            }
        }
    }
}
