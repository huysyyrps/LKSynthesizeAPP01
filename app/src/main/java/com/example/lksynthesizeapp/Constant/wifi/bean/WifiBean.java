package com.example.lksynthesizeapp.Constant.wifi.bean;

public class WifiBean implements Comparable<WifiBean> {

    private String wifiName; // wifi名称
    private int level; // wifi信号
    private String state; // 已连接  正在连接  未连接 三种状态
    private String capabilities; // 加密方式
    private String bsssiD;//wifi地址

    public String getBsssiD() {
        return bsssiD;
    }

    public void setBsssiD(String bsssiD) {
        this.bsssiD = bsssiD;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public String toString() {
        return "WifiBean{" +
                "wifiName='" + wifiName + '\'' +
                ", level=" + level +
                ", state='" + state + '\'' +
                ", capabilities='" + capabilities + '\'' +
                ", bsssiD='" + bsssiD + '\'' +
                '}';
    }

    @Override
    public int compareTo(WifiBean o) {
        int level1 = this.getLevel();
        int level2 = o.getLevel();
        return level2 - level1;
    }

    public boolean equals(Object obj) {
        WifiBean u = (WifiBean) obj;
        return wifiName.equals(u.wifiName);
    }

    public int hashCode() {
        String in = wifiName;
        return in.hashCode();
    }

}
