package com.example.lksynthesizeapp.ChiFen.Module;

import com.example.lksynthesizeapp.ChiFen.bean.PhotoUp;
import com.example.lksynthesizeapp.Constant.Base.BaseEView;
import com.example.lksynthesizeapp.Constant.Base.BasePresenter;

import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/4/11.
 */

public interface PhotoContract {
    interface View extends BaseEView<presenter> {
        //设置查询线路
        void setPhoto(PhotoUp photoUp);
        void setPhotoMessage(String message);
    }

    interface presenter extends BasePresenter {
        //线路回调
        void getPhoto(RequestBody company);
    }
}
