package com.healbe.healbe_example_andorid.tools;

import android.bluetooth.BluetoothAdapter;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Bluetooth {
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isOn() {
        if (BluetoothAdapter.getDefaultAdapter() == null)
            return false;
        else
            return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public static Completable setOn() {
        return Completable.fromAction(() -> {
            if (BluetoothAdapter.getDefaultAdapter() != null
                    && !BluetoothAdapter.getDefaultAdapter().isEnabled())
                BluetoothAdapter.getDefaultAdapter().enable();})
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
