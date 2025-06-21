package com.example.lksynthesizeapp.ChiFen.test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ScreenCaptureService extends Service {

    private static final String TAG = "ScreenCaptureService";
    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "screen_record_channel";

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String EXTRA_RESULT_CODE = "extra_result_code";
    public static final String EXTRA_DATA_INTENT = "extra_data_intent";
    public static final String EXTRA_SEGMENT_DURATION_MS = "extra_segment_duration_ms";

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private int screenWidth, screenHeight, screenDpi;
    private long segmentDurationMs = 5 * 60 * 1000; // Default: 5 minutes in milliseconds

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable segmentRunnable;

    // Use this to hold MediaProjection between Activity and Service.
    // In a production app, use Intent extras with parcelable or a more robust IPC.
    // For this example, we'll use a simple static holder (not recommended for production).
    public static MediaProjection sMediaProjection;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            screenDpi = metrics.densityDpi;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            // Retrieve MediaProjection from the static holder.
            // In production, pass result code and data intent directly from Activity's onActivityResult
            // and use mediaProjectionManager.getMediaProjection(resultCode, dataIntent) here.
            mediaProjection = sMediaProjection; // This is illustrative; pass reliably in real app.

            if (mediaProjection == null) {
                Log.e(TAG, "MediaProjection is null. Cannot start recording.");
                stopSelf();
                return START_NOT_STICKY;
            }

            // Get segment duration from intent, if provided
            long extraDuration = intent.getLongExtra(EXTRA_SEGMENT_DURATION_MS, -1);
            if (extraDuration > 0) {
                segmentDurationMs = extraDuration;
            }

            // Start as a foreground service
            Notification notification = createNotification();
            startForeground(NOTIFICATION_ID, notification);

            startNewRecordingSegment(); // Start the first segment
            scheduleNextSegment(); // Schedule subsequent segments

            return START_REDELIVER_INTENT; // If killed, try to redeliver this intent
        } else if (ACTION_STOP.equals(action)) {
            stopRecordingAndCleanup();
            stopSelf();
            return START_NOT_STICKY;
        }

        return START_NOT_STICKY;
    }

    private void startNewRecordingSegment() {
        Log.d(TAG, "Starting new recording segment...");
        // Stop any existing recording gracefully
        stopCurrentRecordingInternal();

        mediaRecorder = new MediaRecorder();
        try {
            // Configure MediaRecorder
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // For microphone audio
            // For internal audio (API 29+), you'd need AudioPlaybackCaptureConfiguration
            // mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            // mediaRecorder.setAudioCaptureSession(new AudioPlaybackCaptureConfiguration.Builder(mediaProjection).build());

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            // Set video size to screen resolution
            mediaRecorder.setVideoSize(screenWidth, screenHeight);
            mediaRecorder.setVideoFrameRate(30); // 30 FPS
            mediaRecorder.setVideoEncodingBitRate(6 * 1024 * 1024); // 6 Mbps, adjust as needed

            // Set output file path with a timestamp for uniqueness
            String filePath = getOutputFilePath();
            mediaRecorder.setOutputFile(filePath);
            Log.d(TAG, "New segment file: " + filePath);

            mediaRecorder.prepare();

            // Create VirtualDisplay using the MediaProjection and MediaRecorder's surface
            virtualDisplay = mediaProjection.createVirtualDisplay(
                    "ScreenCaptureSegment",
                    screenWidth,
                    screenHeight,
                    screenDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(),
                    null,
                    null
            );

            mediaRecorder.start();
            Log.d(TAG, "Recording segment started.");
        } catch (IllegalStateException | IOException e) {
            Log.e(TAG, "Error starting new recording segment", e);
            stopRecordingAndCleanup(); // If error, stop everything
        }
    }

    private void stopCurrentRecordingInternal() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                Log.d(TAG, "Current recording segment stopped and released.");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error stopping MediaRecorder (may have already stopped/failed)", e);
            } finally {
                mediaRecorder = null;
            }
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
    }

    private void scheduleNextSegment() {
        if (segmentRunnable != null) {
            handler.removeCallbacks(segmentRunnable); // Remove any pending
        }

        segmentRunnable = () -> {
            Log.d(TAG, "Segment duration reached. Starting next segment...");
            startNewRecordingSegment();
            scheduleNextSegment(); // Schedule the next one
        };
        handler.postDelayed(segmentRunnable, segmentDurationMs);
        Log.d(TAG, "Next segment scheduled in " + (segmentDurationMs / 1000) + " seconds.");
    }

    private void stopRecordingAndCleanup() {
        Log.d(TAG, "Stopping all recording and cleaning up...");
        if (handler != null && segmentRunnable != null) {
            handler.removeCallbacks(segmentRunnable);
            segmentRunnable = null;
        }
        stopCurrentRecordingInternal(); // Stop the current segment
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
            sMediaProjection = null; // Clear static reference
        }
        stopForeground(true); // Remove notification
        Log.d(TAG, "Recording fully stopped.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecordingAndCleanup(); // Ensure all resources are released when service is destroyed
        Log.d(TAG, "Service destroyed.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        createNotificationChannel(); // Ensure channel exists for Android O+

        Intent stopIntent = new Intent(this, ScreenCaptureService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("屏幕录制中")
                .setContentText("正在录制屏幕并分段存储")
                .setSmallIcon(android.R.drawable.ic_media_play) // Use a suitable icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true) // Makes it non-dismissible
                .addAction(android.R.drawable.ic_media_pause, "停止录制", stopPendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "屏幕录制通知",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("用于屏幕录制服务的前台通知");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private String getOutputFilePath() {
        File moviesDir = new File(Environment.getExternalStorageDirectory() + "/LUKEDescVideo/");
        if (!moviesDir.exists()) {
            moviesDir.mkdirs();
        }
//        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        // Create a specific folder for your app's recordings
//        File appDir = new File(moviesDir, "YourAppRecordings");
//        if (!appDir.exists()) {
//            appDir.mkdirs();
//        }
        String fileName = getNowDate() + ".mp4";
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(moviesDir, fileName).getAbsolutePath();
    }
    /**
     * 获取当前时间,用来给文件夹命名
     */
    private String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.US);
        return format.format(new Date());
    }
}