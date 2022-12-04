package com.lindewiemann.wvcaddy;

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

import okhttp3.OkHttpClient;

public class LwMailJetClient {
    public void sendMail(String file1Path) throws MailjetException, JSONException, IOException {
        MailjetRequest request;
        MailjetResponse response;


        OkHttpClient customHttpClient = new OkHttpClient.Builder()
                //.connectTimeout(60, 60)
                //.readTimeout(60, 60)
                //.writeTimeout(60, 60)
                //.addInterceptor(logging)
                .build();

        ClientOptions options = ClientOptions.builder()
                .apiKey("7da4ffd781e937f9e55bb0b6861a3fee")
                .apiSecretKey("1bcf03be2202d1223ed81b19f0dcd494")
                .build();

        MailjetClient client = new MailjetClient(options);

        String base64 = "";
        File file = new File(file1Path);
        byte[] buffer = new byte[(int) file.length() + 100];
        @SuppressWarnings("resource")
        int length = new FileInputStream(file).read(buffer);
        base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);


        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "kamil.sykora.ks@gmail.com")
                                        .put("Name", "Wv Caddy"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", "kamil.sykora@seznam.cz")
                                                .put("Name", "You")))
                                .put(Emailv31.Message.SUBJECT, "VW Caddy Test")
                                .put(Emailv31.Message.TEXTPART, "Výpis vadných VW Caddy podsestav je v příloze.")
                                .put(Emailv31.Message.HTMLPART, "Výpis vadných VW Caddy podsestav je v příloze.")
                                .put(Emailv31.Message.ATTACHMENTS,
                                        new JSONArray()
                                                .put(new JSONObject().put("ContentType", "application/csv")
                                                        .put("Filename", "abc.csv")
                                                        .put("Base64Content", base64))
                                                    /*.put(new JSONObject().put("ContentType", "application/csv")
                                                            .put("Filename", "abc1.csv")
                                                            .put("Base64Content", base64))*/
                                )
                        ));
        response = client.post(request);
        System.out.println(response.getStatus());
        System.out.println(response.getData());


    }
}
