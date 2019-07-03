package com.healbe.healbe_example_andorid.dashboard;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbesdk.business_api.tasks.entity.TasksState;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import static com.healbe.healbesdk.device_api.ClientState.READY;


/**
 * Created by Alexey on 06.03.2018.
 */

@SuppressWarnings("WeakerAccess")
public class SensorViewHolder {
    WristbandStateInfo wristbandStateInfo = null;
    View v;
    CardView round;
    ProgressBar progressBar;
    TextView title;
    TextView status;
    TextView indi;
    ImageView bluetooth;
    TextView warnText;
    ImageView badContact;
    View warnCont;
    View div;

    public SensorViewHolder(View itemView) {
        v = itemView;
        round = v.findViewById(R.id.round);
        progressBar = v.findViewById(R.id.progress);
        title = v.findViewById(R.id.title);
        status = v.findViewById(R.id.status);
        indi = v.findViewById(R.id.indicator);
        bluetooth = v.findViewById(R.id.bluetooth);
        warnText = v.findViewById(R.id.warn_text);
        badContact = v.findViewById(R.id.bad_contact);
        warnCont = v.findViewById(R.id.warn_cont);
        div = v.findViewById(R.id.div);

    }

    private void setWristbandName(WristbandStateInfo stateInfo) {
        title.setText(stateInfo.getWristbandName());
    }

    public enum DeviceState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        SYNCING,;

        public static DeviceState fromStates(WristbandStateInfo stateInfo) {
            TasksState tasksState = stateInfo.getTasksState();

            if (stateInfo.getClientState() == READY)
                if (tasksState == TasksState.SYNC) {
                    return SYNCING;
                } else {
                    return CONNECTED;
                }

            if (stateInfo.getBtState() == WristbandStateInfo.BluetoothState.BT_ACTIVE)
                return CONNECTING;

            return DISCONNECTED;
        }
    }

    private void setConnectionState(WristbandStateInfo stateInfo) {
        progressBar.setVisibility(View.GONE);
        DeviceState deviceState = DeviceState.fromStates(stateInfo);

        switch (deviceState) {
            case CONNECTED:
            case SYNCING:
                round.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.main_purple));
                indi.setVisibility(View.VISIBLE);
                if (stateInfo.getTasksState() != TasksState.SYNC)
                    status.setText(R.string.connected);
                status.setTextColor(ContextCompat.getColor(v.getContext(), R.color.light_black_54));
                break;

            case CONNECTING:
                status.setText(R.string.connecting);
                status.setTextColor(ContextCompat.getColor(v.getContext(), R.color.light_black_54));
                round.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.light_black_38));
                indi.setVisibility(View.GONE);
                break;

            case DISCONNECTED:
                round.setCardBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.light_black_38));
                status.setTextColor(ContextCompat.getColor(v.getContext(), R.color.light_black_54));
                status.setText(R.string.disconnected);
                indi.setVisibility(View.GONE);
                break;

            default:
                indi.setVisibility(View.GONE);
        }

    }

    private void setSyncProgress(WristbandStateInfo stateInfo) {
        TasksState tasksState = stateInfo.getTasksState();
        int progress = stateInfo.getSyncProgress();

        if (tasksState == TasksState.SYNC && stateInfo.getClientState() == READY) {
            status.setText(v.getContext().getString(R.string.synchronization_p, progress));
            progressBar.setProgress(progress);

            if (progressBar.getVisibility() != View.VISIBLE)
                progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

    }

    private void setBluetoothState(WristbandStateInfo stateInfo) {
        WristbandStateInfo.BluetoothState bluetoothState = stateInfo.getBtState();
        if (bluetoothState == WristbandStateInfo.BluetoothState.BT_ACTIVE) {
            bluetooth.setImageResource(R.drawable.ic_bluetooth_search);
        } else if (bluetoothState == WristbandStateInfo.BluetoothState.BT_ON) {
            bluetooth.setImageResource(R.drawable.ic_bluetooth);
        } else if (bluetoothState == WristbandStateInfo.BluetoothState.BT_OFF) {
            bluetooth.setImageResource(R.drawable.ic_bluetooth_not_connected);
        } else {
            bluetooth.setImageResource(R.drawable.ic_bluetooth_connected_black);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setChargingValue(WristbandStateInfo stateInfo) {
        int percents = stateInfo.getBatteryLevel();
        indi.setVisibility(percents >= 0 && percents <= 101 && stateInfo.getClientState() == READY ? View.VISIBLE : View.GONE);
        if (percents == 101)
            indi.setText("Charging");
        else
            indi.setText(percents + "%");
    }

    public void setWristbandStateInfo(WristbandStateInfo stateInfo) {
        wristbandStateInfo = stateInfo;
        setWristbandName(stateInfo);
        setConnectionState(stateInfo);
        setBluetoothState(stateInfo);
        setSyncProgress(stateInfo);
        setChargingValue(stateInfo);
        setBottomView(stateInfo);
    }

    public void setBottomView(WristbandStateInfo stateInfo) {
        if (stateInfo.getBtState() == WristbandStateInfo.BluetoothState.BT_OFF) {
            //content
            warnText.setText(R.string.enable_bluetooth);
            warnText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.validation_red));
            badContact.setImageResource(R.drawable.ic_cant_find);
            //visibility
            warnText.setVisibility(View.VISIBLE);
            badContact.setVisibility(View.GONE);
            warnCont.setVisibility(View.VISIBLE);
            div.setVisibility(View.VISIBLE);
        } else if (stateInfo.isBleError()) {
            warnText.setText(R.string.bluetooth_error);
            warnText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.validation_red));
            badContact.setImageResource(R.drawable.ic_cant_find);
            //visibility
            warnText.setVisibility(View.VISIBLE);
            badContact.setVisibility(View.VISIBLE);
            warnCont.setVisibility(View.VISIBLE);
            div.setVisibility(View.VISIBLE);
        } else if (stateInfo.isConnectionError()) {
            warnText.setText(R.string.gobe_connection_error_short);
            warnText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.validation_red));
            badContact.setImageResource(R.drawable.ic_cant_find);
            //visibility
            warnText.setVisibility(View.VISIBLE);
            badContact.setVisibility(View.VISIBLE);
            warnCont.setVisibility(View.VISIBLE);
            div.setVisibility(View.VISIBLE);
        } else if (!stateInfo.isOnHand() && stateInfo.getClientState() == READY && stateInfo.getBatteryLevel() != 101) {
            //content
            warnText.setText(R.string.no_contact);
            warnText.setTextColor(ContextCompat.getColor(v.getContext(), R.color.light_black_54));
            badContact.setImageResource(R.drawable.ic_bad_contact);
            warnText.setVisibility(View.VISIBLE);
            badContact.setVisibility(View.VISIBLE);
            warnCont.setVisibility(View.VISIBLE);
            div.setVisibility(View.VISIBLE);
        } else {
            //visibility
            warnText.setVisibility(View.GONE);
            badContact.setVisibility(View.GONE);
            warnCont.setVisibility(View.GONE);
            div.setVisibility(View.GONE);
        }
    }
}
