package com.example.lksynthesizeapp.ChiFen.Netty;

import android.util.Log;

import com.example.lksynthesizeapp.ChiFen.Modbus.BytesHexChange;
import com.littlegreens.netty.client.listener.MessageStateListener;

public class BaseTcpClient {
    //直接初始化静态变量，确保线程安全
    private static final BaseTcpClient mInstance= new BaseTcpClient();
    private String heaetData = "";
    BytesHexChange bytesHexChange = BytesHexChange.getInstance();
    private BaseTcpClient() {
    }
    public static BaseTcpClient getInstance(){
        return mInstance;
    }

    private NettyTcpClient mNettyTcpClient;
    public NettyTcpClient initTcpClient(String host, int port){
//        byte[] heartData = setHeadrtData();
        mNettyTcpClient = new NettyTcpClient.Builder()
                .setHost(host)    //设置服务端地址
                .setTcpPort(port) //设置服务端端口号
                .setMaxReconnectTimes(5)    //设置最大重连次数
                .setReconnectIntervalTime(5)    //设置重连间隔时间。单位：秒
                .setSendheartBeat(true) //设置是否发送心跳
                .setHeartBeatInterval(1)    //设置心跳间隔时间。单位：秒
                .setHeartBeatData(setHeadrtData()) //设置心跳数据，可以是String类型，也可以是byte[]，以后设置的为准
                .setIndex(0)    //设置客户端标识.(因为可能存在多个tcp连接)
//                .setPacketSeparator("#")//用特殊字符，作为分隔符，解决粘包问题，默认是用换行符作为分隔符
//                .setMaxPacketLong(1024)//设置一次发送数据的最大长度，默认是1024
                .build();
        return mNettyTcpClient;
    };

    //new byte[]{(byte) 0xA1, (byte) 0x1A, 0x01, 0x03, 0x1E}
    private static byte[] setHeadrtData(){
        byte[] headerData = new byte[5];
        headerData[0] = (byte) 0xA1;
        headerData[1] = (byte) 0x1A;
        headerData[2] = (byte) 0x01;
        headerData[3] = (byte) 0x03;
        headerData[4] = (byte) 0x1E;
        return headerData;
    }
    public void tcpClientConntion(NettyTcpClient mNettyTcpClient){
        if (!mNettyTcpClient.isConnecting()) {
            mNettyTcpClient.connect();//连接服务器
        } else {
            mNettyTcpClient.disconnect();
        }
    }
    public void sendTcpData(byte[] data, SendCallBack sendCallBack){
        mNettyTcpClient.sendMsgToServer(data, new MessageStateListener() {
            @Override
            public void isSendSuccss(boolean isSuccess) {
                if (isSuccess) {
                    sendCallBack.success("send successful");
//                    Log.e("XXX", "send successful");
                } else {
                    sendCallBack.faild("send error");
                    Log.e("XXX", "send error");
                }
            }
        });
    }

    //数据格式转换
    public String StringToHex(String data, int num,boolean tag){
        String settingData = "";
        int bitData;
        if (tag){
            bitData = (int) (Float.parseFloat(data)*10);
        }else {
            bitData = (int) (Float.parseFloat(data));
        }
        if (num == 2){
            settingData = String.format("%02x",bitData);
        }else if (num == 4){
            settingData = String.format("%04x",bitData);
        }else if (num == 8){
            settingData = String.format("%08x",bitData);
        }
        return settingData;
    }
}
