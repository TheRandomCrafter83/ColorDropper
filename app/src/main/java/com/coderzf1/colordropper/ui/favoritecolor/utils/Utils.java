package com.coderzf1.colordropper.ui.favoritecolor.utils;

import androidx.annotation.ColorInt;

public class Utils {
    public static String colorIntToHexString(@ColorInt int color){
        int alpha = (color>>24)&0xFF;
        int red = (color>>16)&0xFF;
        int green = (color>>8)&0xFF;
        int blue = (color)&0xFF;
        return String.format("#%02x%02x%02x%02x",alpha,red,green,blue).toUpperCase();
    }
}
