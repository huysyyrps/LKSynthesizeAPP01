package com.example.lksynthesizeapp.ChiFen.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
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
import com.example.lksynthesizeapp.ChiFen.Base.TirenSet;
import com.example.lksynthesizeapp.ChiFen.Media.AudioEncodeConfig;
import com.example.lksynthesizeapp.ChiFen.Media.CreateConfig;
import com.example.lksynthesizeapp.ChiFen.Media.GetRecorder;
import com.example.lksynthesizeapp.ChiFen.Media.MediaCallBack;
import com.example.lksynthesizeapp.ChiFen.Media.Notifications;
import com.example.lksynthesizeapp.ChiFen.Media.ScreenRecorder;
import com.example.lksynthesizeapp.ChiFen.Media.VideoEncodeConfig;
import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.ProgressDialogUtil;
import com.example.lksynthesizeapp.Constant.Net.SSHCallBack;
import com.example.lksynthesizeapp.Constant.Net.SSHExcuteCommandHelper;
import com.example.lksynthesizeapp.R;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.webView)
    MyWebView webView;
    @BindView(R.id.rbCamera)
    RadioButton rbCamera;
    @BindView(R.id.rbSound)
    RadioButton rbSound;
    @BindView(R.id.rbAlbum)
    RadioButton rbAlbum;
    @BindView(R.id.rbRefresh)
    RadioButton rbRefresh;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tvCompName)
    TextView tvCompName;
    @BindView(R.id.tvWorkName)
    TextView tvWorkName;
    @BindView(R.id.tvWorkCode)
    TextView tvWorkCode;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    @BindView(R.id.linearLayoutStop)
    LinearLayout linearLayoutStop;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.linlayoutData)
    LinearLayout linlayoutData;
    @BindView(R.id.rbBack)
    RadioButton rbBack;

    private Toast toast;
    String address;
    String toastData = "";
    private int mWindowWidth;
    private int mWindowHeight;
    private int mScreenDensity;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private WindowManager mWindowManager;
    private ScreenRecorder mRecorder;
    private Notifications mNotifications;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private String project = "", workName = "", workCode = "";
    public static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".action.STOP";
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    String SelectTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_local);
        ButterKnife.bind(this);

        setWorkData();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
//        mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        mWindowHeight = displayMetrics.heightPixels;
        mWindowWidth = displayMetrics.widthPixels;
        mScreenDensity = displayMetrics.densityDpi;

        Display display = getWindowManager().getDefaultDisplay();
        Log.e("XXXXX", "width-display :" + display.getWidth() + "heigth-display :" + display.getHeight());
        mImageReader = ImageReader.newInstance(display.getWidth(), display.getHeight(), 0x1, 2);

        frameLayout.setBackgroundColor(getResources().getColor(R.color.black));
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mNotifications = new Notifications(getApplicationContext());
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (project.trim().equals("") && workName.trim().equals("") && workCode.trim().equals("")) {
            radioGroup.setVisibility(View.GONE);
        }
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }

        WebSettings WebSet = webView.getSettings();    //获取webview设置
        WebSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);   //自适应屏幕
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setBackgroundColor(getColor(R.color.black));

        address = getIntent().getStringExtra("address");
        if (address != null) {
            address = "http://" + address + ":8080";
            webView.loadUrl(address);
        } else {
            Toast.makeText(mNotifications, "IP为空,请等待连接", Toast.LENGTH_SHORT).show();
            finish();
        }
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    private void setWorkData() {
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (project.trim().equals("") && workName.trim().equals("") && workCode.trim().equals("")) {
            linlayoutData.setVisibility(View.GONE);
        }
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }
    }

    @OnClick({R.id.rbCamera, R.id.rbSound, R.id.rbAlbum, R.id.rbRefresh, R.id.linearLayoutStop, R.id.rbBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCamera:
//                SelectTag = "Camera";
//                if (mMediaProjection == null) {
//                    requestMediaProjection();
//                } else {
//                    radioGroup.setVisibility(View.GONE);
//                    if (toast != null) {
//                        toast.cancel();
//                    }
//                    if (mMediaProjection != null) {
//                        setUpVirtualDisplay();
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startCapture();
//                            }
//                        }, 200);
//                    }
//                }
                if (toast!=null){
                    toast.cancel();
                }
                radioGroup.setVisibility(View.GONE);
                View view1 = frameLayout;
                view1.setDrawingCacheEnabled(true);
                view1.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
                if (bitmap != null) {
                    boolean backstate = new ImageSave().saveBitmap("/LUKEImage/", project, workName, workCode, this, bitmap);
                    if (backstate) {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(LocalActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存成功");
                    } else {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(LocalActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存失败");
                    }
                } else {
                    System.out.println("bitmap is NULL!");
                }
//                boolean backstate = new ImageSave().saveBitmap("/LUKEImage/", project, workName, workCode, this, bitmap);
                break;
            case R.id.rbSound:
                if (toast!=null){
                    toast.cancel();
                }
                SelectTag = "Sound";
                if (mMediaProjection == null) {
                    requestMediaProjection();
                } else {
                    if (EasyPermissions.hasPermissions(this, PERMS)) {
                        new TirenSet().checkTirem(ivTimer);
                        startCapturing(mMediaProjection);
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
                new MainUI().showPopupMenu(rbAlbum, "Local", this);
                break;
            case R.id.rbRefresh:
//                ShowDialog("/etc/init.d/mjpg-streamer restart");
                ShowDialog("uci set mjpg-streamer.core.fps=30", "uci commit", "/etc/init.d/mjpg-streamer restart");
                break;
            case R.id.rbBack:
                finish();
                break;
        }
    }

    private void setUpVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mWindowWidth, mWindowHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Log.e("MainActivity", "image is null.");
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
            boolean backstate = new ImageSave().saveBitmap("/LUKEImage/", project, workName, workCode, this, mBitmap);
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
        Toast.makeText(LocalActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    //创建申请录屏的 Intent
    private void requestMediaProjection() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, Constant.TAG_ONE);
    }

    private void startCapturing(MediaProjection mediaProjection) {
        radioGroup.setVisibility(View.GONE);
        linearLayoutStop.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    LocalActivity.this.runOnUiThread(() -> {
                        VideoEncodeConfig video = new CreateConfig().createVideoConfig();
                        AudioEncodeConfig audio = new CreateConfig().createAudioConfig(); // audio can be null
                        if (video == null) {
                            Toast.makeText(LocalActivity.this, "Create ScreenRecorder failure", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        File dir = new File(Environment.getExternalStorageDirectory() + "/LUKEVideo/" + project + "/" + workName + "/" + workCode + "/");
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

    private void startRecorder() {
        if (mRecorder == null) return;
        mRecorder.start();
        registerReceiver(mStopActionReceiver, new IntentFilter(ACTION_STOP));
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

    /**
     * 重启服务刷新视频
     *
     * @param data1
     */
    private void ShowDialog(String data1, String data2, String data3) {
        try {
            ProgressDialogUtil.startLoad(this, "重启中");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SSHExcuteCommandHelper.writeBefor(address, data1, new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            SSHExcuteCommandHelper.writeBefor(address, data2, new SSHCallBack() {
                                @Override
                                public void confirm(String data) {
                                    SSHExcuteCommandHelper.writeBefor(address, data3, new SSHCallBack() {
                                        @Override
                                        public void confirm(String data) {
                                            handlerSetting.sendEmptyMessage(Constant.TAG_ONE);
                                        }

                                        @Override
                                        public void error(String s) {
                                            toastData = s;
                                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                        }
                                    });
                                }

                                @Override
                                public void error(String s) {
                                    toastData = s;
                                    handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                }
                            });
                        }

                        @Override
                        public void error(String s) {
                            toastData = s;
                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                                    startCapture();
                                }
                            }, 200);
                        } else if (SelectTag.equals("Sound")) {
                            if (EasyPermissions.hasPermissions(this, PERMS)) {
                                new TirenSet().checkTirem(ivTimer);
                                startCapturing(mMediaProjection);
                            } else {
                                // 没有申请过权限，现在去申请
                                EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
                            }
                        }
                    }
//                    new TirenSet().checkTirem(ivTimer);
//                    startCapturing(mMediaProjection);
                } else {
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    Handler handlerSetting = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TAG_ONE:
                    Toast.makeText(LocalActivity.this, "重启成功", Toast.LENGTH_SHORT).show();
                    ProgressDialogUtil.stopLoad();
                    address = "http://" + address + ":8080";
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(LocalActivity.this, toastData, Toast.LENGTH_LONG).show();
                    ProgressDialogUtil.stopLoad();
                    break;
            }
        }
    };
}