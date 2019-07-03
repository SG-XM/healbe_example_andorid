package com.healbe.healbe_example_andorid.tools;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by Alexey on 16.04.2018.
 */

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static BluetoothBroadcastReceiver instance;

    private BehaviorSubject<Integer> subj = BehaviorSubject.createDefault(Bluetooth.isOn() ? BluetoothAdapter.STATE_ON : BluetoothAdapter.STATE_OFF);

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Timber.tag("BroadcastActions").d("Action %s received", action);

        if (!TextUtils.isEmpty(action) && BluetoothAdapter.ACTION_STATE_CHANGED.equalsIgnoreCase(action))
            subj.onNext(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
    }

    public static BluetoothBroadcastReceiver get() {
        if(instance == null)
            instance = new BluetoothBroadcastReceiver();

        return instance;
    }

    public Observable<Integer> observeBluetoothState(){
        return subj;
    }
}