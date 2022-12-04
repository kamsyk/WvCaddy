package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.util.Base64;


import com.mailjet.client.errors.MailjetException;
//import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class LwMailJetClient {
    Context _appContext = null;
    ContainerDbHelper _dbHelper = null;

    public LwMailJetClient(Context appContext ) {
        _appContext = appContext;
        _dbHelper = new ContainerDbHelper(_appContext);
    }

    public void sendMail(String file1Path) throws Exception {
        MailjetRequest request;
        MailjetResponse response;
        String mailSender = "kamil.sykora.ks@gmail.com";
        String mailRecipients = null;
        String mailJetApiKey = "7da4ffd781e937f9e55bb0b6861a3fee";
        String mailJetSecretKey = "1bcf03be2202d1223ed81b19f0dcd494";

        Cursor cursor = getVwCaddySettingCursor();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            mailRecipients = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS));

        }

        if(mailRecipients == null || mailRecipients.length() == 0) {
            throw new Exception("Recipients is blank");
        }

        OkHttpClient customHttpClient = new OkHttpClient.Builder()
                //.connectTimeout(60, 60)
                //.readTimeout(60, 60)
                //.writeTimeout(60, 60)
                //.addInterceptor(logging)
                .build();

        ClientOptions options = ClientOptions.builder()
                .apiKey(mailJetApiKey)
                .apiSecretKey(mailJetSecretKey)
                .build();

        MailjetClient client = new MailjetClient(options);

        String base64 = "";
        File file = new File(file1Path);
        byte[] buffer = new byte[(int) file.length() + 100];
        @SuppressWarnings("resource")
        int length = new FileInputStream(file).read(buffer);
        base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);

        ArrayList<String> strRecipients= new ArrayList<String>();
        if(mailRecipients != null) {
            String[] strSubcodes = mailRecipients.split(";");
            for (int i = 0; i < strSubcodes.length; i++) {
                strRecipients.add(strSubcodes[i]);
            }
        }

        /*request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", mailSender)
                                        .put("Name", "Wv Caddy"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", "mailRecipients")
                                                .put("Name", "You")))
                                .put(Emailv31.Message.SUBJECT, "VW Caddy Test")
                                .put(Emailv31.Message.TEXTPART, "Výpis vadných VW Caddy podsestav je v příloze.")
                                .put(Emailv31.Message.HTMLPART, "Výpis vadných VW Caddy podsestav je v příloze.")
                                .put(Emailv31.Message.ATTACHMENTS,
                                        new JSONArray()
                                                .put(new JSONObject().put("ContentType", "application/csv")
                                                        .put("Filename", "abc.csv")
                                                        .put("Base64Content", base64))
                                                    .put(new JSONObject().put("ContentType", "application/csv")
                                                            .put("Filename", "abc1.csv")
                                                            .put("Base64Content", base64))
                                )
                        ));*/

        String[] strFile1NameItems = file1Path.split("/");
        String strFile1Name = strFile1NameItems[strFile1NameItems.length - 1];

        JSONObject msg = new JSONObject();
        msg.put(Emailv31.Message.FROM, new JSONObject()
                .put("Email", mailSender)
                .put("Name", "Wv Caddy"));
        msg.put(Emailv31.Message.SUBJECT, "VW Caddy Test");
        msg.put(Emailv31.Message.TEXTPART, "Výpis vadných VW Caddy podsestav je v příloze.");
        msg.put(Emailv31.Message.HTMLPART, "Výpis vadných VW Caddy podsestav je v příloze.");
        msg.put(Emailv31.Message.ATTACHMENTS,
                        new JSONArray()
                                .put(new JSONObject().put("ContentType", "application/csv")
                                        .put("Filename", strFile1Name)
                                        .put("Base64Content", base64)));
        JSONArray recipientsArray = new JSONArray();
        for(int i=0; i<strRecipients.size(); i++) {
            recipientsArray.put(new JSONObject()
                    .put("Email", strRecipients.get(i))
                    .put("Name", "You"));
        }
        msg.put(Emailv31.Message.TO, recipientsArray);

        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                                .put(msg));

        response = client.post(request);
        System.out.println(response.getStatus());
        System.out.println(response.getData());


    }
    private Cursor getVwCaddySettingCursor() {

        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        return db.query(
                LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                null,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null);                               // The sort order

    }
}
