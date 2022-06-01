package com.coderzf1.colordropper.ui.favoritecolor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.coderzf1.colordropper.Database.Color;
import com.coderzf1.colordropper.Database.ColorRepository;

import java.util.List;

public class FavoriteColorViewModelFactory implements ViewModelProvider.Factory{

    private List<Color> dataSource;

    public FavoriteColorViewModelFactory(List<Color> dataSource){
        this.dataSource = dataSource;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(FavoriteColorViewModel.class)){
            //return (T) new FavoriteColorViewModel(dataSource);
            return null;
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
