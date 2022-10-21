package com.example.lksynthesizeapp.ChiFen.Activity;

import static com.example.lksynthesizeapp.ChiFen.Base.ByteUtil.hexStringToAlgorism;
import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.itemName;
import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.mItemImgs;
import static com.example.lksynthesizeapp.Constant.Base.Constant.MODBUS_CODE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_EIGHT;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FIVE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FOUR;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_ONE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_SEVEN;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_THERE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_TWO;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.ByteUtil;
import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Base.MyMediaRecorder;
import com.example.lksynthesizeapp.ChiFen.Base.TirenSet;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusContion;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusFloatCallBack;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenu;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenuAdapter;
import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.R;
import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class RobotActivity extends BaseActivity {

    @BindView(R.id.webView)
    MyWebView webView;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvCHTime)
    TextView tvCHTime;
    @BindView(R.id.tvCEControl)
    TextView tvCEControl;
    @BindView(R.id.tvLightSelect)
    TextView tvLightSelect;
    @BindView(R.id.tvSearchlightControl)
    TextView tvSearchlightControl;
    @BindView(R.id.tvCHControl)
    TextView tvCHControl;
    Button btnLightSelect;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.cm_main)
    CircleMenu cmMain;
    @BindView(R.id.btnStop)
    Button btnStop;
    String address = null;
    Message message;
    List<ItemInfo> data = new ArrayList<>();
    @BindView(R.id.rbCamera)
    RadioButton rbCamera;
    @BindView(R.id.rbVideo)
    RadioButton rbVideo;
    @BindView(R.id.rbAlbum)
    RadioButton rbAlbum;
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
    private CircleMenuAdapter circleMenuAdapternew;
    private boolean isConnect = false;
    private int TIME = 2000;
    ModbusContion modbusContion = new ModbusContion();
    Handler handlerConfigure = new Handler();
    public static String TAG = "RobotActivitytest";
    private Handler handlerData = new Handler();
    private Toast toast;
    public static String project = "", workName = "", workCode = "";
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    private MediaRecorder mediaRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        ButterKnife.bind(this);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        webView.setBackgroundColor(Color.BLACK);
        WebSettings WebSet = webView.getSettings();    //获取webview设置
        WebSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);   //自适应屏幕
        //禁止上下左右滚动(不显示滚动条)
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        address = getIntent().getStringExtra("address");
        if (address != null) {
//            address = "http://" + address + ":8080";
            webView.loadUrl("http://" + address + ":8080");
        } else {
            Toast.makeText(this, "IP为空,请等待连接", Toast.LENGTH_SHORT).show();
            finish();
        }

        modbusConnect();
        setUiData();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_robot;
    }

    @Override
    protected boolean isHasHeader() {
        return false;
    }

    @Override
    protected void rightClient() {

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //todo what you want
            modbusContion.readInputRegistersClickEvent(MODBUS_CODE, 3, TAG_FIVE, new ModbusCallBack() {
                @Override
                public void success(String s) {
                    message = new Message();
                    message.what = TAG_THERE;
                    message.obj = s;
                    handler.sendMessage(message);

                    modbusContion.readHoldingRegistersClickEvent(MODBUS_CODE, 16, TAG_EIGHT, new ModbusFloatCallBack() {
                        @Override
                        public void success(short[] s) {
                            message = new Message();
                            message.what = TAG_FOUR;
                            message.obj = s;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void fail(String s) {
                            message = new Message();
                            message.what = TAG_FIVE;
                            message.obj = s;
                            handler.sendMessage(message);
                        }
                    });
                }

                @Override
                public void fail(String s) {
                    message = new Message();
                    message.what = TAG_FIVE;
                    message.obj = s;
                    handler.sendMessage(message);
                }
            });
            handlerData.postDelayed(runnable, 2000);
        }
    };


    /**
     * 连接
     */
    private void modbusConnect() {
        ModbusReq.getInstance().setParam(new ModbusParam()
//                .setHost("172.16.16.68")
                .setHost(address)
                .setPort(502)
                .setEncapsulated(true)
                .setKeepAlive(true)
                .setTimeout(2000)
                .setRetries(0))
                .init(new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        isConnect = true;
                        handlerData.postDelayed(runnable, TIME);//触发定时器
                        Log.e(TAG, "onSuccess " + s);
                        message = new Message();
                        message.what = TAG_ONE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.e(TAG, "onFailed " + msg);
                        message = new Message();
                        message.what = TAG_TWO;
                        handler.sendMessage(message);
                    }
                });
    }

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
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_ONE);
                    }
                    if (itemName[position].equals("左")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_THERE);
                    }
                    if (itemName[position].equals("右")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_FOUR);
                    }
                    if (itemName[position].equals("下")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_TWO);
                    }
                } else {
                    message = new Message();
                    message.what = TAG_TWO;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @OnClick({R.id.btnStop, R.id.tvSpeed, R.id.tvDistance, R.id.tvCHTime,
            R.id.tvCEControl, R.id.tvLightSelect, R.id.tvSearchlightControl, R.id.tvCHControl,
            R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum, R.id.linearLayoutStop, R.id.rbSetting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStop:
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_SEVEN);
                break;
            case R.id.tvSpeed:
                //爬行速度
                showFDialog("Speed");
                break;
            case R.id.tvDistance:
                //行走距离
                showFDialog("Distance");
                break;
            case R.id.tvCHTime:
                //磁化时间
                showFDialog("Time");
                break;
            case R.id.tvCEControl:
                CEControl(tvCEControl);
                //磁轭控制
                break;
            case R.id.tvLightSelect:
                LightSelect(tvLightSelect);
                //黑白选择
                break;
            case R.id.tvSearchlightControl:
                SearchlightControl(tvSearchlightControl);
                //探  照  灯
                break;
            case R.id.tvCHControl:
                CHControl(tvCHControl);
                //磁化控制
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
                    boolean backstate = new ImageSave().saveBitmap("/LUKERobotImage/", project, workName, workCode, this, bitmap);
                    if (backstate) {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(RobotActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存成功");
                    } else {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(RobotActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
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
                    if (EasyPermissions.hasPermissions(this, PERMS)) {
                        new TirenSet().checkTirem(ivTimer);
                        startMedia();
                    } else {
                        // 没有申请过权限，现在去申请
                        EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", TAG_ONE, PERMS);
                    }
                }
                break;
            case R.id.linearLayoutStop:
                radioGroup.setVisibility(View.VISIBLE);
                linearLayoutStop.setVisibility(View.GONE);
                if (mediaRecorder != null) {
                    stopMedia();
                }
                break;
            case R.id.rbAlbum:
                new MainUI().showPopupMenu(rbAlbum, "Robot", this);
                break;
            case R.id.rbSetting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("address",address);
                intent.putExtra("tag","local");
                startActivity(intent);
                break;
        }
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
            //获取mediaRecorder
            mediaRecorder = new MyMediaRecorder().getMediaRecorder(this,project, workName, workCode, "/LUKERobotVideo/");
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("你的name",
                    2400, 1080, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(),
                    null, null);
        }
        //开始录制
        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void stopMedia() {
        mediaRecorder.stop();
    }

    /**
     * 黑白选择
     */
    private void LightSelect(View view) {
        new MainUI().showPopupMenuLight(view, this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 6, TAG_THERE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 6, TAG_ONE);
            }
        });
    }

    /**
     * 探  照  灯
     */
    private void SearchlightControl(View view) {
        new MainUI().showPopupMenu(view, "SearchlightControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 7, TAG_ONE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 7, TAG_THERE);
            }
        });
    }

    /**
     * 磁化控制
     */
    private void CHControl(View view) {
        new MainUI().showPopupMenu(view, "CHControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 3, TAG_ONE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 3, TAG_THERE);
            }
        });
    }

    /**
     * 磁轭控制
     */
    private void CEControl(View view) {
        new MainUI().showPopupMenu(view, "CEControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 5, TAG_THERE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 5, TAG_ONE);
            }
        });
    }

    /**
     * 弹窗数据设置
     */
    private void showFDialog(String tag) {
        String hint = "";
        if (tag.equals("Speed")) {
            hint = "请输入爬行速度";
        } else if (tag.equals("Distance")) {
            hint = "请输入行走距离";
        } else if (tag.equals("Time")) {
            hint = "请输入磁化时间";
        }
        new AlertDialogUtil(this).showWriteDialog(hint, new ModbusCallBack() {
            @Override
            public void success(String backData) {
                if (backData != null && !backData.trim().equals("")) {
                    float distanceData = Float.parseFloat(backData);
                    if (tag.equals("Speed")) {
                        String hex = new ByteUtil().singleToHex(distanceData);
                        short[] shorts = new short[2];
                        if (hex.length() == 8) {
                            short shortH = Short.parseShort(hexStringToAlgorism(hex.substring(4, 8)) + "");
                            short shortL = Short.parseShort(hexStringToAlgorism(hex.substring(0, 4)) + "");
                            shorts[0] = shortH;
                            shorts[1] = shortL;
                        }
                        new ModbusContion().writeRegistersClickEvent(MODBUS_CODE, 18, shorts);
                    }
                    if (tag.equals("Distance")) {
                        new ModbusContion().writeRegistersClickEvent(MODBUS_CODE, 20, byte2ShortArray(float2byte(distanceData)));
                    }
                    if (tag.equals("Time")) {
                        modbusContion.writeRegistersClickEvent(MODBUS_CODE, 22, byte2ShortArray(float2byte(distanceData)));
                    }
                }
            }

            @Override
            public void fail(String s) {

            }
        });
    }

    public static byte[] float2byte(float f) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    // byte数组转short数组
    public static short[] byte2ShortArray(byte[] data) {
        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    // byte数组转short数组
    public static short[] byte2ShortArray1(String data) {
        //先把Stirng数据转成char数组
        char[] dataChar = data.toCharArray();
        //创建一个short数组，大小为参数的长度
        short[] dataShort = new short[data.length()];
        //循环dataChar数组，把dataChar一个一个存到short中
        for (int i = 0; i < dataChar.length; i++) {
            dataShort[i] = (short) dataChar[i];
        }
        return dataShort;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modbusContion.destory();
        handlerData.removeCallbacksAndMessages(null);
    }

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TAG_ONE:
                    Toast.makeText(RobotActivity.this, R.string.connect_success, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_TWO:
                    Toast.makeText(RobotActivity.this, R.string.connect_faile, Toast.LENGTH_SHORT).show();
                    break;
                case TAG_THERE:
                    String data = msg.obj.toString();
                    set16Data(substringData(data));
                    break;
                case TAG_FOUR:
                    short[] dataArray = (short[]) msg.obj;
                    String[] typeData = new String[dataArray.length];
                    for (int i = 0; i < dataArray.length; i++) {
                        String strHex2 = String.format("%04x", dataArray[i]).toUpperCase();//高位补0
                        typeData[i] = strHex2;
                    }
                    setFloatData(typeData);
                    break;
                case TAG_FIVE:
                    String message = msg.obj.toString();
                    Toast.makeText(RobotActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };


    //显示16进制数据
    private void set16Data(String[] strs) {
        //磁化
        if (strs[0] != null && strs[0].trim().equals("1")) {
            tvCHControl.setText(R.string.ch_control_open);
        } else if (strs[0] != null && strs[0].trim().equals("3")) {
            tvCHControl.setText(R.string.ch_control_close);
        }

        //磁轭
        if (strs[2] != null && strs[2].trim().equals("1")) {
            tvCEControl.setText(R.string.ce_up);
        } else if (strs[2] != null && strs[2].trim().equals("3")) {
            tvCEControl.setText(R.string.ce_down);
        }

        //黑白灯光
        if (strs[3] != null && strs[3].trim().equals("1")) {
            tvLightSelect.setText(R.string.light_black);
            btnLightSelect.setText(R.string.light_black);
        } else if (strs[3] != null && strs[3].trim().equals("3")) {
            tvLightSelect.setText(R.string.light_wither);
            btnLightSelect.setText(R.string.light_wither);
        }

        //探照灯
        if (strs[4] != null && strs[4].trim().equals("1")) {
            tvSearchlightControl.setText(R.string.search_light_open);
        } else if (strs[4] != null && strs[4].trim().equals("3")) {
            tvSearchlightControl.setText(R.string.search_light_close);
        }

    }

    //显示Float数据
    private void setFloatData(String[] typeData) {
        if (typeData[2] != null && typeData[3] != null) {
            String speenHex = typeData[3] + typeData[2];
            int ieee754Speed = Integer.parseInt(speenHex, 16);
            float realApeed = Float.intBitsToFloat(ieee754Speed);
            tvSpeed.setText(realApeed + "m/min");
        }
        if (typeData[4] != null && typeData[5] != null) {
            String distanceHex = typeData[5] + typeData[4];
            int ieee754Distance = Integer.parseInt(distanceHex, 16);
            float realDistance = Float.intBitsToFloat(ieee754Distance);
            tvDistance.setText(realDistance + "mm");
        }
        if (typeData[6] != null && typeData[7] != null) {
            String cHTimeHex = typeData[7] + typeData[6];
            int ieee754CHTime = Integer.parseInt(cHTimeHex, 16);
            float realCHTime = Float.intBitsToFloat(ieee754CHTime);
            tvCHTime.setText(realCHTime + "s");
        }
    }

    //切割数据
    private String[] substringData(String data) {
        String data1 = data.substring(1, data.length());
        String data2 = data1.substring(0, data1.length() - 1);
        String[] strs = data2.split(",");
        return strs;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (address != null) {
//            address = "http://" + address + ":8080";
            webView.loadUrl("http://" + address + ":8080");
        }
    }

}