package com.healbe.healbe_example_andorid.dashboard;

import com.healbe.healbe_example_andorid.tools.Bluetooth;
import com.healbe.healbesdk.business_api.gobe.firmware.FirmwareSummary;
import com.healbe.healbesdk.business_api.tasks.entity.TasksState;
import com.healbe.healbesdk.device_api.ClientState;

/**
 * Created by Alexey on 12.03.2018.
 */

@SuppressWarnings({"WeakerAccess"})
public class WristbandStateInfo {
    public enum  BluetoothState {
        BT_OFF, BT_ON, BT_ACTIVE, BT_ONLINE
    }

    private boolean onHand;
    private ClientState clientState;
    private String wristbandName;
    private String wristbandMac;
    private int batteryLevel;
    private BluetoothState btState;
    private int syncProgress;
    private TasksState tasksState;
    private FirmwareSummary fwData;
    private boolean bleError;
    private boolean connectionError;
    private long firstTs;

    public WristbandStateInfo() {
        this.onHand = true;
        this.clientState = ClientState.DISCONNECTED;
        this.wristbandName = "";
        this.wristbandMac = "-";
        this.batteryLevel = -1;
        this.btState = Bluetooth.isOn() ? BluetoothState.BT_ON : BluetoothState.BT_OFF;
        this.syncProgress = -1;
        this.tasksState = TasksState.STOPPED;
        this.fwData = FirmwareSummary.EMPTY;
        this.bleError = false;
        this.connectionError = false;
        this.firstTs = 0;
    }

    public boolean isOnHand() {
        return onHand;
    }

    public void setOnHand(boolean onHand) {
        this.onHand = onHand;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setClientState(ClientState clientState) {
        this.clientState = clientState;
    }

    public String getWristbandName() {
        return wristbandName;
    }

    public void setWristbandName(String wristbandName) {
        this.wristbandName = wristbandName;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public BluetoothState getBtState() {
        return btState;
    }

    public void setBtState(BluetoothState btState) {
        this.btState = btState;
    }

    public int getSyncProgress() {
        return syncProgress;
    }

    public void setSyncProgress(int syncProgress) {
        this.syncProgress = syncProgress;
    }

    public TasksState getTasksState() {
        return tasksState;
    }

    public void setTasksState(TasksState tasksState) {
        this.tasksState = tasksState;
    }

    public boolean isBleError() {
        return bleError;
    }

    public void setBleError(boolean bleError) {
        this.bleError = bleError;
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public void setConnectionError(boolean connectionError) {
        this.connectionError = connectionError;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "WristbandStateInfo{" +
                "onHand=" + onHand +
                ", clientState=" + clientState +
                ", wristbandName='" + wristbandName + '\'' +
                ", wristbandMac='" + wristbandMac + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", btState=" + btState +
                ", syncProgress=" + syncProgress +
                ", tasksState=" + tasksState +
                ", fwData=" + fwData +
                ", bleError=" + bleError +
                ", connectionError=" + connectionError +
                ", firstTs=" + firstTs +
                '}';
    }
}
