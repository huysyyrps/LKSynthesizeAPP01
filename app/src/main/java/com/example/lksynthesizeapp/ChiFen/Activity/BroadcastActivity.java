package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.KeyCenter;
import com.example.lksynthesizeapp.ChiFen.Base.VideoCaptureScreen;
import com.example.lksynthesizeapp.ChiFen.View.MyWebView;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoPublisherUpdateCdnUrlCallback;
import im.zego.zegoexpress.constants.ZegoPublishChannel;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoTrafficControlProperty;
import im.zego.zegoexpress.constants.ZegoVideoBufferType;
import im.zego.zegoexpress.entity.ZegoCDNConfig;
import im.zego.zegoexpress.entity.ZegoCustomVideoCaptureConfig;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;

public class BroadcastActivity extends BaseActivity {

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.tvCompName)
    TextView tvCompName;
    @BindView(R.id.tvWorkName)
    TextView tvWorkName;
    @BindView(R.id.tvWorkCode)
    TextView tvWorkCode;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.webView)
    MyWebView myWebView;
    public static MediaProjectionManager mMediaProjectionManager;
    public static MediaProjection mMediaProjection;
    private String project = "", workName = "", workCode = "", address = "";
    //?????????????????????????????????
    public static final String ACTION_SERVICE_NEED = "action.ServiceNeed";
    String userID;
    String publishStreamID;
    String playStreamID;
    String roomID;
    ZegoExpressEngine engine;
    ZegoUser user;
    private static final int DEFAULT_VIDEO_WIDTH = 1280;
    private static final int DEFAULT_VIDEO_HEIGHT = 720;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        //??????????????????
        new BottomUI().hideBottomUIMenu(this.getWindow());
        //?????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // ????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (project.trim().equals("") && workName.trim().equals("") && workCode.trim().equals("")) {
            linearLayout.setVisibility(View.GONE);
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
        frameLayout.setBackgroundColor(getResources().getColor(R.color.black));
//        try {
//            address = new getIp().getConnectIp();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        address = "http://" + address + ":8080";
        Log.e("XXXXX", address);
        myWebView.setBackgroundColor(Color.BLACK);
        myWebView.loadUrl(address);
        setDefaultValue();
    }

    public void setDefaultValue() {
        userID = "Android_" + Build.MODEL.replaceAll(" ", "_");
        roomID = "0033";
        publishStreamID = "0033";
        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = KeyCenter.appID();
        profile.appSign = KeyCenter.appSign();
        profile.scenario = ZegoScenario.GENERAL;
        profile.application = getApplication();
        engine = ZegoExpressEngine.createEngine(profile, null);
        user = new ZegoUser(userID);
        engine.loginRoom(roomID, user);
        //???/???????????????????????????????????????"true" ????????????????????????"false" ????????????????????????
        engine.enableCamera(true);
        //??????????????????????????????????????????"true" ????????????????????????????????????"false" ?????????????????????
        engine.muteMicrophone(true);
        //?????????????????????????????????????????????"true" ????????????(??????????????????)???"false" ????????????????????????
        engine.muteSpeaker(true);
        //??????????????????????????????true ???????????????????????????false ????????????????????????????????? false???
        engine.mutePublishStreamAudio (true);
        //???????????????????????????true ?????????????????????false ?????????????????????????????? true???
        // ??????????????????????????? SDK ????????????????????????????????????????????????1 ???1 ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        engine.enableTrafficControl(true, ZegoTrafficControlProperty.BASIC.value());
        //?????????
        engine.enableHardwareEncoder(true);

        ZegoVideoConfig videoConfig = new ZegoVideoConfig();
        videoConfig.captureHeight = 320;
        videoConfig.captureWidth = 180;
        videoConfig.encodeHeight = 740;
        videoConfig.encodeWidth = 1280;
//        videoConfig = new ZegoVideoConfig(ZegoVideoConfigPreset.PRESET_720P);
        // ??????????????????
        engine.setVideoConfig(videoConfig);
        //?????????????????????????????????
        engine.mutePublishStreamAudio(true);
        prepareScreenCapture();
    }

    public void prepareScreenCapture() {
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this, "Require root permission", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // 5.0???????????????
            // ???????????????????????????????????????
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Constant.TAG_ONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.TAG_ONE && resultCode == RESULT_OK) {
            //Target????????????10.0????????????MediaProjection
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            setCustomCapture();
            ZegoCDNConfig config = new ZegoCDNConfig();
            // set CDN URL
//            config.url = "rtmp://221.2.36.238:2012/live/live1";p
//            engine.enablePublishDirectToCDN(true, config);
            engine.startPublishingStream(publishStreamID);
            SharePreferencesUtils sharePreferencesUtils = new SharePreferencesUtils();
            String cid = sharePreferencesUtils.getString(this, "cid", "");
            //rtmp://221.2.36.238:2012/live/live1/"+cid
            engine.addPublishCdnUrl(publishStreamID, "rtmp://live-push.bilivideo.com/live-bvc/", new IZegoPublisherUpdateCdnUrlCallback() {
                @Override
                public void onPublisherUpdateCdnUrlResult(int errorCode) {
                    if (errorCode == 0){
                        // Add CDN URL successfully
                        Toast.makeText(BroadcastActivity.this, getString(R.string.rtc_success), Toast.LENGTH_LONG).show();
                    } else {
                        // Fail to add CDN URL.
                        Toast.makeText(BroadcastActivity.this, getString(R.string.rtc_faile), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void setCustomCapture() {
        // VideoCaptureScreen inherits IZegoCustomVideoCaptureHandler, which is used to monitor custom capture onStart and onStop callbacks
        // VideoCaptureScreen??????IZegoCustomVideoCaptureHandler??????????????????????????????onStart???onStop??????
        VideoCaptureScreen videoCapture = new VideoCaptureScreen(mMediaProjection, DEFAULT_VIDEO_WIDTH, DEFAULT_VIDEO_HEIGHT, engine);
        engine.setCustomVideoCaptureHandler(videoCapture);
        ZegoCustomVideoCaptureConfig videoCaptureConfig = new ZegoCustomVideoCaptureConfig();
        videoCaptureConfig.bufferType = ZegoVideoBufferType.SURFACE_TEXTURE;
        // Start Custom Capture
        engine.enableCustomVideoCapture(true, videoCaptureConfig, ZegoPublishChannel.MAIN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ZegoExpressEngine.destroyEngine(null);
        engine.stopPreview();
        engine.logoutRoom(roomID);
        engine.stopPublishingStream();
    }

    @Override
    protected void onDestroy() {
        ZegoExpressEngine.destroyEngine(null);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myWebView.loadUrl(address);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_broadcast;
    }

    @Override
    protected boolean isHasHeader() {
        return false;
    }

    @Override
    protected void rightClient() {
    }
}