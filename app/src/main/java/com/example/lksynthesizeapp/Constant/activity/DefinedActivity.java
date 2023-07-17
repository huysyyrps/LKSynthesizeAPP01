package com.example.lksynthesizeapp.Constant.activity;

import static com.huawei.hms.hmsscankit.RemoteView.REQUEST_CODE_PHOTO;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.wifi.deal.ConnectionManager;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class DefinedActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.rbLightClose)
    RadioButton rbLightClose;
    @BindView(R.id.rbLightOpen)
    RadioButton rbLightOpen;
    @BindView(R.id.rbPhoto)
    RadioButton rbPhoto;
    @BindView(R.id.tvAlbum)
    LinearLayout tvAlbum;
    private FrameLayout frameLayout;
    int mScreenWidth;
    int mScreenHeight;
    String tag = "first";
    private RemoteView remoteView;
    final int SCAN_FRAME_SIZE = 240;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_COARSE_LOCATION};
    SharePreferencesUtils sharePreferencesUtils;
    private int[] img = {R.drawable.ic_light_close, R.drawable.ic_light_open};
    private AlertDialogUtil alertDialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // Bind the camera preview screen.
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
        remoteView.onCreate(savedInstanceState);
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
                                startActivity(new Intent(DefinedActivity.this, SendSelectActivity.class));
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

        new ConnectionManager(DefinedActivity.this).openWithWAP();
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
        new MainUI().showPopupMenuMain(tvAlbum, "Desc", DefinedActivity.this);
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
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + DefinedActivity.this.getPackageName()));
                startActivityForResult(intent,Constant.TAG_ONE);
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

    /**
     * Call the lifecycle management method of the remoteView activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    @OnClick({R.id.rbLightClose, R.id.rbLightOpen, R.id.rbPhoto})
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
                                startActivity(new Intent(DefinedActivity.this, SendSelectActivity.class));
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

}