package com.healbe.healbe_example_andorid.dashboard;

import android.view.View;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.tools.UnitsFormatter;
import com.healbe.healbe_example_andorid.views.DashboardView;
import com.healbe.healbesdk.business_api.healthdata.data.heart.Average;
import com.healbe.healbesdk.business_api.healthdata.data.heart.AverageType;
import com.healbe.healbesdk.business_api.healthdata.data.sleep.SleepRecommendations;
import com.healbe.healbesdk.business_api.healthdata.data.stress.StressState;
import com.healbe.healbesdk.business_api.healthdata.data.water.HydrationState;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;

/**
 * Created by Alexey on 06.03.2018.
 */

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class SummaryViewHolder {
    private View v;
    SummaryInfo todaySummaryInfo = null;

    DashboardView energyBar;
    DashboardView waterBar;
    DashboardView heartBar;
    DashboardView stressBar;
    DashboardView sleepBar;

    public SummaryViewHolder(View itemView) {
        v = itemView;

        energyBar = v.findViewById(R.id.energy_bar);
        waterBar = v.findViewById(R.id.water_bar);
        heartBar = v.findViewById(R.id.heart_bar);
        stressBar = v.findViewById(R.id.stress_bar);
        sleepBar = v.findViewById(R.id.sleep_bar);

    }

    boolean isNight() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 - TimeZone.getDefault().getRawOffset() / (1000 * 60) > 9 * 60
                || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 - TimeZone.getDefault().getRawOffset() / (1000 * 60) < 6 * 60;
    }


    @SuppressWarnings("ConstantConditions")
    private boolean setEnergySum(SummaryInfo o) {
        boolean connected = o.isConnected();
        boolean onHand = o.isOnHand();

        boolean energyBalancePresent = !o.getDaySummary().getEnergySummary().isEmpty()
                && o.getDaySummary().getEnergySummary().get().getEnergyOut() > 0;

        boolean energyPresent = energyBalancePresent
                && o.getDaySummary().getEnergySummary().get().getSteps() > 0
                && o.getDaySummary().getEnergySummary().get().getWalkingMins() > 0;

        // text to out
        int active = 0;

        if (energyPresent)
            active = o.getDaySummary().getEnergySummary().get().getWalkingMins()
                    + o.getDaySummary().getEnergySummary().get().getRunningMins()
                    + o.getDaySummary().getEnergySummary().get().getRoutineMins();

        String energyText = !energyPresent ? "" :
                UnitsFormatter.stepsAndTime(v.getContext(),
                        o.getDaySummary().getEnergySummary().get().getSteps(), active).toString();

        CharSequence energyValue = energyBalancePresent
                ? UnitsFormatter.energyBalance(v.getContext(),
                (int) (o.getDaySummary().getEnergySummary().get().getEnergyIn() -
                        o.getDaySummary().getEnergySummary().get().getEnergyOut()), true) : "";

        energyBar.getRound().setCardBackgroundColor(connected && onHand
                ? getColor(R.color.secondary_purple)
                : getColor(R.color.light_black_38));
        energyBar.setTitleText(R.string.energy_header);
        energyBar.setTextVisible(energyPresent);
        energyBar.setText(energyText);
        energyBar.setValueVisible(energyBalancePresent);
        energyBar.setValueText(energyValue);

        energyBar.setVisibility(View.VISIBLE);

        return energyBalancePresent;
    }

    @SuppressWarnings({"KotlinInternalInJava", "ConstantConditions"})
    private boolean setHydrationSum(SummaryInfo o) {
        boolean connected = o.isConnected();
        boolean onHand = o.isOnHand();

        boolean waterCurPresent = o.getHydrationState() != HydrationState.NO_DATA;
        boolean waterCalc = o.getHydrationState() == HydrationState.CALCULATING;

        boolean waterSumPr = !o.getDaySummary().getHydrationSummary().isEmpty();
        boolean waterSumPresent = waterSumPr;

        if (waterSumPr)
            waterSumPresent = (o.getDaySummary().getHydrationSummary().get().lowDuration() +
                    o.getDaySummary().getHydrationSummary().get().normalDuration()) > 0;

        String waterText = waterCalc ? v.getResources().getString(R.string.may_take_up_to_hour) : "";

        CharSequence waterValueCur = waterCurPresent
                ? UnitsFormatter.formatHydrationState(v.getContext(), o.getHydrationState()) : "";

        //Timber.d(waterValueCur.toString());

        waterBar.getRound().setCardBackgroundColor(connected && onHand
                ? getColor(R.color.main_water_blue)
                : getColor(R.color.light_black_38));


        waterBar.setTitleText(connected && onHand ? R.string.hydration_header_cur : R.string.hydration_header);
        waterBar.setValueText(waterValueCur);
        waterBar.setTextVisible(waterCalc);
        waterBar.setText(waterText);
        waterBar.setValueVisible(waterCurPresent);

        waterBar.setVisibility(View.VISIBLE);
        return waterSumPresent;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean setHeartSum(SummaryInfo o) {
        boolean connected = o.isConnected();
        boolean onHand = o.isOnHand();

        boolean pulseSumPresent = !o.getDaySummary().getHeartSummary().isEmpty();
        Average awakeAverage = o.getDaySummary().getHeartSummary().get().getAverages().get(AverageType.AWAKE);
        Average sleepAverage = o.getDaySummary().getHeartSummary().get().getAverages().get(AverageType.ASLEEP_LAST_NIGHT);

        boolean pulseStatPresent = pulseSumPresent
                && ((awakeAverage != null && awakeAverage.getAverage() > 0)
                        || (sleepAverage != null && sleepAverage.getAverage() > 0));

        boolean pulseCurPresent = o.getHeartRate().isValid();

        String heartText = !pulseStatPresent ? ""
                : UnitsFormatter.formatPulseSleepAwake(v.getContext(),
                awakeAverage != null ? awakeAverage.getAverage() : 0,
                sleepAverage != null ? sleepAverage.getAverage() : 0);

        CharSequence heartValueCur = pulseCurPresent
                ? UnitsFormatter.pulseSpan(v.getContext(), o.getHeartRate().getHeartRate()) : "";

        heartBar.getRound().setCardBackgroundColor(connected && onHand
                ? getColor(R.color.main_heart_red)
                : getColor(R.color.light_black_38));

        heartBar.setTitleText(connected && onHand ? R.string.pulse_header_cur : R.string.pulse_header);
        heartBar.setTextVisible(pulseStatPresent);
        heartBar.setText(heartText);
        heartBar.setValueVisible(pulseCurPresent);
        heartBar.setValueText(heartValueCur);

        heartBar.setVisibility(View.VISIBLE);

        return pulseSumPresent;
    }

    private boolean setStressSum(SummaryInfo o) {
        boolean connected = o.isConnected();
        boolean onHand = o.isOnHand();
        boolean stressCurPresent = o.getStressState() != StressState.NO_DATA;
        boolean stressCalc = o.getStressState() == StressState.CALCULATING;
        boolean stressSumPresent = !o.getDaySummary().getStressSummary().isEmpty();

        String stressText = stressCalc ? v.getResources().getString(R.string.may_take_up_to_hour) : "";

        CharSequence stressValue = stressCurPresent
                ? UnitsFormatter.formatStressState(v.getContext(), o.getStressState(), o.getStressLevel()) : "";

        stressBar.getRound().setCardBackgroundColor(connected && onHand
                ? getColor(R.color.secondary_stress_orange)
                : getColor(R.color.light_black_38));

        stressBar.setTitleText(connected && onHand ? R.string.stress_header_cur : R.string.stress_header);
        stressBar.setTextVisible(stressCalc);
        stressBar.setValueVisible(stressCurPresent);
        stressBar.setText(stressText);
        stressBar.setValueText(stressValue);

        stressBar.setVisibility(View.VISIBLE);


        return stressSumPresent;
    }


    @SuppressWarnings("ConstantConditions")
    private boolean setSleepSum(SummaryInfo o) {
        boolean connected = o.isConnected();
        boolean onHand = o.isOnHand();

        boolean sleepPresent = !o.getDaySummary().getSleepSummary().isEmpty();
        boolean night = isNight();

        SleepRecommendations sleepRecom = o.getDaySummary().getSleepRecommendations().get();
        int recSleepDuration = sleepRecom != null ? sleepRecom.getRecommendedSleepDuration() : 0;
        String sleepText = !sleepPresent ? "" : (
                night ? v.getContext().getString(R.string.recommended,
                        UnitsFormatter.timePeriodFromMins(v.getContext(), recSleepDuration))
                        : v.getContext().getString(R.string.sleep_quality).toLowerCase() + ": "
                        + o.getDaySummary().getSleepSummary().get().getQuality() + "%");



        int sleepDur = sleepPresent ? (int) o.getDaySummary().getSleepSummary().get().getSleepDuration(TimeUnit.MINUTES) : 0;
        CharSequence sleepValue = sleepPresent ?
                UnitsFormatter.minutesBoldValNormalHelper(v.getContext(), sleepDur) : "";

        sleepBar.getRound().setCardBackgroundColor(connected && onHand
                ? getColor(R.color.main_sleep_blue)
                : getColor(R.color.light_black_38));

        sleepBar.setTitleText(!connected || !onHand || !sleepPresent ? R.string.night_header :
                (night ? R.string.night_header_today : R.string.night_header_last));

        sleepBar.setTextVisible(sleepPresent);
        sleepBar.setValueVisible(sleepPresent);
        sleepBar.setText(sleepText);
        sleepBar.setValueText(sleepValue);


        sleepBar.setVisibility(View.VISIBLE);

        return sleepPresent;
    }

    private int getColor(int colId) {
        return ContextCompat.getColor(v.getContext(), colId);
    }


    public void setSummary(SummaryInfo todaySummaryInfo) {
        this.todaySummaryInfo = todaySummaryInfo;

        setEnergySum(todaySummaryInfo);
        setHydrationSum(todaySummaryInfo);
        setHeartSum(todaySummaryInfo);
        setStressSum(todaySummaryInfo);
        setSleepSum(todaySummaryInfo);
    }
}
