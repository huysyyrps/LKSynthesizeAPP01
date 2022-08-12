package com.example.lksynthesizeapp.ChiFen.MediaCodec;

import android.media.projection.MediaProjection;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class ScreenLive extends Thread {

    static {
        System.loadLibrary("yolov5ncnn");
    }

    private static final String TAG = "------>dddd<--------";
    private boolean isLiving;
    private LinkedBlockingQueue<RTMPPackage> queue = new LinkedBlockingQueue<>();
    private String url;
    private MediaProjection mediaProjection;
    VideoCodec videoCodec;


    public void startLive(String url, MediaProjection mediaProjection) {
        this.url = url;
        this.mediaProjection = mediaProjection;
        LiveTaskManager.getInstance().execute(this);
    }


    public void addPackage(RTMPPackage rtmpPackage) {
        if (!isLiving) {
            return;
        }
        queue.add(rtmpPackage);
    }

    public void stopLive() {
        isLiving = false;
        queue.clear();
        disConnect();
        if (videoCodec!=null){
            videoCodec.stopLive();
        }
    }


    @Override
    public void run() {
        //1推送到
        if (!connect(url)) {
            Log.i(TAG, "run: ----------->推送失败");
            return;
        }

        videoCodec = new VideoCodec(this);
        videoCodec.startLive(mediaProjection);

//        AudioCodec audioCodec = new AudioCodec(this);
//        audioCodec.startLive();

        isLiving = true;
        while (isLiving) {
            RTMPPackage rtmpPackage = null;
            try {
                rtmpPackage = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "取出数据");
            if (rtmpPackage.getBuffer() != null && rtmpPackage.getBuffer().length != 0) {
                sendData(rtmpPackage.getBuffer(), rtmpPackage.getBuffer()
                        .length, rtmpPackage.getTms(), rtmpPackage.getType());
            }
        }
        isLiving = false;
        videoCodec.stopLive();
        queue.clear();
        disConnect();
    }

    //连接RTMP服务器
    private native boolean connect(String url);

    //断开服务器
    private native boolean disConnect();

    //发送RTMP Data
    private native boolean sendData(byte[] data, int len, long tms, int type);
}
