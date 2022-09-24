package com.example.lksynthesizeapp.ChiFen.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.BuildConfig;
import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.ChiFen.Base.MainUI;
import com.example.lksynthesizeapp.ChiFen.Base.MyMediaRecorder;
import com.example.lksynthesizeapp.ChiFen.Base.TirenSet;
import com.example.lksynthesizeapp.ChiFen.Media.Notifications;
import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.webView)
    MyWebView webView;
    @BindView(R.id.rbCamera)
    RadioButton rbCamera;
    @BindView(R.id.rbSound)
    RadioButton rbSound;
    @BindView(R.id.rbAlbum)
    RadioButton rbAlbum;
    @BindView(R.id.rbRefresh)
    RadioButton rbRefresh;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.tvCompName)
    TextView tvCompName;
    @BindView(R.id.tvWorkName)
    TextView tvWorkName;
    @BindView(R.id.tvWorkCode)
    TextView tvWorkCode;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    @BindView(R.id.linearLayoutStop)
    LinearLayout linearLayoutStop;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.linlayoutData)
    LinearLayout linlayoutData;
    @BindView(R.id.rbBack)
    RadioButton rbBack;

    private Toast toast;
    String address;
    private VirtualDisplay mVirtualDisplay;
    private Notifications mNotifications;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private String project = "", workName = "", workCode = "";
    public static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".action.STOP";
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE};
    String SelectTag = "";
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_local);
        ButterKnife.bind(this);

        setWorkData();

        Display display = getWindowManager().getDefaultDisplay();
        Log.e("XXXXX", "width-display :" + display.getWidth() + "heigth-display :" + display.getHeight());

        frameLayout.setBackgroundColor(getResources().getColor(R.color.black));
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mNotifications = new Notifications(getApplicationContext());
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (project.trim().equals("") && workName.trim().equals("") && workCode.trim().equals("")) {
            radioGroup.setVisibility(View.GONE);
        }
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }

        WebSettings WebSet = webView.getSettings();    //获取webview设置
        WebSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);   //自适应屏幕
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setBackgroundColor(getColor(R.color.black));

        address = getIntent().getStringExtra("address");
        if (address != null) {
            address = "http://" + address + ":8080";
            webView.loadUrl(address);
        } else {
            Toast.makeText(mNotifications, "IP为空,请等待连接", Toast.LENGTH_SHORT).show();
            finish();
        }
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    private void setWorkData() {
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (project.trim().equals("") && workName.trim().equals("") && workCode.trim().equals("")) {
            linlayoutData.setVisibility(View.GONE);
        }
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }
    }

    @OnClick({R.id.rbCamera, R.id.rbSound, R.id.rbAlbum, R.id.rbRefresh, R.id.linearLayoutStop, R.id.rbBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCamera:
                if (toast != null) {
                    toast.cancel();
                }
                radioGroup.setVisibility(View.GONE);
                View view1 = frameLayout;
                view1.setDrawingCacheEnabled(true);
                view1.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
                if (bitmap != null) {
                    boolean backstate = new ImageSave().saveBitmap("/LUKEImage/", project, workName, workCode, this, bitmap);
                    if (backstate) {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(LocalActivity.this, R.string.save_success, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存成功");
                    } else {
                        radioGroup.setVisibility(View.VISIBLE);
                        toast = Toast.makeText(LocalActivity.this, R.string.save_faile, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("XXX", "保存失败");
                    }
                } else {
                    System.out.println("bitmap is NULL!");
                }
                break;
            case R.id.rbSound:
                if (toast!=null){
                    toast.cancel();
                }
                radioGroup.setVisibility(View.GONE);
                linearLayoutStop.setVisibility(View.VISIBLE);
                if (mMediaProjection == null) {
                    requestMediaProjection();
                } else {
                    if (EasyPermissions.hasPermissions(this, PERMS)) {
                        new TirenSet().checkTirem(ivTimer);
                        startMedia();
                    } else {
                        // 没有申请过权限，现在去申请
                        EasyPermissions.requestPermissions(this, "PERMISSION_STORAGE_MSG", Constant.TAG_ONE, PERMS);
                    }
                }
                break;
            case R.id.linearLayoutStop:
                radioGroup.setVisibility(View.VISIBLE);
                linearLayoutStop.setVisibility(View.GONE);
                if (mediaRecorder != null) {
                    stopMedia();
                }
                break;
            case R.id.rbAlbum:
                new MainUI().showPopupMenu(rbAlbum, "Local", this);
                break;
            case R.id.rbRefresh:
//                ShowDialog("/etc/init.d/mjpg-streamer restart");
//                ShowDialog("uci set mjpg-streamer.core.fps=30", "uci commit", "/etc/init.d/mjpg-streamer restart");
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("address",address);
                intent.putExtra("tag","local");
                startActivity(intent);
                break;
            case R.id.rbBack:
                finish();
                break;
        }
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
        requestMediaProjection();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 请求权限被拒
        Toast.makeText(LocalActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    //创建申请录屏的 Intent
    private void requestMediaProjection() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, Constant.TAG_ONE);
    }

    private void stopMedia() {
        mediaRecorder.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent backdata) {
        super.onActivityResult(requestCode, resultCode, backdata);
        switch (requestCode) {
            case Constant.TAG_ONE:
                if (resultCode == Activity.RESULT_OK) {
                    new BottomUI().hideBottomUIMenu(this.getWindow());
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, backdata);
                    new TirenSet().checkTirem(ivTimer);
                    startMedia();
                } else {
                    finish();
                }
                break;
        }
    }


    private void startMedia(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //获取mediaRecorder
            mediaRecorder = new MyMediaRecorder().getMediaRecorder(project,workName,workCode,"/LUKEVideo/");
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("你的name",
                    2400, 1080, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(),
                    null, null);
        }
        //开始录制
        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }
}