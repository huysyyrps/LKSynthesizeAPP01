package com.example.lksynthesizeapp.ChiFen.Activity;

import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.itemName;
import static com.example.lksynthesizeapp.ChiFen.Robot.RobotData.mItemImgs;
import static com.example.lksynthesizeapp.Constant.Base.Constant.MODBUS_CODE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FIVE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_FOUR;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_ONE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_SEVEN;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_SIX;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_THERE;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TAG_TWO;
import static com.example.lksynthesizeapp.Constant.Base.Constant.TWENTYFOUR;

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
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusContion;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusFloatCallBack;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenu;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenuAdapter;
import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.activity.SendSelectActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.example.lksynthesizeapp.YoloV5Ncnn;
import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class RobotDescernActivity extends AppCompatActivity {
    @BindView(R.id.imageView)
    ImageView imageView;
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
    @BindView(R.id.tvBattery)
    TextView tvBattery;
    @BindView(R.id.rbBack)
    RadioButton rbBack;
    @BindView(R.id.tvVoltage)
    TextView tvVoltage;
    @BindView(R.id.linearLayoutWrite)
    LinearLayout linearLayoutWrite;
    @BindView(R.id.rbOther)
    RadioButton rbOther;
    @BindView(R.id.tvSpace)
    TextView tvSpace;
    @BindView(R.id.rbDescern)
    RadioButton rbDescern;
    @BindView(R.id.rbNoDescern)
    RadioButton rbNoDescern;
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
    ModbusContion modbusContion = new ModbusContion();
    public static String TAG = "RobotActivitytest";
    private Handler handlerData = new Handler();
    ModbusReq modbusReq = ModbusReq.getInstance();
    public static String project = "", workName = "", workCode = "";
    private VirtualDisplay mVirtualDisplay;
    //???????????????
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    private MediaRecorder mediaRecorder;
    public long saveTime = 0;
    public long currentTmeTime = 0;
    private boolean openDescern = false;
//    private String selectMode;
//    private int selectnum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // ????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //??????????????????
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_robot_descern);
        ButterKnife.bind(this);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
//        selectMode = intent.getStringExtra("selectMode");
//        if (selectMode.equals("mode1")) {
//            selectnum = 1;
//        } else if (selectMode.equals("mode2")) {
//            selectnum = 2;
//        } else if (selectMode.equals("mode3")) {
//            selectnum = 3;
//        }
//        boolean ret_init = yolov5ncnn.Init(getAssets(), selectnum);
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Log.e("MainActivity", "yolov5ncnn Init failed");
        }
        url = "http://" + Constant.URL + ":8080?action=snapshot";
        mythread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runing) {
                    draw();
                    currentTme = System.currentTimeMillis();
                }
            }
        });
        mythread.start();

        new BottomUI().hideBottomUIMenu(this.getWindow());

        modbusConnect();
        setUiData();
    }

    /**
     * ??????
     */
    private void modbusConnect() {
        modbusReq.setParam(new ModbusParam()
//                .setHost("172.16.16.68")
                .setHost(Constant.URL)
                .setPort(502)
                .setEncapsulated(true)
                .setKeepAlive(true)
                .setTimeout(2000)
                .setRetries(0))
                .init(new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        isConnect = true;
                        handlerData.postDelayed(runnable, TIME);//???????????????
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
                        return;
                    }
                });
    }

    /**
     * ?????????
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
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_ONE);
                    }
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_THERE);
                    }
                    if (itemName[position].equals("???")) {
                        modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_FOUR);
                    }
                    if (itemName[position].equals("???")) {
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //todo what you want
            modbusContion.readInputRegistersClickEvent(MODBUS_CODE, 3, TAG_SIX, new ModbusCallBack() {
                @Override
                public void success(String s) {
                    message = new Message();
                    message.what = TAG_THERE;
                    message.obj = s;
                    handler.sendMessage(message);
                    if (linearLayoutWrite.getVisibility() == View.VISIBLE) {
                        modbusContion.readHoldingRegistersClickEvent(MODBUS_CODE, 0, TWENTYFOUR, new ModbusFloatCallBack() {
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
                                return;
                            }
                        });
                    }
                }

                @Override
                public void fail(String s) {
                    message = new Message();
                    message.what = TAG_FIVE;
                    message.obj = s;
                    handler.sendMessage(message);
                    return;
                }
            });
            handlerData.postDelayed(runnable, 1000);
        }
    };


    @OnClick({R.id.btnStop, R.id.tvSpeed, R.id.tvCHTime,
            R.id.tvCEControl, R.id.tvLightSelect, R.id.tvSearchlightControl,
            R.id.tvCHControl, R.id.rbCamera, R.id.rbVideo, R.id.rbAlbum,
            R.id.linearLayoutStop, R.id.rbSetting, R.id.tvBattery, R.id.rbBack,
            R.id.rbOther, R.id.tvSpace, R.id.rbDescern, R.id.rbNoDescern})
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
            case R.id.rbOther:
                if (linearLayoutWrite.getVisibility() == View.VISIBLE) {
                    linearLayoutWrite.setVisibility(View.GONE);
                } else {
                    linearLayoutWrite.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnStop:
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 4, TAG_SEVEN);
                break;
            case R.id.tvSpace:
                showFDialog("Space");
                break;
            case R.id.tvSpeed:
                //????????????
                showFDialog("Speed");
                break;
            case R.id.tvCHTime:
                //????????????
                showFDialog("Time");
                break;
            case R.id.tvCEControl:
                CEControl(tvCEControl);
                //????????????
                break;
            case R.id.tvLightSelect:
                LightSelect(tvLightSelect);
                //????????????
                break;
            case R.id.tvSearchlightControl:
                SearchlightControl(tvSearchlightControl);
                //???  ???  ???
                break;
            case R.id.tvCHControl:
                CHControl(tvCHControl);
                //????????????
                break;
            case R.id.tvBattery:
                BatteryControl(tvBattery);
                //?????????
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
                    boolean backstate = new ImageSave().saveBitmap("/LUKERobotDescImage/", project, workName, workCode, this, bitmap);
                    if (backstate) {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(RobotDescernActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "????????????");
                    } else {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(RobotDescernActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "????????????");
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
                        // ???????????????????????????????????????
                        EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", TAG_ONE, PERMS);
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

    //????????????????????? Intent
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
            //??????mediaRecorder
            mediaRecorder = new MyMediaRecorder().getMediaRecorder(this, project, workName, workCode, "/LUKERobotDescVideo/");
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("??????name",
                    width, height, 1,
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

    /**
     * ????????????
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
     * ???  ???  ???
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
     * ????????????
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
     * ????????????
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
     * ???????????????
     */
    private void BatteryControl(View view) {
        new MainUI().showPopupMenu(view, "BatteryControl", this, new ModbusCallBack() {
            @Override
            public void success(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 8, TAG_ONE);
            }

            @Override
            public void fail(String s) {
                modbusContion.writeRegisterClickEvent(MODBUS_CODE, 8, TAG_THERE);
            }
        });
    }

    /**
     * ??????????????????
     */
    private void showFDialog(String tag) {
        String hint = "";
        if (tag.equals("Speed")) {
            hint = "?????????????????????";
        } else if (tag.equals("Time")) {
            hint = "?????????????????????";
        }else if (tag.equals("Space")) {
            hint = "?????????????????????";
        }
        new AlertDialogUtil(this).showWriteDialog(hint, new ModbusCallBack() {
            @Override
            public void success(String backData) {
                if (backData != null && !backData.trim().equals("")) {
                    float distanceData = Float.parseFloat(backData);
                    if (tag.equals("Speed")) {
//                        String hex = new ByteUtil().singleToHex(distanceData);
//                        short[] shorts = new short[2];
//                        if (hex.length() == 8) {
//                            short shortH = Short.parseShort(hexStringToAlgorism(hex.substring(4, 8)) + "");
//                            short shortL = Short.parseShort(hexStringToAlgorism(hex.substring(0, 4)) + "");
//                            shorts[0] = shortH;
//                            shorts[1] = shortL;
//                        }
                        new ModbusContion().writeRegistersClickEvent(MODBUS_CODE, 0, byte2ShortArray(float2byte(distanceData)));
                    }
                    if (tag.equals("Time")) {
                        modbusContion.writeRegistersClickEvent(MODBUS_CODE, 2, byte2ShortArray(float2byte(distanceData)));
                    }
                    if (tag.equals("Space")) {
                        modbusContion.writeRegistersClickEvent(MODBUS_CODE, 1, byte2ShortArray(float2byte(distanceData)));
                    }
                }
            }

            @Override
            public void fail(String s) {

            }
        });
    }

    public static byte[] float2byte(float f) {
        // ???float?????????byte[]
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // ????????????
        int len = b.length;
        // ???????????????????????????????????????????????????
        byte[] dest = new byte[len];
        // ????????????????????????????????????????????????????????????
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // ????????????i???????????????i?????????
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    // byte?????????short??????
    public static short[] byte2ShortArray(byte[] data) {
        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    //??????16????????????
    private void set16Data(String[] strs) {
        Log.e("XXX", String.join(",", strs));
        //??????
        if (strs[0] != null && strs[0].trim().equals("1")) {
            tvCHControl.setText(R.string.ch_control_open);
        } else if (strs[0] != null && strs[0].trim().equals("3")) {
            tvCHControl.setText(R.string.ch_control_close);
        }

        //??????
        if (strs[2] != null && strs[2].trim().equals("2")) {
            tvCEControl.setText(R.string.ce_up);
        } else if (strs[2] != null && strs[2].trim().equals("4")) {
            tvCEControl.setText(R.string.ce_down);
        }

        //????????????
        if (strs[3] != null && strs[3].trim().equals("1")) {
            tvLightSelect.setText(R.string.light_black);
        } else if (strs[3] != null && strs[3].trim().equals("3")) {
            tvLightSelect.setText(R.string.light_wither);
        }

        //?????????
        if (strs[4] != null && strs[4].trim().equals("1")) {
            tvSearchlightControl.setText(R.string.search_light_open);
        } else if (strs[4] != null && strs[4].trim().equals("3")) {
            tvSearchlightControl.setText(R.string.search_light_close);
        }

        //?????????
        if (strs[5] != null && strs[5].trim().equals("1")) {
            tvBattery.setText(R.string.battery_open);
        } else if (strs[5] != null && strs[5].trim().equals("3")) {
            tvBattery.setText(R.string.battery_close);
        }

    }

    //??????Float??????
    private void setFloatData(String[] typeData) {
        if (typeData[0] != null && typeData[1] != null) {
            String actualDistanceHex = typeData[1] + typeData[0];
            tvVoltage.setText(dataChange(actualDistanceHex) + "V");
        }

        if (typeData[20] != null && typeData[21] != null) {
            String cHTimeHex = typeData[21] + typeData[20];
            tvSpace.setText(dataChange(cHTimeHex) + "mm");
        }

        Log.e("XXX", String.join(",", typeData));
        if (typeData[18] != null && typeData[19] != null) {
            String speenHex = typeData[19] + typeData[18];
            tvSpeed.setText(dataChange(speenHex) + "m/min");
        }
        if (typeData[16] != null && typeData[17] != null) {
            String distanceHex = typeData[17] + typeData[16];
            tvDistance.setText(dataChange(distanceHex) + "mm");
        }
        if (typeData[22] != null && typeData[23] != null) {
            String cHTimeHex = typeData[23] + typeData[22];
            tvCHTime.setText(dataChange(cHTimeHex) + "s");
        }
    }

    private float dataChange(String hex) {
        Float valueVoltage = Float.intBitsToFloat(new BigInteger(hex, 16).intValue());
        DecimalFormat df = new DecimalFormat("#.00");
        Float floatData = Float.valueOf(df.format(valueVoltage));
        return floatData;
    }

    //????????????
    private String[] substringData(String data) {
        String data1 = data.substring(1, data.length());
        String data2 = data1.substring(0, data1.length() - 1);
        String[] strs = data2.split(",");
        return strs;
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
//            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false, selectnum);
            YoloV5Ncnn.Obj[] objects = null;
            if (openDescern){
                objects = yolov5ncnn.Detect(bmp, false);
                showObjects(objects);
            }else {
                showObjects(objects);
            }
            showObjects(objects);
            //??????HttpURLConnection??????
            conn.disconnect();
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
            Message message = new Message();
            message.what = TAG_THERE;
            message.obj = ex.toString();
            handlerLoop.sendMessage(message);
        } finally {
        }
    }

    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects==null||objects.length == 0) {
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
            Log.e("XXX", "????????????");
        } else {
            Log.e("XXX", "????????????");
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
        Log.e("XXXXX", "onResume");
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
                        Toast.makeText(RobotDescernActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("XXX", toastString);
//                    Toast.makeText(DescernActivity.this, getResources().getString(R.string.dialog_close), Toast.LENGTH_SHORT).show();
//                    finish();
                    break;
                case TAG_FOUR:
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

    //???????????????,????????????Handler???????????????,???????????????Handler????????????????????????(handleMessage())
    private Handler handler = new Handler() {
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
                    String data = msg.obj.toString();
                    set16Data(substringData(data));
                    break;
                case TAG_FOUR:
                    short[] dataArray = (short[]) msg.obj;
                    String[] typeData = new String[dataArray.length];
                    for (int i = 0; i < dataArray.length; i++) {
                        String strHex2 = String.format("%04x", dataArray[i]).toUpperCase();//?????????0
                        typeData[i] = strHex2;
                    }
                    setFloatData(typeData);
                    break;
                case TAG_FIVE:
                    String message = msg.obj.toString();
                    Toast.makeText(RobotDescernActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };

    @OnClick(R.id.tvSpace)
    public void onClick() {
    }
}
