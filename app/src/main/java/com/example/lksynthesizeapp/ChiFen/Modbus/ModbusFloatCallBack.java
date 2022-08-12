package com.example.lksynthesizeapp.ChiFen.Modbus;

public interface ModbusFloatCallBack {
    void success(short[] s);

    void fail(String s);
}
