package com.coderzf1.colordropper.ui.favoritecolor.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.coderzf1.colordropper.database.Color;
import com.coderzf1.colordropper.database.ColorRepository;

import java.util.List;

@SuppressWarnings("unused")
public class FavoriteColorViewModel extends AndroidViewModel {
    private final ColorRepository mRepository;
    private final LiveData<List<Color>> mAllColors;

    public FavoriteColorViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ColorRepository(application);
        mAllColors = mRepository.getAllColors();
    }

    public LiveData<List<Color>> getAllColors(){
        //Log.d("DebugColorsCount", "getAllColors: " + mAllColors.getValue().size());
        return mAllColors;
    }

    public void insert(Color color){
        mRepository.insert(color);
    }

    public void delete(Color color){
        mRepository.delete(color);
    }

    public void update(Color color) {mRepository.update(color);}
}
