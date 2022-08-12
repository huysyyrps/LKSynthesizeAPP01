package com.example.lksynthesizeapp.ChiFen.test;


/**
 * 功能码（十进制显示）
 */
public class ModbusFunction {

    //读线圈寄存器
    public static final int READ_COILS = 1;

    //读离散输入寄存器
    public static final int READ_DISCRETE_INPUTS = 2;

    //读保持寄存器
    public static final int READ_HOLDING_REGISTERS = 3;

    //读输入寄存器
    public static final int READ_INPUT_REGISTERS = 4;

    //写单个线圈寄存器
    public static final int WRITE_SINGLE_COIL = 5;

    //写单个保持寄存器
    public static final int WRITE_SINGLE_REGISTER = 6;

    //写入多个线圈寄存器
    public static final int WRITE_COILS = 15;

    //写入多个保持寄存器
    public static final int WRITE_HOLDING_REGISTERS = 16;
}


