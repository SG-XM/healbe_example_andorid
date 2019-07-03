package com.healbe.healbe_example_andorid.tools;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;

import com.healbe.healbe_example_andorid.R;
import com.healbe.healbesdk.business_api.healthdata.data.stress.StressState;
import com.healbe.healbesdk.business_api.healthdata.data.water.HydrationState;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class UnitsFormatter {

    private static final int DISPLAY_FLOAT_PRECISION = 2;
    private static final int EDIT_FLOAT_PRECISION = 4;

    public static CharSequence stepsAndTime(Context context, int steps, int minutes) {
        String stepsUnit = context.getResources().getQuantityString(R.plurals.plurals_steps, steps);

        DecimalFormat dfSteps = new DecimalFormat();
        dfSteps.setGroupingSize(3);
        dfSteps.setGroupingUsed(true);

        if (minutes > 0)
            return Editor.format(context, "%s %s, %s " + context.getString(R.string.active),
                    dfSteps.format(steps),
                    stepsUnit,
                    timePeriodFromMins(context, minutes));
        else
            return Editor.format(context, "%s %s" + context.getString(R.string.active),
                    dfSteps.format(steps),
                    stepsUnit);
    }


    public static CharSequence timePeriodFromMins(Context context, int totalLength) {
        String sHours = context.getResources().getString(R.string.time_period_hours);
        String sMins = context.getResources().getString(R.string.time_period_mins);

        int mins = totalLength % 60;
        int hours = totalLength / 60;
        if (hours != 0)
            return Editor.format(context, context.getString(R.string.time_period_long), hours, sHours, mins, sMins);
        else
            return Editor.format(context, context.getString(R.string.time_period_short), mins, sMins);
    }

    public static CharSequence energyBalance(Context context, int val) {
        return energyBalance(context, val, false);
    }
    public static CharSequence energyBalance(Context context, int val, boolean showZero) {

        if (!showZero && val == 0) {
            Spannable spannable = new SpannableString(context.getString(R.string.na));
            spannable.setSpan(new TypefaceSpan("sans-serif-medium"), 0, spannable.length(), Spannable.SPAN_POINT_POINT);
            return spannable;
        }

        DecimalFormat dfkcal = new DecimalFormat();
        dfkcal.setGroupingSize(3);
        dfkcal.setGroupingUsed(true);
        dfkcal.setPositivePrefix("+");
        dfkcal.setNegativePrefix("-");

        String vl = dfkcal.format(val);
        String kcal = context.getString(R.string.kcal);

        Spannable spannable = new SpannableString(vl + " " + kcal);
        spannable.setSpan(new TypefaceSpan("sans-serif-medium"), 0, vl.length(), Spannable.SPAN_POINT_POINT);
//        spannable.setSpan(new TypefaceSpan("sans-serif"), vl.length() + 1, spannable.length(), Spannable.SPAN_POINT_POINT);

        return spannable;
    }

    public static CharSequence formatHydrationState(Context context, HydrationState state) {
        final String stateString = hydratationState(context, state);
        Spannable spannable = new SpannableString(stateString);
        spannable.setSpan(new TypefaceSpan("sans-serif-medium"), 0, stateString.length(), Spannable.SPAN_POINT_POINT);
        return spannable;
    }

    public static CharSequence formatStressState(Context context, StressState state, float val) {
        final String stateString = getStressLevelString(context, state);
        Spannable spannable = new SpannableString(stateString + (
                state != StressState.NO_DATA && state != StressState.CALCULATING
                        ? " (" + formatFloatMaxN(val, 1) + ")" : ""));
        spannable.setSpan(new TypefaceSpan("sans-serif-medium"), 0, stateString.length(), Spannable.SPAN_POINT_POINT);
        return spannable;
    }

    public static String hydratationState(Context context, HydrationState hydratainState) {
        switch (hydratainState) {
            case LOW:
                return context.getString(R.string.low);
            case NORMAL:
                return context.getString(R.string.normal);
            case CALCULATING:
                return context.getString(R.string.calculating);
            default:
                return context.getString(R.string.no_data);
        }
    }

    public static String getStressLevelString(Context context, StressState stressState) {
        switch (stressState) {
            case VERY_HIGH:
                return context.getString(R.string.level_very_high);
            case HIGH:
                return context.getString(R.string.level_high);
            case MODERATE:
                return context.getString(R.string.level_moderate);
            case LIGHT:
                return context.getString(R.string.level_light);
            case NO_STRESS:
                return context.getString(R.string.level_no_stress);
            case CALCULATING:
                return context.getString(R.string.calculating);
            default:
                return context.getString(R.string.no_data);
        }
    }

    public static String formatFloatMaxN(float f, int n) {
        float resFloat = setFloatPrecision(f, n);
        if (resFloat == (long) resFloat) {
            return Editor.format("%d", (long) resFloat);
        } else {
            return Editor.format("%." + getFloatPrecision(resFloat) + "f", resFloat);
        }
    }

    public static float setFloatPrecision(float f, int n) {
        BigDecimal value = new BigDecimal(f);
        value = value.setScale(n, RoundingMode.HALF_EVEN);
        return value.floatValue();
    }

    public static int getFloatPrecision(float f) {
        String sFloat = Editor.format("%s", f);
        return sFloat.split("\\D")[1].length();
    }

    public static String formatPulseSleepAwake(Context context, int pulseAwake, int pulseAsleep) {
        List<String> as = new ArrayList<>();

        if (pulseAwake > 0)
            as.add(Editor.format(context, "%s %d %s",
                    context.getString(R.string.awake).toLowerCase(),
                    pulseAwake,
                    context.getString(R.string.bpm)));

        if (pulseAsleep > 0)
            as.add(Editor.format(context, "%s %d %s",
                    context.getString(R.string.asleep).toLowerCase(),
                    pulseAsleep,
                    context.getString(R.string.bpm)));

        return TextUtils.join(", ", as);
    }

    public static CharSequence pulseSpan(Context context, int val) {
        DecimalFormat dfpulse = new DecimalFormat();
        dfpulse.setGroupingSize(3);
        dfpulse.setGroupingUsed(true);

        String vl = dfpulse.format(val);
        String bpm = context.getString(R.string.bpm);

        Spannable spannable = new SpannableString(vl + " " + bpm);
        spannable.setSpan(new TypefaceSpan("sans-serif-medium"), 0, vl.length(), Spannable.SPAN_POINT_POINT);
        spannable.setSpan(new TypefaceSpan("sans-serif"), vl.length() + 1, spannable.length(), Spannable.SPAN_POINT_POINT);

        return spannable;
    }

    public static CharSequence bloodPressureSpan(Context context, int diastolic, int systolic) {
        DecimalFormat dfpulse = new DecimalFormat();
        dfpulse.setGroupingSize(3);
        dfpulse.setGroupingUsed(true);

        String diastolicStr = dfpulse.format(diastolic);
        String systolicStr = dfpulse.format(systolic);
        String bpm = context.getString(R.string.mmHg);

        Spannable spannable = new SpannableString("\n" + systolicStr + "/" + diastolicStr + " " + bpm);
        spannable.setSpan(new TypefaceSpan("sans-serif-medium"), 0, systolicStr.length(), Spannable.SPAN_POINT_POINT);
        spannable.setSpan(new TypefaceSpan("sans-serif"), systolicStr.length() + 1, spannable.length(), Spannable.SPAN_POINT_POINT);

        return spannable;
    }

    public static CharSequence minutesBoldValNormalHelper(Context context, int val) {

        int minutes = val % 60;
        int hours = val / 60;

        final String min = String.valueOf(minutes);
        final String h = String.valueOf(hours);

        String sHours = context.getResources().getString(R.string.time_period_hours);
        String sMins = context.getResources().getString(R.string.time_period_mins);

        final String timeString = (String) timePeriodFromMins(context, val);
        final Spannable s = new SpannableString(timeString);

        if (hours == 0) {
            int minStartIndex = timeString.indexOf(min);

            s.setSpan(new TypefaceSpan("sans-serif-medium"), minStartIndex, min.length(), Spannable.SPAN_POINT_POINT);
            s.setSpan(new TypefaceSpan("sans-serif"), min.length() + 1, s.length(), Spannable.SPAN_POINT_POINT);
        } else {
            int hStart = timeString.indexOf(h + " " + sHours);
            int mStart = timeString.indexOf(min + " " + sMins);

            s.setSpan(new TypefaceSpan("sans-serif-medium"), hStart, h.length(), Spannable.SPAN_POINT_POINT);
            s.setSpan(new TypefaceSpan("sans-serif"), h.length() + 1, mStart - 1, Spannable.SPAN_POINT_POINT);
            s.setSpan(new TypefaceSpan("sans-serif-medium"), mStart, mStart + min.length(), Spannable.SPAN_POINT_POINT);
            s.setSpan(new TypefaceSpan("sans-serif"), mStart + min.length() + 1, s.length(), Spannable.SPAN_POINT_POINT);

        }

        return s;
    }
}
