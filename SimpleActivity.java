package com.healbe.healbe_example_andorid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.tools.Permissions;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.business_api.tasks.entity.HeartRate;
import com.healbe.healbesdk.business_api.user.data.HealbeSessionState;
import com.healbe.healbesdk.business_api.user_storage.entity.HealbeDevice;
import com.healbe.healbesdk.device_api.ClientState;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

// just tracks pulse / no pulse when you wear or take off your gobe,
// shows connection state just for first-time connection
@SuppressWarnings("FieldCanBeLocal")
public class SimpleActivity extends AppCompatActivity {
    private CompositeDisposable unsubscribeOnDestroy = new CompositeDisposable();
    private TextView state;
    private TextView pulse;
    private ProgressBar progress;
    private String userEmail = "user@healbe.com";
    private String userPassword = "password";
    private HealbeDevice device = new HealbeDevice("deviceName", "00:00:00:00:00:00", "000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_simple);

        pulse = findViewById(R.id.text);
        progress = findViewById(R.id.progress);
        state = findViewById(R.id.state);

        progress.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        pulse.setVisibility(View.GONE);

        //check permissions, turn on bluetooth and run
        Permissions.checkBluetoothPermission(this::runChain, this);
    }

    private void runChain() {
        unsubscribeOnDestroy.add(initSdk()
                .andThen(prepareSession())
                .andThen(login())
                .andThen(setupDevice())
                .andThen(connect())
                .andThen(observeHeartRate())
                .subscribe(this::showPulse, this::showError));
    }

    private Completable initSdk() {
        return Completable.defer(() -> HealbeSdk.init(getApplicationContext()));
    }

    private Completable prepareSession() {
        return Completable.defer(() -> HealbeSdk.get().USER.prepareSession().ignoreElement());
    }

    private Completable login() {
        return Completable.defer(() -> HealbeSdk.get().USER.login(userEmail, userPassword)
                .flatMapCompletable(this::checkState));
    }

    private Completable setupDevice() {
        return Completable.defer(() -> HealbeSdk.get().GOBE.set(device));
    }

    private Completable connect() {
        return Completable.defer(() -> HealbeSdk.get().GOBE.connect()
                // observe connection states
                .andThen(HealbeSdk.get().GOBE.observeConnectionState())
                // show state
                .doOnNext(clientState ->
                        state.post(() -> state.setText(String.valueOf(clientState))))
                // wait for READY state
                .filter(clientState -> clientState == ClientState.READY)
                // now wristband is READY
                .firstElement()
                // we can ignore state because of filter
                .ignoreElement()
        );
    }

    private Observable<HeartRate> observeHeartRate() {
        return Observable.defer(() -> HealbeSdk.get().TASKS.observeHeartRate()
                // observe on main thread for correctly update ui
                .observeOn(AndroidSchedulers.mainThread()));
    }

    @SuppressLint("SetTextI18n")
    private void showPulse(HeartRate heartRate) {
        progress.setVisibility(View.GONE);

        if (!heartRate.isEmpty()) {
            state.setVisibility(View.GONE);
            pulse.setVisibility(View.VISIBLE);
            pulse.setText("Pulse: " + heartRate.getHeartRate() + " bpm");
        } else {
            state.setVisibility(View.VISIBLE);
            pulse.setVisibility(View.GONE);
            state.setText("Pulse not found, did you wear your GoBe?");
        }
    }

    @SuppressLint("SetTextI18n")
    private void showError(Throwable e) {
        progress.setVisibility(View.GONE);
        pulse.setVisibility(View.GONE);
        state.setText(e.getMessage());
    }

    private Completable checkState(HealbeSessionState sessionState) {
        if (HealbeSessionState.isUserValid(sessionState))
            return Completable.complete();
        else
            return Completable.error(new RuntimeException("user invalid"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clear all subscribers
        unsubscribeOnDestroy.clear();
    }
}
