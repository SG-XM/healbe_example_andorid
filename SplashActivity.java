package com.healbe.healbe_example_andorid;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.connect.ConnectActivity;
import com.healbe.healbe_example_andorid.enter.EnterActivity;
import com.healbe.healbe_example_andorid.tools.BluetoothBroadcastReceiver;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.business_api.user.data.HealbeSessionState;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {
    // for saving any opened subscribers in activity
    private CompositeDisposable destroy = new CompositeDisposable();

    //TODO 1.初始化HealbeSdk
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView tv = findViewById(R.id.versionText);
        tv.setText(getString(R.string.sdk_example, BuildConfig.VERSION_NAME));

        getApplicationContext().registerReceiver(BluetoothBroadcastReceiver.get(),  new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        initSdk();
    }

    // init sdk
    private void initSdk() {
        // in big project recommended to move sdk operations in some abstract layer like presenters in mvp or smth. else
        // in example its ok, but remember our "destroy" disposable to support lifecycle
        destroy.add(HealbeSdk.init(getApplicationContext(), 1)
                // all sdk rx methods work in own schedulers, so we need to listen them in ui thread for ui operations
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initSession, Timber::e));
        //and we need to consume error in each call of sdk reactive methods
        //when we don't know what to do with error - we log it with Timber
    }

    // session preparing
    private void initSession() {
        //TODO 2.检查session中登录的用户信息
        destroy.add(HealbeSdk.get().USER.prepareSession()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::sessionStateSwitch, Timber::e));
    }

    //TODO 3.未登录或用户信息过期则登录Healbe用户，登录后或已有登录信息连接设备
    private void sessionStateSwitch(HealbeSessionState sessionState) {
        switch (sessionState) {
            case VALID_OLD_USER: // user authorized and paired wristband
            case VALID_NEW_USER: // user never paired wristband
                goConnect(); // but we don't know if user has or hasn't wristband paired right now
                break;
            case USER_NOT_AUTHORIZED: // we have no user session id
                goEnter();
                break;
            case NEED_TO_FILL_PERSONAL: // need to fill/correct profile fields
            case NEED_TO_FILL_PARAMS:   // but we won't do this in example
                destroy.add(HealbeSdk.get().USER.logout() // so we logout this user and suggest to login as other
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::goEnter, Timber::e));
                break;
        }
    }

    private void goEnter() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, EnterActivity.class), bundle);
        finish();
    }

    private void goConnect() {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        startActivity(new Intent(this, ConnectActivity.class), bundle);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clear all subscribes
        destroy.clear();
    }

}
