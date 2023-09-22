package com.example.lksynthesizeapp.Constant.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.lksynthesizeapp.ChiFen.Activity.DescernActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.RobotDescernActivity;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.EditTextLengClient;
import com.example.lksynthesizeapp.Constant.Base.ExitApp;
import com.example.lksynthesizeapp.Constant.Net.BaseDialogProgress;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.message.update.fileview.DialogUpdate;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

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
    private DialogUpdate dialogUpdate;
    BaseDialogProgress dialogProgress;
    private String inTag = "";

    //推出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inTag.equals("button")){
                finish();
                return false;
            }else {
                new ExitApp().exit(alertDialogUtil, SendSelectActivity.this);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        inTag = getIntent().getStringExtra("intag");
        dialogProgress = new BaseDialogProgress(this);
        intance = this;
        loadingDialog = new LoadingDialog(this);
        dialogUpdate = new DialogUpdate(this);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        alertDialogUtil = new AlertDialogUtil(this);
        new EditTextLengClient().textLeng(etProject, this);
        new EditTextLengClient().textLeng(etWorkCode, this);
        new EditTextLengClient().textLeng(etWorkName, this);
        deviceName = sharePreferencesUtils.getString(SendSelectActivity.this, "deviceName", "");
        wifiDialog();
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
            }else if (deviceName.equals("磁探机")) {
                connect("CFTSYHAVEDESCERN");
            }else {
                Intent intent = new Intent(SendSelectActivity.this, DescernActivity.class);
                intent.putExtra("project", etProject.getText().toString().trim());
                intent.putExtra("etWorkName", etWorkName.getText().toString().trim());
                intent.putExtra("etWorkCode", etWorkCode.getText().toString().trim());
                startActivity(intent);
                finish();
            }
//            if (deviceName.equals("")) {
//                alertDialogUtil.showDialog("如想使用后续功能请扫码进入" , new AlertDialogCallBack() {
//                            @Override
//                            public void confirm(String name) {
//                                Intent intent = new Intent(SendSelectActivity.this, DefinedActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                            @Override
//                            public void cancel() {
//
//                            }
//
//                            @Override
//                            public void save(String name) {
//
//                            }
//
//                            @Override
//                            public void checkName(String name) {
//
//                            }
//                        });
//            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private void wifiDialog(){
        if (sharePreferencesUtils.getString(SendSelectActivity.this, "wifiName", "")!=""){
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
    }
}