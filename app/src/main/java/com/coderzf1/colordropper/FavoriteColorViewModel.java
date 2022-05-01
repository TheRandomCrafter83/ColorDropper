package com.coderzf1.colordropper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.Database.ColorRepository;

import java.util.List;

public class FavoriteColorViewModel extends AndroidViewModel {
    private ColorRepository mRepository;
    private final LiveData<List<Color>> mAllColors;

    public FavoriteColorViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ColorRepository(application);
        mAllColors = mRepository.getAllColors();
    }

    LiveData<List<Color>> getmAllColors(){
        return mAllColors;
    }

    public void insert(Color color){
        mRepository.insert(color);
    }

    public void delete(Color color){
        mRepository.delete(color);
    }
}
