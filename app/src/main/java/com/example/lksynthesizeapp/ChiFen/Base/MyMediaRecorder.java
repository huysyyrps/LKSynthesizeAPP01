package com.example.lksynthesizeapp.ChiFen.Base;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyMediaRecorder {
    public MediaRecorder getMediaRecorder(String project,String workName,String workCode,String path) {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);    //音频载体
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);    //视频载体
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);   //输出格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);  //音频格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //视频格式

        mediaRecorder.setVideoSize(2400, 1080);  //size
        mediaRecorder.setVideoFrameRate(30);    //帧率
        mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024); //比特率
        mediaRecorder.setOrientationHint(0);    //旋转角度

        //创建文件夹
        File dir = new File(Environment.getExternalStorageDirectory() + path + project + "/" + workName + "/" + workCode + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //创建文件名
        String fileName = getNowDate() + ".mp4";

        //设置文件位置
        String filePath = dir + "/" + fileName;
        mediaRecorder.setOutputFile(filePath);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return mediaRecorder;
    }

    /**
     * 获取当前时间,用来给文件夹命名
     */
    private String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return format.format(new Date());
    }
}
