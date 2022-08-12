package com.example.lksynthesizeapp.Constant.Base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class ExitApp {
    private static boolean isExit = false;
    //推出程序
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public void exit(AlertDialogUtil alertDialogUtil, Activity activity) {
        if (!isExit) {
            isExit = true;
            alertDialogUtil.showDialog("您确定要退出程序吗", new AlertDialogCallBack() {

                @Override
                public void confirm(String name) {
                    activity.finish();
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
            mHandler.sendEmptyMessageDelayed(0, 1500);
        } else {
            activity.finish();
            System.exit(0);
        }
    }
}
