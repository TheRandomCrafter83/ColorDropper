package com.coderzf1.colordropper.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;


public class ColorRepository {
    private FavoriteColorsDao dao;
    private final LiveData<List<Color>> mAllColors;

    public ColorRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.dao();
        mAllColors = dao.getAllColors();
    }

    public LiveData<List<Color>> getAllColors() {
        return mAllColors;
    }

    public void insert(Color color) {
        AppDatabase.databaseWriteExecutor.execute(() -> {

            AsyncTask.execute(
                    () -> dao.insert(color)
            );

        });
    }

    public void update(Color color) {
        AppDatabase.databaseWriteExecutor.execute(()-> {
                    AsyncTask.execute(
                            ()-> dao.update(color)
                    );
                }
        );
    }

    public void delete(Color color) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AsyncTask.execute(
                    ()-> dao.delete(color)
            );
        });
    }
}
