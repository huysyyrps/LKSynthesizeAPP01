package com.example.lksynthesizeapp.ChiFen.Robot;

public class Const {
    //串口 波特率
    public static final String SPORT_NAME = "172.16.16.176";
    public static final int BAUD_RATE = 502;

    public static final String TXT_TYPE_SEND = "hello";
    public static final String HEX_TYPE_SEND = "123ABC";


//    //读输出位
//        modbusRtuMaster.readCoils(1, 17, 4);
//    //读保持寄存器
//        modbusRtuMaster.readHoldingRegisters(1, 27, 4);
//    //写单个保持寄存器
//        modbusRtuMaster.writeSingleRegister(1, 27, 1);
//    //写多个保持寄存器
//        modbusRtuMaster.writeHoldingRegisters(1, 27, 4, new int[]{1, 2, 3, 4});
//    //写单个位（写1时为 FF 00,写0时为00 00）
//        modbusRtuMaster.writeSingleCoil(1,0, true);
//    //写多个位 每隔8位从右到左取值，所以这个最后需要反转
//        modbusRtuMaster.writeCoils(1, 0, 16, new int[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0});
}
