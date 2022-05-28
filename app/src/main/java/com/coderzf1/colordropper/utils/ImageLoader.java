package com.coderzf1.colordropper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader {
    public interface ImageLoaderListener{
        void onImageLoaded(Uri uri);
        void onError(String error);
    }
    Uri loadedUri = null;
    int error = 0;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                listener.onImageLoaded(loadedUri);
            } else if (msg.what == 1){
                listener.onError("Error downloading Image");
            }

        }
    };

    private ImageLoaderListener listener;

    public ImageLoader(){
        listener = null;
    }

    public void setImageLoaderListener(ImageLoaderListener listener){
        this.listener = listener;
    }

    public void loadImageFromUrl(Context context, String url){
        new Thread(() -> {
            File tmpFile = null;
            try {
                tmpFile = com.coderzf1.colordropper.ui.colorpicker.utils.Utils.createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tmpFile == null){
                error = 1;
                handler.sendEmptyMessage(error);
                return;
            }

            URL loadUrl = null;
            try {
                loadUrl = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (loadUrl == null){
                error = 1;
                handler.sendEmptyMessage(error);
                return;
            }

            InputStream sourceStream = null;
            try {
                sourceStream = loadUrl.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (sourceStream == null){
                error = 1;
                handler.sendEmptyMessage(error);
                return;
            }

            OutputStream destFile = null;
            try {
                destFile = new FileOutputStream(tmpFile);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (destFile == null){
                error=1;
                handler.sendEmptyMessage(error);
                return;
            }

            byte[] buffer = new byte[8192];
            int length;

            try {
                while ((length = sourceStream.read(buffer)) > 0) {
                    destFile.write(buffer, 0, length);
                }
            } catch(Exception e){
                e.printStackTrace();
                error = 1;
                handler.sendEmptyMessage(error);
                return;
            }
            try {
                destFile.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = 1;
                handler.sendEmptyMessage(error);
                return;
            }

            try {
                sourceStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = 1;
                handler.sendEmptyMessage(error);
                return;
            }

            loadedUri = FileProvider.getUriForFile(context,context.getPackageName(), tmpFile);
            error = 0;
            handler.sendEmptyMessage(error);
        }).start();
    }
}

