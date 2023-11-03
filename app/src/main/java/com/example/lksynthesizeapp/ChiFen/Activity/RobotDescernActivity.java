package com.example.lksynthesizeapp.ChiFen.Activity;

import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.itemName;
import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.mItemImgs;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FOUR;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_ONE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_THERE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_TWO;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_CLOSED;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_ERROR;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_SUCCESS;

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
import android.widget.Button;
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
import com.example.lksynthesizeapp.ChiFen.Modbus.BytesHexChange;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.ChiFen.Netty.BaseTcpClient;
import com.example.lksynthesizeapp.ChiFen.Netty.NettyTcpClient;
import com.example.lksynthesizeapp.ChiFen.Netty.SendCallBack;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenu;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenuAdapter;
import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.activity.SendSelectActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.example.lksynthesizeapp.YoloV5Ncnn;
import com.littlegreens.netty.client.listener.NettyClientListener;
import com.zgkxzx.modbus4And.requset.ModbusReq;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotDescernActivity extends AppCompatActivity implements NettyClientListener<String> {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvCEControl)
    TextView tvCEControl;
    @BindView(R.id.tvLightSelect)
    TextView tvLightSelect;
    @BindView(R.id.tvLight)
    TextView tvLight;
    @BindView(R.id.tvCHControl)
    TextView tvCHControl;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.cm_main)
    CircleMenu cmMain;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.rbCamera)
    RadioButton rbCamera;
    @BindView(R.id.rbVideo)
    RadioButton rbVideo;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.rbAlbum)
    RadioButton rbAlbum;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    @BindView(R.id.linearLayoutStop)
    LinearLayout linearLayoutStop;
    @BindView(R.id.rbSetting)
    RadioButton rbSetting;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.tvSpary)
    TextView tvSpary;
    @BindView(R.id.rbBack)
    RadioButton rbBack;
    @BindView(R.id.tvVoltage)
    TextView tvVoltage;
    @BindView(R.id.linearLayoutWrite)
    LinearLayout linearLayoutWrite;
    @BindView(R.id.rbOther)
    RadioButton rbOther;
    @BindView(R.id.tvProtectVoltage)
    TextView tvProtectVoltage;
    @BindView(R.id.tvRunningDistance)
    TextView tvRunningDistance;
    @BindView(R.id.tvMagnetizeDistance)
    TextView tvMagnetizeDistance;
    @BindView(R.id.tvMagnetizeTime)
    TextView tvMagnetizeTime;
    @BindView(R.id.tvMagnetizeVoltage)
    TextView tvMagnetizeVoltage;
    @BindView(R.id.tvMagnetizeRate)
    TextView tvMagnetizeRate;
    @BindView(R.id.tvBatteryVoltage)
    TextView tvBatteryVoltage;
    @BindView(R.id.tvBatterycurrent)
    TextView tvBatterycurrent;
    @BindView(R.id.tvProtectCurrent)
    TextView tvProtectCurrent;
    @BindView(R.id.tvRunningSpeed)
    TextView tvRunningSpeed;
    @BindView(R.id.tvControl)
    TextView tvControl;
    private String url;
    private Bitmap bmp = null;
    private Thread mythread, saveThread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    URL videoUrl;
    HttpURLConnection conn;
    private Toast toast;
    private MediaPlayer mediaPlayer;
    long currentTme = 0, currentTme1 = 0;
    public boolean runing = true;
    public boolean isFirst = true;
    public static final int TIME = 1000;
    Message message;
    List<ItemInfo> data = new ArrayList<>();
    private CircleMenuAdapter circleMenuAdapternew;
    private boolean isConnect = false;
    private Handler handlerData = new Handler();
    ModbusReq modbusReq = ModbusReq.getInstance();
    public static String project = "", workName = "", workCode = "";
    private VirtualDisplay mVirtualDisplay;
    //获取电源锁
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaRecorder mediaRecorder;
    public long saveTime = 0;
    public long currentTmeTime = 0;
    NettyTcpClient mNettyTcpClient;
    BaseTcpClient baseTcpClient;
    //方向指令
    private String direction = "00";
    //磁化指令
    private String magnetize = "0";
    //喷涂指令
    private String spray = "0";
    //照明状态
    private String lighting = "0";
    //磁轭状态
    private String chie = "1";
    //磁轭黑白光
    private String chieLight = "00";
    //10倍的保护电压设定值
    private String protectVoltage = "00";
    //10倍的保护电流设定值
    private String protectCurrent = "00";
    //10倍的运行速度
    private String protectSpeed = "00";
    //运行距离
    private String runningDistance = "00000000";
    //磁化间隔距离
    private String magnetizeDistance = "0000";
    //单次磁化时间
    private String magnetizeTime = "0000";
    //10倍磁化电压
    private String magnetizeVoltage = "0000";
    //磁化频率
    private String magnetizeRate = "14";
    private String spinnerData = "";
    BytesHexChange bytesHexChange = BytesHexChange.getInstance();
    byte[] hexArray = new byte[19];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_robot_descern);
        ButterKnife.bind(this);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Log.e("MainActivity", "yolov5ncnn Init failed");
        }
//        url = "http://" + Constant.URL + ":8080?action=snapshot";
        url = "http://192.168.43.251" + ":8080?action=snapshot";
        mythread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runing) {
                    draw();
//                    currentTme = System.currentTimeMillis();
                }
            }
        });
        mythread.start();
        new BottomUI().hideBottomUIMenu(this.getWindow());
        baseTcpClient = BaseTcpClient.getInstance();
        setUiData();
        settingNetty();
    }

    //--------------tcp----------------
    private void settingNetty() {
        mNettyTcpClient = baseTcpClient.initTcpClient("192.168.144.101", 14551);
//        mNettyTcpClient = baseTcpClient.initTcpClient("172.16.20.5", 5000);
        mNettyTcpClient.setListener(this); //设置TCP监听
        baseTcpClient.tcpClientConntion(mNettyTcpClient);
    }

    //数据回调监听
    @Override
    public void onMessageResponseClient(String msg, int index) {
        //6为帧头、命令码、检验的长度和
        Log.e("XXX", msg);
        if (msg.length() > 6) {
            String first = msg.substring(2, msg.length() - 2);
            String end = msg.substring(msg.length() - 2);
            String checkData = bytesHexChange.hexStringToBytes(first);
            if (checkData.length() >= 2) {
                String checkDataLow = checkData.substring(checkData.length() - 2, checkData.length()).toUpperCase();
                if (checkDataLow.equals(end)) {
                    String[] backHeartData = bytesHexChange.HexToByteArr(msg);
                    //循环返回数据
                    if (backHeartData[0].equals("B1") && backHeartData[1].equals("1B")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setHeaderData(backHeartData);
                            }
                        });
                    }
                    //单次返回数据
                    if (backHeartData[0].equals("B1") && backHeartData[1].equals("2B")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setOnlyBackData(backHeartData);
                            }
                        });
                    }
                }
            } else {
                handler.sendEmptyMessage(TAG_THERE);
            }
        }
    }

    private void setHeaderData(String[] backHeartData) {
        String controlData = bytesHexChange.HexToBinary(backHeartData[2]);
        if (controlData.length() == 8) {
            String mode = controlData.substring(6, 8);
            if (mode.equals("00")) {
                tvControl.setText("安全模式");
            } else if (mode.equals("01")) {
                tvControl.setText("手动模式");
            } else if (mode.equals("02")) {
                tvControl.setText("自动模式");
            } else if (mode.equals("03")) {
                tvControl.setText("工程模式");
            }
            if (controlData.substring(5, 6).equals("0")) {
                tvLight.setText("关闭");
                lighting = "0";
            } else if (controlData.substring(5, 6).equals("1")) {
                tvLight.setText("开启");
                lighting = "1";
            }
            if (controlData.substring(4, 5).equals("0")) {
                tvSpary.setText("关闭");
                spray = "0";
            } else if (controlData.substring(4, 5).equals("1")) {
                tvSpary.setText("开启");
                spray = "1";
            }
            if (controlData.substring(3, 4).equals("0")) {
                tvCEControl.setText("落下");
                chie = "0";
            } else if (controlData.substring(3, 4).equals("1")) {
                tvCEControl.setText("抬起");
                chie = "1";
            }
            if (controlData.substring(2, 3).equals("0")) {
                tvCHControl.setText("关闭");
                magnetize = "0";
            } else if (controlData.substring(2, 3).equals("1")) {
                tvCHControl.setText("开启");
                magnetize = "1";
            }
            String selectLight = controlData.substring(0, 2);
            if (selectLight.equals("01")) {
                tvLightSelect.setText("黑光");
                chieLight = "01";
            } else if (selectLight.equals("10")) {
                tvLightSelect.setText("白光");
                chieLight = "10";
            }
        }

        tvBatteryVoltage.setText(bytesHexChange.dataChange(backHeartData[3], true) + "V");
        tvBatterycurrent.setText(bytesHexChange.dataChange(backHeartData[4], true) + "A");
        tvDistance.setText(bytesHexChange.dataChange(backHeartData[11] + backHeartData[10] + backHeartData[9] + backHeartData[8], false) + "mm");
    }

    private void setOnlyBackData(String[] backHeartData) {
        protectVoltage = backHeartData[2];
        tvProtectVoltage.setText(bytesHexChange.dataChange(backHeartData[2], true) + "V");

        protectCurrent = backHeartData[3];
        tvProtectCurrent.setText(bytesHexChange.dataChange(backHeartData[3], true) + "A");

        protectSpeed = backHeartData[4];
        tvRunningSpeed.setText(bytesHexChange.dataChange(backHeartData[4], runing) + "m/min");

        runningDistance = backHeartData[8] + backHeartData[7] + backHeartData[6] + backHeartData[5];
        tvRunningDistance.setText(bytesHexChange.dataChange(backHeartData[8] + backHeartData[7] + backHeartData[6] + backHeartData[5], false) + "mm");

        magnetizeDistance = backHeartData[10] + backHeartData[9];
        tvMagnetizeDistance.setText(bytesHexChange.dataChange(backHeartData[10] + backHeartData[9], false) + "mm");

        magnetizeTime = backHeartData[12] + backHeartData[11];
        tvMagnetizeTime.setText(bytesHexChange.dataChange(backHeartData[12] + backHeartData[11], false) + "s");

        magnetizeVoltage = backHeartData[14] + backHeartData[13];
        tvMagnetizeVoltage.setText(bytesHexChange.dataChange(backHeartData[14] + backHeartData[13], true) + "V");

        magnetizeRate = backHeartData[15];
        tvMagnetizeRate.setText(bytesHexChange.dataChange(backHeartData[15], false) + "HZ");
    }

    //状态监听
    @Override
    public void onClientStatusConnectChanged(int statusCode, int index) {
        //连接状态回调
        if (statusCode == STATUS_CONNECT_SUCCESS) {
            Log.e("XXX", "成功");
            isConnect = true;
            handler.sendEmptyMessage(TAG_ONE);
        } else if (statusCode == STATUS_CONNECT_CLOSED) {
            Log.e("XXX", "断开");
            isConnect = false;
            handler.sendEmptyMessage(TAG_TWO);
        } else if (statusCode == STATUS_CONNECT_ERROR) {
            Log.e("XXX", "失败");
            isConnect = false;
            handler.sendEmptyMessage(TAG_TWO);
        }
    }

    //组装数据
    private void makeData() {
        String sendData = "";
        String bitData = "00"+spray + magnetize + chieLight + chie + lighting;
        int ten = Integer.parseInt(bitData, 2);
        String strHex2 = String.format("%02x", ten).toUpperCase();//高位补0
        sendData = Constant.HEART_DATA + direction + strHex2 + protectVoltage + protectCurrent +
                protectSpeed + runningDistance + magnetizeDistance + magnetizeTime + magnetizeVoltage + magnetizeRate;
        sendData = Constant.HEART_FRAME + sendData+bytesHexChange.hexStringToBytes(sendData);
        Log.e("XXXX",sendData);
        byte[] s = bytesHexChange.HexStringToByteArr(sendData);
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

    //--------------tcp----------------

    /**
     * 方向键
     */
    private void setUiData() {
        ItemInfo item = null;
        for (int i = 0; i < itemName.length; i++) {
            item = new ItemInfo(mItemImgs[i], itemName[i]);
            data.add(item);
        }
        circleMenuAdapternew = new CircleMenuAdapter(this, data);
        cmMain.setAdapter(circleMenuAdapternew);
        setClient();
    }

    private void setClient() {
        cmMain.setOnItemClickListener(new CircleMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (isConnect) {
                    if (itemName[position].equals("上")) {
                        direction = "01";
                        makeData();
                    }
                    if (itemName[position].equals("左")) {
                        direction = "03";
                        makeData();
                    }
                    if (itemName[position].equals("右")) {
                        direction = "04";
                        makeData();
                    }
                    if (itemName[position].equals("下")) {
                        direction = "02";
                        makeData();
                    }
                } else {
                    message = new Message();
                    message.what = TAG_TWO;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @OnClick({R.id.btnStop, R.id.tvCEControl, R.id.tvLightSelect, R.id.tvLight, R.id.tvCHControl,
            R.id.tvProtectVoltage, R.id.tvRunningDistance, R.id.tvMagnetizeDistance, R.id.tvMagnetizeTime,
            R.id.tvMagnetizeVoltage, R.id.tvMagnetizeRate, R.id.tvProtectCurrent, R.id.tvRunningSpeed,
            R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum, R.id.linearLayoutStop, R.id.rbSetting, R.id.tvSpary, R.id.rbBack, R.id.rbOther})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbOther:
                if (linearLayoutWrite.getVisibility() == View.VISIBLE) {
                    linearLayoutWrite.setVisibility(View.GONE);
                } else {
                    linearLayoutWrite.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnStop:
                direction = "00";
                makeData();
                break;
            case R.id.tvCEControl:
                spinnerData(tvCEControl, "CEControl");
                //磁轭控制
                break;
            case R.id.tvLightSelect:
                spinnerData(tvLightSelect, "BlackOrWhiteLight");
                //黑白选择
                break;
            case R.id.tvLight:
                //探  照  灯
                spinnerData(tvLight, "Light");
                break;
            case R.id.tvCHControl:
                //磁化控制
                spinnerData(tvCHControl, "CHControl");
                break;
            case R.id.tvSpary:
                //电池阀
                spinnerData(tvSpary, "Battery");
                break;
            case R.id.tvMagnetizeRate:
                //磁化频率
                spinnerData(tvMagnetizeRate, "MagnetizeRate");
                break;
            case R.id.tvProtectVoltage:
                showFDialog("protectVoltage", 1);
                break;
            case R.id.tvProtectCurrent:
                showFDialog("protectCurrent", 1);
                break;
            case R.id.tvRunningSpeed:
                showFDialog("runningSpeed", 1);
                break;
            case R.id.tvRunningDistance:
                showFDialog("runningDistance", 0);
                break;
            case R.id.tvMagnetizeDistance:
                showFDialog("magnetizeDistance", 0);
                break;
            case R.id.tvMagnetizeTime:
                showFDialog("magnetizeTime", 0);
                break;
            case R.id.tvMagnetizeVoltage:
                showFDialog("magnetizeVoltage", 1);
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
                    boolean backstate = new ImageSave().saveBitmap("/LUKERobotDescImage/", this, bitmap);
                    if (backstate) {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(RobotDescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存成功");
                    } else {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(RobotDescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存失败");
                    }
                }
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
                    new TirenSet().checkTirem(ivTimer);
                    startMedia();
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
                new MainUI().showPopupMenu(rbAlbum, "RobotDesc", this);
                break;
            case R.id.rbSetting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("address", Constant.URL);
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

    //创建申请录屏的 Intent
    private void requestMediaProjection() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, TAG_ONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent backdata) {
        super.onActivityResult(requestCode, resultCode, backdata);
        switch (requestCode) {
            case TAG_ONE:
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
            case TAG_TWO:
                if (resultCode == Activity.RESULT_OK) {
                    String position = backdata.getStringExtra("position");
                }

        }
    }

    private void startMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            //获取mediaRecorder
            mediaRecorder = new MyMediaRecorder().getMediaRecorder(this, "/LUKERobotDescVideo/");
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("你的name",
                    width, height, 1,
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

    //下拉选择监听
    private String spinnerData(View view, String tvCHControl) {
        new MainUI().showPopupMenu(view, tvCHControl, this, new ModbusCallBack() {
            @Override
            public void success(String sData) {
                spinnerBackData(tvCHControl, sData);
            }

            @Override
            public void fail(String fData) {
                spinnerBackData(tvCHControl, fData);
            }
        });
        return spinnerData;
    }

    private void spinnerBackData(String tvCHControl, String sData) {
        if (tvCHControl.equals("CEControl")) {
            chie = sData;
            makeData();
        } else if (tvCHControl.equals("BlackOrWhiteLight")) {
            chieLight = sData;
            makeData();
        } else if (tvCHControl.equals("Light")) {
            lighting = sData;
            makeData();
        } else if (tvCHControl.equals("CHControl")) {
            magnetize = sData;
            makeData();
        } else if (tvCHControl.equals("Battery")) {
            spray = sData;
            makeData();
        } else if (tvCHControl.equals("MagnetizeRate")) {
            magnetizeRate = sData;
            makeData();
        }
    }


    /**
     * 弹窗数据设置
     */
    private void showFDialog(String tag, int num) {
        //protectVoltage
        String hint = "";
        if (tag.equals("protectVoltage")) {
            hint = "保护电压设定值";
        } else if (tag.equals("protectCurrent")) {
            hint = "保护电流设定值";
        } else if (tag.equals("runningSpeed")) {
            hint = "运行速度设定值";
        } else if (tag.equals("runningDistance")) {
            hint = "运行距离设定值";
        } else if (tag.equals("magnetizeDistance")) {
            hint = "磁化间隔距离设定值";
        } else if (tag.equals("magnetizeTime")) {
            hint = "磁化时间设定值";
        } else if (tag.equals("magnetizeVoltage")) {
            hint = "磁化电压";
        }
        new AlertDialogUtil(this).showWriteDialog(hint, num, new ModbusCallBack() {
            @Override
            public void success(String backData) {
                if (backData != null && !backData.trim().equals("")) {
                    int distanceData = (int) (Float.parseFloat(backData) * 10);
                    String hex = Integer.toHexString(distanceData);
                    switch (tag) {
                        case "protectVoltage":
                            if (dataCheck(backData, 18, 22)) {
                                protectVoltage = baseTcpClient.StringToHex(backData, 2,true);
                                Log.e("XXXX",protectVoltage+"保护电压");
                                makeData();
                            } else {
                                Toast.makeText(RobotDescernActivity.this, "请输入正确的保护电压", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case "protectCurrent":
                            protectCurrent = baseTcpClient.StringToHex(backData, 2,true);
                            makeData();
                            break;
                        case "runningSpeed":
                            if (dataCheck(backData, 0, 6)) {
                                protectSpeed = baseTcpClient.StringToHex(backData, 2,true);
                                makeData();
                            } else {
                                Toast.makeText(RobotDescernActivity.this, "请输入正确的运行速度", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case "runningDistance":
                            if (dataCheck(backData, 0, 4294967295L)) {
                                runningDistance = baseTcpClient.StringToHex(backData, 8,false);
                                makeData();
                            } else {
                                Toast.makeText(RobotDescernActivity.this, "请输入正确的运行距离", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case "magnetizeDistance":
                            if (dataCheck(backData, 0, 65535)) {
                                magnetizeDistance = baseTcpClient.StringToHex(backData, 4,false);
                                makeData();
                            } else {
                                Toast.makeText(RobotDescernActivity.this, "请输入正确的磁化间隔距离", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case "magnetizeTime":
                            if (dataCheck(backData, 0, 65535)) {
                                magnetizeTime = baseTcpClient.StringToHex(backData, 4,false);
                                makeData();
                            } else {
                                Toast.makeText(RobotDescernActivity.this, "请输入正确的磁化时间", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        case "magnetizeVoltage":
                            if (dataCheck(backData, 0, 400)) {
                                magnetizeVoltage = baseTcpClient.StringToHex(backData, 4,true);
                                makeData();
                            } else {
                                Toast.makeText(RobotDescernActivity.this, "请输入正确的磁化间隔距离", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                    }
                    Log.e("XXX", hex);
                }
            }

            @Override
            public void fail(String s) {

            }
        });
    }

    private boolean dataCheck(String data, double startData, long endData) {
        if (startData <= Float.parseFloat(data) && Float.parseFloat(data) <= endData) {
            return true;
        }
        return false;
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
//            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false, selectnum);
            YoloV5Ncnn.Obj[] objects = null;
            if (magnetize.equals("1")) {
                objects = yolov5ncnn.Detect(bmp, false);
                showObjects(objects);
            } else {
                showObjects(objects);
            }
            showObjects(objects);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
//            Log.e("XXX", ex.toString());
            Message message = new Message();
            message.what = TAG_THERE;
            message.obj = ex.toString();
            handlerLoop.sendMessage(message);
        } finally {
        }
    }

    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null || objects.length == 0) {
            Message message = new Message();
            message.what = TAG_ONE;
            message.obj = bmp;
            handlerLoop.sendMessage(message);
//            imageView.setImageBitmap(bmp);
            return;
        } else {
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
            Message message = new Message();
            message.what = TAG_FOUR;
            message.obj = rgba;
            handlerLoop.sendMessage(message);
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        boolean backstate = new ImageSave().saveBitmap("/LUKERobotDescImage/", project, workName, workCode, context, bmp);
        if (backstate) {
            Log.e("XXX", "保存成功");
        } else {
            Log.e("XXX", "保存失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modbusReq.destory();
//        modbusContion.destory();
        handlerData.removeCallbacksAndMessages(null);
        if (mythread != null) {
            mythread.interrupt();
        }
        runing = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String audio = new SharePreferencesUtils().getString(this, "audio", "");
        mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.nan);
        if (audio.equals("fengming")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.fengming);
        }
        if (audio.equals("nv")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.nv);
        }
        if (audio.equals("nan")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.nan);
        }
        if (audio.equals("ami")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.ami);
        }
        if (audio.equals("dzy1")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.dzy1);
        }
        if (audio.equals("dzy2")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.dzy2);
        }
        if (audio.equals("jsq1")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.jsq1);
        }
        if (audio.equals("jsq2")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.jsq2);
        }
        if (audio.equals("db")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.db);
        }
        if (audio.equals("dh")) {
            mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.dh);
        }
    }

    Handler handlerLoop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TAG_ONE:
                    Bitmap bit = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bit);
                    break;
                case TAG_THERE:
                    String toastString = msg.obj.toString();
                    if (toastString.contains("java.net.ConnectException: Failed to connect")
                            || toastString.contains("java.io.IOException: unexpected end")
                            || toastString.contains("java.io.InterruptedIOException: thread interrupted")
                            || toastString.contains("java.lang.NullPointerException: Attempt to get length of null array")) {
                        break;
                    } else {
//                        Toast.makeText(RobotDescernActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
//                    Log.e("XXX", toastString);
                    break;
                case TAG_FOUR:
                    Bitmap bitH = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitH);
                    saveThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap rgba1 = bitH.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas1 = new Canvas(rgba1);
                            canvas1.drawText("工程名称:" + project, 15, 30, new MyPaint().getTextpaint());
                            canvas1.drawText("工件名称:" + workName, 15, 70, new MyPaint().getTextpaint());
                            canvas1.drawText("工件编号:" + workCode, 15, 110, new MyPaint().getTextpaint());
                            mediaPlayer.start();
                            if (isFirst) {
                                saveImageToGallery(RobotDescernActivity.this, rgba1);
                                saveTime = System.currentTimeMillis();
                                isFirst = false;
                            } else {
                                currentTmeTime = System.currentTimeMillis();
                                if (currentTmeTime - saveTime > 3000) {
                                    saveImageToGallery(RobotDescernActivity.this, rgba1);
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

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAG_ONE:
                    Toast.makeText(RobotDescernActivity.this, R.string.connect_success, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_TWO:
                    Toast.makeText(RobotDescernActivity.this, R.string.connect_faile, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_THERE:
                    Toast.makeText(RobotDescernActivity.this, R.string.receive_faile, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

}
