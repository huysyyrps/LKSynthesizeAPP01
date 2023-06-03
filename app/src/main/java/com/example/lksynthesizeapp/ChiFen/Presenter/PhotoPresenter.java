package com.example.lksynthesizeapp.ChiFen.Presenter;

import android.content.Context;

import com.example.lksynthesizeapp.ChiFen.Module.PhotoContract;
import com.example.lksynthesizeapp.ChiFen.bean.SavePhotoBack;
import com.example.lksynthesizeapp.ChiFen.bean.UpPhoto;
import com.example.lksynthesizeapp.Constant.Base.BaseObserverNoEntry;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Net.RetrofitUtil;
import com.example.lksynthesizeapp.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
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
    @Override
    public void getPhoto(MultipartBody.Part part) {
        RetrofitUtil.getInstance().initRetrofitMain().getPhoto(part).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverNoEntry<UpPhoto>(context, context.getResources().getString(R.string.handler_data)) {
                    @Override
                    protected void onSuccees(UpPhoto t) throws Exception {
                        if (t.getMsg().equals("操作成功")){
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

    @Override
    //String code,String project, String workpiece,int type,String attachments
    public void getsavePhoto(RequestBody companys) {
        RetrofitUtil.getInstance().initRetrofitMain().savePhoto(companys).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverNoEntry<SavePhotoBack>(context, context.getResources().getString(R.string.handler_data)) {
                    @Override
                    protected void onSuccees(SavePhotoBack t) throws Exception {
                        if (t.getMsg().equals("操作成功")){
                            view.savePhoto(t);
                        }else {
                            view.savePhotoMessage("上传失败");
                        }
                    }
                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (new NetStat().isNetworkConnected(context)){
                            view.savePhotoMessage(""+ e.getMessage());
                        }else {
                            view.savePhotoMessage("网络异常");
                        }
                    }
                });
    }
}
