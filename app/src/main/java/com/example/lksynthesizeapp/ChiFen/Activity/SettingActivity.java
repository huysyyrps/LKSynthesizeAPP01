package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lksynthesizeapp.ChiFen.Base.MobileButlerUtil;
import com.example.lksynthesizeapp.ChiFen.Base.MyCallBack;
import com.example.lksynthesizeapp.ChiFen.Base.RegionalChooseUtil;
import com.example.lksynthesizeapp.ChiFen.Module.VersionInfoContract;
import com.example.lksynthesizeapp.ChiFen.Presenter.VersionInfoPresenter;
import com.example.lksynthesizeapp.ChiFen.bean.Setting;
import com.example.lksynthesizeapp.ChiFen.bean.VersionInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.BaseRecyclerAdapter;
import com.example.lksynthesizeapp.Constant.Base.BaseViewHolder;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Net.SSHCallBack;
import com.example.lksynthesizeapp.Constant.Net.SSHExcuteCommandHelper;
import com.example.lksynthesizeapp.Constant.activity.SendSelectActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.google.gson.Gson;
import com.message.update.fileview.DialogUpdate;
import com.message.update.fileview.FileDownLoadTask;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SettingActivity extends BaseActivity implements VersionInfoContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    String address,tag;
    String toastData = "";
    BaseRecyclerAdapter baseRecyclerAdapter;
    SharePreferencesUtils sharePreferencesUtils;
    List<Setting> settingList = new ArrayList<>();
    LoadingDialog loadingDialog;
    VersionInfoPresenter versionInfoPresenter;
    private DialogUpdate dialogUpdate;
    String apkNETVersion = "";
    String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        dialogUpdate = new DialogUpdate(this);
        versionInfoPresenter = new VersionInfoPresenter(this,this);
        address = getIntent().getStringExtra("address");
        tag = getIntent().getStringExtra("tag");
        //数据组装
        setData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SettingActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        baseRecyclerAdapter = new BaseRecyclerAdapter<Setting>(SettingActivity.this, R.layout.setting_item, settingList) {
            @Override
            public void convert(BaseViewHolder holder, final Setting setting) {
                holder.setText( R.id.tvTitle, setting.getTitle());
                if (setting.getTitle().equals("当前版本")){
                    holder.setText( R.id.tvData, getVersionName());
                    holder.setInImage( R.id.ivGo);
                }
                holder.setImage( SettingActivity.this, R.id.imageView,setting.getImagePath());
                holder.setOnClickListener(R.id.linearLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (setting.getTitle().equals("息屏运行")){
                            requestIgnoreBatteryOptimizations();
                        }
                        if (setting.getTitle().equals("报警音设置")){
                            startActivity(new Intent(SettingActivity.this,AudioActivity.class));
                            finish();
                        }
                        //重启视频服务
                        if (setting.getTitle().equals("服务重启")){
                            ShowDialog("/etc/init.d/mjpg-streamer restart","重启中");
//                            ShowDialog("uci set mjpg-streamer.core.fps=30", "uci commit", "/etc/init.d/mjpg-streamer restart");
                        }

                        //重启设备
                        if (setting.getTitle().equals("设备重启")){
                            new AlertDialogUtil(SettingActivity.this).showDialog("您确定要重启设备吗？", new AlertDialogCallBack() {
                                @Override
                                public void confirm(String name) {
                                    ShowDialog("reboot","设备重启中");
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
//                            ShowDialog("uci set mjpg-streamer.core.fps=30", "uci commit", "/etc/init.d/mjpg-streamer restart");
                        }

                        if (setting.getTitle().equals("帧数设置")){
                            RegionalChooseUtil.initJsonData(SettingActivity.this,"frames");
                            RegionalChooseUtil.showPickerView(SettingActivity.this, new MyCallBack() {
                                @Override
                                public void callBack(Object object) {
                                    ShowDialog("uci set mjpg-streamer.core.fps=" + object.toString(), "uci commit", "/etc/init.d/mjpg-streamer restart");
                                    new SharePreferencesUtils().setString(SettingActivity.this, "frames", object.toString());
                                }
                            });
                        }

                        if (setting.getTitle().equals("像素设置")){
                            RegionalChooseUtil.initJsonData(SettingActivity.this,"resolving");
                            RegionalChooseUtil.showPickerView(SettingActivity.this, new MyCallBack() {
                                @Override
                                public void callBack(Object object) {
                                    ShowDialog("uci set mjpg-streamer.core.resolution=" + object.toString(), "uci commit", "/etc/init.d/mjpg-streamer restart");
                                    new SharePreferencesUtils().setString(SettingActivity.this, "resolving", object.toString());
                                }
                            });
                        }

                        if (setting.getTitle().equals("版本检测")){
                            upDataClient();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(baseRecyclerAdapter);
        loadingDialog = new LoadingDialog(this);
    }

    private void upDataClient() {
        if (new NetStat().isNetworkConnected(SettingActivity.this)) {
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
            Toast.makeText(this, getResources().getString(R.string.change_net), Toast.LENGTH_SHORT).show();
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

    private void setData() {
        Setting setting = new Setting();
        setting.setTitle("当前版本");
        setting.setImagePath(R.drawable.ic_appversion);
        settingList.add(setting);

        Setting setting7 = new Setting();
        setting7.setTitle("版本检测");
        setting7.setData("");
        setting7.setImagePath(R.drawable.ic_version);
        settingList.add(setting7);


        if (tag.equals("desc")){
            Setting setting1 = new Setting();
            setting1.setTitle("报警音设置");
            setting1.setImagePath(R.drawable.ic_audio);
            settingList.add(setting1);
        }

        Setting setting2 = new Setting();
        setting2.setTitle("服务重启");
        setting2.setImagePath(R.drawable.ic_restart);
        settingList.add(setting2);

        Setting setting3 = new Setting();
        setting3.setTitle("设备重启");
        setting3.setImagePath(R.drawable.ic_prorestart);
        settingList.add(setting3);

        Setting setting4 = new Setting();
        setting4.setTitle("帧数设置");
        setting4.setImagePath(R.drawable.ic_fps);
        settingList.add(setting4);

        Setting setting5 = new Setting();
        setting5.setTitle("像素设置");
        setting5.setImagePath(R.drawable.ic_pixel);
        settingList.add(setting5);

        Setting setting6 = new Setting();
        setting6.setTitle("息屏运行");
        setting6.setImagePath(R.drawable.ic_service);
        settingList.add(setting6);

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {

    }

    /**
     * 重启服务刷新视频
     *
     * @param data1
     */
    private void ShowDialog(String data1, String title) {
        try {
            loadingDialog.setLoadingText(getResources().getString(R.string.device_setting))
//                        .setSuccessText("加载成功")//显示加载成功时的文字
                    //.setFailedText("加载失败")
                    .setSize(200)
                    .setShowTime(1)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                    .setRepeatCount(1)
                    .show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SSHExcuteCommandHelper.writeBefor(address, data1, new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            if (title.equals("设备重启中")){
                                handlerSetting.sendEmptyMessage(Constant.TAG_THERE);
                            }else {
                                handlerSetting.sendEmptyMessage(Constant.TAG_ONE);
                            }
                        }

                        @Override
                        public void error(String s) {
                            toastData = s;
                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //设置帧数  像素
    private void ShowDialog(String data1, String data2, String data3) {
        try {
            loadingDialog.setLoadingText(getResources().getString(R.string.device_setting))
//                        .setSuccessText("加载成功")//显示加载成功时的文字
                    //.setFailedText("加载失败")
                    .setSize(200)
                    .setShowTime(1)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                    .setRepeatCount(1)
                    .show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SSHExcuteCommandHelper.writeBefor(address, data1, new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            SSHExcuteCommandHelper.writeBefor(address, data2, new SSHCallBack() {
                                @Override
                                public void confirm(String data) {
                                    SSHExcuteCommandHelper.writeBefor(address, data3, new SSHCallBack() {
                                        @Override
                                        public void confirm(String data) {
                                            handlerSetting.sendEmptyMessage(Constant.TAG_ONE);
                                        }

                                        @Override
                                        public void error(String s) {
                                            toastData = s;
                                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                        }
                                    });
                                }

                                @Override
                                public void error(String s) {
                                    toastData = s;
                                    handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                }
                            });
                        }

                        @Override
                        public void error(String s) {
                            toastData = s;
                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断是否再白名单
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    //如果不在白名单中，可以通过以下代码申请加入白名单
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,Constant.TAG_THERE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.TAG_THERE) {
            if(isIgnoringBatteryOptimizations()){
                Toast.makeText(this, "已开启", Toast.LENGTH_SHORT).show();
                new AlertDialogUtil(SettingActivity.this).showDialog("为了您正常使用此程序，请您 "
                        + "\n"
                        + "选择->鲁科检测系统->启动管理->关闭自动管理->开启允许自启动和允许后台活动。", new AlertDialogCallBack() {
                    @Override
                    public void confirm(String name) {
                        Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                        ResolveInfo resolveInfo = getPackageManager().resolveActivity(powerUsageIntent, 0);
// check that the Battery app exists on this device
                        if(resolveInfo != null){
                            startActivity(powerUsageIntent);
                        }
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

    public void checkPermission(){
        MobileButlerUtil mobileButlerUtil = new MobileButlerUtil(this);
        mobileButlerUtil.goHuaweiSetting();
        new SharePreferencesUtils().setString(this,"keep","true");
    }

    Handler handlerSetting = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TAG_ONE:
                    Toast.makeText(SettingActivity.this, "重启成功", Toast.LENGTH_SHORT).show();
                    loadingDialog.close();
                    finish();
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(SettingActivity.this, toastData, Toast.LENGTH_LONG).show();
                    loadingDialog.close();
                    break;
                case Constant.TAG_THERE:
                    new DescernActivity().intance.finish();
                    new SendSelectActivity().intance.finish();
                    System.exit(0);
                    break;
            }
        }
    };

    @Override
    public void setVersionInfo(VersionInfo versionInfo) throws Exception {
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
                        new FileDownLoadTask(SettingActivity.this, versionInfo.getData().getApkUrl()).execute();
                    }

                    @Override
                    public void onDialogUpdateCancel() {
                    }
                });
            } else {
                if (i == netVersionArray.length - 1) {
                    Toast.makeText(this, getResources().getString(R.string.bast_version), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void setVersionInfoMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}