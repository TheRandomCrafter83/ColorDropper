package com.coderzf1.colordropper.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_colors")
public class Color {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "color_name")
    private final String colorName;
    @ColumnInfo(name="color_value")
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
