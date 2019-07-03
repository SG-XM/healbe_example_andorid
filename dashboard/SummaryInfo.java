package com.healbe.healbe_example_andorid.dashboard;

import com.healbe.healbesdk.business_api.healthdata.data.DaySummary;
import com.healbe.healbesdk.business_api.healthdata.data.stress.StressState;
import com.healbe.healbesdk.business_api.healthdata.data.water.HydrationState;
import com.healbe.healbesdk.business_api.tasks.entity.HeartRate;
import com.healbe.healbesdk.business_api.user.data.WeightUnits;
import com.healbe.healbesdk.device_api.ClientState;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
class SummaryInfo {
    private ClientState clientState;
    private boolean onHand;
    private DaySummary daySummary;
    private HeartRate heartRate;
    private HydrationState hydrationState;
    private StressState stressState;
    private float stressLevel;
    private WeightUnits weightUnits;

    public SummaryInfo() {
        this.clientState = ClientState.DISCONNECTED;
        this.onHand = false;
        this.daySummary = new DaySummary();
        this.heartRate = new HeartRate();
        this.hydrationState = HydrationState.NO_DATA;
        this.stressState = StressState.NO_DATA;
    }

    public SummaryInfo(ClientState clientState, boolean onHand, DaySummary daySummary, HeartRate heartRate, HydrationState hydrationState, StressState stressState, float stressLevel) {
        this.clientState = clientState;
        this.onHand = onHand;
        this.daySummary = daySummary;
        this.heartRate = heartRate;
        this.hydrationState = hydrationState;
        this.stressState = stressState;
        this.stressLevel = stressLevel;
    }


    public SummaryInfo(ClientState clientState, boolean onHand, DaySummary daySummary, HeartRate heartRate) {
        this.clientState = clientState;
        this.onHand = onHand;
        this.daySummary = daySummary;
        this.heartRate = heartRate;
    }

    public boolean isConnected() {
        return clientState == ClientState.READY;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setClientState(ClientState clientState) {
        this.clientState = clientState;
    }

    public boolean isOnHand() {
        return onHand;
    }

    public void setOnHand(boolean onHand) {
        this.onHand = onHand;
    }

    public DaySummary getDaySummary() {
        return daySummary;
    }

    public void setDaySummary(DaySummary daySummary) {
        this.daySummary = daySummary;
    }

    public HeartRate getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(HeartRate heartRate) {
        this.heartRate = heartRate;
    }

    public HydrationState getHydrationState() {
        return hydrationState;
    }

    public void setHydrationState(HydrationState hydrationState) {
        this.hydrationState = hydrationState;
    }

    public StressState getStressState() {
        return stressState;
    }

    public void setStressState(StressState stressState) {
        this.stressState = stressState;
    }

    public float getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(float stressLevel) {
        this.stressLevel = stressLevel;
    }

    public WeightUnits getWeightUnits() {
        return weightUnits;
    }

    public void setWeightUnits(WeightUnits weightUnits) {
        this.weightUnits = weightUnits;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryInfo that = (SummaryInfo) o;
        return onHand == that.onHand &&
                Float.compare(that.stressLevel, stressLevel) == 0 &&
                clientState == that.clientState &&
                Objects.equals(daySummary, that.daySummary) &&
                Objects.equals(heartRate, that.heartRate) &&
                hydrationState == that.hydrationState &&
                stressState == that.stressState &&
                weightUnits == that.weightUnits;
    }

    @Override
    public int hashCode() {

        return Objects.hash(clientState, onHand, daySummary, heartRate, hydrationState, stressState, stressLevel, weightUnits);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "SummaryInfo{" +
                "clientState=" + clientState +
                ", onHand=" + onHand +
                ", daySummary=" + daySummary +
                ", heartRate=" + heartRate +
                ", hydrationState=" + hydrationState +
                ", stressState=" + stressState +
                ", stressLevel=" + stressLevel +
                ", weightUnits=" + weightUnits +
                '}';
    }
}
