package com.healbe.healbe_example_andorid.connect.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.ConnectRouter;
import com.healbe.healbe_example_andorid.connect.ConnectionRoutedFragment;
import com.healbe.healbe_example_andorid.tools.TextWatcherAdapter;
import com.healbe.healbesdk.business_api.HealbeSdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;


@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class EnterPinFragment extends ConnectionRoutedFragment {
    private CompositeDisposable destroy = new CompositeDisposable();

    private TextInputLayout pinCodeTil;
    private Button skipButton;
    private View progress;
    private EditText pinCode;
    private TextView pinHelper;

    public static EnterPinFragment newInstance() {
        return new EnterPinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enter_pin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pinCodeTil = view.findViewById(R.id.pin_code_til);
        pinCode = view.findViewById(R.id.pin_code);
        skipButton = view.findViewById(R.id.skip_button);
        progress = view.findViewById(R.id.progress);
        pinHelper = view.findViewById(R.id.pin_helper);

        pinCodeTil.setErrorEnabled(false);

        pinCode.addTextChangedListener(new TextWatcherAdapter(this::checkPin));
        skipButton.setOnClickListener(v -> goSearch());
        pinHelper.setText(R.string.pin_helper);

        progress.setVisibility(View.INVISIBLE);
    }

    private void checkPin() {
        String pin = pinCode.getText().toString();
        if (!TextUtils.isEmpty(pin) && pin.length() == 6 && destroy.size() == 0) {
            progress.setVisibility(View.VISIBLE);
            //set pin for connection and go connection screen
            destroy.add(HealbeSdk.get().GOBE.setPin(pin)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> progress.setVisibility(View.VISIBLE))
                    .doOnError(throwable -> progress.setVisibility(View.VISIBLE))
                    .doOnError(throwable -> Toast.makeText(getContext(), "Something goes wrong, try to delete and retry pin-code", Toast.LENGTH_LONG).show())
                    .subscribe(this::goConnect, Timber::e));
        }
    }

    private void goConnect() {
        getRouter().goState(ConnectRouter.State.CONNECT, true);
    }

    private void disconnectAnd(Runnable r) {
        destroy.add(HealbeSdk.get().GOBE.disconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r::run, Timber::e));
    }

    private void goSearch() {
        disconnectAnd(() -> getRouter().goState(ConnectRouter.State.SEARCH, false));
    }

    @Override
    public boolean consumeBackPressed() {
        goSearch();
        return true;
    }
}
