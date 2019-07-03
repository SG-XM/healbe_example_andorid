package com.healbe.healbe_example_andorid.tools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.healbe.healbe_example_andorid.SplashActivity;
import com.healbe.healbesdk.business_api.HealbeSdk;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

// when we get fatal exception we need to re-init our sdk, so go to splash if sdk not initialized
// we can init it with Application.onCreate instead, but we want more sensitive app
// with not "skipped XXX frames" warning in log, because sdk has long initializing process
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate %s", this.getClass().getSimpleName());

        try {
            //noinspection ResultOfMethodCallIgnored
            HealbeSdk.get();
        } catch (IllegalStateException e) {
            Timber.e(e, "HealbeSdk did not initialized yet!");

            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent, null);
            finish();
        }
    }
}
