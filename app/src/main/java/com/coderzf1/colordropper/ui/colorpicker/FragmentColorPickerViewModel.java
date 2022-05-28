package com.coderzf1.colordropper.ui.colorpicker;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.Database.ColorRepository;

public class FragmentColorPickerViewModel extends AndroidViewModel {
    private final ColorRepository mRepository;

    private final MutableLiveData<Uri> loadedImage;

    private final MutableLiveData<String> loadedUrl;

    private final MutableLiveData<String> pickedColor;

    public FragmentColorPickerViewModel(@NonNull Application application) {
        super(application);
        loadedImage = new MutableLiveData<>();
        loadedUrl = new MutableLiveData<>();
        pickedColor = new MutableLiveData<>();
        mRepository = new ColorRepository(application);
    }

    public LiveData<String> getPickedColor(){
        return pickedColor;
    }

    public void setPickedColor(String pickedColor){
        this.pickedColor.setValue(pickedColor);
    }

    public LiveData<String> getLoadedUrl() {
        return loadedUrl;
    }

    public void setLoadedUrl(String loadedUrl) {
        this.loadedUrl.setValue(loadedUrl);
    }

    public LiveData<Uri> getLoadedImage() {
        return loadedImage;
    }

    public void setLoadedImage(Uri loadedImage) {
        this.loadedImage.setValue(loadedImage);
    }

    public void insert(Color color){
        mRepository.insert(color);
    }
}
