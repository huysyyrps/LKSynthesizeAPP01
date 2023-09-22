package com.example.lksynthesizeapp.Constant.activity;

import static com.huawei.hms.hmsscankit.RemoteView.REQUEST_CODE_PHOTO;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.lksynthesizeapp.ChiFen.Activity.PhotoActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.VideoActivity;
import com.example.lksynthesizeapp.ChiFen.Module.VersionInfoContract;
import com.example.lksynthesizeapp.ChiFen.Presenter.VersionInfoPresenter;
import com.example.lksynthesizeapp.ChiFen.bean.VersionInfo;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.ProgressDialogUtil;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.example.lksynthesizeapp.View.BaseButton;
import com.example.lksynthesizeapp.View.BaseLinlayout;
import com.google.gson.Gson;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import constant.UiType;
import listener.OnInitUiListener;
import model.UiConfig;
import model.UpdateConfig;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import update.UpdateAppUtils;

public class DefinedActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, VersionInfoContract.View {
    @BindView(R.id.rbLightClose)
    RadioButton rbLightClose;
    @BindView(R.id.rbLightOpen)
    RadioButton rbLightOpen;
    @BindView(R.id.rbPhoto)
    RadioButton rbPhoto;
    @BindView(R.id.tvAlbum)
    LinearLayout tvAlbum;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.linImageList)
    BaseLinlayout linImageList;
    @BindView(R.id.linVideoList)
    BaseLinlayout linVideoList;
    @BindView(R.id.btnFinish)
    BaseButton btnFinish;
    @BindView(R.id.linInProject)
    BaseLinlayout linInProject;
    @BindView(R.id.tvCurrentVersion)
    TextView tvCurrentVersion;
    @BindView(R.id.linVersionCheck)
    BaseLinlayout linVersionCheck;
    @BindView(R.id.scan_area)
    ImageView scanArea;
    private FrameLayout frameLayout;
    int mScreenWidth;
    int mScreenHeight;
    String tag = "first";
    private RemoteView remoteView;
    final int SCAN_FRAME_SIZE = 240;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
    SharePreferencesUtils sharePreferencesUtils;
    private int[] img = {R.drawable.ic_light_close, R.drawable.ic_light_open};
    private AlertDialogUtil alertDialogUtil;
    VersionInfoPresenter versionInfoPresenter;
    private Bundle savedInstanceState;
    private boolean isBaseVersion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        ButterKnife.bind(this);
        // Bind the camera preview screen.
//        new ConnectionManager(DefinedActivity.this).openWithWAP();

        tvCurrentVersion.setText("V"+getVersionName());
        versionInfoPresenter = new VersionInfoPresenter(this, this);
//        ProgressDialogUtil.startLoad(this, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                upDataTag();
            }
        }).start();
        remoteSetting(savedInstanceState);
    }

    private void upDataTag() {
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
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_defined;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {
//        new MainUI().showPopupMenuMain(tvAlbum, "Desc", DefinedActivity.this);
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

    // 实现“onRequestPermissionsResult”函数接收校验权限结果。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将结果转发给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // 授予权限后操作
        remoteView.onStart();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        new AlertDialogUtil(DefinedActivity.this).showDialog("为了您正常使用此程序，请您 "
                + "\n"
                + "到设置界面手动开启程序所需权限。", new AlertDialogCallBack() {
            @Override
            public void confirm(String name) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + DefinedActivity.this.getPackageName()));
                startActivityForResult(intent, Constant.TAG_ONE);
                finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        remoteSetting(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        upDataTag();
//        remoteView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (remoteView != null) {
            remoteView.onDestroy();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (remoteView != null) {
            remoteView.onStop();
        }
    }

    @OnClick({R.id.rbLightClose, R.id.rbLightOpen, R.id.rbPhoto, R.id.tvAlbum, R.id.linImageList,
            R.id.linVideoList,  R.id.btnFinish, R.id.linInProject, R.id.linVersionCheck})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbLightClose:
                if (!remoteView.getLightStatus()) {
                    remoteView.switchLight();
                    rbLightClose.setVisibility(View.GONE);
                    rbLightOpen.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rbLightOpen:
                if (remoteView.getLightStatus()) {
                    remoteView.switchLight();
                    rbLightClose.setVisibility(View.VISIBLE);
                    rbLightOpen.setVisibility(View.GONE);
                }
                break;
            case R.id.rbPhoto:
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                DefinedActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
                break;
            case R.id.tvAlbum:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
            case R.id.linImageList:
                Intent intent = new Intent(this, PhotoActivity.class);
                intent.putExtra("tag", "Desc");
                startActivity(intent);
                break;
            case R.id.linVideoList:
                Intent intent1 = new Intent(this, VideoActivity.class);
                intent1.putExtra("tag", "Desc");
                startActivity(intent1);
                break;
            case R.id.linInProject:
                SharePreferencesUtils sharePreferencesUtils = new SharePreferencesUtils();
                sharePreferencesUtils.setString(this, "max", "");
                sharePreferencesUtils.setString(this, "deviceCode", "");
                sharePreferencesUtils.setString(this, "deviceName", "");
                sharePreferencesUtils.setString(this, "deviceModel", "");
                sharePreferencesUtils.setString(this, "wifiName", "");
                sharePreferencesUtils.setString(this, "haveDescern", "");
                Intent intent2 = new Intent(this, SendSelectActivity.class);
                intent2.putExtra("tag", "Desc");
                intent2.putExtra("intag", "button");
                startActivity(intent2);
                break;
            case R.id.btnFinish:
                finish();
                break;
            case R.id.linVersionCheck:
                isBaseVersion = true;
                scanArea.setVisibility(View.GONE);
                drawer_layout.closeDrawers();
                ProgressDialogUtil.startLoad(this,"");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        upDataTag();
                    }
                }).start();

                break;
        }
    }

    /**
     * Handle the return results from the album.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataBack) {
        super.onActivityResult(requestCode, resultCode, dataBack);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dataBack.getData());
                HmsScan[] result = ScanUtil.decodeWithBitmap(DefinedActivity.this, bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
                // 处理扫码结果
                if (result != null && result.length > 0) {
                    if (result != null && result.length > 0 && result[0] != null) {
                        if (result[0].getOriginalValue() != null) {
                            String qrData = result[0].getOriginalValue();
//                            String data = decodeToString(qrData);
                            if (qrData.contains("/")) {
                                Log.e("XXXXXX", qrData);
                                String[] dataArray = qrData.split("/");
                                sharePreferencesUtils.setString(DefinedActivity.this, "max", dataArray[0]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "deviceCode", dataArray[1]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "deviceName", dataArray[2]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "deviceModel", dataArray[3]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "wifiName", dataArray[4]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "haveDescern", dataArray[5]);
//                            definedPresenter.getDefined(data[0]);
                                Intent intent = new Intent(this, SendSelectActivity.class);
                                intent.putExtra("tag", "Desc");
                                intent.putExtra("intag", "");
                                startActivity(intent);
                                finish();
                                tag = "second";
                                return;
                            } else {
                                Toast.makeText(DefinedActivity.this, "二维码数据错误", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void setVersionInfo(VersionInfo versionInfo) throws Exception {
        ProgressDialogUtil.stopLoad();
        scanArea.setVisibility(View.VISIBLE);
        String netVersion = versionInfo.getData().getVersion();
        String[] netVersionArray = netVersion.split("\\.");
        String[] localVersionArray = getVersionName().split("\\.");

        for (int i = 0; i < netVersionArray.length; i++) {
            if (Integer.parseInt(netVersionArray[i]) > Integer.parseInt(localVersionArray[i])) {
                if (versionInfo.getData().getUpdateFlag() == 0) {
                    //无需SSH升级,APK需要升级时值为0
                    showUpDataDialog(versionInfo, 0);
                    return;
                } else if (versionInfo.getData().getUpdateFlag() == 1) {
                    //SSH需要升级APK不需要升级
                    showUpDataDialog(versionInfo, 1);
                    return;
//                    downloadSSHFile(versionInfo,0);
                } else if (versionInfo.getData().getUpdateFlag() == 2) {
                    showUpDataDialog(versionInfo, 2);
                    return;
//                    downloadSSHFile(versionInfo,1);
                } else {
                    remoteSetting(this.savedInstanceState);
                }
            }
        }
//        remoteSetting(this.savedInstanceState);
        if (isBaseVersion){
            Toast.makeText(this, "已是最新版本 V"+netVersion, Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpDataDialog(VersionInfo versionInfo, int i) {
        //UI配置信息
        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setAlwaysShowDownLoadDialog(true);
        UiConfig uiConfig = new UiConfig();
        uiConfig.setUiType(UiType.CUSTOM);
        uiConfig.setCustomLayoutId(R.layout.view_update_dialog_custom);
        String updateInfo = versionInfo.getData().getUpdateInfo();

        String[] updataItem = updateInfo.split("~");
        String updateInfo1 = "";
        if (updataItem != null && updataItem.length > 0) {
            for (int j = 0; j < updataItem.length; j++) {
                updateInfo1 = updateInfo1 + updataItem[j] + "\n";
            }
        }

        UpdateAppUtils
                .getInstance()
                .apkUrl(versionInfo.getData().getApkUrl())
                .updateTitle("发现新版本V" + versionInfo.getData().getVersion())
                .updateContent(updateInfo1)
                .uiConfig(uiConfig)
                .updateConfig(updateConfig)
                .setOnInitUiListener(new OnInitUiListener() {
                    @Override
                    public void onInitUpdateUi(View view, UpdateConfig updateConfig, UiConfig uiConfig) {
                        TextView tvTitle = view.findViewById(R.id.tv_update_title);
                        tvTitle.setText("版本更新啦");
                        TextView tvHeaderVersion = view.findViewById(R.id.tv_version_name);
                        tvHeaderVersion.setText("V" + versionInfo.getData().getVersion());
                    }

                })
//                .setCancelBtnClickListener(new OnBtnClickListener() {
//                    @Override
//                    public boolean onClick() {
//                        remoteSetting(savedInstanceState);
//                        return false;
//                    }
//                })
                .update();
    }


    @Override
    public void setVersionInfoMessage(String message) {
        Log.e("TAG", message);
        scanArea.setVisibility(View.VISIBLE);
        ProgressDialogUtil.stopLoad();
        remoteSetting(savedInstanceState);
    }

    private void remoteSetting(Bundle savedInstanceState) {
        alertDialogUtil = new AlertDialogUtil(this);
        sharePreferencesUtils = new SharePreferencesUtils();
        frameLayout = findViewById(R.id.rim);
        //设置扫码识别区域，您可以按照需求调整参数
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        //当前demo扫码框的宽高是300dp
        final int SCAN_FRAME_SIZE = 300;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;
        //初始化RemoteView，并通过如下方法设置参数:setContext()（必选）传入context、setBoundingBox()设置扫描区域、setFormat()设置识别码制式，设置完毕调用build()方法完成创建
        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).build();
        remoteView.onCreate(this.savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);
        //请求相机权限
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            // 已经申请过权限，做想做的事
            remoteView.onStart();
        } else {
            // 没有申请过权限，现在去申请
            /**
             *@param host Context对象
             *@param rationale  权限弹窗上的提示语。
             *@param requestCode 请求权限的唯一标识码
             *@param perms 一系列权限
             */
            EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
        }

        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                if (tag.equals("first")) {
                    if (result != null && result.length > 0 && result[0] != null) {
                        if (result[0].getOriginalValue() != null) {
                            String qrData = result[0].getOriginalValue();
//                            String data = decodeToString(qrData);
                            if (qrData.contains("/")) {
                                Log.e("DefinedActivity", qrData);
                                String[] dataArray = qrData.split("/");
                                sharePreferencesUtils.setString(DefinedActivity.this, "max", dataArray[0]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "deviceCode", dataArray[1]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "deviceName", dataArray[2]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "deviceModel", dataArray[3]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "wifiName", dataArray[4]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "haveDescern", dataArray[5]);
                                Intent intent2 = new Intent(DefinedActivity.this, SendSelectActivity.class);
                                intent2.putExtra("tag", "Desc");
                                intent2.putExtra("intag", "button");
                                startActivity(intent2);
                                tag = "second";
                                finish();
                                return;
                            } else {
                                Toast.makeText(DefinedActivity.this, "二维码数据错误", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            }
        });
    }

}