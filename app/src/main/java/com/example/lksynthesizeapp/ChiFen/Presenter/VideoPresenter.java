package com.example.lksynthesizeapp.ChiFen.Presenter;

import android.content.Context;

import com.example.lksynthesizeapp.ChiFen.Module.VideoContract;
import com.example.lksynthesizeapp.ChiFen.bean.Video;
import com.example.lksynthesizeapp.Constant.Base.BaseObserverNoEntry;
import com.example.lksynthesizeapp.Constant.Base.NetStat;
import com.example.lksynthesizeapp.Constant.Net.RetrofitUtil;
import com.example.lksynthesizeapp.R;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Part;


/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description:
 */

public class VideoPresenter implements VideoContract.presenter {

    private Context context;
    private VideoContract.View view;

    public VideoPresenter(Context context, VideoContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void getHaveVideo(@Part List<MultipartBody.Part> partList) {
        RetrofitUtil.getInstance().initLoginRetrofitMainNoSSL().getHaveVideoUp(partList).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserverNoEntry<Video>(context, context.getResources().getString(R.string.handler_data)) {
                    @Override
                    protected void onSuccees(Video t) throws Exception {
                        if (t.result){
                            view.setHaveVideo(t);
                        }else {
                            view.setHaveVideoMessage("上传失败");
                        }
                    }
                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (new NetStat().isNetworkConnected(context)){
                            view.setHaveVideoMessage(""+ e.getMessage());
                        }else {
                            view.setHaveVideoMessage("网络异常");
                        }
                    }
                });
    }
}
