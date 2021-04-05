package com.beebapcay.notesapp.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "tile")
    private String mTitle;

    @ColumnInfo(name = "date_time")
    private String mDateTime;

    @ColumnInfo(name = "content")
    private String mContent;

    @ColumnInfo(name = "color")
    private String mColor;

    @ColumnInfo(name = "image_path")
    private String mImagePath;

    public Note() {
        mTitle = "";
        mContent = "";
        mDateTime = null;
        mColor = "#FFFFFF";
        mImagePath = null;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        mDateTime = dateTime;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Note{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mDateTime='" + mDateTime + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mColor='" + mColor + '\'' +
                ", mImagePath='" + mImagePath + '\'' +
                '}';
    }
}
