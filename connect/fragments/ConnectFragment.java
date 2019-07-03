package com.healbe.healbe_example_andorid.connect.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.ConnectRouter;
import com.healbe.healbe_example_andorid.connect.ConnectionRoutedFragment;
import com.healbe.healbe_example_andorid.tools.Bluetooth;
import com.healbe.healbe_example_andorid.tools.Permissions;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.device_api.ClientState;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static android.Manifest.permission.BLUETOOTH;

public class ConnectFragment extends ConnectionRoutedFragment {
    private CompositeDisposable destroy = new CompositeDisposable();
    private String name;

    //state map for our states valuable on connection screen
    private HashMap<ClientState, Runnable> stateMap = new HashMap<ClientState, Runnable>() {{
        put(ClientState.CONNECTING, () -> statusConnecting(name));
        put(ClientState.CONNECTED, () -> statusWaiting(name));

        put(ClientState.REQUEST_FUNC_FW, () -> goError()); // we show what we don't want to setup
        put(ClientState.REQUEST_UPDATE_FW, () -> goError()); // firmware in this version of example

        put(ClientState.REQUEST_NEW_PIN_CODE, () -> requestNewPin());
        put(ClientState.REQUEST_PIN_CODE, () -> requestPin());
        put(ClientState.READY, () -> getRouter().connected());
    }};

    @SuppressWarnings("FieldCanBeLocal")
    private Button cancel;
    private TextView status;


    public static ConnectFragment newInstance() {
        return new ConnectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        status = view.findViewById(R.id.status);
        cancel = view.findViewById(R.id.skip_button);

        cancel.setOnClickListener(v -> goSearch());
        Timber.d("view created..");

        checkBluetoothPerm();
    }

    // check necessary bluetooth permission and connect
    private void checkBluetoothPerm() {
        Permissions.requestPerm(boolValue -> {
            if (boolValue) {
                // turn on bluetooth if turned off
                if (Bluetooth.isOn())
                    connect();
                else
                    //noinspection ResultOfMethodCallIgnored
                    Bluetooth.setOn().subscribe(this::connect, Timber::e);
            } else
                // warn and retry or logout
                Permissions.warnUserPermission(getActivity(), R.string.warn_blu_permission,
                        this::checkBluetoothPerm, () -> getRouter().logout());

        }, BLUETOOTH, getActivity());
    }

    //TODO 11.连接设备，根据HealbeSdk.get().GOBE.observeConnectionState()返回状态，stateMap找出对应方法
    // 首次连接ClientState >> REQUEST_PIN_CODE需要输入PIN码
    // 连接成功ClientState >> READY，执行DashboardActivity
    @SuppressLint("LogNotTimber")
    private void connect() {
        Log.d("ConnectFragment", "connect..");
        //noinspection ConstantConditions
        destroy.add(HealbeSdk.get().GOBE.observeConnectionState()
                //we observe connection states and show them
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(status -> Log.d("ConnectFragment", "state: " + status.name()))
                .filter(state -> stateMap.containsKey(state))
                .subscribe(state -> stateMap.get(state).run(), Timber::e));

        destroy.add(HealbeSdk.get().GOBE.get()
                // we get gobe what saved on scan screen (maybe not in this session)
                .observeOn(AndroidSchedulers.mainThread())
                //and start connection
                .subscribe(healbeGoBe -> startConnect(healbeGoBe.getName()), Timber::e));
    }

    private void startConnect(String name) {
        statusConnecting(name);
        destroy.add(HealbeSdk.get().GOBE.connect()
                .subscribe(() -> {}, Timber::e));
    }

    private void disconnectAnd(Runnable r) {
        destroy.clear();
        destroy.add(HealbeSdk.get().GOBE.disconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r::run, Timber::e));
    }

    private void statusConnecting(String name) {
        this.name = name;
        status.setText(getString(R.string.connecting_to, name));
    }

    private void statusWaiting(String name) {
        status.setText(getString(R.string.waiting, name));
    }

    private void requestPin() {
        getRouter().goState(ConnectRouter.State.ENTER_PIN, false);
    }

    private void requestNewPin() {
        getRouter().goState(ConnectRouter.State.SETUP_PIN, false);
    }

    private void goError() {
        disconnectAnd(() -> getRouter().goState(ConnectRouter.State.ERROR, false));
    }

    private void goSearch() {
        disconnectAnd(() -> getRouter().goState(ConnectRouter.State.SEARCH, false));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy.clear();
    }

    @Override
    public boolean consumeBackPressed() {
        goSearch();
        return true;
    }
}
