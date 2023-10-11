package com.mountfox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class CrashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        final TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText("Sorry, Something went wrong. \nPlease send error logs to the developer.");

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // so it will first save the error trace in vm folder of parent directory of SD card
                String filePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/vm/" + ".errorTrace.txt";
                sendErrorMail(CrashActivity.this, filePath);
                finish();
            }
        });

    }


    private void sendErrorMail(Context mContext, String filePath) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error Description"; // here subject
        String body = "Sorry for your inconvenience .\nWe assure you that we will solve this problem as soon as possible."
                + "\n\nThanks for using app."; // here email body

        sendIntent.setType("plain/text");
        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { "radiantdeveloper18@gmail.com" }); // your developer email id // password : radiant@123

        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.fromFile(new File(filePath)));
        sendIntent.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(sendIntent, "Complete action using"));
    }


}
