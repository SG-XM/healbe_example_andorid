package com.healbe.healbe_example_andorid.connect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.connect.ConnectRouter;
import com.healbe.healbe_example_andorid.tools.Bluetooth;
import com.healbe.healbe_example_andorid.connect.ConnectionRoutedFragment;
import com.healbe.healbe_example_andorid.tools.Permissions;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.business_api.user_storage.entity.HealbeDevice;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;

public class SearchFragment extends ConnectionRoutedFragment {
    private ImageView placeholderImage;
    private RecyclerView list;
    private View divider;
    private ProgressBar progressBar;
    private ImageView errorIcon;
    private TextView searchingText;
    private SearchAdapter adapter;
    private Button logoutButton;
    private Button retryButton;

    private CompositeDisposable destroy = new CompositeDisposable();
    private Disposable searchSubscription; // subscription for search

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeholderImage = view.findViewById(R.id.placeholder);
        searchingText = view.findViewById(R.id.searching);
        errorIcon = view.findViewById(R.id.error_icon);
        progressBar = view.findViewById(R.id.progress);
        list = view.findViewById(R.id.list);
        logoutButton = view.findViewById(R.id.logout_button);
        retryButton = view.findViewById(R.id.retry_button);
        divider = view.findViewById(R.id.divider);

        adapter = new SearchAdapter();
        adapter.setListener(v -> setDevice(adapter.get(list.getChildLayoutPosition(v)), adapter.getSaved()));
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
        initialState();

        // check necessary permissions in chain
        checkBluetoothPerm();

        logoutButton.setOnClickListener(v -> {
            stopScan();
            getRouter().logout();
        });

        retryButton.setOnClickListener(v -> startScan());
    }

    private void setDevice(HealbeDevice device, HealbeDevice saved) {
        Timber.d("setDevice %s:%s", device.getName(), device.getMac());

        if (device.equals(saved))
            device = new HealbeDevice(device.getName(), saved.getMac(), saved.getPin(), saved.isActive());

        //set default device to locale storage for using in future
        destroy.add(HealbeSdk.get().GOBE.set(device)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Timber.e("ok");
                            stopScan();
                            getRouter().goState(ConnectRouter.State.CONNECT, false);
                        }, Timber::e));
    }


    // check necessary bluetooth permission
    private void checkBluetoothPerm() {
        Permissions.requestPerm(boolValue -> {
            if (boolValue) {
                // turn on bluetooth if turned off
                if (Bluetooth.isOn())
                    checkLocationPerm();
                else
                    //noinspection ResultOfMethodCallIgnored
                    Bluetooth.setOn().subscribe(this::checkLocationPerm, Timber::e);
            } else
                // warn and retry or logout
                Permissions.warnUserPermission(getActivity(), R.string.warn_blu_permission,
                        this::checkBluetoothPerm, () -> getRouter().logout());

        }, BLUETOOTH, getActivity());
    }

    // check necessary location permission
    private void checkLocationPerm() {
        Permissions.requestPerm(boolValue -> {
            if (boolValue)
                startScan();
            else
                Permissions.warnUserPermission(getActivity(), R.string.warn_blu_permission,
                        this::checkLocationPerm, () -> getRouter().logout());
        }, ACCESS_COARSE_LOCATION, getActivity());
    }


    private void initialState() {
        placeholderImage.setVisibility(View.VISIBLE);
        searchingText.setText(R.string.searching_devices_nearby);
        errorIcon.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        list.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
    }

    private void scanStartState() {
        placeholderImage.setVisibility(View.VISIBLE);
        searchingText.setText(R.string.searching_devices_nearby);
        errorIcon.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        list.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
    }

    private void scanFoundAndContinueScanState() {
        placeholderImage.setVisibility(View.INVISIBLE);
        searchingText.setText(R.string.searching_devices_nearby);
        errorIcon.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.VISIBLE);
    }

    private void scanFoundAndStopState() {
        placeholderImage.setVisibility(View.INVISIBLE);
        searchingText.setText(R.string.searching_devices_nearby);
        errorIcon.setImageResource(R.drawable.ic_success);
        errorIcon.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        list.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
    }

    private void scanNotFoundAndStopState() {
        placeholderImage.setVisibility(View.VISIBLE);
        searchingText.setText(R.string.searching_devices_not_found);
        errorIcon.setImageResource(R.drawable.ic_error);
        errorIcon.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        list.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        divider.setVisibility(View.INVISIBLE);
    }

    private void startScan() {
        scanStartState();
        adapter.clear();
        destroy.add(HealbeSdk.get().GOBE.get()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(healbeGoBe -> adapter.setSaved(healbeGoBe), Timber::e));

        searchSubscription =
                HealbeSdk.get().GOBE.scan() // start devices scanning for 15 sec
                        .observeOn(AndroidSchedulers.mainThread())
                        .take(15, TimeUnit.SECONDS)
                        .doFinally(() -> {
                            if (!searchSubscription.isDisposed())
                                searchSubscription.dispose();
                        })
                        .subscribe(
                                healbeGoBe -> {
                                    scanFoundAndContinueScanState();
                                    Timber.d("found device: [%s:%s]", healbeGoBe.getName(), healbeGoBe.getMac());
                                    adapter.add(healbeGoBe);
                                },
                                e -> showErrorIfEmpty(),
                                this::showErrorIfEmpty);

        destroy.add(searchSubscription); // unsubcribe this on destroy too
    }

    private void showErrorIfEmpty() {
        stopScan();
        if (adapter.devices.isEmpty()) {
            Timber.d("Error [no devices  found]!");
            scanNotFoundAndStopState();
        } else {
            scanFoundAndStopState();
        }
    }

    private void stopScan() {
        if (searchSubscription != null)
            searchSubscription.dispose();
    }

    @Override
    public boolean consumeBackPressed() {
        stopScan();
        getRouter().logout();
        return true;
    }
}
