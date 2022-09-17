package com.example.lksynthesizeapp.ChiFen.Media;

import static android.media.MediaFormat.MIMETYPE_AUDIO_AAC;
import static android.media.MediaFormat.MIMETYPE_VIDEO_AVC;

import android.media.MediaCodecInfo;

public class CreateConfig {
    static final String VIDEO_AVC = MIMETYPE_VIDEO_AVC; // H.264 Advanced Video Coding
    static final String AUDIO_AAC = MIMETYPE_AUDIO_AAC;
    public AudioEncodeConfig createAudioConfig() {
        String codec = "c2.android.aac.encoder";
        int bitrate = 80000;
        int samplerate = 44100;
        int channelCount = 1;
        int profile = 1;
        return new AudioEncodeConfig(codec, AUDIO_AAC, bitrate, samplerate, channelCount, profile);
    }

    public VideoEncodeConfig createVideoConfig() {
        final String codec = "c2.android.avc.encoder";
        int height = 1080;
        int width = 2400;
        int framerate = 25;
        int iframe = 1;
        int bitrate = 800000;
        MediaCodecInfo.CodecProfileLevel profileLevel = null;
        return new VideoEncodeConfig(width, height, bitrate, framerate, iframe, codec, VIDEO_AVC, profileLevel);
    }
}
