package com.lindewiemann.wvcaddy;

import android.os.StrictMode;

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

import okhttp3.OkHttpClient;

public class LwMailJetClient {
    public static void sendMail() throws MailjetException, JSONException {
        MailjetRequest request;
        MailjetResponse response;

        try {
            StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);

            //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //logging.setLevel(Level.BASIC);
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

            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", "kamil.sykora.ks@gmail.com")
                                            .put("Name", "Kamil Sykora"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", "kamil.sykora@seznam.cz")
                                                    .put("Name", "You")))
                                    .put(Emailv31.Message.SUBJECT, "VW Caddy Test")
                                    .put(Emailv31.Message.TEXTPART, "Zatim bez prilohy")
                                    .put(Emailv31.Message.HTMLPART, "<h3>Dear passenger 1, welcome to <a href=\"https://www.mailjet.com/\">Mailjet</a>!</h3><br />May the delivery force be with you! Zatim bez prilohy")));
            response = client.post(request);
            System.out.println(response.getStatus());
            System.out.println(response.getData());
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
