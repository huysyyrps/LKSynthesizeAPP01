package com.example.lksynthesizeapp.ChiFen.Netty;

public class StringUtil {
    //  十六进制的字符串转换成byte数组
    public static byte[] HexCommandtoByte(byte[] data) {
        if (data == null) {
            return null;
        }
        int nLength = data.length;

        String strTemString = new String(data, 0, nLength);
        int j = 0;
        String[] strings = new String[strTemString.length()/2];
        for (int i = 0; i < strTemString.length(); i += 2) {
            strings[j] = strTemString.substring(i, i + 2);
            j++;
        }
//        String[] strings = strTemString.split(" ");
        nLength = strings.length;
        data = new byte[nLength];
        for (int i = 0; i < nLength; i++) {
            if (strings[i].length() != 2) {
                data[i] = 00;
                continue;
            }
            try {
                data[i] = (byte)Integer.parseInt(strings[i], 16);
            } catch (Exception e) {
                data[i] = 00;
                continue;
            }
        }

        return data;
    }
}