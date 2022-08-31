package com.example.lksynthesizeapp.Constant.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Bean.Defined;
import com.example.lksynthesizeapp.Constant.Module.DefinedContract;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class DefinedActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, DefinedContract.View  {
    private FrameLayout frameLayout;
    int mScreenWidth;
    int mScreenHeight;
    String tag = "first";
    private RemoteView remoteView;
//    DefinedPresenter definedPresenter;
    final int SCAN_FRAME_SIZE = 240;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_WIFI_STATE};
    SharePreferencesUtils sharePreferencesUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // Bind the camera preview screen.
        sharePreferencesUtils = new SharePreferencesUtils();
//        definedPresenter = new DefinedPresenter(this,this);
        frameLayout = findViewById(R.id.rim);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;
        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build();
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
                if (tag.equals("first")){
                    if (result != null && result.length > 0 && result[0] != null) {
                        if (result[0].getOriginalValue()!=null){
                            String qrData = result[0].getOriginalValue();
                            String data = decodeToString(qrData);
                            Log.e("XXXXXX",data);
                            String[] dataArray = data.split("~~");
                            sharePreferencesUtils.setString(DefinedActivity.this, "max", dataArray[0]);
                            sharePreferencesUtils.setString(DefinedActivity.this, "model", dataArray[3]);
                            sharePreferencesUtils.setString(DefinedActivity.this, "havaCamer", dataArray[4]);
                            sharePreferencesUtils.setString(DefinedActivity.this, "haveDescern", dataArray[5]);
//                            definedPresenter.getDefined(data[0]);
                            startActivity(new Intent(DefinedActivity.this, SendSelectActivity.class));
                            finish();
                            tag = "second";
                            return;
                        }
                    }
                }
            }
        });
    }


    /**
     * 字符Base64加密
     * @param str
     * @return
     */
    public static String encodeToString(String str){
        try {
            return Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 字符Base64解密
     * @param str
     * @return
     */
    public static String decodeToString(String str){
        try {
            return new String(Base64.decode(str.getBytes("UTF-8"), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * short数组转byte数组
     * @param src
     * @return
     */
    public byte[] toByteArray(short[] src) {

        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) ((src[i] >> 8) & 0xFF);
            dest[i * 2 + 1] = (byte) (src[i] & 0xFF);
        }

        return dest;
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
        // 请求权限被拒
        Toast.makeText(DefinedActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
        finish();
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

    @Override
    public void setDefined(Defined defined) {
        if (defined.getResult()==null){
            Toast.makeText(this, "派工单为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String company = defined.getResult().getCompanyName();
        String deviceName = defined.getResult().getDeviceName();
        String deviceCode = defined.getResult().getDeviceCode();
        sharePreferencesUtils.setString(DefinedActivity.this, "company", company);
        sharePreferencesUtils.setString(DefinedActivity.this, "deviceName", deviceName);
        sharePreferencesUtils.setString(DefinedActivity.this, "deviceCode", deviceCode);
        startActivity(new Intent(this, SendSelectActivity.class));
        finish();
    }

    @Override
    public void setDefinedMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}