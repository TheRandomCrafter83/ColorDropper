package com.coderzf1.colordropper.ui.colorpicker.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coderzf1.colordropper.database.Color;
import com.coderzf1.colordropper.database.ColorRepository;

public class FragmentColorPickerViewModel extends AndroidViewModel {
    private final ColorRepository mRepository;

    private final MutableLiveData<Uri> loadedImage;

    private final MutableLiveData<String> loadedUrl;

    private final MutableLiveData<String> pickedColor;

    private Integer pickedColorInt;

    public FragmentColorPickerViewModel(@NonNull Application application) {
        super(application);
        loadedImage = new MutableLiveData<>();
        loadedUrl = new MutableLiveData<>();
        pickedColor = new MutableLiveData<>();
        pickedColor.setValue("#FFFFFFFF");
        pickedColorInt = 0;
        mRepository = new ColorRepository(application);
    }

    @SuppressWarnings("unused")
    public LiveData<String> getPickedColor(){
        return pickedColor;
    }

    @SuppressWarnings("unused")
    public void setPickedColor(String pickedColor){
        this.pickedColor.setValue(pickedColor);
    }

    public Integer getPickedColorInt(){return pickedColorInt;}

    public void setPickedColorInt(Integer pickedColor){this.pickedColorInt = pickedColor;}

    @SuppressWarnings("unused")
    public LiveData<String> getLoadedUrl() {
        return loadedUrl;
    }

    @SuppressWarnings("unused")
    public LiveData<Uri> getLoadedImage() {
        return loadedImage;
    }

    public void setLoadedImage(Uri loadedImage) {
        this.loadedImage.setValue(loadedImage);
    }

    @SuppressWarnings("unused")
    public void insert(Color color){
        mRepository.insert(color);
    }
}
