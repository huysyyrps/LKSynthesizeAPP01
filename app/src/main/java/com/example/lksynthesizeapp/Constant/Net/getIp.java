package com.example.lksynthesizeapp.Constant.Net;

import android.util.Log;

import com.example.lksynthesizeapp.ChiFen.Base.IpScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * 连接当前热点的设备的IP
 */
public class getIp {
    String address;

    public void getConnectIp1(GetIpCallBack getIpCallBack) throws Exception {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("ip neigh show");
            proc.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            //BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String newString = ip.toString().replace(".", "=");
                    String[] strs = newString.split("=");
                    if (strs.length > 3 && strs[2].equals("43")) {
                        address = ip;
                        getIpCallBack.success(address);
                        return;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            getIpCallBack.faile();
        }
    }


    public void cleanARP(GetIpCallBack getIpCallBack){
        IpScanner ipScanner = new IpScanner();
        ipScanner.setOnScanListener(new IpScanner.OnScanListener() {
            @Override
            public void scan(Map<String, String> resultMap) {
                if (resultMap.size()!=0){
                    Log.e("XXXXXX",resultMap.get("lladdr"));
                    address = resultMap.get("lladdr");
                    if (address!=null){
                        getIpCallBack.success(address);
                    }else {
                        getIpCallBack.faile();
                    }
                }
            }
        });
        ipScanner.startScan();
    }

    public String getConnectIp() throws Exception {
        ArrayList<String> connectIpList = new ArrayList<String>();
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ip neigh show");
        proc.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//        BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitted = line.split(" +");
            if (splitted != null && splitted.length >= 4) {
                String ip = splitted[0];
                String newString = ip.toString().replace(".","=");
                String[] strs = newString.split("=");
                if (strs.length>3&&strs[2].equals("43")){
                    connectIpList.add(ip);
                }
            }
        }
        for (int i=0;i<connectIpList.size();i++){
            String ip = connectIpList.get(i);
            Log.e("XXXXX",ip);
            ip = ip.replace(".",",");
            String[] all=ip.split(",");
            if (all[2].equals("43"));
            address = connectIpList.get(i);
        }
        return address;
    }
}
