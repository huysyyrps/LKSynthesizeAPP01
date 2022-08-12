package com.example.lksynthesizeapp.Constant.Net.NetCookie;


import android.util.Log;

import com.example.lksynthesizeapp.MyApplication;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @date: 2018/7/27
 * @description: 读取cookie
 */

public class CookieReadInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String cookieString = SharePreferencesUtils.getString(MyApplication.myApp, "cookiess", "");
        String[] splitCookie = cookieString.split(";");
        String[] splitSessionId = splitCookie[0].split("=");
//        cookieString = splitSessionId[1];
        cookieString = splitCookie[0];
        Log.e("sessionGet",cookieString);
        builder.addHeader("Cookie", cookieString);
        return chain.proceed(builder.build());
    }
}
