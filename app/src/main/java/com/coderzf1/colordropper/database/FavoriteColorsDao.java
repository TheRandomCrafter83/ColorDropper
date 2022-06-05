package com.coderzf1.colordropper.database;

import androidx.room.Dao;
import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteColorsDao {
    @Query("SELECT * FROM favorite_colors")
    LiveData<List<Color>> getAllColors();

    @Query("SELECT * FROM favorite_colors WHERE color_name LIKE :colorName LIMIT 1")
    Color findByName(String colorName);

    @Update
    void update(Color color);

    @Insert
    void insert(Color color);

    @Delete
    void delete(Color color);

}
