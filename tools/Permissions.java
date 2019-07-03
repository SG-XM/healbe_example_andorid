package com.healbe.healbe_example_andorid.tools;

import android.app.Activity;
import android.content.Context;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbesdk.utils.Consumer;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.appcompat.app.AlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.Manifest.permission.BLUETOOTH;

public class Permissions {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void requestPerm(Consumer<Boolean> consumer, String permisson, Activity activity) {
        //noinspection ConstantConditions
        RxPermissions perm = new RxPermissions(activity);
        if (perm.isGranted(permisson))
            consumer.accept(true);
        else
            perm.request(permisson)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer::accept, Timber::e);
    }

    public static void warnUserPermission(Context context, int textId, Runnable onPositive, Runnable onNegative) {
        AlertDialog dialog =
                new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                        .setMessage(textId)
                        .setPositiveButton(context.getString(R.string.ok), (dialogInterface, i) -> onPositive.run())
                        .setNegativeButton(context.getString(R.string.logout), ((dialogInterface, i) -> onNegative.run()))
                        .create();
        dialog.show();
    }

    public static void checkBluetoothPermission(Runnable onSuccess, Activity a) {
        Permissions.requestPerm(boolValue -> {
            if (boolValue) {
                // turn on bluetooth if turned off
                if (Bluetooth.isOn())
                    onSuccess.run();
                else
                    //noinspection ResultOfMethodCallIgnored
                    Bluetooth.setOn().subscribe(onSuccess::run, Timber::e);
            } else
                // warn and retry or finish
                Permissions.warnUserPermission(a, R.string.warn_blu_permission,
                        () -> checkBluetoothPermission(onSuccess, a), a::finish);
        }, BLUETOOTH, a);
    }
}
