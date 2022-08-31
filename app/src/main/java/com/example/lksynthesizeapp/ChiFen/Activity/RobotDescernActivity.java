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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.ByteUtil;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusContion;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusFloatCallBack;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenu;
import com.example.lksynthesizeapp.ChiFen.Robot.View.CircleMenuAdapter;
import com.example.lksynthesizeapp.ChiFen.bean.ItemInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.YoloV5Ncnn;
import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotDescernActivity extends AppCompatActivity {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.ivVisition)
    ImageView ivVisition;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvCHTime)
    TextView tvCHTime;
    @BindView(R.id.tvActualDistance)
    TextView tvActualDistance;
    @BindView(R.id.tvCEControl)
    TextView tvCEControl;
    @BindView(R.id.tvLightSelect)
    TextView tvLightSelect;
    @BindView(R.id.tvSearchlightControl)
    TextView tvSearchlightControl;
    @BindView(R.id.tvCHControl)
    TextView tvCHControl;
    @BindView(R.id.linSetting)
    LinearLayout linSetting;
    @BindView(R.id.btnCEControl)
    Button btnCEControl;
    @BindView(R.id.btnCHControl)
    Button btnCHControl;
    @BindView(R.id.btnSearchlightControl)
    Button btnSearchlightControl;
    @BindView(R.id.btnLightSelect)
    Button btnLightSelect;
    @BindView(R.id.linBtn)
    LinearLayout linBtn;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.ivGone)
    ImageView ivGone;
    @BindView(R.id.cm_main)
    CircleMenu cmMain;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    private String url;
    private Bitmap bmp = null;
    private Thread mythread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    URL videoUrl;
    HttpURLConnection conn;
    Paint paint;
    Paint textbgpaint;
    Paint textpaint;
    private MediaPlayer mediaPlayer;
    long currentTme = 0, currentTme1 = 0;
    public boolean runing = true;
    public static String address = "";
    public boolean isFirst = true;
    public static final int TIME = 3000;
    Message message;
    List<ItemInfo> data = new ArrayList<>();
    private CircleMenuAdapter circleMenuAdapternew;
    private boolean isConnect = false;
    ModbusContion modbusContion = new ModbusContion();
    public static String TAG = "RobotActivitytest";
    private Handler handlerData = new Handler();


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
        mediaPlayer = MediaPlayer.create(RobotDescernActivity.this, R.raw.fengming);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.GREEN);

        textbgpaint = new Paint();
        textbgpaint.setColor(Color.WHITE);
        textbgpaint.setStyle(Paint.Style.FILL);

        textpaint = new Paint();
        textpaint.setColor(Color.GREEN);
        textpaint.setTextSize(13);
        textpaint.setTextAlign(Paint.Align.LEFT);
//        w = getWindowManager().getDefaultDisplay().getWidth();
//        h = getWindowManager().getDefaultDisplay().getHeight();
//        scanw = w/364;
//        scanh = h/237;
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Log.e("MainActivity", "yolov5ncnn Init failed");
        }
        address = getIntent().getStringExtra("address");
        if (address != null) {
            url = "http://" + address + ":8080?action=snapshot";
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
        } else {
            Toast.makeText(this, "IP为空,请等待连接", Toast.LENGTH_SHORT).show();
            finish();
        }

        new BottomUI().hideBottomUIMenu(this.getWindow());

        modbusConnect();
        setUiData();
    }

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
                        return;
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
                            return;
                        }
                    });
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
            handlerData.postDelayed(runnable, 2000);
        }
    };


    @OnClick({R.id.ivVisition, R.id.ivGone, R.id.btnStop, R.id.tvSpeed, R.id.tvDistance, R.id.tvCHTime,
            R.id.tvCEControl, R.id.tvLightSelect, R.id.tvSearchlightControl, R.id.tvCHControl,
            R.id.btnCEControl, R.id.btnCHControl, R.id.btnSearchlightControl, R.id.btnLightSelect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivVisition:
                linSetting.setVisibility(View.VISIBLE);
                ivVisition.setVisibility(View.GONE);
                ivGone.setVisibility(View.VISIBLE);
                break;
            case R.id.ivGone:
                linSetting.setVisibility(View.GONE);
                ivVisition.setVisibility(View.VISIBLE);
                ivGone.setVisibility(View.GONE);
                break;
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
            case R.id.btnCEControl:
                //磁轭控制
                CEControl(btnCEControl);
                break;
            case R.id.btnCHControl:
                CHControl(btnCHControl);
                break;
            case R.id.btnSearchlightControl:
                //探  照  灯
                SearchlightControl(btnSearchlightControl);
                break;
            case R.id.btnLightSelect:
                //黑白选择
                LightSelect(btnLightSelect);
                break;
        }
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

    //消息处理者,创建一个Handler的子类对象,目的是重写Handler的处理消息的方法(handleMessage())
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
                        String strHex2 = String.format("%04x", dataArray[i]).toUpperCase();//高位补0
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

    //显示16进制数据
    private void set16Data(String[] strs) {
        //磁化
        if (strs[0] != null && strs[0].trim().equals("1")) {
            tvCHControl.setText(R.string.ch_control_open);
            btnCHControl.setText(R.string.ch_control_open);
            linBtn.setVisibility(View.VISIBLE);
        } else if (strs[0] != null && strs[0].trim().equals("3")) {
            tvCHControl.setText(R.string.ch_control_close);
            btnCHControl.setText(R.string.ch_control_close);
            linBtn.setVisibility(View.VISIBLE);
        }

        //磁轭
        if (strs[2] != null && strs[2].trim().equals("1")) {
            tvCEControl.setText(R.string.ce_up);
            btnCEControl.setText(R.string.ce_up);
            linBtn.setVisibility(View.VISIBLE);
        } else if (strs[2] != null && strs[2].trim().equals("3")) {
            tvCEControl.setText(R.string.ce_down);
            btnCEControl.setText(R.string.ce_down);
            linBtn.setVisibility(View.VISIBLE);
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
            btnSearchlightControl.setText(R.string.search_light_open);
        } else if (strs[4] != null && strs[4].trim().equals("3")) {
            tvSearchlightControl.setText(R.string.search_light_close);
            btnSearchlightControl.setText(R.string.search_light_close);
        }

    }

    //显示Float数据
    private void setFloatData(String[] typeData) {
        if (typeData[0] != null && typeData[1] != null) {
            String actualDistanceHex = typeData[1] + typeData[0];
            int ieee754ActualDistanceHex = Integer.parseInt(actualDistanceHex, 16);
            float realActualDistanceHex = Float.intBitsToFloat(ieee754ActualDistanceHex);
            tvActualDistance.setText(realActualDistanceHex + "mm");
        }
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
//            currentTme1 = System.currentTimeMillis();
//            Log.e("XXX",(currentTme1-currentTme)+"");
            return;
        }

        // draw objects on bitmap
        Bitmap rgba = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(rgba);
        for (int i = 0; i < objects.length; i++) {
            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, paint);
            // draw filled text inside image
            {
                String text = objects[i].label + " = " + String.format("%.1f", objects[i].prob * 100) + "%";

                float text_width = textpaint.measureText(text) + 10;
                float text_height = -textpaint.ascent() + textpaint.descent() + 10;

                float x = objects[i].x;
                float y = objects[i].y - text_height;
                if (y < 0)
                    y = 0;
                if (x + text_width > rgba.getWidth())
                    x = rgba.getWidth() - text_width;
//                canvas.drawRect(x, y, x + text_width, y + text_height, textbgpaint);
                canvas.drawText(text, x, y - textpaint.ascent(), textpaint);
            }
        }
        if (objects.length != 0) {
            mediaPlayer.start();
        }
        imageView.setImageBitmap(rgba);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        modbusContion.destory();
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
}
