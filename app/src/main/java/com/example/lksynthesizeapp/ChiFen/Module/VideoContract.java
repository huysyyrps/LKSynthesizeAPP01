package com.example.lksynthesizeapp.ChiFen.Module;


import com.example.lksynthesizeapp.ChiFen.bean.Video;
import com.example.lksynthesizeapp.Constant.Base.BaseEView;
import com.example.lksynthesizeapp.Constant.Base.BasePresenter;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2019/4/11.
 */

public interface VideoContract {
    interface View extends BaseEView<presenter> {
        //设置查询线路
        void setHaveVideo(Video HaveVideoUp);
        void setHaveVideoMessage(String message);
    }

    interface presenter extends BasePresenter {
        //线路回调
        void getHaveVideo(@Part List<MultipartBody.Part> partList);
    }
}
