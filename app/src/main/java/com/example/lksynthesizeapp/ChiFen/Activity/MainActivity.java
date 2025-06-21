package com.example.lksynthesizeapp.ChiFen.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Base.MyPaint;
import com.example.lksynthesizeapp.ChiFen.test.ScreenCaptureService;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.YoloV5Ncnn;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String url;
    private Thread mythread;
    HttpURLConnection conn;
    private Bitmap croppedBitmap = null;
    private ImageView imageView;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;
    private MediaProjectionManager mediaProjectionManager;

    private Button startButton;
    private Button stopButton;
    private boolean isRecording = false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        imageView = findViewById(R.id.imageView1);
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Toast.makeText(this, "yolov5ncnn Init failed", Toast.LENGTH_SHORT).show();
        }
        mythread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    draw();
                }
            }
        });
        mythread.start();

        startButton = findViewById(R.id.start_record_button);
        stopButton = findViewById(R.id.stop_record_button);
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startButton.setOnClickListener(v -> requestScreenCapturePermission());
        stopButton.setOnClickListener(v -> stopRecordingService());
        updateUI();
    }

    private void requestScreenCapturePermission() {
        if (!isRecording) {
            // This intent will launch a system dialog asking for screen capture permission.
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);
        } else {
            Toast.makeText(this, "Already recording.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // User granted permission. Now, start the service.
                // IMPORTANT: In a real app, you should parcel the data Intent or pass its components
                // (resultCode and data) to the service, and then call
                // mediaProjectionManager.getMediaProjection(resultCode, data) inside the service.
                // For this simplified example, we're using a static holder (not recommended for production).
                ScreenCaptureService.sMediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);

                if (ScreenCaptureService.sMediaProjection != null) {
                    startRecordingService(5 * 60 * 1000); // Record segments of 5 minutes
                    isRecording = true;
                    updateUI();
                    Toast.makeText(this, "屏幕录制已开始 (分段存储)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "获取MediaProjection失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                // User denied permission.
                Toast.makeText(this, "屏幕录制权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecordingService(long segmentDurationMs) {
        Intent serviceIntent = new Intent(this, ScreenCaptureService.class);
        serviceIntent.setAction(ScreenCaptureService.ACTION_START);
        serviceIntent.putExtra(ScreenCaptureService.EXTRA_SEGMENT_DURATION_MS, segmentDurationMs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void stopRecordingService() {
        if (isRecording) {
            Intent serviceIntent = new Intent(this, ScreenCaptureService.class);
            serviceIntent.setAction(ScreenCaptureService.ACTION_STOP);
            startService(serviceIntent); // Send stop command to service
            isRecording = false;
            updateUI();
            Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有正在进行的录制", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        startButton.setEnabled(!isRecording);
        stopButton.setEnabled(isRecording);
    }



    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //创建一个URL对象
            URL videoUrl = new URL("http://192.168.43.251:8080?action=snapshot");
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = (HttpURLConnection) videoUrl.openConnection();
            //设置输入流
            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            Bitmap bmp = BitmapFactory.decodeStream(inputstream);
            croppedBitmap = Bitmap.createBitmap(bmp, 172, 100, bmp.getWidth()-172, bmp.getHeight()-100);
            YoloV5Ncnn.Obj[] objects = null;
//            showObjects(objects);
            showObjects(objects);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
            Log.e("XXX",ex.toString());
        } finally {
        }
    }

    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null || objects.length == 0) {
            imageView.setImageBitmap(croppedBitmap);
            return;
        }

        Bitmap rgba = croppedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(rgba);
        for (int i = 0; i < objects.length; i++) {
            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, new MyPaint().getLinePaint());
            {
                String text = objects[i].label + " = " + String.format("%.1f", objects[i].prob * 100) + "%";

                float text_width = new MyPaint().getTextpaint().measureText(text) + 10;
                float text_height = -new MyPaint().getTextpaint().ascent() + new MyPaint().getTextpaint().descent() + 10;

                float x = objects[i].x;
                float y = objects[i].y - text_height;
                if (y < 0)
                    y = 0;
                if (x + text_width > rgba.getWidth())
                    x = rgba.getWidth() - text_width;
                canvas.drawText(text, x, y - new MyPaint().getTextpaint().ascent(), new MyPaint().getTextpaint());
            }
        }
        imageView.setImageBitmap(rgba);
    }
}