package com.beebapcay.notesapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ActivityUtils {
    public static void hideKeyboard(Context context) {
        Activity activity = (Activity) context;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null)
            imm.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), imm.HIDE_NOT_ALWAYS);
    }

    public static Activity getActivity(Context context)
    {
        if (context == null)
            return null;
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
                return (Activity) context;
            else
                return getActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }
}
