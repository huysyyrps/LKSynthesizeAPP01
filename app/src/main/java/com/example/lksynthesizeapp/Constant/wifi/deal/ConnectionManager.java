package com.example.lksynthesizeapp.Constant.wifi.deal;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.thanosfisherman.wifiutils.WifiConnectorBuilder;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;
import com.thanosfisherman.wifiutils.wifiWps.ConnectionWpsListener;

import java.util.List;

public class ConnectionManager {

    private Context context;

    public ConnectionManager(Context context) {
        this.context = context;
    }

    //打开wifi
    public void openWithWAP() {
        WifiUtils.withContext(context).enableWifi(this::checkResult);
    }

    //关闭wifi
    public void closeWithWAP() {
        WifiUtils.withContext(context).disableWifi();
    }

    //扫描wifi
    public void scanWithWAP() {
        WifiUtils.withContext(context).scanWifi(new ScanResultsListener() {
            @Override
            public void onScanResults(@NonNull List<ScanResult> scanResults) {

                for (int i = 0; i < scanResults.size(); i++) {
                    //SSID:wifi名字
                    //加密方式:capabilities
                    //信号强度:level

                    Log.d("TAG", "====扫码结果====" +
                            scanResults.get(i).SSID + "====" +
                            scanResults.get(i).BSSID + "===" +
                            scanResults.get(i).level + "===" +
                            scanResults.get(i).capabilities);

                }

            }
        }).start();
    }

    //连接wifi
    public void connectWifiWithWAP(String name, String password) {
        WifiUtils.withContext(context)
                .connectWith(name, password)
                .setTimeout(20000)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Log.d("TAG","========连接wifi成功==SUCCESS!======");
                        Toast.makeText(context, "===连接wifi成功==SUCCESS!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@NonNull ConnectionErrorCode errorCode) {
                        Log.d("TAG","========连接wifi失败==FAIL!======"+errorCode.toString());
                        Toast.makeText(context, "===连接wifi失败==EPIC FAIL!" + errorCode.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    //取消正在连接的wifi
    public void cancelConnectWithWAP(String name, String password) {
        WifiConnectorBuilder.WifiUtilsBuilder builder = WifiUtils.withContext(context);
        builder.connectWith(name, password)
                .onConnectionResult(new ConnectionSuccessListener() {
                    @Override
                    public void success() {
                        Toast.makeText(context, "取消 SUCCESS!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@NonNull ConnectionErrorCode errorCode) {
                        Toast.makeText(context, "EPIC FAIL!" + errorCode.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
        builder.cancelAutoConnect();
    }

    //wps连接wifi
    public void wpsConnectWithWAP(String name, String password) {
        WifiUtils.withContext(context)
                .connectWithWps(name, password)
                .onConnectionWpsResult(new ConnectionWpsListener() {
                    @Override
                    public void isSuccessful(boolean isSuccess) {
                        Toast.makeText(context, "" + isSuccess, Toast.LENGTH_SHORT).show();

                    }
                })
                .start();
    }


    //断开wifi
    public void disConnectWithWAP() {
        WifiUtils.withContext(context)
                .disconnect(new DisconnectionSuccessListener() {
                    @Override
                    public void success() {
                        Toast.makeText(context, "Disconnect success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@NonNull DisconnectionErrorCode errorCode) {
                        Toast.makeText(context, "Failed to disconnect: " + errorCode.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //断开连接并删除保存的网络配置 wifi
    public void disConnectDeleteWithWAP(String nameSsid) {
        WifiUtils.withContext(context)
                .remove(nameSsid, new RemoveSuccessListener() {
                    @Override
                    public void success() {
                        Toast.makeText(context, "Remove success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(@NonNull RemoveErrorCode errorCode) {
                        Toast.makeText(context, "Failed to disconnect and remove: $errorCode", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getScanResults(@NonNull final List<ScanResult> results) {
        if (results.isEmpty()) {
            Log.e("TAG", "SCAN RESULTS IT'S EMPTY");
            return;
        }
        Log.e("TAG", "GOT SCAN RESULTS " + results);
    }


    private void checkResult(boolean isSuccess) {
//        if (isSuccess)
//            Toast.makeText(context, "WIFI已经打开", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(context, "无法打开wifi", Toast.LENGTH_SHORT).show();
    }


}
