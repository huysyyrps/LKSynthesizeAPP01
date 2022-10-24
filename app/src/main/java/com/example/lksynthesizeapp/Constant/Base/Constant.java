package com.example.lksynthesizeapp.Constant.Base;

import android.media.projection.MediaProjection;

public class Constant {

    public static final int TAG_ONE=1;
    public static final int TAG_TWO=2;
    public static final int TAG_THERE=3;
    public static final int TAG_FOUR=4;
    public static final int TAG_FIVE=5;
    public static final int TAG_SIX=6;
    public static final int TAG_SEVEN=7;
    public static final int TAG_EIGHT=8;
    public static final int TAG_NINE=9;
    public static final int TAG_TEN=10;
    public static final int TWENTYFOUR=24;
    public static final int MODBUS_CODE=1;


    public static final String URL="192.168.43.251";
    public static final String PASSWORD="88888888";




    //方向控制
    public static final String MOB_DIRECTION = "01060004000";
    //黑白光控制
    public static final String MOB_BW_LIGHT = "01060006000";
    //磁轭控制
    public static final String CE_CONTROL = "01060005000";
    //探照灯
    public static final String TZD_CONTROL = "01060007000";
    //行走距离
    public static final String MOB_DISTANCE = "01100020000204";
    //行走速度
    public static final String MOB_SPEED = "01100018000204";
    //磁化时间
    public static final String MOB_TIME = "01100022000204";
    //读取浮点型数据
    public static final String READ_FLOAT = "01030000001A";
    //读取16进制数据
    public static final String READ_HEX = "010400050003";

    public static MediaProjection mediaProjection = null;

}