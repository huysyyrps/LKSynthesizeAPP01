package com.example.lksynthesizeapp.Constant.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.lksynthesizeapp.ChiFen.Activity.DescernActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.RobotDescernActivity;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.EditTextLengClient;
import com.example.lksynthesizeapp.Constant.Base.ExitApp;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 磁粉检测上传方式选择页
 */
public class SendSelectActivity extends BaseActivity {
    @BindView(R.id.tvConfim)
    TextView tvConfim;
    @BindView(R.id.etProject)
    EditText etProject;
    @BindView(R.id.etWorkName)
    EditText etWorkName;
    @BindView(R.id.etWorkCode)
    EditText etWorkCode;
    @BindView(R.id.spinner)
    Spinner spinner;
    //富有动感的Sheet弹窗
    Intent intent;
    @BindView(R.id.header)
    Header header;
    private static AlertDialogUtil alertDialogUtil;
    SharePreferencesUtils sharePreferencesUtils;
    MediaProjectionManager projectionManager;
    public static SendSelectActivity intance = null;
    private Thread mythread;
    private String url;
    Disposable disposable;
    LoadingDialog loadingDialog;
    String deviceName;
    private String[] starArray = {"mode1","mode2","mode3"};
    private String selectMode = "";
//    Timer timer;

    //推出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new ExitApp().exit(alertDialogUtil, SendSelectActivity.this);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        intance = this;
        loadingDialog = new LoadingDialog(this);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        alertDialogUtil = new AlertDialogUtil(this);
        new EditTextLengClient().textLeng(etProject, this);
        new EditTextLengClient().textLeng(etWorkCode, this);
        new EditTextLengClient().textLeng(etWorkName, this);
        deviceName = sharePreferencesUtils.getString(SendSelectActivity.this, "deviceName", "");
        Toast.makeText(intance, "WIFI名称"+ sharePreferencesUtils.getString(SendSelectActivity.this, "wifiName", "")+"\n"
                +"WIFI密码"+Constant.PASSWORD, Toast.LENGTH_SHORT).show();
        initSpinner();
    }

    @OnClick({R.id.tvConfim})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfim:
                haveAddress();
                loadingDialog.setLoadingText(getResources().getString(R.string.device_connect))
//                        .setSuccessText("加载成功")//显示加载成功时的文字
                        //.setFailedText("加载失败")
                        .setSize(200)
                        .setShowTime(1)
                        .setInterceptBack(false)
                        .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                        .setRepeatCount(1)
                        .show();
                break;
        }
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_send_select;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {
    }


    private void initSpinner(){
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,starArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        //从布局文件中获取名叫sp_dialog的下拉框
        Spinner sp = findViewById(R.id.spinner);
        //设置下拉框的标题，不设置就没有难看的标题了
        sp.setPrompt("模型");
        //设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        sp.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//            Toast.makeText(SendSelectActivity.this,"您选择的是："+starArray[i],Toast.LENGTH_SHORT).show();
            selectMode = starArray[i];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void haveAddress() {
        disposable = Observable.interval(0, 3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        url = "http://" + Constant.URL + ":8080?action=snapshot";
                        mythread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                InputStream inputstream = null;
                                //创建一个URL对象
                                URL videoUrl = null;
                                try {
                                    videoUrl = new URL(url);
                                    //利用HttpURLConnection对象从网络中获取网页数据
                                    HttpURLConnection conn = (HttpURLConnection) videoUrl.openConnection();
                                    //设置输入流
                                    conn.setDoInput(true);
                                    conn.setConnectTimeout(5 * 1000);
                                    //连接
                                    conn.connect();
                                    //得到网络返回的输入流
                                    inputstream = conn.getInputStream();
                                    //创建出一个bitmap
                                    Bitmap bmp = BitmapFactory.decodeStream(inputstream);
                                    if (bmp != null && !bmp.equals("")) {
                                        Message message = new Message();
                                        message.what = Constant.TAG_ONE;
                                        handler.sendMessage(message);
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        mythread.start();
                    }
                });
    }

    public void seSPData() {
        sharePreferencesUtils.setString(SendSelectActivity.this, "project", etProject.getText().toString());
        sharePreferencesUtils.setString(SendSelectActivity.this, "workName", etWorkName.getText().toString());
        sharePreferencesUtils.setString(SendSelectActivity.this, "workCode", etWorkCode.getText().toString());
    }

    public void SelectActivity(String data) {
        if (etProject.getText().toString().trim().equals("")) {
            Toast.makeText(SendSelectActivity.this, "请输入工程名称", Toast.LENGTH_SHORT).show();
            loadingDialog.close();
            return;
        } else if (etWorkName.getText().toString().trim().equals("")) {
            Toast.makeText(SendSelectActivity.this, "请输入工件名称", Toast.LENGTH_SHORT).show();
            loadingDialog.close();
            return;
        } else if (etWorkCode.getText().toString().trim().equals("")) {
            Toast.makeText(SendSelectActivity.this, "请输入工件编号", Toast.LENGTH_SHORT).show();
            loadingDialog.close();
            return;
        } else {
            if (deviceName.equals("爬行器")) {
                connect("PXQHAVEDESCERN");
            }
            if (deviceName.equals("磁探机")) {
                connect("CFTSYHAVEDESCERN");
            }
            seSPData();
        }
    }


    private void connect(String tag) {
        if (tag.equals("PXQHAVEDESCERN")) {
            intent = new Intent(SendSelectActivity.this, RobotDescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("selectMode",selectMode);
            startActivity(intent);
        }
        if (tag.equals("CFTSYHAVEDESCERN")) {
            intent = new Intent(SendSelectActivity.this, DescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("selectMode",selectMode);
            startActivity(intent);
        }
        loadingDialog.close();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
//            timer.cancel();
            mythread.interrupt();
        } catch (Exception e) {

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.TAG_ONE:
                    disposable.dispose();
                    mythread.interrupt();
                    SelectActivity("");
                    break;
            }
        }
    };

}