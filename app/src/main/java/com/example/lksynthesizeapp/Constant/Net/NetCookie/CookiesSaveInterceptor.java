package com.example.lksynthesizeapp.Constant.Net.NetCookie;


import android.util.Log;

import com.example.lksynthesizeapp.MyApplication;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description:
 */

public class CookiesSaveInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        List<String> s = originalResponse.headers("Set-Cookie");
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            String header =originalResponse.headers("Set-Cookie").get(0);
            Log.e("sessionLogin",header);
            SharePreferencesUtils.setString(MyApplication.myApp,"cookiess",header);
        }
        return originalResponse;
    }

}
