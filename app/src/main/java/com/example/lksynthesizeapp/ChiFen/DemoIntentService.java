package com.example.lksynthesizeapp.ChiFen;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.lksynthesizeapp.ChiFen.Activity.BroadcastActivity;
import com.example.lksynthesizeapp.MyApplication;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * 继承 GTIntentService 接收来自个推的消息，所有消息在线程中回调，如果注册了该服务，则务必要在 AndroidManifest 中声明，否则无法接受消息
 */
public class DemoIntentService extends GTIntentService {

    boolean shouldPlayBeep = true;
    Context context1;
    int index = 1;

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    // 处理透传消息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        this.context1 = context;
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{0, 1000}, -1);
        }
    }

    // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e("PUSH_LOG", "onReceiveClientId -> " + "clientid = " + clientid);
        SharePreferencesUtils sharePreferencesUtils = new SharePreferencesUtils();
        sharePreferencesUtils.setString(context, "cid", clientid);
    }

    // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e("PUSH_LOG处理回执", cmdMessage.getAction() + "");
    }

    // 通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
//        try {
//            String address = new getIp().getConnectIp();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    //发送设置数据
//                    SSHExcuteCommandHelper.writeBefor(address, "XXX", new SSHCallBack() {
//                        @Override
//                        public void confirm(String data) {
//                            Log.e("PUSH_LOG通知到达", data + "111111");
//                            SSHExcuteCommandHelper.disconnect();
//                        }
//
//                        @Override
//                        public void error(String s) {
//                            Log.e("PUSH_LOG通知到达", "222" + "111111");
//                        }
//                    });
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        //在Service服务类中发送广播消息给Activity活动界面
        Intent intentBroadcastReceiver = new Intent();
        intentBroadcastReceiver.setAction(BroadcastActivity.ACTION_SERVICE_NEED);
        sendBroadcast(intentBroadcastReceiver);
        showNotification("huifang");
    }
    /**
     * 简单的发送通知
     */
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showNotification(String qudaoid) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context1, qudaoid);
        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        Notification notification = builder.build();
        MyApplication.notificationChannelManager.notify(index, notification);
        index++;
    }

    // 通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        Log.e("PUSH_LOG通知到达", msg.getContent() + "");
    }
}