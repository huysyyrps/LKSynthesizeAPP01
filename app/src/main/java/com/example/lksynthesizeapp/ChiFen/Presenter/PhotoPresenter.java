package com.example.lksynthesizeapp.ChiFen.Presenter;

import android.content.Context;

import com.example.lksynthesizeapp.ChiFen.Module.PhotoContract;
import com.example.lksynthesizeapp.ChiFen.bean.PhotoUp;
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

public class PhotoPresenter implements PhotoContract.presenter {

    private Context context;
    private PhotoContract.View view;

    public PhotoPresenter(Context context, PhotoContract.View view) {
        this.context = context;
        this.view = view;
    }

    /**
     * 登录
     */
    @Override
    public void getPhoto(RequestBody company) {
        RetrofitUtil.getInstance().initLoginRetrofitMainNoSSL().getPhoto(company).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverNoEntry<PhotoUp>(context, context.getResources().getString(R.string.handler_data)) {
                    @Override
                    protected void onSuccees(PhotoUp t) throws Exception {
                        if (t.result){
                            view.setPhoto(t);
                        }else {
                            view.setPhotoMessage("上传失败");
                        }
                    }
                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (new NetStat().isNetworkConnected(context)){
                            view.setPhotoMessage(""+ e.getMessage());
                        }else {
                            view.setPhotoMessage("网络异常");
                        }
                    }
                });
    }
}
