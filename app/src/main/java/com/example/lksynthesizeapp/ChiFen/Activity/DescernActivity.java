package com.example.lksynthesizeapp.ChiFen.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.BuildConfig;
import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.GetDate;
import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Base.MyPaint;
import com.example.lksynthesizeapp.ChiFen.Base.MyWindowManager;
import com.example.lksynthesizeapp.ChiFen.Base.TirenSet;
import com.example.lksynthesizeapp.ChiFen.Media.AudioEncodeConfig;
import com.example.lksynthesizeapp.ChiFen.Media.CreateConfig;
import com.example.lksynthesizeapp.ChiFen.Media.GetRecorder;
import com.example.lksynthesizeapp.ChiFen.Media.MediaCallBack;
import com.example.lksynthesizeapp.ChiFen.Media.Notifications;
import com.example.lksynthesizeapp.ChiFen.Media.ScreenRecorder;
import com.example.lksynthesizeapp.ChiFen.Media.VideoEncodeConfig;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.YoloV5Ncnn;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class DescernActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.rbCamera)
    RadioButton rbCamera;
    @BindView(R.id.rbVideo)
    RadioButton rbVideo;
    @BindView(R.id.rbAlbum)
    RadioButton rbAlbum;
    @BindView(R.id.rbBack)
    RadioButton rbBack;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.tvCompName)
    TextView tvCompName;
    @BindView(R.id.tvWorkName)
    TextView tvWorkName;
    @BindView(R.id.tvWorkCode)
    TextView tvWorkCode;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    @BindView(R.id.linearLayoutStop)
    LinearLayout linearLayoutStop;
    private String url;
    private Bitmap bmp = null;
    private Thread mythread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    URL videoUrl;
    HttpURLConnection conn;
    private MediaPlayer mediaPlayer;
    public boolean runing = true;
    public static String project = "", workName = "", workCode = "", address = "";
    public boolean isFirst = true;
    public long saveTime = 0;
    public long currentTmeTime = 0;
    public static final int TIME = 3000;
    Handler handler;
    String SelectTag = "";
    Display display;
    private Toast toast;
    private ImageReader mImageReader;
    private ScreenRecorder mRecorder;
    private Notifications mNotifications;
    //创建一个虚屏VirtualDisplay，内含一个真实的Display对象
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;
    private MyWindowManager myWindowManager;
    private MediaProjectionManager mMediaProjectionManager;
    public static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".action.STOP";
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_descern);
        ButterKnife.bind(this);
        display = getWindowManager().getDefaultDisplay();
        myWindowManager = new MyWindowManager(this);
        mNotifications = new Notifications(getApplicationContext());
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        //ImageReader允许应用程序直接获取渲染到surface的图形数据，并转换为图片，这里我用
        Log.e("XXXXX","width-display :" + display.getWidth()+"heigth-display :" + display.getHeight());
        mImageReader = ImageReader.newInstance(display.getWidth(), display.getHeight(), 0x1, 2);
        mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.fengming);
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Toast.makeText(this, "yolov5ncnn Init failed", Toast.LENGTH_SHORT).show();
        }
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }

        address = getIntent().getStringExtra("address");
        if (address != null) {
            url = "http://" + address + ":8080?action=snapshot";
            mythread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runing) {
                        draw();
                    }
                }
            });
            mythread.start();
        } else {
            Toast.makeText(mNotifications, "IP为空,请等待连接", Toast.LENGTH_SHORT).show();
            finish();
        }

        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //创建一个URL对象
            videoUrl = new URL(url);
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = (HttpURLConnection) videoUrl.openConnection();
            //设置输入流
            conn.setDoInput(true);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            bmp = BitmapFactory.decodeStream(inputstream);
            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false);
            showObjects(objects);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
        } finally {
        }
    }

    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null) {
            imageView.setImageBitmap(bmp);
            return;
        }

        // draw objects on bitmap
        Bitmap rgba = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(rgba);
        for (int i = 0; i < objects.length; i++) {
            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, new MyPaint().getLinePaint());
            // draw filled text inside image
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
//                canvas.drawRect(x, y, x + text_width, y + text_height, textbgpaint);
                canvas.drawText(text, x, y - new MyPaint().getTextpaint().ascent(), new MyPaint().getTextpaint());
            }
        }
        if (objects.length != 0) {
            mediaPlayer.start();
            if (isFirst) {
                saveImageToGallery(DescernActivity.this, rgba);
                saveTime = System.currentTimeMillis();
                isFirst = false;
            } else {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                } else {
                    currentTmeTime = System.currentTimeMillis();
                    if (currentTmeTime - saveTime > 3000) {
                        saveImageToGallery(DescernActivity.this, rgba);
                        saveTime = currentTmeTime;
                    }
                }
            }
        }
        imageView.setImageBitmap(rgba);
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        boolean backstate = new ImageSave().saveBitmap("/LUKEDecsImage/", project, workName, workCode, context, bmp);
        if (backstate) {
            Log.e("XXX", "保存成功");
        } else {
            Log.e("XXX", "保存失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mythread != null) {
            mythread.interrupt();
        }
        runing = false;
    }

    @OnClick({R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum, R.id.rbBack, R.id.linearLayoutStop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCamera:
                SelectTag = "Camera";
                if (mMediaProjection == null) {
                    requestMediaProjection();
                } else {
                    radioGroup.setVisibility(View.GONE);
                    if (toast != null) {
                        toast.cancel();
                    }
                    if (mMediaProjection != null) {
                        setUpVirtualDisplay();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startCaptureImg();
                            }
                        }, 200);
                    }
                }
                break;
            case R.id.rbVideo:
                SelectTag = "Sound";
                if (mMediaProjection == null) {
                    requestMediaProjection();
                } else {
                    if (EasyPermissions.hasPermissions(this, PERMS)) {
                        new TirenSet().checkTirem(ivTimer);
                        startCaptureVideo(mMediaProjection);
                    } else {
                        // 没有申请过权限，现在去申请
                        EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
                    }
                }
                break;
            case R.id.linearLayoutStop:
                radioGroup.setVisibility(View.VISIBLE);
                linearLayoutStop.setVisibility(View.GONE);
                if (mRecorder != null) {
                    stopRecordingAndOpenFile();
                }
                break;
            case R.id.rbAlbum:
//                Intent intent = new Intent(this, PhotoActivity.class);
//                intent.putExtra("tag", "Descern");
//                startActivity(intent);
                new MainUI().showPopupMenu(rbAlbum, "Desc", this);
                break;
            case R.id.rbBack:
                if (mythread != null) {
                    mythread.interrupt();
                }
                runing = false;
                finish();
                break;
        }
    }

    //创建申请录屏的 Intent
    private void requestMediaProjection() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, Constant.TAG_ONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent backdata) {
        super.onActivityResult(requestCode, resultCode, backdata);
        switch (requestCode) {
            case Constant.TAG_ONE:
                if (resultCode == Activity.RESULT_OK) {
                    new BottomUI().hideBottomUIMenu(this.getWindow());
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, backdata);
                    if (mMediaProjection != null) {
                        if (SelectTag.equals("Camera")) {
                            radioGroup.setVisibility(View.GONE);
                            if (toast != null) {
                                toast.cancel();
                            }
                            setUpVirtualDisplay();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startCaptureImg();
                                }
                            }, 200);
                        } else if (SelectTag.equals("Sound")) {
                            if (EasyPermissions.hasPermissions(this, PERMS)) {
                                new TirenSet().checkTirem(ivTimer);
                                startCaptureVideo(mMediaProjection);
                            } else {
                                // 没有申请过权限，现在去申请
                                EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
                            }
                        }
                    }
                } else {
                    finish();
                }
                break;
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                myWindowManager.getWeight(), myWindowManager.getHeight(), myWindowManager.getScreenDensity(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }
    //获取截图并保存
    private void startCaptureImg() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Toast.makeText(mNotifications, "image is null", Toast.LENGTH_SHORT).show();
            radioGroup.setVisibility(View.VISIBLE);
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(buffer);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);
        image.close();
        stopScreenCapture();
        if (mBitmap != null) {
            boolean backstate = new ImageSave().saveBitmap("/LUKEDecsImage/", project, workName, workCode, this, mBitmap);
            if (backstate) {
                radioGroup.setVisibility(View.VISIBLE);
//                toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
//                toast.show();
                Log.e("XXX", "保存成功");
            } else {
                radioGroup.setVisibility(View.VISIBLE);
//                toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
//                toast.show();
                Log.e("XXX", "保存失败");
            }
        } else {
            System.out.println("bitmap is NULL!");
        }
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }


    //录屏部分
    private void startCaptureVideo(MediaProjection mediaProjection) {
        radioGroup.setVisibility(View.GONE);
        linearLayoutStop.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    DescernActivity.this.runOnUiThread(() -> {
                        VideoEncodeConfig video = new CreateConfig().createVideoConfig();
                        AudioEncodeConfig audio = new CreateConfig().createAudioConfig(); // audio can be null
                        if (video == null) {
                            Toast.makeText(DescernActivity.this, "Create ScreenRecorder failure", Toast.LENGTH_SHORT).show();
                            radioGroup.setVisibility(View.VISIBLE);
                            linearLayoutStop.setVisibility(View.GONE);
                            return;
                        }
                        File dir = new File(Environment.getExternalStorageDirectory() + "/LUKEDescVideo/" + project + "/" + workName + "/" + workCode + "/");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, new GetDate().getNowDate() + ".mp4");
//                        checkTirem();
                        mRecorder = new GetRecorder().newRecorder(mediaProjection, video, audio, file, new MediaCallBack() {
                            @Override
                            public void onStop(Throwable error, File output) {
                                runOnUiThread(() -> stopRecorder());
                                if (error != null) {
                                    error.printStackTrace();
                                    output.delete();
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                                            .addCategory(Intent.CATEGORY_DEFAULT)
                                            .setData(Uri.fromFile(output));
                                    sendBroadcast(intent);
                                }
                            }

                            @Override
                            public void onStart() {
                                mNotifications.recording(0);
                            }

                            @Override
                            public void onRecording(Long presentationTimeUs) {
                                long startTime = 0;
                                if (startTime <= 0) {
                                    startTime = presentationTimeUs;
                                }
                                long time = (presentationTimeUs - startTime) / 1000;
                                mNotifications.recording(time);
                            }
                        });
                        startRecorder();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 实现“onRequestPermissionsResult”函数接收校验权限结果。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // 授予权限后操作
        requestMediaProjection();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 请求权限被拒
        Toast.makeText(DescernActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    private void startRecorder() {
        if (mRecorder == null) return;
        mRecorder.start();
        registerReceiver(mStopActionReceiver, new IntentFilter(ACTION_STOP));
    }

    private BroadcastReceiver mStopActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_STOP.equals(intent.getAction())) {
                stopRecordingAndOpenFile();
            }
        }
    };
    private void stopRecordingAndOpenFile() {
        stopRecorder();
        StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
        try {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        } finally {
            StrictMode.setVmPolicy(vmPolicy);
        }
    }

    private void stopRecorder() {
        mNotifications.clear();
        if (mRecorder != null) {
            mRecorder.quit();
        }
        mRecorder = null;
        try {
            unregisterReceiver(mStopActionReceiver);
        } catch (Exception e) {
            //ignored
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }
}
