package com.coderzf1.colordropper;

import static com.coderzf1.colordropper.Utilities.getContrastColor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FragmentColorPicker extends Fragment {

    public final String URL_KEY = "loaded_url";
    public final String URI_KEY = "loaded_uri";

    private StateModel saveState = null;
    private Bundle savedState = null;

    String loadedUrl = "";

    ImageView imageviewSelectedImage;
    TextView tv_color;
    ProgressBar pb_load;
    ImageButton iv_browse;
    ImageButton iv_web;
    ImageButton iv_camera;
    ImageButton iv_screenshot;

    Uri loadedImage = null;
    String[] permissionsToCheck = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private ActivityResultLauncher<String> browseImageLauncher ;

    private ActivityResultLauncher<Uri> captureImageLauncher;

    private ActivityResultLauncher<String[]> checkPermissions;

    private ActivityResultLauncher<String> checkCameraPermission;

    public FragmentColorPicker() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        browseImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            loadedImage = result;
            imageviewSelectedImage.setImageURI(loadedImage);
            SaveState state = new SaveState();
            state.fileUri = result;
            saveState.setSaveState(state);
            loadedUrl = "";
        });
        captureImageLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), hasPictureBeenCreated -> {
            Log.d("CameraApp", "onAttach: hasPictureBeenTaken" + hasPictureBeenCreated);
            if (hasPictureBeenCreated){
                imageviewSelectedImage.setImageURI(loadedImage);
                SaveState state = new SaveState();
                state.fileUri = loadedImage;
                saveState.setSaveState(state);
                loadedUrl = "";
            }
        });

        checkPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean hasPermission = false;
            for(boolean i: result.values()){
                hasPermission = i;
            }
            if(hasPermission){
                openCamera();
            }
        });

        checkCameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), hasPermission -> {
            if(hasPermission){
                openCamera();
            }
        });
    }

    @Override
    public void onDestroyView() {

        Log.println(Log.DEBUG,"onDestroyView","onDestroyViewCalled");
         savedState = saveState();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
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
                loadedImage = savedInstanceState != null ? savedInstanceState.getParcelable(URI_KEY) : null;
                Bitmap bmp = Utilities.loadFromUri(getActivity(), loadedImage);
                imageviewSelectedImage.setImageBitmap(bmp);
                imageviewSelectedImage.setVisibility(View.VISIBLE);
            } else if (savedState.containsKey(URL_KEY)) {
                loadedUrl = savedState.getString(URL_KEY);
                pb_load.setVisibility(View.VISIBLE);
                loadBitmapFromUrl(loadedUrl);
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
        imageviewSelectedImage = parent.findViewById(R.id.imageView);
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
                        imageviewSelectedImage.setImageBitmap(bmp);
                        imageviewSelectedImage.setVisibility(View.VISIBLE);
                    } else if (saveState.url != null) {
                        loadedUrl = saveState.url;
                        pb_load.setVisibility(View.VISIBLE);
                        loadBitmapFromUrl(loadedUrl);
                    }});
        //Initialize OnClickListeners
        iv_browse.setOnClickListener(buttonBrowseListener);

        iv_camera.setOnClickListener(buttonCameraListener);

        iv_web.setOnClickListener(buttonEnterUrlListener);

        iv_screenshot.setOnClickListener(buttonCameraListener);

        imageviewOnTouchHandler(imageviewSelectedImage);
        return parent;
    }

    //ImageButton Click Event Handlers=====================================================================================================================================
    private final View.OnClickListener buttonBrowseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            browseImageLauncher.launch("image/*");
        }
    };

    private final View.OnClickListener buttonCameraListener = view -> {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            checkNewPermissions();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkOldPermissions();
            } else {
                openCamera();
            }
        }
    };

    private final View.OnClickListener buttonScreenshotListener = view -> {
        //TODO: Color from screenshot
    };



    private final View.OnClickListener buttonEnterUrlListener = view ->  {
        InputDialog inputDialog = new InputDialog(getContext());
        inputDialog.setInputDialogCallback(new InputDialog.InputDialogCallback() {
            @Override
            public void onOkClick(String result) {
                pb_load.setVisibility(View.VISIBLE);
                loadBitmapFromUrl(result);
                SaveState state = new SaveState();
                state.url = result;
                saveState.setSaveState(state);
            }

            @Override
            public void onCancelClick() {

            }
        });

        inputDialog.showInputDialog(
                "Please enter the url to the image.",
                "Color Dropper",
                loadedUrl,
                ContextCompat.getColor(requireActivity(),R.color.primary),
                ContextCompat.getColor(requireActivity(),R.color.yellow)
        );
    };

    //Camera Stuff++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private File createImageFile() throws IOException {
        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_"+timeStamp+"_",".jpg",storageDir);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkOldPermissions(){
        if(requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openCamera();
            return;
        }
        checkPermissions.launch(permissionsToCheck);
    }

    private void checkNewPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                openCamera();
                return;
            }
        }
        checkCameraPermission.launch(Manifest.permission.CAMERA);
    }

    private void openCamera(){
        File pictureFile = null;
        try {
            pictureFile = createImageFile();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if(pictureFile != null) {
                loadedImage = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName(), pictureFile);
                captureImageLauncher.launch(loadedImage);
            }
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    private void loadBitmapFromUrl(String url){
        ImageLoader imgLoader = new ImageLoader();
        imgLoader.setImageLoaderListener(new ImageLoader.ImageLoaderListener(){
            @Override
            public void onImageSaved(String filename, Bitmap bmp){

            }

            @Override
            public void onImageLoaded(Bitmap bmp){
                //loadedBitmap = bmp;
                imageviewSelectedImage.setImageBitmap(bmp);
                imageviewSelectedImage.setVisibility(View.VISIBLE);
                pb_load.setVisibility(View.GONE);
                loadedImage= null;
            }

            @Override
            public void onError(String error) {
                imageviewSelectedImage.setImageBitmap(BitmapFactory.decodeResource(requireContext().getResources(),R.drawable.imgnotfound));
                imageviewSelectedImage.setVisibility(View.VISIBLE);
                pb_load.setVisibility(View.GONE);
            }

        });
        imgLoader.loadImageFromUrl(url);
    }

    private void imageviewOnTouchHandler (final View view) {
        view.setClickable(true);

        view.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){

                //Create a Bitmap
                Bitmap bmp = ((android.graphics.drawable.BitmapDrawable)((ImageView)view).getDrawable()).getBitmap();

                //Create a Color Object and set it to the selected Color
                int color;

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    color = bmp.getColor(x, y).toArgb();
                } else {
                    color = bmp.getPixel(x,y);
                }

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
        });

    }



}