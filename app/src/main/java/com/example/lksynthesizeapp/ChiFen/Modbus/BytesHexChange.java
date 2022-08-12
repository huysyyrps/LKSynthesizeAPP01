package com.example.lksynthesizeapp.ChiFen.Modbus;


import static com.example.lksynthesizeapp.ChiFen.Modbus.ByteUtil.Byte2Hex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BytesHexChange {
    public final String HEX = "0123456789abcdef";

    public byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.trim();
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    //转hex字符串转字节数组
    static public byte[] HexToByteArr(String inHex)//hex字符串转字节数组
    {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    // 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    static public int isOdd(int num) {
        return num & 0x1;
    }

    static public byte HexToByte(String inHex)//Hex字符串转byte
    {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = bs.length - 1; i >= 0; i--) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public String strToUnicode(String strText) throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * 获取float的IEEE754存储格式
     *
     * @param value
     * @return
     */
    public static String floatToIEEE754(float value) {
        //符号位
        String sflag = value > 0 ? "0" : "1";

        //整数部分
        int fz = (int) Math.floor(value);
        //整数部分二进制
        String fzb = Integer.toBinaryString(fz);
        //小数部分，格式： 0.02
        String valueStr = String.valueOf(value);
        String fxStr = "0" + valueStr.substring(valueStr.indexOf("."));
        float fx = Float.parseFloat(fxStr);
        //小数部分二进制
        String fxb = toBin(fx);

        //指数位
        String e = Integer.toBinaryString(127 + fzb.length() - 1);
        //尾数位
        String m = fzb.substring(1) + fxb;

        String result = sflag + e + m;

        while (result.length() < 32) {
            result += "0";
        }
        if (result.length() > 32) {
            result = result.substring(0, 32);
        }
        return result;
    }

    public static String toBin(float f) {
        List<Integer> list = new ArrayList<Integer>();
        Set<Float> set = new HashSet<Float>();
        int MAX = 24; // 最多8位

        int bits = 0;
        while (true) {
            f = calc(f, set, list);
            bits++;
            if (f == -1 || bits >= MAX)
                break;
        }
        String result = "";
        for (Integer i : list) {
            result += i;
        }
        return result;
    }

    /**
     * 字节数组转float
     * 采用IEEE 754标准
     *
     * @param bytes
     * @return
     */
    public static float bytes2Float(byte[] bytes) {
        //获取 字节数组转化成的2进制字符串
        String BinaryStr = bytes2BinaryStr(bytes);
        //符号位S
        Long s = Long.parseLong(BinaryStr.substring(0, 1));
        //指数位E
        Long e = Long.parseLong(BinaryStr.substring(1, 9), 2);
        //位数M
        String M = BinaryStr.substring(9);
        float m = 0, a, b;
        for (int i = 0; i < M.length(); i++) {
            a = Integer.valueOf(M.charAt(i) + "");
            b = (float) Math.pow(2, i + 1);
            m = m + (a / b);
        }
        Float f = (float) ((Math.pow(-1, s)) * (1 + m) * (Math.pow(2, (e - 127))));
        return f;
    }

    /**
     * 将字节数组转换成2进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytes2BinaryStr(byte[] bytes) {
        StringBuffer binaryStr = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String str = Integer.toBinaryString((bytes[i] & 0xFF) + 0x100).substring(1);
            binaryStr.append(str);
        }
        return binaryStr.toString();
    }

    private static float calc(float f, Set<Float> set, List<Integer> list) {
        if (f == 0 || set.contains(f))
            return -1;
        float t = f * 2;
        if (t >= 1) {
            list.add(1);
            return t - 1;
        } else {
            list.add(0);
            return t;
        }
    }

    /**
     * 十六进制转换字符串
     *
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    /**
     * 16进制中的字符集
     */
    private static final String HEX_CHAR = "0123456789ABCDEF";

    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp;
        sb.append("[");
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;//只有一位的前面补个0
            }
            sb.append(tmp).append(" ");//每个字节用空格断开
        }
        sb.delete(sb.length() - 1, sb.length());//删除最后一个字节后面对于的空格
        sb.append("]");
        return sb.toString();
    }

    /**
     * 16进制字节数组转换为10进制字节数组
     * <p>
     * 两个16进制字节对应一个10进制字节，则将第一个16进制字节对应成16进制字符表中的位置(0~15)并向左移动4位，
     * 再与第二个16进制字节对应成16进制字符表中的位置(0~15)进行或运算，则得到对应的10进制字节
     *
     * @param b 10进制字节数组
     * @return 16进制字节数组
     */
    public static byte[] hex2byte(byte[] b) {
        if (b.length % 2 != 0) {
            throw new IllegalArgumentException("byte array length is not even!");
        }

        int length = b.length >> 1;
        byte[] b2 = new byte[length];
        int pos;
        for (int i = 0; i < length; i++) {
            pos = i << 1;
            b2[i] = (byte) (HEX_CHAR.indexOf(b[pos]) << 4 | HEX_CHAR.indexOf(b[pos + 1]));
        }
        return b2;
    }

    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
            strBuilder.append("");
        }
        return strBuilder.toString();
    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }
}
