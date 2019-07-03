package com.healbe.healbe_example_andorid.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public final class LocaleTool {

    public static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getLocaleN(context.getResources().getConfiguration());
        } else{
            return getLocaleBeforeN(context.getResources().getConfiguration());
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Locale getLocaleN(Configuration configuration) {
        return configuration.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    private static Locale getLocaleBeforeN(Configuration configuration) {
        return configuration.locale;
    }
}
