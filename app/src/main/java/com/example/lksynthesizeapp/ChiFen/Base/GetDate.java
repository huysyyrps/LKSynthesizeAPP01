package com.example.lksynthesizeapp.ChiFen.Base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetDate {
    /**
     * 获取当前时间,用来给文件夹命名
     */
    public String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US);
        return format.format(new Date())+".png";
    }
}
