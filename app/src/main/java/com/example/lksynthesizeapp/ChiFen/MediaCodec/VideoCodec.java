package com.example.lksynthesizeapp.ChiFen.MediaCodec;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoCodec extends Thread {

    private static final String TAG = "------>dddd<---------";
    private final ScreenLive screenLive;

    private MediaProjection mediaProjection;

    private MediaCodec mediaCodec;//硬编
    private boolean isLiving;
    private VirtualDisplay virtualDisplay;//虚拟画布
    private long timeStamp;
    private long startTime;
    private int width = 1280;
    private int height = 720;

    public VideoCodec(ScreenLive screenLive) {
        this.screenLive = screenLive;
    }

    public void startLive(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;

        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,
                width,
                height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //码率，帧率，分辨率，关键帧间隔
        format.setInteger(MediaFormat.KEY_BIT_RATE, width*height);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);//手机
            //表示编码操作CONFIGURE_FLAG_ENCODE
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = mediaCodec.createInputSurface();

            virtualDisplay = mediaProjection.createVirtualDisplay(
                    "screen-codec",
                    width, height, 2,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    surface, null, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
        LiveTaskManager.getInstance().execute(this);
    }

    @Override
    public void run() {
        isLiving = true;
        mediaCodec.start();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (isLiving) {
            //2000毫秒 手动触发输出关键帧(包里的时间大于2秒才去处理timeStamp >= 2000)
            if (System.currentTimeMillis() - timeStamp >= 1000) {
                Bundle params = new Bundle();
                //立即刷新 让下一帧是关键帧
                params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                //传到编码器
                mediaCodec.setParameters(params);
                timeStamp = System.currentTimeMillis();
            }
            int index = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000);//原值100000
//            Log.i(TAG, "VideoCodec run: " + index);
            if (index >= 0) {
                ByteBuffer buffer = mediaCodec.getOutputBuffer(index);
                byte[] outData = new byte[bufferInfo.size];
                buffer.get(outData);
                if (startTime == 0) {
                    // 微妙转为毫秒
                    startTime = bufferInfo.presentationTimeUs / 1000;
                }
//                包含   分隔符
                RTMPPackage rtmpPackage = new RTMPPackage(outData, (bufferInfo.presentationTimeUs / 1000) - startTime);
                rtmpPackage.setType(RTMPPackage.RTMP_PACKET_TYPE_VIDEO);
                screenLive.addPackage(rtmpPackage);
                mediaCodec.releaseOutputBuffer(index, false);
            }
        }
        isLiving = false;
        startTime = 0;
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
        virtualDisplay.release();
        virtualDisplay = null;
        mediaProjection.stop();
        mediaProjection = null;
    }
    public void stopLive(){
        isLiving = false;
        try{
            join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
