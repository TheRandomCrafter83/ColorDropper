package com.coderzf1.colordropper.ui.colorpicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coderzf1.colordropper.R;
import com.coderzf1.colordropper.databinding.FragmentColorPickerBinding;
import com.coderzf1.colordropper.ui.colorpicker.listeners.CompoundDrawableClickListener;
import com.coderzf1.colordropper.ui.colorpicker.utils.Utils;
import com.coderzf1.colordropper.utils.ImageLoader;

import java.io.File;


public class FragmentColorPicker extends Fragment {
    FragmentColorPickerBinding binding;
    private FragmentColorPickerViewModel viewModel;
    String loadedUrl = "";
    Uri loadedImage = null;
    String[] permissionsToCheck = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    Bitmap bitmapCanvas;
    Canvas bitmapCanvasCanvas;

    //region |-----------Launchers-----------|
    private ActivityResultLauncher<String> browseImageLauncher;
    private ActivityResultLauncher<Uri> captureImageLauncher;
    private ActivityResultLauncher<String[]> checkPermissions;
    private ActivityResultLauncher<String> checkCameraPermission;
    //endregion

    public FragmentColorPicker() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        browseImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            viewModel.setLoadedImage(result);
            loadedUrl = "";
        });
        captureImageLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), hasPictureBeenCreated -> {
            Log.d("CameraApp", "onAttach: hasPictureBeenTaken" + hasPictureBeenCreated);
            if (hasPictureBeenCreated) {
                viewModel.setLoadedImage(loadedImage);
                loadedUrl = "";
            }
        });

        checkPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean hasPermission = false;
            for (boolean i : result.values()) {
                hasPermission = i;
            }
            if (hasPermission) {
                openCamera();
            }
        });

        checkCameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), hasPermission -> {
            if (hasPermission) {
                openCamera();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(Log.DEBUG, "onCreate", "onCreate Called");
        viewModel = new ViewModelProvider(this).get(FragmentColorPickerViewModel.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentColorPickerBinding.inflate(getLayoutInflater());
        viewModel.getLoadedImage().observe(getViewLifecycleOwner(), uri -> {
            if (uri == null) return;
            binding.imageView.setImageURI(uri);
            binding.imageView.setVisibility(View.VISIBLE);
            int w = binding.imagePlaceHolder.getWidth();
            int h = binding.imagePlaceHolder.getHeight();
            bitmapCanvas = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            bitmapCanvasCanvas = new Canvas(bitmapCanvas);
        });
        viewModel.getLoadedUrl().observe(getViewLifecycleOwner(), url -> {
            if (url == null) return;
            if (url.isEmpty()) return;
            loadBitmapFromUrl(url);
        });
        //viewModel.getPickedColor().observe(getViewLifecycleOwner(), pickedColor -> binding.textView.setText(pickedColor));
        viewModel.getPickedColor().observe(getViewLifecycleOwner(), new Observer<String>() {
            @SuppressLint("UseCompatTextViewDrawableApis")
            @Override
            public void onChanged(String pickedColor) {
                binding.textView.setText(pickedColor);
                int color = Color.parseColor(pickedColor);
                binding.textView.setTextColor(Utils.getContrastColor(color));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int[][] states = new int[][]{
                            new int[]{android.R.attr.state_enabled}
                    };
                    int[] colors = new int[]{Utils.getContrastColor(Color.parseColor(pickedColor))};
                    ColorStateList compoundStateListColors = new ColorStateList(states, colors);
                    binding.textView.setCompoundDrawableTintList(compoundStateListColors);
                }
                binding.cardviewColor.setCardBackgroundColor(Color.parseColor(pickedColor));
            }
        });
        viewModel.setPickedColor("#FFFFFFFF");
        //Initialize OnClickListeners
        binding.ivBrowse.setOnClickListener(buttonBrowseListener);
        binding.ivCamera.setOnClickListener(buttonCameraListener);
        binding.ivWeb.setOnClickListener(buttonEnterUrlListener);
        binding.ivScreenshot.setOnClickListener(buttonScreenshotListener);
        binding.imageView.setOnTouchListener(imageViewListener);
        binding.textView.setOnTouchListener(textViewListener);
        binding.textView.setClickable(true);
        return binding.getRoot();
    }

    //region |-----------Listeners-----------|
    private final View.OnClickListener buttonBrowseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            browseImageLauncher.launch("image/*");
        }
    };

    private final View.OnClickListener buttonCameraListener = view -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

    private final View.OnClickListener buttonEnterUrlListener = view -> {
        InputDialog inputDialog = new InputDialog(getContext());
        inputDialog.setInputDialogCallback(new InputDialog.InputDialogCallback() {
            @Override
            public void onOkClick(String result) {
                loadBitmapFromUrl(result);
            }

            @Override
            public void onCancelClick() {

            }
        });

        inputDialog.showInputDialog(
                "Please enter the url to the image.",
                "Color Dropper",
                loadedUrl,
                ContextCompat.getColor(requireActivity(), R.color.primary),
                ContextCompat.getColor(requireActivity(), R.color.yellow)
        );
    };

    private final CompoundDrawableClickListener textViewListener = new CompoundDrawableClickListener() {
        @Override
        protected void onDrawableClick(View v, int drawableIndex) {
            if(drawableIndex == 0){
                Toast.makeText(getContext(),"Copy",Toast.LENGTH_SHORT).show();
            }
            if(drawableIndex == 2){
                Toast.makeText(getContext(),"Favorite",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnTouchListener imageViewListener = new View.OnTouchListener() {
        @SuppressLint("UseCompatTextViewDrawableApis")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                binding.imageView.draw(bitmapCanvasCanvas);
                int color;

                int eventX = (int) event.getX();
                int eventY = (int) event.getY();
                float[] eventXY = new float[]{eventX, eventY};

                int x = (int) eventXY[0];
                int y = (int) eventXY[1];

                if (x < 0) {
                    x = 0;
                } else if (x > bitmapCanvas.getWidth() - 1) {
                    x = bitmapCanvas.getWidth() - 1;
                }

                if (y < 0) {
                    y = 0;
                } else if (y > bitmapCanvas.getHeight() - 1) {
                    y = bitmapCanvas.getHeight() - 1;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    color = bitmapCanvas.getColor(x, y).toArgb();
                } else {
                    color = bitmapCanvas.getPixel(x, y);
                }

                //Show the color value
                int a = Color.alpha(color);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                String hex = String.format("#%02x%02x%02x%02x",a , r, g, b);
                viewModel.setPickedColor(hex.toUpperCase());
                binding.textView.setTextColor(Utils.getContrastColor(Color.parseColor(hex)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int[][] states = new int[][]{
                            new int[]{android.R.attr.state_enabled}
                    };
                    int[] colors = new int[]{Utils.getContrastColor(Color.parseColor(hex))};
                    ColorStateList compoundStateListColors = new ColorStateList(states, colors);
                    binding.textView.setCompoundDrawableTintList(compoundStateListColors);
                }
                binding.cardviewColor.setCardBackgroundColor(Color.parseColor(hex));
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return false;
            }
            return false;
        }
    };
    //endregion

    //region |-----------Camera Stuff-----------|
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkOldPermissions() {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
            return;
        }
        checkPermissions.launch(permissionsToCheck);
    }

    private void checkNewPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
                return;
            }
        }
        checkCameraPermission.launch(Manifest.permission.CAMERA);
    }

    private void openCamera() {
        File pictureFile = null;
        try {
            pictureFile = Utils.createImageFile(requireContext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pictureFile != null) {
                loadedImage = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName(), pictureFile);
                captureImageLauncher.launch(loadedImage);
            }
        }
    }
    //endregion


    private void loadBitmapFromUrl(String url) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ImageLoader imgLoader = new ImageLoader();
        imgLoader.setImageLoaderListener(new ImageLoader.ImageLoaderListener() {
            @Override
            public void onImageLoaded(Uri uri) {
                viewModel.setLoadedImage(uri);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                binding.imageView.setImageBitmap(BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.imgnotfound));
                binding.imageView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }

        });
        imgLoader.loadImageFromUrl(requireContext(),url);
    }
}