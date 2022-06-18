package com.coderzf1.colordropper.utils;

import android.content.Context;
import android.content.res.Configuration;

public class Utils {
    public static boolean isDarkMode(Context context){
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch(nightModeFlags){
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
        }
        return false;
    }
}
