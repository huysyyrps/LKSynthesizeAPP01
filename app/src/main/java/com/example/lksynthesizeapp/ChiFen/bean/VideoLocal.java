package com.example.lksynthesizeapp.ChiFen.bean;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

public class VideoLocal implements Serializable {
    public String time;
    public File file;
    public Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
