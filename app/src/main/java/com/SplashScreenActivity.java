package com;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
//import android.os.PersistableBundle;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.tryone.R;
//
//import gr.net.maroulis.library.EasySplashScreen;
//
//public class SplashScreenActivity extends AppCompatActivity {
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
//                .withFullScreen()
//                .withTargetActivity(MainActivity.class)
//                .withSplashTimeOut(5000)
//                .withLogo(R.drawable.splashscreen);
//        View easySplashScreen = config.create();
//        setContentView(easySplashScreen);
//
//    }
//}

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}
