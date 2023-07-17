package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyMediaRecorder {
    public MediaRecorder getMediaRecorder(Context context, String path) {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);    //音频载体
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);    //视频载体
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);   //输出格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);  //音频格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //视频格式
        mediaRecorder.setVideoSize(2400, 1080);  //size
        mediaRecorder.setVideoFrameRate(30);    //帧率
        //创建文件夹
//        File dir = null;
//        if (Build.VERSION.SDK_INT > 29) {
//            dir = new File(context.getExternalFilesDir(null).getAbsolutePath() + path + project + "/" + workName + "/" + workCode + "/");
//        } else {
//            dir = new File(Environment.getExternalStorageDirectory() + path + project + "/" + workName + "/" + workCode + "/");
//        }
        File dir = new File(Environment.getExternalStorageDirectory() + path + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //创建文件名
        String fileName = getNowDate() + ".mp4";

        //设置文件位置
        String filePath = dir + "/" + fileName;
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setVideoEncodingBitRate(3 * 1920 * 1080); //比特率
        mediaRecorder.setOrientationHint(0);    //旋转角度


        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            Log.e("XXX",e.toString());
        }
        return mediaRecorder;
    }


    /**
     * 获取屏幕的宽度px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }

    /**
     * 获取当前时间,用来给文件夹命名
     */
    private String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.US);
        return format.format(new Date());
    }
}
