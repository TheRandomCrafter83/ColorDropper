package com.coderzf1.colordropper.ui.favoritecolor;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.Database.ColorRepository;

import java.util.List;

public class FavoriteColorViewModel extends AndroidViewModel {
    private final ColorRepository mRepository;
    private final LiveData<List<Color>> mAllColors;

    public FavoriteColorViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ColorRepository(application);
        mAllColors = mRepository.getAllColors();
    }

    LiveData<List<Color>> getAllColors(){
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
