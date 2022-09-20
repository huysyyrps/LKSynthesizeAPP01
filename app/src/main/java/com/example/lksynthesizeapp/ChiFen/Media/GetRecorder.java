package com.example.lksynthesizeapp.ChiFen.Media;

import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;

import java.io.File;

public class GetRecorder {
    //VirtualDisplay对应虚拟Display，主要用来进行屏幕录制等相关功能；
    public VirtualDisplay mVirtualDisplay;
    public ScreenRecorder newRecorder(MediaProjection mediaProjection, VideoEncodeConfig video, AudioEncodeConfig audio, File output, MediaCallBack mediaCallBack) {
        final VirtualDisplay display = getOrCreateVirtualDisplay(mediaProjection, video);
        ScreenRecorder r = new ScreenRecorder(video, audio, display, output.getAbsolutePath());
        r.setCallback(new ScreenRecorder.Callback() {
            long startTime = 0;

            @Override
            public void onStop(Throwable error) {
                mediaCallBack.onStop(error, output);
            }

            @Override
            public void onStart() {
                mediaCallBack.onStart();
            }

            @Override
            public void onRecording(long presentationTimeUs) {
                mediaCallBack.onRecording(presentationTimeUs);
            }
        });
        return r;
    }

    private VirtualDisplay getOrCreateVirtualDisplay(MediaProjection mediaProjection, VideoEncodeConfig config) {
        if (mVirtualDisplay == null) {
            mVirtualDisplay = mediaProjection.createVirtualDisplay("ScreenRecorder-display0",
                    config.width, config.height, 1 /*dpi*/,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    null /*surface*/, null, null);
        } else {
            // resize if size not matched
            Point size = new Point();
            mVirtualDisplay.getDisplay().getSize(size);
            if (size.x != config.width || size.y != config.height) {
                mVirtualDisplay.resize(config.width, config.height, 1);
            }
        }
        return mVirtualDisplay;
    }
}
