package com.coderzf1.colordropper.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoriteColors")
public class Color {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "colorName")
    private final String colorName;
    @ColumnInfo(name="colorValue")
    private final int colorValue;

    public Color(String colorName, int colorValue) {
        this.colorName = colorName;
        this.colorValue = colorValue;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getColorName() {
        return colorName;
    }

    public int getColorValue() {
        return colorValue;
    }
}
