package com.example.lksynthesizeapp.ChiFen.Module;

import com.example.lksynthesizeapp.ChiFen.bean.SavePhotoBack;
import com.example.lksynthesizeapp.ChiFen.bean.UpPhoto;
import com.example.lksynthesizeapp.Constant.Base.BaseEView;
import com.example.lksynthesizeapp.Constant.Base.BasePresenter;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/4/11.
 */

public interface PhotoContract {
    interface View extends BaseEView<presenter> {
        //上传图片
        void setPhoto(UpPhoto photoUp);
        void setPhotoMessage(String message);
        //保存图片
        void savePhoto(SavePhotoBack savePhotoBack);
        void savePhotoMessage(String message);
    }

    interface presenter extends BasePresenter {
        //上传图片
        void getPhoto(MultipartBody.Part part);
        //保存图片
        //String code,String project, String workpiece,int type,String attachments
        void getsavePhoto(RequestBody companys);
    }
}
