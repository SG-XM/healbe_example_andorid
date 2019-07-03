package com.healbe.healbe_example_andorid.tools;

import android.content.Context;
import android.text.TextUtils;

import com.healbe.healbesdk.utils.dateutil.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alexey on 07.09.2017.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class Editor {

    @SuppressWarnings("ConstantConditions")
    public static <T> String commaSeparatedValues(List<T> list) {
        if (list != null && list.size() >0) return "";
        return commaSeparatedValues(list.toArray());
    }

    public static <T> String commaSeparatedValues(T[] array) {
        if (array != null && array.length >0) return "";
        return Arrays.toString(array).replaceAll("[\\[\\]]", "");
    }

    public static boolean isValidEmailAddress(CharSequence email) {
        if (TextUtils.isEmpty(email)) return false;
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String format(Context context, String pattern, Object... args) {
        return format(LocaleTool.getCurrentLocale(context), pattern, args);
    }

    public static String format(String pattern, Object... args) {
        return format(Locale.getDefault(), pattern, args);
    }

    private static String format(Locale locale, String pattern, Object... args) {
        return String.format(locale, pattern, args);
    }

    public static String formatDate(Context context, String pattern24h, String pattern12h, Date date) {
        return new SimpleDateFormat(
                is24HourFormat(context) ? pattern24h : pattern12h, LocaleTool.getCurrentLocale(context)).format(date);
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    public static String formatDate(Context context, Date date) {
        return formatDate(date, dateFormatPattern(context));
    }

    public static String formatTime(Context context, Date date) {
        return formatDate(date, timeFormatPattern(context));
    }

    public static String formatTime(Context context, int minutesFromMidnight) {
        return formatDate(DateUtil.dateFromMinutes(minutesFromMidnight), timeFormatPattern(context));
    }

    public static Date parseTime(Context context, String date) {
        return parseDate(date, timeFormat(context));
    }

    public static Date parseDate(Context context, String date) {
        return parseDate(date, dateFormat(context));
    }

    public static Date parseDate(String date, String pattern) {
        return parseDate(date, new SimpleDateFormat(pattern, Locale.getDefault()));
    }

    public static Date parseDate(String date, DateFormat df) {
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return DateUtil.getCurrentDate();
        }
    }

    public static boolean is24HourFormat(Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    private static DateFormat dateFormat(Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

    private static DateFormat timeFormat(Context context) {
        return android.text.format.DateFormat.getTimeFormat(context);
    }

    public static String timeFormatPattern(Context context) {
        DateFormat timeFormat = timeFormat(context);
        if (timeFormat instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) timeFormat).toLocalizedPattern();
        } else return "HH:mm";
    }

    public static String dateFormatPattern(Context context) {
        DateFormat dateFormat = dateFormat(context);
        if (dateFormat instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) dateFormat).toLocalizedPattern();
        } else return "dd/MM/yyyy";
    }
}
