package com.example.lksynthesizeapp;

import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_CLOSED;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_ERROR;
import static com.littlegreens.netty.client.status.ConnectState.STATUS_CONNECT_SUCCESS;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Netty.BaseTcpClient;
import com.example.lksynthesizeapp.ChiFen.Netty.NettyTcpClient;
import com.littlegreens.netty.client.listener.NettyClientListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TestActivity extends AppCompatActivity implements NettyClientListener<String> {
    @BindView(R.id.button)
    Button button;
    private boolean connectState;
    NettyTcpClient mNettyTcpClient;
    BaseTcpClient baseTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        baseTcpClient = BaseTcpClient.getInstance();
        settingNetty();
        //B11B6DFC69B70C3768881E00F5
        conver16HexToByte("B11B6DFC69B70C3768881E00F5");
    }

    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index  > hexString.length() - 1) {
                return byteArray;
            }
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    public static byte[] conver16HexToByte(String hex16Str){
        char[] chars = hex16Str.toCharArray();
        byte[] b = new byte[chars.length/2];
        for (int i = 0; i < b.length; i++) {
            int pos = i * 2;
            b[i] = (byte) ("0123456789ABCDEF".indexOf(chars[pos]) << 4 | "0123456789ABCDEF".indexOf(chars[pos+1]));
        }
        return b;
    }

    private void settingNetty() {
        mNettyTcpClient  = BaseTcpClient.getInstance().initTcpClient("172.16.20.5",5000);
        mNettyTcpClient.setListener(this); //设置TCP监听
        baseTcpClient.tcpClientConntion(mNettyTcpClient);
    }


    @OnClick(R.id.button)
    public void onClick() {
//        String sendHeartData = "";
//        String data = Constant.HEART_COMMAND+"01"+"03";
//        data = Constant.HEART_FRAME+data+new BytesHexChange().hexStringToBytes(data);
//        sendData(data);
//        Log.e("XXX", data);
    }

    @Override
    public void onMessageResponseClient(String msg, int index) {
        //服务端过来的消息回调
        Log.e("XXX", msg);
    }

    @Override
    public void onClientStatusConnectChanged(int statusCode, int index) {
        //连接状态回调
        if (statusCode == STATUS_CONNECT_SUCCESS) {
            Log.e("XXX", "成功");
        } else if (statusCode == STATUS_CONNECT_CLOSED) {
            Log.e("XXX", "断开");
        } else if (statusCode == STATUS_CONNECT_ERROR) {
            Log.e("XXX", "失败");
        }
    }
}