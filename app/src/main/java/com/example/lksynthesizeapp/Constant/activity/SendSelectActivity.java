package com.example.lksynthesizeapp.Constant.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.lksynthesizeapp.ApiAddress;
import com.example.lksynthesizeapp.ChiFen.Activity.DescernActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.RobotDescernActivity;
import com.example.lksynthesizeapp.ChiFen.Module.VersionInfoContract;
import com.example.lksynthesizeapp.ChiFen.Presenter.VersionInfoPresenter;
import com.example.lksynthesizeapp.ChiFen.bean.VersionInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.EditTextLengClient;
import com.example.lksynthesizeapp.Constant.Base.ExitApp;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Net.BaseDialogProgress;
import com.example.lksynthesizeapp.Constant.Net.DownloadUtil;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.google.gson.Gson;
import com.message.update.fileview.DialogUpdate;
import com.message.update.fileview.FileDownLoadTask;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
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
    //    @BindView(R.id.spinner)
//    Spinner spinner;
    //富有动感的Sheet弹窗
    Intent intent;
    @BindView(R.id.header)
    Header header;
    private static AlertDialogUtil alertDialogUtil;
    SharePreferencesUtils sharePreferencesUtils;
    MediaProjectionManager projectionManager;
    public static SendSelectActivity intance = null;
//    private Thread mythread;
//    private String url;
    Disposable disposable;
    LoadingDialog loadingDialog;
    String deviceName;
    VersionInfoPresenter versionInfoPresenter;
    private DialogUpdate dialogUpdate;
    BaseDialogProgress dialogProgress;

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
        dialogProgress = new BaseDialogProgress(this);
        intance = this;
        loadingDialog = new LoadingDialog(this);
        dialogUpdate = new DialogUpdate(this);
        versionInfoPresenter = new VersionInfoPresenter(this, this);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        alertDialogUtil = new AlertDialogUtil(this);
        new EditTextLengClient().textLeng(etProject, this);
        new EditTextLengClient().textLeng(etWorkCode, this);
        new EditTextLengClient().textLeng(etWorkName, this);
        deviceName = sharePreferencesUtils.getString(SendSelectActivity.this, "deviceName", "");
        upDataClient();
    }

    private void upDataClient() {
        if (new NetStat().isNetworkConnected(SendSelectActivity.this)) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("projectName", "济宁鲁科");
            params.put("actionName", "鲁科智能检测系统");
            params.put("appVersion", getVersionName());
            params.put("channel", "default");
            params.put("appType", "android");
            params.put("clientType", "磁探机");
            params.put("phoneSystemVersion", "10.0.1");
            params.put("phoneType", "华为");
            Gson gson = new Gson();
            String s = gson.toJson(params);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(params));
            versionInfoPresenter.getVersionInfo(requestBody);
        }else {
            wifiDialog();
        }
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

    @OnClick({R.id.tvConfim})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfim:
//                haveAddress();
//                loadingDialog.setLoadingText(getResources().getString(R.string.device_connect))
////                        .setSuccessText("加载成功")//显示加载成功时的文字
//                        //.setFailedText("加载失败")
//                        .setSize(200)
//                        .setShowTime(1)
//                        .setInterceptBack(false)
//                        .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
//                        .setRepeatCount(1)
//                        .show();
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
//    private void haveAddress() {
//        disposable = Observable.interval(0, 3, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        url = "http://" + Constant.URL + ":8080?action=snapshot";
//                        mythread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputStream inputstream = null;
//                                //创建一个URL对象
//                                URL videoUrl = null;
//                                try {
//                                    videoUrl = new URL(url);
//                                    //利用HttpURLConnection对象从网络中获取网页数据
//                                    HttpURLConnection conn = (HttpURLConnection) videoUrl.openConnection();
//                                    //设置输入流
//                                    conn.setDoInput(true);
//                                    conn.setConnectTimeout(5 * 1000);
//                                    //连接
//                                    conn.connect();
//                                    //得到网络返回的输入流
//                                    inputstream = conn.getInputStream();
//                                    //创建出一个bitmap
//                                    Bitmap bmp = BitmapFactory.decodeStream(inputstream);
//                                    if (bmp != null && !bmp.equals("")) {
//                                        Message message = new Message();
//                                        message.what = Constant.TAG_ONE;
//                                        handler.sendMessage(message);
//                                    }
//                                } catch (MalformedURLException e) {
//                                    e.printStackTrace();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        mythread.start();
//                    }
//                });
//    }

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
            if (deviceName.equals("机器人")) {
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
//            intent.putExtra("selectMode",selectMode);
            startActivity(intent);
        }
        if (tag.equals("CFTSYHAVEDESCERN")) {
            intent = new Intent(SendSelectActivity.this, DescernActivity.class);
            intent.putExtra("project", etProject.getText().toString().trim());
            intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
            intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
//            intent.putExtra("selectMode",selectMode);
            startActivity(intent);
        }
        loadingDialog.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setVersionInfo(VersionInfo versionInfo) throws Exception {
        String netVersion = versionInfo.getData().getVersion();
        String[] netVersionArray = netVersion.split("\\.");
        String[] localVersionArray = getVersionName().split("\\.");

        for (int i = 0; i < netVersionArray.length; i++) {
            if (Integer.parseInt(netVersionArray[i]) > Integer.parseInt(localVersionArray[i])) {
                if (versionInfo.getData().getUpdateFlag() == 0) {
                    //无需SSH升级,APK需要升级时值为0
                    showDialog(versionInfo, 0);
                    break;
                } else if (versionInfo.getData().getUpdateFlag() == 1) {
                    //SSH需要升级APK不需要升级
                    showDialog(versionInfo, 1);
                    break;
//                    downloadSSHFile(versionInfo,0);
                } else if (versionInfo.getData().getUpdateFlag() == 2) {
                    showDialog(versionInfo, 2);
                    break;
//                    downloadSSHFile(versionInfo,1);
                }
            }
        }
        if ( Arrays.equals(netVersionArray,localVersionArray)){
            wifiDialog();
        }
    }

    @Override
    public void setVersionInfoMessage(String message) {
        Log.e("TAG", message);
        wifiDialog();
    }

    private void wifiDialog(){
        alertDialogUtil.showDialog("请链接网络" + sharePreferencesUtils.getString(SendSelectActivity.this, "wifiName", ""),
                new AlertDialogCallBack() {
                    @Override
                    public void confirm(String name) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void save(String name) {

                    }

                    @Override
                    public void checkName(String name) {

                    }
                });
    }


    private void showDialog(VersionInfo versionInfo, int tag) {
        dialogUpdate.setMessage("版本号 "
                + versionInfo.getData().getVersion()
                + "\n"
                + versionInfo.getData().getUpdateInfo());
        dialogUpdate.show();
        dialogUpdate.setOnDialogUpdateOkListener(new DialogUpdate.OnDialogUpdateOkListener() {
            @Override
            public void onDialogUpdateOk() {
                if (tag == 0) {
                    new FileDownLoadTask(SendSelectActivity.this, versionInfo.getData().getApkUrl()).execute();
                } else if (tag == 1) {
                    downloadSSHFile(versionInfo, 0);
                } else if (tag == 2) {
                    downloadSSHFile(versionInfo, 1);
                }

            }

            @Override
            public void onDialogUpdateCancel() {
                Log.e("XXXXXXXXXX", "XXXXXXXXXXXXXXXXXXXXXXX");
            }
        });
    }

    private void downloadSSHFile(VersionInfo versionInfo, int tag) {
        String url = ApiAddress.api + "app_api/files/luke-ssh.bin";
        dialogProgress.show();
        dialogProgress.setFileSize(versionInfo.getData().getApkSize() + "MB");
        DownloadUtil.get().download(url, "LUKESSH", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                //成功
                Log.i("注意", "下载成功");
                if (tag == 0) {
                    dialogProgress.dismiss();
                } else if (tag == 1) {
                    dialogProgress.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new FileDownLoadTask(SendSelectActivity.this, versionInfo.getData().getApkUrl()).execute();
                        }
                    });


                }

            }

            @Override
            public void onDownloading(int progress) {
                //进度
                Log.i("注意", progress + "%");
                dialogProgress.setProgress(progress);
            }

            @Override
            public void onDownloadFailed() {
                //失败
                Log.i("注意", "下载失败");
                dialogProgress.dismiss();
            }
        });
    }
}