package com.example.lksynthesizeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.nick.library.RecBridgeServiceProxy;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        RecBridgeServiceProxy proxy = RecBridgeServiceProxy.from(this);
//        RecBridgeServiceProxy.from(this)
//                .start(IParam.builder()
//                        .audioSource(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE))
//                        .frameRate(settingsProvider.getInt(SettingsProvider.Key.FAME_RATE))
//                        .audioBitrate(settingsProvider.getInt(SettingsProvider.Key.AUDIO_BITRATE_RATE_K))
//                        .orientation(settingsProvider.getInt(SettingsProvider.Key.ORIENTATION))
//                        .resolution(settingsProvider.getString(SettingsProvider.Key.RESOLUTION))
//                        .stopOnScreenOff(settingsProvider.getBoolean(SettingsProvider.Key.SCREEN_OFF_STOP))
//                        .useMediaProjection(!isPlatformBridge)
//                        .stopOnShake(settingsProvider.getBoolean(SettingsProvider.Key.SHAKE_STOP))
//                        .shutterSound(settingsProvider.getBoolean(SettingsProvider.Key.SHUTTER_SOUND))
//                        .path(SettingsProvider.get().createVideoFilePath())
//                        .showNotification(true)
//                        .showTouch(settingsProvider.getBoolean(SettingsProvider.Key.SHOW_TOUCH))
//                        .build();
    }
}