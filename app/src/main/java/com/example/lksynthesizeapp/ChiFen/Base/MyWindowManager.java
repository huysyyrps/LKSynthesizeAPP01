package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MyWindowManager {
    Context context;
    private int mWindowWidth ;
    private int mWindowHeight ;
    private int mScreenDensity;
    private ImageReader mImageReader;
    private WindowManager mWindowManager;
    DisplayMetrics displayMetrics;
    public MyWindowManager(Context context){
        this.context = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
    }
    public int getHeight(){
        mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
        return displayMetrics.heightPixels;
    }
    public int getWeight(){
        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
        return displayMetrics.widthPixels;
    }

    public int getScreenDensity(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        mScreenDensity = displayMetrics.densityDpi;
        return mScreenDensity;
    }
    public ImageReader getImageReader(){
        mImageReader = ImageReader.newInstance(mWindowWidth, mWindowHeight, 0x1, 2);
        return mImageReader;
    }
}
