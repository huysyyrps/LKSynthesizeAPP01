package com.example.lksynthesizeapp.ChiFen.Presenter;

import android.content.Context;

import com.example.lksynthesizeapp.ChiFen.Module.VersionInfoContract;
import com.example.lksynthesizeapp.ChiFen.bean.VersionInfo;
import com.example.lksynthesizeapp.Constant.Base.BaseObserverNoEntry;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Net.RetrofitUtil;
import com.example.lksynthesizeapp.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;


/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description:
 */

public class VersionInfoPresenter implements VersionInfoContract.presenter {

    private Context context;
    private VersionInfoContract.View view;

    public VersionInfoPresenter(Context context, VersionInfoContract.View view) {
        this.context = context;
        this.view = view;
    }

    /**
     * 版本
     */

    @Override
    public void getVersionInfo(RequestBody company) {
        RetrofitUtil.getInstance().initLoginRetrofitMainNoSSL().getVersionInfo(company).subscribeOn(Schedulers.io())//请求在新的线程中执行
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在io线程中执行
                .subscribe(new BaseObserverNoEntry<VersionInfo>(context, context.getResources().getString(R.string.handler_data)) {
                    @Override
                    protected void onSuccees(VersionInfo t) throws Exception {
                        if (t.getCode()==1){
                            view.setVersionInfo(t);
                        }else {
                            view.setVersionInfoMessage("版本信息请求失败");
                        }
                    }
                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (new NetStat().isNetworkConnected(context)){
                            view.setVersionInfoMessage(""+ e.getMessage());
                        }else {
                            view.setVersionInfoMessage("网络异常");
                        }
                    }
                });
    }
}
