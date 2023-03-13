package com.example.lksynthesizeapp.Constant.wifi.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiManagerUtils {

    // 获取 WifiManager 实例.
    public static WifiManager getWifiManager(Context context) {
        return context == null ? null : (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    // 开启/关闭 WIFI.
    public static boolean setWifiEnabled(WifiManager manager, boolean enabled) {
        return manager != null && manager.setWifiEnabled(enabled);
    }

    // 获取 WIFI 的状态.
    public static int getWifiState(WifiManager manager) {
        return manager == null ? WifiManager.WIFI_STATE_UNKNOWN : manager.getWifiState();
    }

    /**
     * 注意:
     * WiFi 的状态目前有五种, 分别是:
     * WifiManager.WIFI_STATE_ENABLING: WiFi正要开启的状态, 是 Enabled 和 Disabled 的临界状态;
     * WifiManager.WIFI_STATE_ENABLED: WiFi已经完全开启的状态;
     * WifiManager.WIFI_STATE_DISABLING: WiFi正要关闭的状态, 是 Disabled 和 Enabled 的临界状态;
     * WifiManager.WIFI_STATE_DISABLED: WiFi已经完全关闭的状态;
     * WifiManager.WIFI_STATE_UNKNOWN: WiFi未知的状态, WiFi开启, 关闭过程中出现异常, 或是厂家未配备WiFi外挂模块会出现的情况;
     */
    // 开始扫描 WIFI.
    public static void startScanWifi(WifiManager manager) {
        if (manager != null) {
            manager.startScan();
        }
    }

    // 获取扫描 WIFI 的热点:
    public static List<ScanResult> getScanResult(WifiManager manager) {
        return manager == null ? null : manager.getScanResults();
    }


}
