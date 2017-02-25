package com.example.yusuf.mobilemechanic;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.backendless.Backendless;

public class Splash_Screen extends AppCompatActivity {

    public static final String APP_ID="25028802-4B1A-B515-FFB8-8EE7E3DBA800";
    public static final String SECRET_KEY="3C18A0CF-6E21-079D-FFD9-00ECD64FBA00";
    public static final String VIRSION="v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash__screen);
        Backendless.initApp(this,APP_ID,SECRET_KEY,VIRSION);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash__screen);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash_Screen.this, Login_Screen.class));
                finish();
            }
        }, 2000);
    }
}
