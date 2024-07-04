package com.example.getfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    private static final String EMAIL = "renu44103@gmail.com";
    private static final String PASSWORD = "qleoshwhiosxtvfa" ;

    public static void sendEmailIntent(Context context, String[] recipients, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
        } else {
            Toast.makeText(context, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendEmailInBackground(Context context, String recipient, String subject, String body) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(EMAIL, PASSWORD);
                    sender.sendMail(recipient, subject, body);
                    showToast(context, "Email Sent");
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(context, "Failed to send email");
                }
            }
        });
    }

    private static void showToast(Context context, String message) {
        new android.os.Handler(context.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    private static class GMailSender {
        private final String username;
        private final String password;
        private final Properties props;

        public GMailSender(String username, String password) {
            this.username = username;
            this.password = password;

            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
        }

        public void sendMail(String recipient, String subject, String body) throws MessagingException {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        }
    }
}
