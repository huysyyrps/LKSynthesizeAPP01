package com.example.lksynthesizeapp.ChiFen.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Base.MyMediaRecorder;
import com.example.lksynthesizeapp.ChiFen.Base.MyPaint;
import com.example.lksynthesizeapp.ChiFen.Base.TirenSet;
import com.example.lksynthesizeapp.ChiFen.Media.Notifications;
import com.example.lksynthesizeapp.ChiFen.service.WhiteService;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.activity.SendSelectActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.example.lksynthesizeapp.YoloV5Ncnn;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.rbSetting)
    RadioButton rbSetting;
    @BindView(R.id.tvDeviceCode)
    TextView tvDeviceCode;
    @BindView(R.id.linlayoutData)
    LinearLayout linlayoutData;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.rbDescern)
    RadioButton rbDescern;
    @BindView(R.id.rbNoDescern)
    RadioButton rbNoDescern;
    private String url;
    private Bitmap bmp = null;
    private Bitmap rgba;
    private Thread mythread, saveThread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    URL videoUrl;
    HttpURLConnection conn;
    private MediaPlayer mediaPlayer;
    public boolean runing = true;
    public static String project = "", workName = "", workCode = "", deviceCode = "";
    public boolean isFirst = true;
    public long saveTime = 0;
    public long currentTmeTime = 0;
    public static final int TIME = 3000;
    private Toast toast;
    private Notifications mNotifications;
    //??????????????????VirtualDisplay????????????????????????Display??????
    private VirtualDisplay mVirtualDisplay;
    //???????????????
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    private MediaRecorder mediaRecorder;
    public static DescernActivity intance = null;
    //    private String selectMode;
    private boolean openDescern = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // ????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //??????????????????
        new BottomUI().hideBottomUIMenu(this.getWindow());
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        setContentView(R.layout.activity_descern);
        ButterKnife.bind(this);
        intance = this;
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
//        selectMode = intent.getStringExtra("selectMode");
//        if (selectMode.equals("mode1")){
//            selectnum = 1;
//        }else if (selectMode.equals("mode2")){
//            selectnum = 2;
//        }else if (selectMode.equals("mode3")){
//            selectnum = 3;
//        }
//        boolean ret_init = yolov5ncnn.Init(getAssets(),selectnum);
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Toast.makeText(this, "yolov5ncnn Init failed", Toast.LENGTH_SHORT).show();
        }
        deviceCode = new SharePreferencesUtils().getString(DescernActivity.this, "deviceCode", "");
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }
        tvDeviceCode.setText(deviceCode);

        url = "http://" + Constant.URL + ":8080?action=snapshot";
        mythread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runing) {
                    draw();
                }
            }
        });
        mythread.start();
        new BottomUI().hideBottomUIMenu(this.getWindow());
        if (new SharePreferencesUtils().getString(this, "keep", "").equals("true")) {
            //??????????????????
            intent = new Intent(DescernActivity.this, WhiteService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0???????????????????????????
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //????????????URL??????
            videoUrl = new URL(url);
            //??????HttpURLConnection????????????????????????????????????
            conn = (HttpURLConnection) videoUrl.openConnection();
            //???????????????
            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            //??????
            conn.connect();
            //??????????????????????????????
            inputstream = conn.getInputStream();
            //???????????????bitmap
            bmp = BitmapFactory.decodeStream(inputstream);
//            int wehit = bmp.getWidth();
//            int height = bmp.getHeight();
//            Log.e("XXX",wehit+"~"+height);
//            Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth() / 2, bmp.getHeight() / 2, Bitmap.Config.ARGB_8888);
//            Log.e("XXX",bitmap.getWidth()+"~"+bitmap.getHeight());
//            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false,selectnum);
            YoloV5Ncnn.Obj[] objects = null;
//            long startTime = System.currentTimeMillis();
            if (openDescern) {
                objects = yolov5ncnn.Detect(bmp, false);
//                long endTime = System.currentTimeMillis();
//                Log.e("XXX",startTime-endTime+"");
                showObjects(objects);
            } else {
                showObjects(objects);
            }
            //??????HttpURLConnection??????
            conn.disconnect();
        } catch (Exception ex) {
            Message message = new Message();
            message.what = Constant.TAG_THERE;
            message.obj = ex.toString();
            handlerLoop.sendMessage(message);
        } finally {
        }
    }

    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null || objects.length == 0) {
//            Message message = new Message();
//            message.what = Constant.TAG_ONE;
//            message.obj = bmp;
//            handlerLoop.sendMessage(message);
            imageView.setImageBitmap(bmp);
            return;
        }

        Bitmap rgba = bmp.copy(Bitmap.Config.ARGB_8888, true);
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
        mediaPlayer.start();
        if (isFirst) {
            saveImageToGallery(DescernActivity.this, rgba);
            saveTime = System.currentTimeMillis();
            isFirst = false;
        } else {
            currentTmeTime = System.currentTimeMillis();
            if (currentTmeTime - saveTime > 3000) {
                saveImageToGallery(DescernActivity.this, rgba);
                saveTime = currentTmeTime;
                isFirst = true;
            }
        }
//            Message message = new Message();
//            message.what = Constant.TAG_FOUR;
//            message.obj = rgba;
//            handlerLoop.sendMessage(message);

    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        boolean backstate = new ImageSave().saveBitmap("/LUKEDescImage/", project, workName, workCode, context, bmp);
        if (backstate) {
            Log.e("XXX", "????????????");
        } else {
            Log.e("XXX", "????????????");
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

    @OnClick({R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum, R.id.rbBack, R.id.linearLayoutStop, R.id.rbSetting, R.id.rbDescern, R.id.rbNoDescern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbDescern:
                rbDescern.setVisibility(View.GONE);
                rbNoDescern.setVisibility(View.VISIBLE);
                openDescern = !openDescern;
                break;
            case R.id.rbNoDescern:
                rbDescern.setVisibility(View.VISIBLE);
                rbNoDescern.setVisibility(View.GONE);
                openDescern = !openDescern;
                break;
            case R.id.rbCamera:
                if (toast != null) {
                    toast.cancel();
                }
                radioGroup.setVisibility(View.GONE);
                View view1 = frameLayout;
                view1.setDrawingCacheEnabled(true);
                view1.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
                if (bitmap != null) {
                    boolean backstate = new ImageSave().saveBitmap("/LUKEDescImage/", project, workName, workCode, this, bitmap);
                    if (backstate) {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "????????????");
                    } else {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "????????????");
                    }
                }
//                String path = Environment.getExternalStorageDirectory().getPath();
//                if (Build.VERSION.SDK_INT > 29) {
//                    path = this.getExternalFilesDir(null).getAbsolutePath() ;
//                }
//                try {
//                    // ??????-???view????????????????????????
//                    View v = this.getWindow().getDecorView();
//                    Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Bitmap.Config.RGB_565);
//                    Canvas c = new Canvas(bitmap);
//                    c.translate(-v.getScrollX(), -v.getScrollY());
//                    v.draw(c);
//                    // ??????Bitmap,?????????png???????????????
//                    Uri insertUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
//                    OutputStream outputStream = getContentResolver().openOutputStream(insertUri, "123");
//                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
//                        Log.e("XXX", "success");
//                    } else {
//                        Log.e("XXX", "fail");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("XXX",e.toString());
//                }
                break;
            case R.id.rbVideo:
                if (toast != null) {
                    toast.cancel();
                }
                radioGroup.setVisibility(View.GONE);
                linearLayoutStop.setVisibility(View.VISIBLE);
                if (mMediaProjection == null) {
                    requestMediaProjection();
                } else {
                    if (EasyPermissions.hasPermissions(this, PERMS)) {
                        new TirenSet().checkTirem(ivTimer);
                        startMedia();
                    } else {
                        // ???????????????????????????????????????
                        EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
                    }
                }
                break;
            case R.id.linearLayoutStop:
                ChronometerEnd();
                radioGroup.setVisibility(View.VISIBLE);
                linearLayoutStop.setVisibility(View.GONE);
                if (mediaRecorder != null) {
                    stopMedia();
                }
                break;
            case R.id.rbAlbum:
                new MainUI().showPopupMenu(rbAlbum, "Desc", this);
                break;
            case R.id.rbSetting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("tag", "desc");
                startActivity(intent);
                break;
            case R.id.rbBack:
                runing = false;
                if (saveThread != null) {
                    saveThread.interrupt();
                    saveThread = null;
                }
                if (mythread != null) {
                    mythread.interrupt();
                    mythread = null;
                }
                startActivity(new Intent(this, SendSelectActivity.class));
                finish();
                break;
        }
    }

    //????????????
    private void ChronometerStart() {
        chronometer.start();//????????????
        chronometer.setBase(SystemClock.elapsedRealtime());
        CharSequence time = chronometer.getText();
        chronometer.setText(time.toString());
    }

    //????????????
    private void ChronometerEnd() {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void startMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //??????mediaRecorder
            mediaRecorder = new MyMediaRecorder().getMediaRecorder(this, project, workName, workCode, "/LUKEDescVideo/");
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("??????name",
                    2400, 1080, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(),
                    null, null);
        }
        //????????????
        try {
            mediaRecorder.start();
            ChronometerStart();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void stopMedia() {
        mediaRecorder.stop();
    }

    //????????????????????? Intent
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
                    if (resultCode == Activity.RESULT_OK) {
                        new BottomUI().hideBottomUIMenu(this.getWindow());
                        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, backdata);
                        new TirenSet().checkTirem(ivTimer);
                        startMedia();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case Constant.TAG_TWO:
                if (resultCode == Activity.RESULT_OK) {
                    String position = backdata.getStringExtra("position");
                }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ?????????????????? EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // ?????????????????????
        requestMediaProjection();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // ??????????????????
        Toast.makeText(DescernActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new BottomUI().hideBottomUIMenu(this.getWindow());
        Log.e("XXXXX", "restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String audio = new SharePreferencesUtils().getString(this, "audio", "");
        mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.nan);
        if (audio.equals("fengming")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.fengming);
        }
        if (audio.equals("nv")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.nv);
        }
        if (audio.equals("nan")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.nan);
        }
        if (audio.equals("ami")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.ami);
        }
        if (audio.equals("dzy1")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.dzy1);
        }
        if (audio.equals("dzy2")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.dzy2);
        }
        if (audio.equals("jsq1")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.jsq1);
        }
        if (audio.equals("jsq2")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.jsq2);
        }
        if (audio.equals("db")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.db);
        }
        if (audio.equals("dh")) {
            mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.dh);
        }
        Log.e("XXXXX", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("info", "stop");
    }


    Handler handlerLoop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.TAG_ONE:
                    Bitmap bit = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bit);
                    break;
                case Constant.TAG_THERE:
                    String toastString = msg.obj.toString();
                    if (toastString.contains("java.net.ConnectException: Failed to connect")
                            || toastString.contains("java.io.IOException: unexpected end")
                            || toastString.contains("java.io.InterruptedIOException: thread interrupted")
                            || toastString.contains("java.lang.NullPointerException: Attempt to get length of null array")) {
                        break;
                    } else {
                        Toast.makeText(DescernActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("XXX", toastString);
                    break;
                case Constant.TAG_FOUR:
                    Bitmap bitH = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitH);
                    saveThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap rgba1 = bitH.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas1 = new Canvas(rgba1);
                            canvas1.drawText("????????????:" + project, 15, 30, new MyPaint().getTextpaint());
                            canvas1.drawText("????????????:" + workName, 15, 70, new MyPaint().getTextpaint());
                            canvas1.drawText("????????????:" + workCode, 15, 110, new MyPaint().getTextpaint());
                            canvas1.drawText("????????????:" + deviceCode, 15, 150, new MyPaint().getTextpaint());
                            mediaPlayer.start();
                            if (isFirst) {
                                saveImageToGallery(DescernActivity.this, rgba1);
                                saveTime = System.currentTimeMillis();
                                isFirst = false;
                            } else {
                                currentTmeTime = System.currentTimeMillis();
                                if (currentTmeTime - saveTime > 3000) {
                                    saveImageToGallery(DescernActivity.this, rgba1);
                                    saveTime = currentTmeTime;
                                    isFirst = true;
                                }
                            }
                        }
                    });
                    saveThread.start();
                    break;
            }
        }
    };

}
