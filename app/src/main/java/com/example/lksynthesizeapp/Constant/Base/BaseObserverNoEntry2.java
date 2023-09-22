package com.example.lksynthesizeapp.Constant.Base;

import android.accounts.NetworkErrorException;
import android.content.Context;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description: 自定义Observer
 */

public abstract class BaseObserverNoEntry2<T> implements Observer<T> {
    protected Context mContext;
    private String labelTxt;

    public BaseObserverNoEntry2(Context cxt, String text) {
        this.mContext = cxt;
        this.labelTxt = text;
    }

    //开始
    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart();
    }

    //获取数据
    @Override
    public void onNext(T tBaseEntity) {
        try {
            onSuccees(tBaseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //失败
    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();
            if (responseBody!=null){
                try {
                    responseBody.string();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        onRequestEnd();
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                onFailure(e, true);  //网络错误
            } else {
                onFailure(e, false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //结束
    @Override
    public void onComplete() {
        onRequestEnd();//请求结束
    }

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    protected abstract void onSuccees(T t) throws Exception;


    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    protected abstract void onFailure(Throwable e, boolean isNetWorkError) throws Exception;

    protected void onRequestStart() {
//        ProgressDialogUtil.startLoad(mContext,labelTxt);
    }

    protected void onRequestEnd() {
//        ProgressDialogUtil.stopLoad();
    }
}
