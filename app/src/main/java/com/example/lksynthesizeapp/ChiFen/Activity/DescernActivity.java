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
import com.example.lksynthesizeapp.ChiFen.test.ScreenCaptureService;
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
    private String url;
    private Bitmap croppedBitmap = null;
    private Bitmap rgba;
    private Thread mythread, saveThread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    HttpURLConnection conn;
    private MediaPlayer mediaPlayer;
    public boolean runing = true;
    public static String project = "", workName = "", workCode = "", deviceCode = "";
    public boolean isFirst = true;
    public long saveTime = 0;
    public long currentTmeTime = 0;
    private Toast toast;
    private MediaProjectionManager mMediaProjectionManager;
    public static DescernActivity intance = null;
    NettyTcpClient mNettyTcpClient;
    BaseTcpClient baseTcpClient;
    BytesHexChange bytesHexChange = BytesHexChange.getInstance();
    boolean descernTag = false;
    String devicesModel;

    private long startTime = 0;
    private long endTime = 0;
    private boolean isRecording = false;
    private boolean videoState = true;

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
        project = intent.getStringExtra("project")+"";
        workName = intent.getStringExtra("etWorkName")+"";
        workCode = intent.getStringExtra("etWorkCode")+"";
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
            if(devicesModel.equals("LKDAC-CMT2SX")){
                startTime = System.currentTimeMillis();
                objects = yolov5ncnn.Detect(croppedBitmap, false);
                showObjects(objects);
            }else {
                if (descernTag) {
                    startTime = System.currentTimeMillis();
                    objects = yolov5ncnn.Detect(croppedBitmap, false);
                    showObjects(objects);
                } else {
                    showObjects(objects);
                }
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
            if (mNettyTcpClient!=null&&mNettyTcpClient.getConnectStatus()){
                makeData("300A");
            }
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
        endTime = System.currentTimeMillis();
        Log.e("TAG11111111111",(endTime-startTime)+"");
        startTime = endTime;
        imageView.setImageBitmap(rgba);
        mediaPlayer.start();
        if (mNettyTcpClient!=null&&mNettyTcpClient.getConnectStatus()){
            makeData("310A");
        }
        if (isFirst) {
            isFirst = false;
            saveTime = System.currentTimeMillis();
            saveImage(objects);
        } else {
            currentTmeTime = System.currentTimeMillis();
            if (currentTmeTime - saveTime > 3000) {
                isFirst = true;
                saveImage(objects);
            }
        }
    }

    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        boolean backstate = new ImageSave().saveBitmap("/LUKEDescImage/", context, bmp);
        return backstate;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mythread != null) {
            mythread.interrupt();
        }
        if (mNettyTcpClient!=null){
            mNettyTcpClient.disconnect();
            mNettyTcpClient = null;
            baseTcpClient = null;
        }
        runing = false;
    }

    @OnClick({R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum, R.id.rbBack, R.id.linearLayoutStop, R.id.rbSetting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCamera:
                if (toast != null) {
                    toast.cancel();
                }
                YoloV5Ncnn.Obj[] objects = null;
                saveImage(objects);
//                if (croppedBitmap!=null){
//                    Bitmap rgba = croppedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                    Canvas canvas = new Canvas(rgba);
//                    canvas.drawText("工程名称 ："+project, 10, 30 , new MyPaint().getHeadTextpaint());
//                    canvas.drawText("工件名称 ："+workName, 10, 70 , new MyPaint().getHeadTextpaint());
//                    canvas.drawText("工件编号 ："+workCode, 10, 110 , new MyPaint().getHeadTextpaint());
//                    boolean backstate = saveImageToGallery(DescernActivity.this, rgba);
//                    if (backstate) {
//                        toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
//                    } else {
//                        toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
//                    }
//                    toast.show();
//                }
                break;
            case R.id.rbVideo:
                if (toast != null) {
                    toast.cancel();
                }
                radioGroup.setVisibility(View.GONE);
                linearLayoutStop.setVisibility(View.VISIBLE);
                requestMediaProjection();
                break;
            case R.id.linearLayoutStop:
                radioGroup.setVisibility(View.VISIBLE);
                linearLayoutStop.setVisibility(View.GONE);
                videoState = true;
                stopMedia();
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

    private void saveImage(YoloV5Ncnn.Obj[] objects) {
        if (objects == null || objects.length == 0) {
            boolean backstate = saveImageToGallery(DescernActivity.this, croppedBitmap);
            if (backstate) {
                toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
            }
            toast.show();
        }else {
            if (croppedBitmap!=null){
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
                canvas.drawText("工程名称 ："+project, 10, 30 , new MyPaint().getHeadTextpaint());
                canvas.drawText("工件名称 ："+workName, 10, 70 , new MyPaint().getHeadTextpaint());
                canvas.drawText("工件编号 ："+workCode, 10, 110 , new MyPaint().getHeadTextpaint());
                boolean backstate = saveImageToGallery(DescernActivity.this, rgba);
                if (backstate) {
                    toast = Toast.makeText(DescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(DescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        }
    }

    private void stopMedia() {
        if (isRecording) {
            Intent serviceIntent = new Intent(this, ScreenCaptureService.class);
            serviceIntent.setAction(ScreenCaptureService.ACTION_STOP);
            startService(serviceIntent); // Send stop command to service
            isRecording = false;
            Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有正在进行的录制", Toast.LENGTH_SHORT).show();
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
                    if (resultCode == Activity.RESULT_OK) {
                        new BottomUI().hideBottomUIMenu(this.getWindow());
                        ScreenCaptureService.sMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, backdata);
                        if (ScreenCaptureService.sMediaProjection != null) {
                            startRecordingService(5 * 60 * 1000); // Record segments of 5 minutes
                            isRecording = true;
                            Toast.makeText(this, "屏幕录制已开始 (分段存储)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "获取MediaProjection失败", Toast.LENGTH_SHORT).show();
                        }
//                        new TirenSet().checkTirem(ivTimer);
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
                Log.e("TAG","发送失败");
            }
        });
    }

    @Override
    public void onMessageResponseClient(String msg, int index) {
        //如果型号为D3/E3 返回数据如果为空，则默认开启识别
//        Log.e("TAG","接收数据"+msg);
        if(devicesModel.equals("LKMT-D3S")||devicesModel.equals("LKMT-E3S")){
            if(msg==null||msg.equals("")){
                descernTag = true;
            }else {
                if (msg.length()==26){
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
                    if (mNettyTcpClient!=null){
                        mNettyTcpClient.disconnect();
                        mNettyTcpClient = null;
                        baseTcpClient = null;
                    }
//                    Toast.makeText(DescernActivity.this, R.string.connect_colse, Toast.LENGTH_SHORT).show();
                    descernTag = false;
                    break;
                case TAG_THERE:
//                    Toast.makeText(DescernActivity.this, R.string.connect_faile, Toast.LENGTH_SHORT).show();
                    if (mythread != null) {
                        mythread.interrupt();
                    }
                    runing = false;
                    baseTcpClient = null;
                    if (mNettyTcpClient!=null){
                        mNettyTcpClient.disconnect();
                        mNettyTcpClient = null;
                        baseTcpClient = null;
                    }
                    finish();
                    break;
            }
        }

    };
}
