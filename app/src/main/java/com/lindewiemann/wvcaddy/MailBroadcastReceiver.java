package com.lindewiemann.wvcaddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MailBroadcastReceiver extends BroadcastReceiver {
    public final static String WORKER_TAG = "MailOneTimeWorkerTag";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Alarm run",Toast.LENGTH_LONG).show();
        OneTimeWorkRequest mailWorkRequest = new OneTimeWorkRequest.Builder(
                MailWorker.class)
                .addTag(WORKER_TAG)
                .build();

        WorkManager
                .getInstance(context)
                .enqueue(mailWorkRequest);
    }
}
