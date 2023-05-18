package com.example.lksynthesizeapp.ChiFen.Activity;

import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_ONE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_THERE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_TWO;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_CLOSED;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_ERROR;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_SUCCESS;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.example.lksynthesizeapp.ChiFen.Modbus.BytesHexChange;
import com.example.lksynthesizeapp.ChiFen.Netty.BaseTcpClient;
import com.example.lksynthesizeapp.ChiFen.Netty.NettyTcpClient;
import com.example.lksynthesizeapp.ChiFen.Netty.SendCallBack;
import com.example.lksynthesizeapp.ChiFen.service.WhiteService;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.example.lksynthesizeapp.YoloV5Ncnn;
import com.littlegreens.netty.client.listener.NettyClientListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class DescernActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, NettyClientListener<String> {
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
    @BindView(R.id.frameLayout1)
    FrameLayout frameLayout1;
    @BindView(R.id.rbSetting)
    RadioButton rbSetting;
    @BindView(R.id.linlayoutData)
    LinearLayout linlayoutData;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
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
    //创建一个虚屏VirtualDisplay，内含一个真实的Display对象
    private VirtualDisplay mVirtualDisplay;
    //获取电源锁
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    private MediaRecorder mediaRecorder;
    public static DescernActivity intance = null;
    NettyTcpClient mNettyTcpClient;
    BaseTcpClient baseTcpClient;
    BytesHexChange bytesHexChange = BytesHexChange.getInstance();
    boolean descernTag = false;
    String devicesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        setContentView(R.layout.activity_descern);
        ButterKnife.bind(this);
        intance = this;
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        boolean ret_init = yolov5ncnn.Init(getAssets());
        devicesModel = new SharePreferencesUtils().getString(this,"deviceModel","");
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
            //开启前台服务
            intent = new Intent(DescernActivity.this, WhiteService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上的开启方式不同
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
//        //获取磁化状态的连接
        baseTcpClient = BaseTcpClient.getInstance();
        settingNetty();
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
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            bmp = BitmapFactory.decodeStream(inputstream);
            YoloV5Ncnn.Obj[] objects = null;
//            showObjects(objects);
            if (descernTag) {
                objects = yolov5ncnn.Detect(bmp, false);
                showObjects(objects);
            } else {
                showObjects(objects);
            }
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
           Log.e("XXX",ex.toString());
        } finally {
        }
    }

    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null || objects.length == 0) {
            //发送报警信息
            makeData("300A");
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
        makeData("310A");
        if (isFirst) {
//            radioGroup.setVisibility(View.GONE);
//            saveImageToGallery(DescernActivity.this, screenImage());
            saveImageToGallery(DescernActivity.this, rgba);
//            radioGroup.setVisibility(View.VISIBLE);
            saveTime = System.currentTimeMillis();
            isFirst = false;
        } else {
            currentTmeTime = System.currentTimeMillis();
            if (currentTmeTime - saveTime > 3000) {
//                radioGroup.setVisibility(View.GONE);
//                saveImageToGallery(DescernActivity.this, screenImage());
                saveImageToGallery(DescernActivity.this, rgba);
//                radioGroup.setVisibility(View.VISIBLE);
                saveTime = currentTmeTime;
                isFirst = true;
            }
        }
    }

    //截图
    private Bitmap screenImage(){
        View view1 = frameLayout1;
        view1.setDrawingCacheEnabled(true);
        view1.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
        return bitmap;
    }


    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        boolean backstate = new ImageSave().saveBitmap("/LUKEDescImage/", project, workName, workCode, context, bmp);
        return backstate;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mythread != null) {
            mythread.interrupt();
        }
        runing = false;
        mNettyTcpClient.disconnect();
    }

    @OnClick({R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum, R.id.rbBack, R.id.linearLayoutStop, R.id.rbSetting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCamera:
                if (toast != null) {
                    toast.cancel();
                }
//                Bitmap bitmap = screenImage();
//                if (bitmap != null) {
//                    boolean backstate = saveImageToGallery(DescernActivity.this, bmp);
//                    if (backstate) {
//                        toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
//                        toast.show();
//                    } else {
//                        toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
//                        toast.show();
//                    }
//                }
                if (bmp!=null){
                    boolean backstate = saveImageToGallery(DescernActivity.this, bmp);
                    if (backstate) {
                        toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                    } else {
                        toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
               
//                String path = Environment.getExternalStorageDirectory().getPath();
//                if (Build.VERSION.SDK_INT > 29) {
//                    path = this.getExternalFilesDir(null).getAbsolutePath() ;
//                }
//                try {
//                    // 截屏-将view作为原图绘制出来
//                    View v = this.getWindow().getDecorView();
//                    Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Bitmap.Config.RGB_565);
//                    Canvas c = new Canvas(bitmap);
//                    c.translate(-v.getScrollX(), -v.getScrollY());
//                    v.draw(c);
//                    // 压缩Bitmap,不支持png图片的压缩
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
                        // 没有申请过权限，现在去申请
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
//                startActivity(new Intent(this, SendSelectActivity.class));
                finish();
                break;
        }
    }

    //开始计时
    private void ChronometerStart() {
        chronometer.start();//开始计时
        chronometer.setBase(SystemClock.elapsedRealtime());
        CharSequence time = chronometer.getText();
        chronometer.setText(time.toString());
    }

    //结束计时
    private void ChronometerEnd() {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    private void startMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //获取mediaRecorder
            mediaRecorder = new MyMediaRecorder().getMediaRecorder(this, project, workName, workCode, "/LUKEDescVideo/");
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("你的name",
                    2400, 1080, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(),
                    null, null);
        }
        //开始录制
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

    private void settingNetty() {
        if(devicesModel.equals("LKMT-D3S")||devicesModel.equals("LKMT-E3S")){
            mNettyTcpClient = baseTcpClient.initTcpClient(Constant.URL, 502);
        }else {
            mNettyTcpClient = baseTcpClient.initTcpClient(Constant.URL, 4000);
        }
        mNettyTcpClient.setListener(this); //设置TCP监听
        baseTcpClient.tcpClientConntion(mNettyTcpClient);
    }

    //组装数据
    private void makeData(String tag) {
        byte[] s = bytesHexChange.HexStringToByteArr(tag);
        sendData(s);
    }

    //发送数据
    private void sendData(byte[] data) {
        baseTcpClient.sendTcpData(data, new SendCallBack() {
            @Override
            public void success(String success) {
            }

            @Override
            public void faild(String message) {
            }
        });
    }

    @Override
    public void onMessageResponseClient(String msg, int index) {
        //如果型号为D3/E3 返回数据如果为空，则默认开启识别
        if(devicesModel.equals("LKMT-D3S")||devicesModel.equals("LKMT-E3S")){
            if(msg==null||msg.equals("")){
                descernTag = true;
            }else {
                String first = msg.substring(2, msg.length() - 2);
                String end = msg.substring(msg.length() - 2);
                String checkData = bytesHexChange.hexStringToBytes(first);
                if (checkData.length() >= 2) {
                    String checkDataLow = checkData.substring(checkData.length() - 2).toUpperCase();
                    if (checkDataLow.equals(end)) {
                        String[] backHeartData = bytesHexChange.HexToByteArr(msg);
                        //循环返回数据
                        if (backHeartData[0].equals("D3")|| backHeartData[0].equals("E3")) {
                            if (backHeartData[1].equals("1B")){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        String controlData = bytesHexChange.HexToBinary(backHeartData[2]);
                                        if (controlData.length() == 8) {
                                            String mode = controlData.substring(7, 8);
                                            if (mode.equals("1")){
                                                descernTag = true;
                                            }else if (mode.equals("0")){
                                                descernTag = false;
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }else {
                        if (devicesModel.equals("LKMT-D3S")){
                            makeData("D3220000000000000000000022");
                        }else if (devicesModel.equals("LKMT-E3S")){
                            makeData("E3220000000000000000000022");
                        }
                    }
                }
            }
        }else {
            if (msg.equals("300A")){
                descernTag = true;
//                Log.e("TAG",msg);
            }else if (msg.equals("310A")){
                descernTag = false;
//                Log.e("TAG",msg);
            }
        }
    }

    @Override
    public void onClientStatusConnectChanged(int statusCode, int index) {
        //连接状态回调
        if (statusCode == STATUS_CONNECT_SUCCESS) {
            handler.sendEmptyMessage(TAG_ONE);
            if (devicesModel.equals("LKMT-D3S")){
                makeData("D3220000000000000000000022");
            }else if (devicesModel.equals("LKMT-E3S")){
                makeData("E3220000000000000000000022");
            }
        } else if (statusCode == STATUS_CONNECT_CLOSED) {
            if(devicesModel.equals("LKMT-D3S")||devicesModel.equals("LKMT-E3S")){
                handler.sendEmptyMessage(TAG_TWO);
            }else if(devicesModel.equals("LKMT-D2S")||devicesModel.equals("LKMT-E2S")||devicesModel.equals("LKEAC-CMT6SX")){
                descernTag = true;
            }
        } else if (statusCode == STATUS_CONNECT_ERROR) {
            if(devicesModel.equals("LKMT-D3S")||devicesModel.equals("LKMT-E3S")){
                //设备断电后结束当前界面 先erroe 后close
                handler.sendEmptyMessage(TAG_THERE);
            }else if(devicesModel.equals("LKMT-D2S")||devicesModel.equals("LKMT-E2S")){
                descernTag = true;
            }
        }
    }

    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAG_ONE:
                    Toast.makeText(DescernActivity.this, R.string.connect_success, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_TWO:
                    Toast.makeText(DescernActivity.this, R.string.connect_colse, Toast.LENGTH_SHORT).show();
                    descernTag = true;
                    break;
                case TAG_THERE:
                    Toast.makeText(DescernActivity.this, R.string.connect_faile, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }

    };
}
