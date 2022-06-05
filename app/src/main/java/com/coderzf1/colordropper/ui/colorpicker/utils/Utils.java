package com.coderzf1.colordropper.ui.colorpicker.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

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

    /*
    public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight)
    {
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        //Keep aspect ratio scaling, mainly long edges
        float scaleRatio = Math.min(scaleHeight, scaleWidth);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        Bitmap newBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        //Create target size bitmap
        Bitmap scaledImage = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledImage);

        float left = 0;
        float top = 0;
        if (width > height){
            top = (float)((newBm.getWidth() - newBm.getHeight()) / 2.0);
        }
        else{
            left = (float)((newBm.getHeight() - newBm.getWidth()) / 2.0);
        }
        canvas.drawBitmap( newBm, left , top, null );
        if (!bm.isRecycled()){
            bm.recycle();
        }
        return scaledImage;
    }
    */

}
