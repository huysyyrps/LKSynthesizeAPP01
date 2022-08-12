package com.example.lksynthesizeapp.ChiFen.test;

import android.os.SystemClock;

import java.io.IOException;

import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

public class ModbusUtil {

    ModbusRtuMaster modbusRtuMaster;
    SerialHelper serialHelper;

    public void start() {
        try {

            //初始化SerialHelper对象，设定串口名称和波特率
            serialHelper = new SerialHelper("172.16.16.176", 502) {
                @Override
                protected void onDataReceived(ComBean paramComBean) {
                    //根据自己的业务自行处理接收的数据
//                    Log.i("数据1==" + Arrays.toString(paramComBean.bRec) + "===" + paramComBean.bRec.length + "==" + paramComBean.bRec[0] + "==" + paramComBean.bRec[1]);
                    //[1, 3, 0, 1, 0, 1, -43, -54]
                    //如果是负数，则十进制结果为256+负数，如256+(-54)=202

                    //这里需要你对接收到的数据进行处理，这里我举个例子，收到数据后，返回一个数据，
                    //当然正式上线肯定是需要按响应报文规则去回复的
                    new Thread(() -> {
                        try {
                            modbusRtuMaster.writeSingleRegister(1, 1, 3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            };

            /*
             * 默认的BaseStickPackageHelper将接收的数据扩展成64位，一般用不到这么多位
             * 我这里重新设定一个自适应数据位数的
             * 这里将64位改为自适应位
             */
            serialHelper.setStickPackageHelper(is -> {
                try {
                    int available = is.available();
                    if (available > 0) {
                        byte[] buffer = new byte[available];
                        int size = is.read(buffer);
                        if (size > 0) {
                            return buffer;
                        }
                    } else {
                        SystemClock.sleep(50);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            new Thread(() -> {
                try {
                    serialHelper.open();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            modbusRtuMaster = new ModbusRtuMaster(serialHelper);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

