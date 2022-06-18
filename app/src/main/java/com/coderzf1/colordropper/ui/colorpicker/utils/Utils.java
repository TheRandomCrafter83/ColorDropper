package com.coderzf1.colordropper.ui.colorpicker.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import androidx.annotation.ColorInt;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings({"CommentedOutCode", "unused"})
public class Utils {
    public static int getContrastColor(int color) {
        double y = (299d * Color.red(color) + 587d * Color.green(color) + 114d * Color.blue(color)) / 1000d;
        return y >= 128d ? Color.BLACK : Color.WHITE;
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_"+timeStamp+"_",".jpg",storageDir);
    }

    public static String colorIntToHexString(@ColorInt int color){
        int alpha = (color>>24)&0xFF;
        int red = (color>>16)&0xFF;
        int green = (color>>8)&0xFF;
        int blue = (color)&0xFF;
        return String.format("#%02x%02x%02x%02x",alpha,red,green,blue).toUpperCase();

    }
}
