package com.coderzf1.colordropper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Color Dropper";
    Bitmap bitmap = null;

    public static Bitmap loadFromUri(Activity activity, Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source,new ImageDecoder.OnHeaderDecodedListener(){

                    @Override
                    public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {
                        imageDecoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                        imageDecoder.setMutableRequired(true);
                    }
                } );
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static int getContrastColor(int color) {
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    public static void decorateColorVal (final View view, final String bgColor) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setStroke(5,Color.BLACK);
        gd.setCornerRadius(20);
        gd.setColor(Color.parseColor(bgColor));
        view.setBackground(gd);
    }

    public static Uri saveImageToFile(final Context context,final Bitmap bmp,final String filename){
        Uri uri = null;
        try {
            uri = saveImage(context, bmp, getApplicationName(context), filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }
    private static String getApplicationName(final Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static Uri saveImage(final Context context,final Bitmap bitmap,final @NonNull String folderName,final @NonNull String fileName) throws IOException {
        OutputStream fos = null;
        File imageFile = null;
        Uri imageUri = null;
        Bitmap.CompressFormat format = null;

        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        String mimeType =  MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);

                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,mimeType) ;
                contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName);
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                if (imageUri == null)
                    throw new IOException("Failed to create new MediaStore record.");

                fos = resolver.openOutputStream(imageUri);
            } else {
                File imagesDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString() + File.separator + folderName);

                String compressionName = mimeType.toLowerCase().replace("image/", "").toUpperCase();
                Log.println(Log.DEBUG,"compressFormat: ", compressionName);
                //try to determine compression format from original file
                try {
                    format = Bitmap.CompressFormat.valueOf(compressionName);
                }catch (Exception e){
                    format = Bitmap.CompressFormat.PNG;
                    extension = "png";
                }

                if (!imagesDir.exists())
                    imagesDir.mkdir();

                imageFile = new File(imagesDir, fileName + "." + extension);
                fos = new FileOutputStream(imageFile);
            }

            if (!bitmap.compress(format, 100, fos))
                throw new IOException("Failed to save bitmap.");
            fos.flush();
        } finally {
            if (fos != null)
                fos.close();
        }

        if (imageFile != null) {//pre Q
            MediaScannerConnection.scanFile(context, new String[]{imageFile.toString()}, null, null);
            imageUri = Uri.fromFile(imageFile);
        }
        return imageUri;
    }


    public static void requestOverlayDisplayPermission(Context context, Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Screen Overlay Permission Needed");
        builder.setMessage("Enable 'Display over other apps' from System Settings.");
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                Uri packageUri = Uri.parse("package:"+ context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,packageUri);
                activity.startActivityForResult(intent,Activity.RESULT_OK);
            }
        });
        builder.create().show();
    }

    public static boolean checkOverlayDisplayPermission(Context context, Activity activity){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(context)){
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
 }
