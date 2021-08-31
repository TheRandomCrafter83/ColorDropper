package com.coderzf1.colordropper;

import android.Manifest;
import android.app.Activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.coderzf1.colordropper.Utilities.getContrastColor;

public class FragmentColorPicker extends Fragment {

    public final int REQ_CD_PICK = 101;
    public final int REQUEST_IMAGE_CAPTURE = 12345;
    public final String URL_KEY = "loaded_url";
    public final String URI_KEY = "loaded_uri";

    private StateModel saveState = null;
    private Bundle savedState = null;

    private Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
    String loadedUrl = "";
    Uri loadedImage = null;
    ImageView image;
    TextView tv_color;
    ProgressBar pb_load;
    ImageButton iv_browse;
    ImageButton iv_web;
    ImageButton iv_camera;
    ImageButton iv_screenshot;

    public FragmentColorPicker() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {

        Log.println(Log.DEBUG,"onDestroyView","onDestroyViewCalled");
         savedState = saveState();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){

//        outState.putParcelable(URI_KEY,loadedImage);
//        outState.putString(URL_KEY,loadedUrl);
        outState.putBundle("state", (savedState != null) ? savedState : saveState());
        Log.d("SavedInstance",outState.toString());
        super.onSaveInstanceState(outState);
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        if (loadedImage != null) {
            state.putParcelable(URI_KEY, loadedImage);
        } else if (!loadedUrl.equals("")) {
            state.putString(URL_KEY, loadedUrl);
        }
        return state;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(Log.DEBUG,"onCreate","onCreate Called");
        if(savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("state");
        }
        if(savedState != null) {
            if (savedState.containsKey(URI_KEY)) {
                loadedImage = savedInstanceState.getParcelable(URI_KEY);
                Bitmap bmp = Utilities.loadFromUri(getActivity(), loadedImage);
                image.setImageBitmap(bmp);
                image.setVisibility(View.VISIBLE);
            } else if (savedState.containsKey(URL_KEY)) {
                loadedUrl = savedState.getString(URL_KEY);
                pb_load.setVisibility(View.VISIBLE);
                loadBitmapFromUrl(getActivity(), loadedUrl);
            }
        }
        savedState = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_color_picker, container, false);

        //Initialize widget references
        image = parent.findViewById(R.id.imageView);
        tv_color = parent.findViewById(R.id.textView);
        pb_load = parent.findViewById(R.id.progressBar);
        iv_browse = parent.findViewById(R.id.iv_browse);
        iv_web = parent.findViewById(R.id.iv_web);
        iv_camera = parent.findViewById(R.id.iv_camera);
        iv_screenshot = parent.findViewById(R.id.iv_screenshot);

        if (saveState == null) {
            saveState = new ViewModelProvider(requireActivity()).get(StateModel.class);
        }
        saveState.getSaveState().observe(getViewLifecycleOwner(),saveState ->{
                    if (saveState.fileUri != null){
                        loadedImage = saveState.fileUri;
                        Bitmap bmp = Utilities.loadFromUri(getActivity(), loadedImage);
                        image.setImageBitmap(bmp);
                        image.setVisibility(View.VISIBLE);
                    } else if (saveState.url != null) {
                        loadedUrl = saveState.url;
                        pb_load.setVisibility(View.VISIBLE);
                        loadBitmapFromUrl(getActivity(), loadedUrl);
                    }});
        //Initialize OnClickListeners
        iv_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (pick.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    pick.setType("image/*");
                    startActivityForResult(pick, REQ_CD_PICK);
                //}
            }
        });

        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCamera();
            }
        });

        iv_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doURL();
            }
        });

        iv_screenshot.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                doScreenShot();
            }
        });

        imageviewOnTouchHandler(image);
        return parent;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        switch (requestCode){
            case REQ_CD_PICK:
                if (resultCode == Activity.RESULT_OK){
                    //String fileName = data.getData().getPath();
                    Uri imageUri = data.getData();
                    loadedImage = data.getData();

                    Bitmap bmp = Utilities.loadFromUri(getActivity(),imageUri); //loadFromUri(imageUri);
                    image.setImageBitmap(bmp);
                    image.setVisibility(View.VISIBLE);
                    SaveState state = new SaveState();
                    state.fileUri = imageUri;
                    saveState.setSaveState(state);
                    loadedUrl = "";
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                Bitmap bmp = Utilities.loadFromUri(getActivity(),loadedImage);
                image.setImageBitmap(bmp);
                SaveState state = new SaveState();
                state.fileUri = loadedImage;
                saveState.setSaveState(state);
                loadedUrl = "";
                break;
            default:
                break;
        }
    }

    private Uri getImageUri(Context activity, Bitmap photo) {
        Uri uri  = null;
        try {
            uri = Utilities.saveImageToFile(activity, photo, new Date().toString() + ".jpg");
        } catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    //ImageButton Click Event Handlers=====================================================================================================================================
    private void doScreenShot() {
        //NOTES:
            //Need Floating Window with Button to initiate screenshot countdown before taking screenshot
            //Upon pressing button, needs to start countdown. at the end of the countdown, needs to hide the button, then take screenshot.
            //Transfer the screenshot to the app
    }

    public void doCamera(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},1000);
        } else {
            dispatchTakePictureIntent();
        }
    }

    public void doURL(){
        InputDialog inputDialog = new InputDialog(getContext());
        inputDialog.setInputDialogCallback(new InputDialog.InputDialogCallback() {
            @Override
            public void onOkClick(String result) {
                pb_load.setVisibility(View.VISIBLE);
                loadBitmapFromUrl(getActivity(),result);
                SaveState state = new SaveState();
                state.url = result;
                saveState.setSaveState(state);
            }

            @Override
            public void onCancelClick() {

            }
        });
        inputDialog.showInputDialog("Please enter the url to the image.","Color Dropper",loadedUrl, Color.valueOf(getResources().getColor(R.color.primary,null)),Color.valueOf(getResources().getColor(R.color.yellow,null)));
    }

    //======================================================================================================================================================================

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == 1000){
            dispatchTakePictureIntent();
        }
    }


    //Camera Stuff++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    String currentPhotoPath ;

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "temp_";
        File storageDir = new File(getContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).toString() + File.separator + getContext().getResources().getString(R.string.app_name));


        if (!storageDir.exists())
            storageDir.mkdir();

        File image = new File(storageDir,"tmp.jpg");
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        //MediaScannerConnection.scanFile(getContext(), new String[]{image.toString()}, null, null);
        Log.d("Image File",image.toString());
        return image;
    }

    private void dispatchTakePictureIntent() {
        Log.d ("dispatchIntent","Dispatching Camera Intent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        //if (takePictureIntent.resolveActivity(getActivity().getApplicationContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.coderzf1.colordropper.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                loadedImage = photoURI;
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Log.d("Error","File not created");
            }
        //} else {
        //    Log.d("dispatchTakePicture", "Something is not right.");
        //}
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    private void loadBitmapFromUrl(Activity activity, String url){
        ImageLoader imgLoader = new ImageLoader();
        imgLoader.setImageLoaderListener(new ImageLoader.ImageLoaderListener(){
            @Override
            public void onImageSaved(String filename, Bitmap bmp){

            }

            @Override
            public void onImageLoaded(Bitmap bmp){
                //loadedBitmap = bmp;
                image.setImageBitmap(bmp);
                image.setVisibility(View.VISIBLE);
                pb_load.setVisibility(View.GONE);
                loadedImage= null;
            }

            @Override
            public void onError(String error) {
                image.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.imgnotfound));
                image.setVisibility(View.VISIBLE);
                pb_load.setVisibility(View.GONE);
            }

        });
        imgLoader.loadImageFromUrl(url);
    }

    private void imageviewOnTouchHandler (final View view) {
        view.setClickable(true);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){

                    //Create a Bitmap
                    android.graphics.Bitmap bmp = ((android.graphics.drawable.BitmapDrawable)((ImageView)view).getDrawable()).getBitmap();

                    //Create a Color Object and set it to the selected Color
                    int color = Color.WHITE;

                    int eventX = (int)event.getX();
                    int eventY = (int)event.getY();
                    float[] eventXY = new float[]{eventX, eventY};

                    Matrix invertMatrix = new Matrix();
                    ((ImageView)v).getImageMatrix().invert(invertMatrix);

                    invertMatrix.mapPoints(eventXY);
                    int x = (int) eventXY[0];
                    int y = (int) eventXY[1];

                    if (x < 0){
                        x=0;
                    }else if(x > bmp.getWidth()-1){
                        x = bmp.getWidth()-1;
                    }

                    if(y < 0){
                        y = 0;
                    }else if(y > bmp.getHeight()-1){
                        y = bmp.getHeight()-1;
                    }


                    color = bmp.getColor(x,y).toArgb();

                    //Show the color value
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    String hex = String.format("#%02x%02x%02x", r, g, b);
                    tv_color.setText(hex.toUpperCase());
                    tv_color.setTextColor(getContrastColor(Color.parseColor(hex)));

                    //Set the background color to the selected color
                    Utilities.decorateColorVal(tv_color,hex);

                    return true;
                } else if (event.getAction()==MotionEvent.ACTION_UP){
                    v.performClick();
                    return false;
                }
                return false;
            }
        });

    }



}