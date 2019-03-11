package com.uc3m.credhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sharedprefs webservice_url
        SharedPreferences.Editor editor = getSharedPreferences("webservice_url", MODE_PRIVATE).edit();
        editor.putString("webservice_url", "http://10.0.2.2/SDM/WebRepo?wsdl");
        editor.commit();

        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();

            }
        }, SPLASH_TIME);
    }
}
