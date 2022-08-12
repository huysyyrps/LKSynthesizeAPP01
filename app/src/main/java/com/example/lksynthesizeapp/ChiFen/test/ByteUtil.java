package com.example.lksynthesizeapp.ChiFen.test;

public class ByteUtil {

    public static byte[] fromInt32(int input) {
        byte[] result = new byte[4];
        result[3] = (byte) (input >> 24 & 0xFF);
        result[2] = (byte) (input >> 16 & 0xFF);
        result[1] = (byte) (input >> 8 & 0xFF);
        result[0] = (byte) (input & 0xFF);
        return result;
    }

    public static byte[] fromInt16(int input) {
        byte[] result = new byte[2];
        result[0] = (byte) (input >> 8 & 0xFF);
        result[1] = (byte) (input & 0xFF);
        return result;
    }
    public static byte[] fromInt16Reversal(int input) {
        byte[] result = new byte[2];
        result[1] = (byte) (input >> 8 & 0xFF);
        result[0] = (byte) (input & 0xFF);
        return result;
    }
}

