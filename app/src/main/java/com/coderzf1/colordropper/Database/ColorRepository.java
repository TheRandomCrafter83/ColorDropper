package com.coderzf1.colordropper.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


public class ColorRepository {
    private Dao dao;
    private LiveData<List<Color>> mAllColors;

    public ColorRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.dao();
        mAllColors = dao.getAll();
    }

    public LiveData<List<Color>> getAllColors() {
        return mAllColors;
    }

    public void insert(Color color) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
                    insert(color);
                }
        );
    }

    public void delete(Color color) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            delete(color);
        });
    }
}
