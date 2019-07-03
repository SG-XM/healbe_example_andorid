package com.healbe.healbe_example_andorid.tools;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by iam on 12.10.2017.
 */

@SuppressWarnings("WeakerAccess")
public final class IntentUtils {
    private IntentUtils() {
    }

    public static Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        throw new IllegalStateException("Context " + context + " NOT contains activity!");
    }
}
