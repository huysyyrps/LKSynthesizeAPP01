package com.example.lksynthesizeapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.igexin.sdk.IUserLoggerInterface;
import com.igexin.sdk.PushManager;
import com.message.update.UpdateApkHelper;

/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description: application
 */

public class MyApplication extends Application {
    public static MyApplication myApp;
    public static final int TIMEOUT = 15;
    private static Context context;//全局上下文
    public static NotificationManager notificationChannelManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationChannelManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            Uri sound=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/"+R.raw.fengming);
            NotificationChannel huifangcannel = new NotificationChannel("huifang", "回访提示音", NotificationManager.IMPORTANCE_HIGH);
            huifangcannel.setSound(sound, att);
            notificationChannelManager.createNotificationChannel(huifangcannel);
        }
//        MobSDK.submitPolicyGrantResult(true, null);
        myApp = this;
        context = getApplicationContext();
        PushManager.getInstance().initialize(this);
        PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
            @Override
            public void log(String s) {
                Log.e("PUSH_LOG",s);
            }
        });
        //app更新组件初始化
        UpdateApkHelper.getInstance().init(this);
//        //初始化线程池管理器
//        initThreadPool();
    }

//    /**
//     * 初始化线程池管理器
//     */
//    private void initThreadPool() {
//        // 创建一个独立的实例进行使用
//        executor = PoolThread.ThreadBuilder
//                .createFixed(5)
//                .setPriority(Thread.MAX_PRIORITY)
//                .setCallback(new LogCallback())
//                .build();
//    }
//
//    /**
//     * 获取线程池管理器对象，统一的管理器维护所有的线程池
//     * @return executor对象
//     */
//    public PoolThread getExecutor() {
//        return executor;
//    }
//
//    //自定义回调监听callback，可以全局设置，也可以单独设置。都行
//    public class LogCallback implements ThreadCallback {
//
//        private final String TAG = "LogCallback";
//
//        @Override
//        public void onError(String name, Throwable t) {
//            Log.e(TAG, "LogCallback"+"------onError"+"-----"+name+"----"+Thread.currentThread()+"----"+t.getMessage());
//        }
//
//        @Override
//        public void onCompleted(String name) {
//            Log.e(TAG, "LogCallback"+"------onCompleted"+"-----"+name+"----"+Thread.currentThread());
//        }
//
//        @Override
//        public void onStart(String name) {
//            Log.e(TAG, "LogCallback"+"------onStart"+"-----"+name+"----"+Thread.currentThread());
//        }
//    }

    //获取全局的上下文
    public static Context getContext() {
        return context;
    }

    public static synchronized MyApplication getInstance() {
        if (null == myApp) {
            myApp = new MyApplication();
        }
        return myApp;
    }
}