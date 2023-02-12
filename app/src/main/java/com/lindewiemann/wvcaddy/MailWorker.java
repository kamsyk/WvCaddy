package com.lindewiemann.wvcaddy;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

public class MailWorker extends Worker {

    public MailWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int rightNowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        /*new ItemListExport(
                getApplicationContext(),
                true,
                null, null
        ).exportToFile();*/

        new ItemListMailer(
                getApplicationContext(),
                true,
                null
        ).sendMail();

        return Result.success();

    }
}
