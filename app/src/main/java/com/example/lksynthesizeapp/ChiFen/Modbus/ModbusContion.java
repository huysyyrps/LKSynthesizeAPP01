package com.example.lksynthesizeapp.ChiFen.Modbus;

import android.util.Log;

import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ModbusContion {
    public static final String TAG = "ModbusContion";
    //读取线圈01
    public void readCoilClickEvent() {
        ModbusReq.getInstance().readCoil(new OnRequestBack<boolean[]>() {
            @Override
            public void onSuccess(boolean[] booleen) {
                Log.d(TAG, "readCoil onSuccess " + Arrays.toString(booleen));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readCoil onFailed " + msg);
            }
        }, 1, 1, 2);


    }

    //读直接寄存器02
    public void readDiscreteInputClickEvent() {
        ModbusReq.getInstance().readDiscreteInput(new OnRequestBack<boolean[]>() {
            @Override
            public void onSuccess(boolean[] booleen) {
                Log.d(TAG, "readDiscreteInput onSuccess " + Arrays.toString(booleen));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readDiscreteInput onFailed " + msg);
            }
        }, 1, 1, 5);
    }

    //读保持寄存器03
    public void readHoldingRegistersClickEvent(int slave, int start, int len, ModbusFloatCallBack modbusCallBack) {
        //readHoldingRegisters
        ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] data) {
                Log.d(TAG, "readHoldingRegisters onSuccess " + get16BitPcm(data));
                modbusCallBack.success(data);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readHoldingRegisters onFailed " + msg);
                modbusCallBack.fail(msg);
            }
        }, slave, start, len);
    }

    //.读输入寄存器04
    public void readInputRegistersClickEvent(int slave, int start, int len, ModbusCallBack modbusCallBack) {
        ModbusReq.getInstance().readInputRegisters(new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] data) {
                modbusCallBack.success(Arrays.toString(data));
                Log.d(TAG, "readInputRegisters onSuccess " + Arrays.toString(data));
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readInputRegisters onFailed " + msg);
                modbusCallBack.fail(msg);
            }
        }, slave, start, len);


    }

    //写线圈05
    public void writeCoilClickEvent() {
        ModbusReq.getInstance().writeCoil(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 1, true);


    }

    //写寄存器06
    public void writeRegisterClickEvent(int code, int off, int data) {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegister onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeRegister onFailed " + msg);
            }
        }, code, off, data);
    }

    //写多个寄存器10
    public void writeRegistersClickEvent(int code, int off, short[] data) {
        ModbusReq.getInstance().writeRegisters(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegisters onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeRegisters onFailed " + msg);
            }
        }, code, off, data);
    }


    public void destory(){
        ModbusReq.getInstance().destory();
    }

    private byte[] short2byteArr(short[] shortArr, int shortArrLen){
        byte[] byteArr = new byte[shortArrLen * 2];
        ByteBuffer.wrap(byteArr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortArr);
        return byteArr;
    }

//    private byte[]get16BitPcm(short[] data){
//        byte[] resultData =new byte[2* data.length];
//        int iter =0;
//        String TAG ="get16BitPcm";
//        for(short sample : data){
//            resultData[iter++]=(byte)( sample &0xff);//
//            resultData[iter++]=(byte)((sample >>8)&0xff);//
//        }
//        return resultData;
//    }

    private byte[] get16BitPcm(short[] data) {
        byte[] resultData = new byte[2 * data.length];
        int iter = 0;

        String TAG = "get16BitPcm";
        for (short sample : data) {
            resultData[iter++] = (byte)( sample & 0xff);     //低位存储，0xff是掩码操作
            resultData[iter++] = (byte)((sample >>8) & 0xff); //高位存储
            /*测试生成的字节是否正确
            //ByteBuffer bb = ByteBuffer.allocate(2);
            //bb.order(ByteOrder.LITTLE_ENDIAN);
            //bb.put((byte)(sample & 0xff));
            //bb.put((byte)((sample >>8) & 0xff));
            //short shortVal = bb.getShort(0);

            //Log.d(TAG,"iter is:"+iter+"\tshortVal is: "+ shortVal);
            //Log.d(TAG,"==========================");
            */
        }
        return resultData;
    }

    /**
     * short数组转byte数组
     * @param src
     * @return
     */
    public byte[] toByteArray(short[] src) {

        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) ((src[i] >> 8) & 0xFF);
            dest[i * 2 + 1] = (byte) (src[i] & 0xFF);
        }

        return dest;
    }


}
