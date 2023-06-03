package com.example.lksynthesizeapp;

/**
 * @author: Allen.
 * @date: 2018/3/8
 * @description: 所有接口地址集
 */

public class ApiAddress {

    //生成环境
//    public final static String  api = "https://www.944414275.top:6060/";
    public final static String  api = "http://101.43.237.219:5000/";
 

    /**************************************个人中心************************************************/
    //获取图片验证码
    public final static String getVerifyCode = "getVerityCode.do";
    //登录
    public final static String login = "api/LoginAuth/Login";
    public final static String photoup = "pic-prod-api/common/upload";
    //图片保存
    public final static String photosave = "pic-prod-api/pic/info/save";
    //视频上传https://172.16.18.73:5001/api/FileProccess/UpLoadPic
    public final static String havevideoup = "api/UploadVideo/UploadVideo3";
    //测试token
    public final static String tokenTest = "api/Get/GetTwo";
    //修改密码
    public final static String checkpassword = "app_change_password";
    //注册
    public final static String register = "mobile_phone/save";
    //测厚数据上传
    public final static String cedatasend = "Post/PostNine";
    //日志上传
    public final static String daily = "api/UploadLog/UploadLog";
    //根据派工单获取信息
    public final static String defined = "api/GetDeviceInfo/GetDeviceInfo";
    //版本信息
    public final static String versioninfo = "app_api/client/init";
}
