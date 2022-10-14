package com.example.lksynthesizeapp.Constant.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.lksynthesizeapp.ChiFen.Activity.DescernActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.LocalActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.RobotActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.RobotDescernActivity;
import com.example.lksynthesizeapp.ChiFen.Module.VersionInfoContract;
import com.example.lksynthesizeapp.ChiFen.Presenter.VersionInfoPresenter;
import com.example.lksynthesizeapp.ChiFen.bean.VersionInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.DialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.EditTextLengClient;
import com.example.lksynthesizeapp.Constant.Base.ExitApp;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Net.GetIpCallBack;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.google.gson.Gson;
import com.message.update.fileview.DialogUpdate;
import com.message.update.fileview.FileDownLoadTask;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 磁粉检测上传方式选择页
 */
public class SendSelectActivity extends BaseActivity implements VersionInfoContract.View {
    @BindView(R.id.tvConfim)
    TextView tvConfim;
    @BindView(R.id.etProject)
    EditText etProject;
    @BindView(R.id.etWorkName)
    EditText etWorkName;
    @BindView(R.id.etWorkCode)
    EditText etWorkCode;
    //富有动感的Sheet弹窗
    Intent intent;
    @BindView(R.id.header)
    Header header;
    private String address = "";
    private static AlertDialogUtil alertDialogUtil;
    SharePreferencesUtils sharePreferencesUtils;
    MediaProjectionManager projectionManager;
    private WifiManager mWifiManager;
    private String sid = "", pwd = "", max = "";
    String Max, model, camer, descern;
    VersionInfoPresenter versionInfoPresenter;
    private DialogUpdate dialogUpdate;
    String passWord;
    public static SendSelectActivity intance = null;
    private Thread mythread;
    private String url;
    Disposable disposable;
    LoadingDialog loadingDialog;
    Timer timer;

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
        dialogUpdate = new DialogUpdate(this);
        versionInfoPresenter = new VersionInfoPresenter(this, this);
        Max = sharePreferencesUtils.getString(SendSelectActivity.this, "max", "");
        model = sharePreferencesUtils.getString(SendSelectActivity.this, "model", "");
        camer = sharePreferencesUtils.getString(SendSelectActivity.this, "havaCamer", "");
        descern = sharePreferencesUtils.getString(SendSelectActivity.this, "haveDescern", "");
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        alertDialogUtil = new AlertDialogUtil(this);
        new EditTextLengClient().textLeng(etProject, this);
        new EditTextLengClient().textLeng(etWorkCode, this);
        new EditTextLengClient().textLeng(etWorkName, this);
        passWord = sharePreferencesUtils.getString(SendSelectActivity.this, "max", "");
        if (passWord != null && passWord.length() >= 6) {
            passWord = passWord.substring(passWord.length() - 8, passWord.length());
            Log.e("XXXXX", passWord);
        } else {
            Toast.makeText(this, "设备MAX地址错误", Toast.LENGTH_SHORT).show();
        }

        //获取版本信息
        upDataClient();

        if (camer.equals("0")) {
            Toast.makeText(this, "当前设备不具备摄像头功能", Toast.LENGTH_SHORT).show();
            return;
        }
        seSPData();
    }

    @OnClick({R.id.tvConfim})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfim:
                countDown();
                haveAddress();
                loadingDialog = new LoadingDialog(this);
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

    public void seSPData() {
        sharePreferencesUtils.setString(SendSelectActivity.this, "project", etProject.getText().toString());
        sharePreferencesUtils.setString(SendSelectActivity.this, "workName", etWorkName.getText().toString());
        sharePreferencesUtils.setString(SendSelectActivity.this, "workCode", etWorkCode.getText().toString());
    }

    public void SelectActivity(String data) {
        if (address != null) {
            if (camer.equals("否")) {
                Toast.makeText(this, "当前设备不具备摄像头功能", Toast.LENGTH_SHORT).show();
                return;
            } else if (etProject.getText().toString().trim().equals("")) {
                Toast.makeText(SendSelectActivity.this, "请输入工程名称", Toast.LENGTH_SHORT).show();
                return;
            } else if (etWorkName.getText().toString().trim().equals("")) {
                Toast.makeText(SendSelectActivity.this, "请输入工件名称", Toast.LENGTH_SHORT).show();
                return;
            } else if (etWorkCode.getText().toString().trim().equals("")) {
                Toast.makeText(SendSelectActivity.this, "请输入工件编号", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (model.equals("爬行器") && descern.equals("否")) {
                    connect("PXQNODESCERN");
                } else if (model.equals("爬行器") && descern.equals("是")) {
                    connect("PXQHAVEDESCERN");
                } else if (model.equals("磁探机") && descern.equals("是")) {
                    connect("CFTSYHAVEDESCERN");
                } else if (model.equals("磁探机") && descern.equals("否")) {
                    connect("CFTSYNODESCERN");
                }
            }
        } else {
            Toast.makeText(SendSelectActivity.this, "获取IP失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void haveAddress() {
        disposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        new getIp().getConnectIp(new GetIpCallBack() {
                            @Override
                            public void success(String backAddress) {
                                address = backAddress;
                                url = "http://" + address + ":8080?action=snapshot";
                                if (address != null) {
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
                                                    disposable.dispose();
                                                    mythread.interrupt();
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
                            }

                            @Override
                            public void faile() {
                            }
                        });
                    }
                });
    }

    private void connect(String tag) {
        if (tag.equals("PXQNODESCERN")) {
//            sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "机器人");
            intent = new Intent(SendSelectActivity.this, RobotActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        if (tag.equals("PXQHAVEDESCERN")) {
//            sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "机器人");
            intent = new Intent(SendSelectActivity.this, RobotDescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        if (tag.equals("CFTSYNODESCERN")) {
            intent = new Intent(SendSelectActivity.this, LocalActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        if (tag.equals("CFTSYHAVEDESCERN")) {
            intent = new Intent(SendSelectActivity.this, DescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        address = "";
        timer.cancel();
        loadingDialog.close();
    }

    //获取当前应用的版本号
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    private void upDataClient() {
        if (new NetStat().isNetworkConnected(SendSelectActivity.this)) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("projectName", "济宁鲁科");
            params.put("actionName", "test");
            params.put("appVersion", "1.0.0");
            params.put("channel", "default");
            params.put("appType", "android");
            params.put("clientType", "pxq");
            params.put("phoneSystemVersion", "10.0.1");
            params.put("phoneType", "华为");
            Gson gson = new Gson();
            String s = gson.toJson(params);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(params));
            versionInfoPresenter.getVersionInfo(requestBody);
        } else {
            showDialog();
        }
    }

    @Override
    public void setVersionInfo(VersionInfo versionInfo) {
        String netVersion = versionInfo.getData().getVersion();
        String[] netVersionArray = netVersion.split("\\.");
        String[] localVersionArray = getVersionName().split("\\.");
        for (int i = 0; i < netVersionArray.length; i++) {
            if (Integer.parseInt(netVersionArray[i]) > Integer.parseInt(localVersionArray[i])) {
                dialogUpdate.setMessage("版本号 "
                        + versionInfo.getData().getVersion()
                        + "\n"
                        + versionInfo.getData().getUpdateInfo());
                dialogUpdate.show();
                dialogUpdate.setOnDialogUpdateOkListener(new DialogUpdate.OnDialogUpdateOkListener() {
                    @Override
                    public void onDialogUpdateOk() {
                        new FileDownLoadTask(SendSelectActivity.this, versionInfo.getData().getApkUrl()).execute();
                    }

                    @Override
                    public void onDialogUpdateCancel() {
                        showDialog();
                    }
                });
            } else {
                if (i == netVersionArray.length - 1) {
                    showDialog();
                }
            }
        }
    }

    @Override
    public void setVersionInfoMessage(String message) {
        showDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDialog() {
        alertDialogUtil.showWifiSetting(SendSelectActivity.this, "office", passWord, new DialogCallBack() {
            @Override
            public void confirm(String data, Dialog dialog) {
                //Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                //startActivity(intent);
            }

            @Override
            public void cancel() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void countDown(){
        timer=new Timer();
        timer.schedule(new TimerTask() {
            int i=30;
            @Override
            public void run() {
                //定义一个消息传过去
                Message msg=new Message();
                msg.what=Constant.TAG_TWO;
                msg.obj = i--;
                handler.sendMessage(msg);
            }
        },0,1000); //延时0毫秒开始计时，每隔1秒计时一
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.TAG_ONE:
                    SelectActivity("");
                    break;
                case Constant.TAG_TWO:
                    int second = (int) msg.obj;
                    if (second==0){
                        timer.cancel();
                        Toast.makeText(SendSelectActivity.this, R.string.dialog_close, Toast.LENGTH_SHORT).show();
                        countDown();
                    }
                    break;
            }
        }
    };

}