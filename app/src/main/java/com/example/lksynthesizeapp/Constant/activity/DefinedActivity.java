package com.example.lksynthesizeapp.Constant.activity;

import static com.huawei.hms.hmsscankit.RemoteView.REQUEST_CODE_PHOTO;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Bean.Defined;
import com.example.lksynthesizeapp.Constant.Module.DefinedContract;
import com.example.lksynthesizeapp.Constant.Presenter.DefinedPresenter;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class DefinedActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, DefinedContract.View {
    @BindView(R.id.rbLightClose)
    RadioButton rbLightClose;
    @BindView(R.id.rbLightOpen)
    RadioButton rbLightOpen;
    @BindView(R.id.rbPhoto)
    RadioButton rbPhoto;
    private FrameLayout frameLayout;
    int mScreenWidth;
    int mScreenHeight;
    String tag = "first";
    private RemoteView remoteView;
    DefinedPresenter definedPresenter;
    final int SCAN_FRAME_SIZE = 240;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.ACCESS_WIFI_STATE};
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
        definedPresenter = new DefinedPresenter(this, this);
        frameLayout = findViewById(R.id.rim);
        //????????????????????????????????????????????????????????????
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        //??????demo?????????????????????300dp
        final int SCAN_FRAME_SIZE = 300;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;
        //?????????RemoteView????????????????????????????????????:setContext()??????????????????context???setBoundingBox()?????????????????????setFormat()??????????????????????????????????????????build()??????????????????
        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).build();
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);

        //??????????????????
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            // ???????????????????????????????????????
            remoteView.onStart();
        } else {
            // ???????????????????????????????????????
            /**
             *@param host Context??????
             *@param rationale  ??????????????????????????????
             *@param requestCode ??????????????????????????????
             *@param perms ???????????????
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
                                sharePreferencesUtils.setString(DefinedActivity.this, "wifiName", dataArray[4]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "haveDescern", dataArray[5]);
                                startActivity(new Intent(DefinedActivity.this, SendSelectActivity.class));
                                finish();
                                tag = "second";
                                return;
                            } else {
                                Toast.makeText(DefinedActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            }
        });
    }

    /**
     * ??????Base64??????
     *
     * @param str
     * @return
     */
    public static String encodeToString(String str) {
        try {
            return Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ??????Base64??????
     *
     * @param str
     * @return
     */
    public static String decodeToString(String str) {
        try {
            return new String(Base64.decode(str.getBytes("UTF-8"), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * short?????????byte??????
     *
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

    // ?????????onRequestPermissionsResult????????????????????????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ?????????????????? EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // ?????????????????????
        remoteView.onStart();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        new AlertDialogUtil(DefinedActivity.this).showDialog("??????????????????????????????????????? "
                + "\n"
                + "????????????????????????????????????????????????", new AlertDialogCallBack() {
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
//        // ??????????????????
//        DialogUpdate dialogUpdate = new DialogUpdate(DefinedActivity.this);
//        dialogUpdate.setButtonText("??????","??????");
//        dialogUpdate.setMessage("??????????????????????????????????????? "
//                + "\n"
//                + "????????????????????????????????????????????????");
//        dialogUpdate.show();
//        dialogUpdate.setOnDialogUpdateOkListener(new DialogUpdate.OnDialogUpdateOkListener() {
//            @Override
//            public void onDialogUpdateOk() {
//                Intent intent = new Intent();
//                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + DefinedActivity.this.getPackageName()));
//                startActivityForResult(intent,Constant.TAG_ONE);
//                finish();
//            }
//
//            @Override
//            public void onDialogUpdateCancel() {
//                finish();
//            }
//        });
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
        if (defined.getResult() == null) {
            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        String company = defined.getResult().getCompanyName();
        String deviceName = defined.getResult().getDeviceName();
        String deviceCode = defined.getResult().getDeviceCode();
        sharePreferencesUtils.setString(DefinedActivity.this, "company", company);
        sharePreferencesUtils.setString(DefinedActivity.this, "deviceName", deviceName);
        sharePreferencesUtils.setString(DefinedActivity.this, "deviceCode", deviceCode);
        startActivity(new Intent(this, SendSelectActivity.class));
    }

    @Override
    public void setDefinedMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                // ??????????????????
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
                                sharePreferencesUtils.setString(DefinedActivity.this, "wifiName", dataArray[4]);
                                sharePreferencesUtils.setString(DefinedActivity.this, "haveDescern", dataArray[5]);
//                            definedPresenter.getDefined(data[0]);
                                startActivity(new Intent(DefinedActivity.this, SendSelectActivity.class));
                                finish();
                                tag = "second";
                                return;
                            } else {
                                Toast.makeText(DefinedActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
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