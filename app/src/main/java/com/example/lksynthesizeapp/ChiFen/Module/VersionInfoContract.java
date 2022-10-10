package com.example.lksynthesizeapp.ChiFen.Module;

import com.example.lksynthesizeapp.ChiFen.bean.VersionInfo;
import com.example.lksynthesizeapp.Constant.Base.BaseEView;
import com.example.lksynthesizeapp.Constant.Base.BasePresenter;

import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/4/11.
 */

public interface VersionInfoContract {
    interface View extends BaseEView<presenter> {
        //获取版本信息
        void setVersionInfo(VersionInfo versionInfo);
        void setVersionInfoMessage(String message);
    }

    interface presenter extends BasePresenter {
        //版本信息回调
        void getVersionInfo(RequestBody company);
    }
}
