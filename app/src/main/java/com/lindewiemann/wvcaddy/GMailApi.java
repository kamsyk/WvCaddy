package com.lindewiemann.wvcaddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailApi {
    ContainerDbHelper _dbHelper = null;
    Context _appContext = null;

    public GMailApi(Context appContext) {
        _appContext = appContext;
        _dbHelper = new ContainerDbHelper(_appContext);
    }

    public void sendGMail(String file1Path) throws Exception {
        String mailSender = null;
        String mailRecipients = null;

        Cursor cursor = getVwCaddySettingCursor();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            mailSender = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER));
            mailRecipients = cursor.getString(cursor.getColumnIndexOrThrow(LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS));
        }

        if(mailSender == null || mailSender.length() == 0) {
            throw new Exception("Není nastaven odesílatel");
        }

        if(mailRecipients == null || mailRecipients.length() == 0) {
            throw new Exception("Nejsou nastaveni příjemci mailu");
        }

        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");

        //Creating a new session
        Session session = Session.getDefaultInstance(
                props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("kamsykde@gmail.com", "wizdkrafrjdtovev");
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress("kamsykde@gmail.com"));

            //Adding recipients
            String[] strRecipients = mailRecipients.split(";");
            for (int i = 0; i < strRecipients.length; i++) {
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(strRecipients[i]));
            }

            //Adding subject
            mm.setSubject("VW Caddy Vadné Podsestavy");

            //Adding message
            //mm.setText("Výpis vadných VW Caddy podsestav je v příloze.");
            Multipart emailContent = new MimeMultipart();

            //Text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText("Výpis vadných VW Caddy podsestav je v příloze.");

            //Attachment body part.
            MimeBodyPart csvAttachment = new MimeBodyPart();
            csvAttachment.attachFile(file1Path);

            //Attach body parts
            emailContent.addBodyPart(textBodyPart);
            emailContent.addBodyPart(csvAttachment);

            //Attach multipart to message
            mm.setContent(emailContent);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            throw e;
        }

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
