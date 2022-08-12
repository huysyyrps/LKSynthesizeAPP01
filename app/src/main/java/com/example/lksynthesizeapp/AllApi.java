package com.example.lksynthesizeapp;

import com.example.lksynthesizeapp.ChiFen.bean.PhotoUp;
import com.example.lksynthesizeapp.ChiFen.bean.Video;
import com.example.lksynthesizeapp.Constant.Bean.Defined;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author: Allen.
 * @date: 2018/7/25
 * @description:
 */

public interface AllApi {


//    /**
//     * 获取图片验证码
//     */
//    @GET(ApiAddress.getVerifyCode)
//    Observable<ResponseBody> getVerityCode();
//
//    /**
//     * 登录
//     *  @GET(ApiAddress.login)
//     */
//    @FormUrlEncoded
//    @POST(ApiAddress.login)
//    @Headers({"Content-Type:application/x-www-form-urlencoded; charset=UTF-8"})
//    Observable<Login> getLogin(@Field("username") String username, @Field("password") String password);
//
//
    /**
     * 图片上传
     *  @GET(ApiAddress.login)
     */
    @POST(ApiAddress.photoup)
    @Headers({"Content-Type:application/json; charset=UTF-8"})
    Observable<PhotoUp> getPhoto(@Body RequestBody body);

    /**
     * 视频上传
     *  @GET(ApiAddress.login)
     */

    @Multipart
    @POST(ApiAddress.havevideoup)
    Observable<Video> getHaveVideoUp(@Part List<MultipartBody.Part> partList);
//
//    /**
//     * 日志上传
//     *  @GET(ApiAddress.login)
//     */
//
//    @Multipart
//    @POST(ApiAddress.daily)
//    Observable<Daily> getDaily(@Part List<MultipartBody.Part> partList);
//
//    /**
//     * 测试token
//     */
//    @GET(ApiAddress.tokenTest)
//    Observable<TokenTest> getTokenTest();
//
//    /**
//     * 注册
//     */
//    @FormUrlEncoded
//    @POST(ApiAddress.register)
//    Observable<Register> getLineStation(@Field("account") String account, @Field("password") String password);
//
//    /**
//     * 修改密码
//     */
//    @FormUrlEncoded
//    @POST(ApiAddress.checkpassword)
//    Observable<CheckPassWord> getCheckPassWord(@Field("account") String account, @Field("newPwd") String newPwd);
//
//
//    /**
//     * 测厚数据上传
//     */
//    @POST(ApiAddress.cedatasend)
//    Observable<SaveDataBack> sendDataSave(@Body SaveData saveData);

    /**
     * 根据派工单获取信息
     * @param pgdNum
     * @return
     */
    @GET(ApiAddress.defined)
    Observable<Defined> getDefined(@Query("pgdNum") String pgdNum);
}
