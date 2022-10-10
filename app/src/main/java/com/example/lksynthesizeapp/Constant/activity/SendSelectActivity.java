package com.example.lksynthesizeapp.Constant.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.lksynthesizeapp.Constant.Base.DialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.EditTextLengClient;
import com.example.lksynthesizeapp.Constant.Base.ExitApp;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Base.ProgressDialogUtil;
import com.example.lksynthesizeapp.Constant.Net.GetIpCallBack;
import com.example.lksynthesizeapp.Constant.Net.getIp;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.google.gson.Gson;
import com.message.update.fileview.DialogUpdate;
import com.message.update.fileview.FileDownLoadTask;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private String address = "";
    private static AlertDialogUtil alertDialogUtil;
    SharePreferencesUtils sharePreferencesUtils;
    MediaProjectionManager projectionManager;
    private WifiManager mWifiManager;
    private String sid = "", pwd = "", max = "";
    Handler handler = new Handler();
    Runnable runnable;
    String Max, model, camer, descern;
    VersionInfoPresenter versionInfoPresenter;
    private DialogUpdate dialogUpdate;
    String passWord;

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
        dialogUpdate = new DialogUpdate(this);
        versionInfoPresenter = new VersionInfoPresenter(this,this);
        Max =   sharePreferencesUtils.getString(SendSelectActivity.this, "max", "");
        model =  sharePreferencesUtils.getString(SendSelectActivity.this, "model", "");
        camer = sharePreferencesUtils.getString(SendSelectActivity.this, "havaCamer", "");
        descern =  sharePreferencesUtils.getString(SendSelectActivity.this, "haveDescern", "");
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
    }

    @OnClick({R.id.tvConfim})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfim:
                seSPData();
                SelectActivity("");
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
            }else if (etProject.getText().toString().trim().equals("")) {
                Toast.makeText(SendSelectActivity.this, "请输入工程名称", Toast.LENGTH_SHORT).show();
                return;
            } else if (etWorkName.getText().toString().trim().equals("")) {
                Toast.makeText(SendSelectActivity.this, "请输入工件名称", Toast.LENGTH_SHORT).show();
                return;
            } else if (etWorkCode.getText().toString().trim().equals("")) {
                Toast.makeText(SendSelectActivity.this, "请输入工件编号", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (model.equals("爬行器")&&descern.equals("否")) {
                    haveAddress("PXQNODESCERN");
                } else if (model.equals("爬行器")&&descern.equals("是")) {
                    haveAddress("PXQHAVEDESCERN");
                } else if (model.equals("磁探机")&&descern.equals("是")) {
                    haveAddress("CFTSYHAVEDESCERN");
                }else if (model.equals("磁探机")&&descern.equals("否")) {
                    haveAddress("CFTSYNODESCERN");
                }
            }
        } else {
            Toast.makeText(SendSelectActivity.this, "获取IP失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void haveAddress(String tag) {
        if (address == null || address.equals("")) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    new getIp().getConnectIp(new GetIpCallBack() {
                        @Override
                        public void success(String backAddress) {
                            address = backAddress;
                            Log.e("ip",address);
                            if (handler != null) {
                                connect(tag);
                            }
                        }

                        @Override
                        public void faile() {

                        }
                    });
                    if (handler!=null){
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
            ProgressDialogUtil.startLoad(this, "设备连接中");
        } else {
            connect(tag);
        }
    }

    private void connect(String tag) {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        ProgressDialogUtil.stopLoad();
        if (tag.equals("PXQNODESCERN")){
//            sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "机器人");
            intent = new Intent(SendSelectActivity.this, RobotActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        if (tag.equals("PXQHAVEDESCERN")){
//            sharePreferencesUtils.setString(SendSelectActivity.this, "sendSelect", "机器人");
            intent = new Intent(SendSelectActivity.this, RobotDescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        if (tag.equals("CFTSYNODESCERN")){
            intent = new Intent(SendSelectActivity.this, LocalActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }
        if (tag.equals("CFTSYHAVEDESCERN")){
            intent = new Intent(SendSelectActivity.this, DescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
            intent.putExtra("address", address + "");
            startActivity(intent);
        }

    }

    //获取当前应用的版本号
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    private void upDataClient() {
        if (new NetStat().isNetworkConnected(SendSelectActivity.this)){
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
        }else {
            showDialog();
        }
    }

    @Override
    public void setVersionInfo(VersionInfo versionInfo) {
        String netVersion = versionInfo.getData().getVersion();
        String[] netVersionArray = netVersion.split("\\.");
        String[] localVersionArray = getVersionName().split("\\.");
        for (int i = 0;i<netVersionArray.length;i++){
            if (Integer.parseInt(netVersionArray[i])>Integer.parseInt(localVersionArray[i])){
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
            }else {
                if (i == netVersionArray.length-1){
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

    private void showDialog(){
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
}