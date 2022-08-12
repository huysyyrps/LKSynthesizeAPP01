package com.example.lksynthesizeapp.Constant.Presenter;

import android.content.Context;

import com.example.lksynthesizeapp.Constant.Base.BaseObserverNoEntry1;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Bean.Defined;
import com.example.lksynthesizeapp.Constant.Module.DefinedContract;
import com.example.lksynthesizeapp.Constant.Net.RetrofitUtil;
import com.example.lksynthesizeapp.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description:
 */

public class DefinedPresenter implements DefinedContract.presenter {

    private Context context;
    private DefinedContract.View view;

    public DefinedPresenter(Context context, DefinedContract.View view) {
        this.context = context;
        this.view = view;
    }

    /**
     * 根据派工单获取信息
     */
    @Override
    public void getDefined(String pgd) {
        RetrofitUtil.getInstance().initLoginRetrofitMainNoSSL().getDefined(pgd).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverNoEntry1<Defined>(context, context.getResources().getString(R.string.handler_data)) {
                    @Override
                    protected void onSuccees(Defined t) throws Exception {
                        view.setDefined(t);
                    }
                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (new NetStat().isNetworkConnected(context)){
                            view.setDefinedMessage(e.toString());
                        }else {
                            view.setDefinedMessage("网络异常");
                        }
                    }
                });
    }
}
