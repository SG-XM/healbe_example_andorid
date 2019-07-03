package com.healbe.healbe_example_andorid.connect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.ConnectRouter;
import com.healbe.healbe_example_andorid.connect.ConnectionRoutedFragment;
import com.healbe.healbesdk.business_api.HealbeSdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class ErrorFragment extends ConnectionRoutedFragment {
    private CompositeDisposable destroy = new CompositeDisposable();

    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button back = view.findViewById(R.id.skip_button);
        back.setOnClickListener(v-> goSearch());
    }

    private void disconnectAnd(Runnable r) {
        destroy.add(HealbeSdk.get().GOBE.disconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r::run, Timber::e));
    }

    private void goSearch() {
        disconnectAnd(()->getRouter().goState(ConnectRouter.State.SEARCH, false));
    }

    @Override
    public boolean consumeBackPressed() {
        goSearch();
        return true;
    }
}
