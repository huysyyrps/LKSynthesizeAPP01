package com.example.lksynthesizeapp.Constant.Net;

import android.util.Log;

import com.example.lksynthesizeapp.Constant.Base.MainUtil;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description: 日志拦截器
 */

public class InterceptorUtil {

    //日志拦截器
    public static HttpLoggingInterceptor LogInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.w(MainUtil.logger, "log: " + message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY);//设置打印数据的级别
    }
}
