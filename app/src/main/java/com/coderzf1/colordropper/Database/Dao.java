package com.coderzf1.colordropper.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Query("SELECT * FROM color")
    LiveData<List<Color>> getAll();

    @Query("SELECT * FROM color WHERE color_name LIKE :colorName LIMIT 1")
    Color findByName(String colorName);

    @Insert
    void insert(Color color);

    @Delete
    void delete(Color color);
}
