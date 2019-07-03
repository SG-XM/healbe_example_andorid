package com.healbe.healbe_example_andorid.dashboard;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.tools.BluetoothBroadcastReceiver;
import com.healbe.healbe_example_andorid.tools.SystemBarManager;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.business_api.healthdata.data.heart.BloodPressure;
import com.healbe.healbesdk.business_api.healthdata.data.stress.StressState;
import com.healbe.healbesdk.business_api.healthdata.data.water.HydrationState;
import com.healbe.healbesdk.business_api.tasks.entity.SensorState;
import com.healbe.healbesdk.business_api.user_storage.entity.HealbeDevice;
import com.healbe.healbesdk.device_api.BLEState;
import com.healbe.healbesdk.device_api.ClientState;
import com.healbe.healbesdk.utils.RxUtils;
import com.healbe.healbesdk.utils.groups.Triple;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DashboardFragment extends Fragment {
    private CompositeDisposable destroy = new CompositeDisposable();

    // here we use holders for visualize combined data
    private SummaryViewHolder sumHolder;
    private SensorViewHolder senHolder;

    // error cache for not blinking error states
    private boolean ble_error = false;
    private boolean wb_error = false;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    private void initPadding(View v) {
        View root = v.findViewById(R.id.root);
        SystemBarManager tintManager = new SystemBarManager(getActivity());
        SystemBarManager.SystemBarConfig config = tintManager.getConfig();
        root.setPadding(root.getPaddingLeft(), root.getPaddingTop(), root.getPaddingRight(), config.getPixelInsetBottom());
    }

    //TODO 13.数据计算和显示：（所有数据通过HealbeSdk.get() GOBE，TASKS，ARCHIVE获取基础信息计算）
    // setSummary计算能量，心率，水合状态，血压，压力，睡眠数据；
    // setWristbandStateInfo获取设备数据：设备名称，电量，连接状态，蓝牙状态，同步进程以及异常提示
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPadding(view);

        sumHolder = new SummaryViewHolder(view);
        senHolder = new SensorViewHolder(view);

        observeToday();
        observeWbState();
    }

    //observe data and combine it to summary
    private void observeToday() {
        destroy.add(Observable.combineLatest(
                HealbeSdk.get().GOBE.observeConnectionState(),
                HealbeSdk.get().TASKS.observeSensorState().map(SensorState::isOnHand),
                HealbeSdk.get().ARCHIVE.getAllDaySummaries(0).toObservable(),
                HealbeSdk.get().TASKS.observeHeartRate(),
                HealbeSdk.get().GOBE.measureBloodPressure().toObservable(),
                SummaryInfo::new)
                .observeOn(Schedulers.computation())
                .distinctUntilChanged(SummaryInfo::equals)
                .map(this::setCurrents)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sumHolder::setSummary, Timber::e));
    }

    // adding current values to summary
    private SummaryInfo setCurrents(SummaryInfo t) {
        Log.e("setCurrents", t.getBloodPressure().toString());
        Log.e("setCurrents", t.getHeartRate().toString());

        Triple<Float, StressState, HydrationState> triple = RxUtils.combine(
                HealbeSdk.get().ARCHIVE.getCurrentStressLevel(),
                HealbeSdk.get().ARCHIVE.getCurrentStressState(),
                HealbeSdk.get().ARCHIVE.getHydrationState()).blockingFirst();
        t.setStressLevel(triple.getFirst());
        t.setStressState(triple.getSecond());
        t.setHydrationState(triple.getThird());
        return t;
    }

    // observe combined state data for wristband
    private void observeWbState() {
        destroy.add(Observable.combineLatest(
                HealbeSdk.get().GOBE.observeConnectionState(),
                HealbeSdk.get().TASKS.observeSensorState(),
                HealbeSdk.get().TASKS.observeTasksState(),
                HealbeSdk.get().TASKS.observeSyncProgress(),
                BluetoothBroadcastReceiver.get().observeBluetoothState(),
                HealbeSdk.get().GOBE.observeBleState(), Six::new)
                .observeOn(Schedulers.computation())
                .map(pack -> {
                    WristbandStateInfo wbState = new WristbandStateInfo();

                    ClientState clientState = pack.getFirst();
                    wbState.setClientState(clientState);
                    if (pack.getFifth() != BluetoothAdapter.STATE_ON) {
                        wbState.setClientState(ClientState.DISCONNECTED);
                        wbState.setBtState(WristbandStateInfo.BluetoothState.BT_OFF);
                    } else if ((clientState == ClientState.DISCONNECTED
                            || clientState == ClientState.DISCONNECTING)
                            && HealbeSdk.get().GOBE.isConnectionStarted()) {
                        clientState = ClientState.CONNECTING;
                        wbState.setBtState(WristbandStateInfo.BluetoothState.BT_ACTIVE);
                    } else if (isBtActive(clientState))
                        wbState.setBtState(WristbandStateInfo.BluetoothState.BT_ACTIVE);
                    else if (clientState == ClientState.READY)
                        wbState.setBtState(WristbandStateInfo.BluetoothState.BT_ONLINE);
                    else
                        wbState.setBtState(WristbandStateInfo.BluetoothState.BT_ON);

                    SensorState ss = pack.getSecond();
                    HealbeDevice goBe = HealbeSdk.get().GOBE.get().blockingGet();

                    String name = goBe.getName();
                    if (TextUtils.isEmpty(name))
                        name = "Healbe GoBe";

                    wbState.setWristbandName(name);
                    wbState.setBatteryLevel(ss.getBatteryLevel());
                    wbState.setOnHand(ss.isOnHand());
                    wbState.setTasksState(pack.getThird());
                    wbState.setSyncProgress(pack.getFourth());

                    // ble error shows from it appears to next connect
                    if (pack.getSixth().getStatus() == BLEState.Status.INTERNAL_GATT_ERROR
                            || pack.getSixth().getStatus() == BLEState.Status.DISCOVERY_TIMEOUT) {
                        ble_error = true;
                        wbState.setBleError(true);
                    } else if (clientState == ClientState.CONNECTED) {
                        wbState.setBleError(false);
                        ble_error = false;
                    } else wbState.setBleError(ble_error);

                    // not found error shows from it appears to next connect with ble  erorr priority
                    if (!wb_error)
                        wb_error = (!wbState.isBleError() && clientState == ClientState.CONNECTING &&
                                (pack.getSixth().getStatus() == BLEState.Status.NOT_FOUND || pack.getSixth().getStatus() == BLEState.Status.OUT_OF_RANGE));
                    else
                        wb_error = !wbState.isBleError() && clientState == ClientState.CONNECTING;

                    wbState.setConnectionError(wb_error);

                    return wbState;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wristbandStateInfo -> senHolder.setWristbandStateInfo(wristbandStateInfo), Timber::e));
    }

    private static boolean isBtActive(ClientState clientState) {
        // all state we see when wristband connecting
        switch (clientState) {
            case CONNECTING:
            case CONNECTED:
            case AUTHORIZING:
            case AUTHORIZED:
            case FW_CHECKING:
            case FW_CHECKED:
            case CONFIGURING:
            case CONFIGURED:
            case INITIALIZING:
            case INITIALIZED:
            case REQUEST_INITIALIZING:
            case REQUEST_RESET_SENSOR:
            case REQUEST_SENSOR_START:
            case REQUEST_USER_INFO:
            case WAIT_FOR_RESTART:
            case RESTARTING:
            case USER_INFO_VALIDATING:
            case USER_INFO_VALIDATED:
            case USER_CONFIG_VALIDATING:
            case USER_CONFIG_VALIDATED:
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy.clear();
    }
}
