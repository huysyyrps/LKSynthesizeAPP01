package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Base.MobileButlerUtil;
import com.example.lksynthesizeapp.SharePreferencesUtils;

public class KeepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestIgnoreBatteryOptimizations();
//        checkPermission();//设置允许后台活动
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
            startActivityForResult(intent,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(isIgnoringBatteryOptimizations()){
                Toast.makeText(this, "已开启", Toast.LENGTH_SHORT).show();
//                checkPermission();
            }
        }
    }

    public void checkPermission(){
        MobileButlerUtil mobileButlerUtil = new MobileButlerUtil(this);
        mobileButlerUtil.goHuaweiSetting();
        new SharePreferencesUtils().setString(this,"keep","true");
    }

}