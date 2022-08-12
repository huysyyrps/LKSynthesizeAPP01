package com.example.lksynthesizeapp.ChiFen.Media;

import java.io.File;

/**
 * Created by dell on 2017/4/25.
 */

public interface MediaCallBack {
    void onStop(Throwable error, File output);
    void onStart();
    void onRecording(Long time);
}
