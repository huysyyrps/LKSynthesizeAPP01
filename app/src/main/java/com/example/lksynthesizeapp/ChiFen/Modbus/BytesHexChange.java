package com.example.lksynthesizeapp.ChiFen.Modbus;


import java.math.BigInteger;
import java.util.Locale;

public class BytesHexChange {
    private static final BytesHexChange mInstance = new BytesHexChange();

    public static BytesHexChange getInstance() {
        return mInstance;
    }

    private BytesHexChange() {
    }

    public byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public String hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.trim();
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        int ad = 0;
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            ad += (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        String checkData = Integer.toHexString(ad);
        if (checkData.length()>2){
            checkData = checkData.substring(checkData.length()-2,checkData.length());
        }
        if (checkData.length()==1){
            checkData = "0"+checkData;
        }
        return checkData;
    }


    //转hex字符串转字节数组
    public String[] HexToByteArr(String inHex)//hex字符串转字节数组
    {
        int hexlen = inHex.length();
//        byte[] result;
        String[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
//            result = new byte[(hexlen / 2)];
            result = new String[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
//            result = new byte[(hexlen / 2)];
            result = new String[(hexlen / 2)];
        }
        int j = 0;
//        for (int i = 0; i < hexlen; i += 2) {
//            result[j] = HexToByte(inHex.substring(i, i + 2));
//            j++;
//        }
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = inHex.substring(i, i + 2);
            j++;
        }
        return result;
    }

    // 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    static public int isOdd(int num) {
        return num & 0x1;
    }

    /**
     * 字符串补0
     *
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                //sb.append("0").append(str);// 左补0
                sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        str = String.format(str).toUpperCase();//转为大写
        return str;
    }

    /**
     * 16进制字符串转二进制
     */
    public String HexToBinary(String Hex) {
        String bin = new BigInteger(Hex, 16).toString(2);
        int inb = Integer.parseInt(bin);
        bin = String.format(Locale.getDefault(), "%08d", inb);
        return bin;
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
     * 设置数据格式
     */
    public String dataChange(String hex, boolean division) {
        String stringData;
        if (division) {
            stringData = String.format("%.1f", ((float) Integer.parseInt(hex, 16)) / 10);
        } else {
            stringData = String.format("%.0f", ((float) Integer.parseInt(hex, 16)));
        }
        return stringData;
    }

    public static byte[] conver16HexToByte(String hex16Str) {
        char[] chars = hex16Str.toCharArray();
        byte[] b = new byte[chars.length / 2];
        for (int i = 0; i < b.length; i++) {
            int pos = i * 2;
            b[i] = (byte) ("0123456789ABCDEF".indexOf(chars[pos]) << 4 | "0123456789ABCDEF".indexOf(chars[pos + 1]));
        }
        return b;
    }


    /*

     * 把16进制字符串转换成字节数组 @param hex @return

     */

    public static byte[] hexStringToByte(String hex) {

        int len = (hex.length() / 2);

        byte[] result = new byte[len];

        char[] achar = hex.toCharArray();

        for (int i = 0; i < len; i++) {

            int pos = i * 2;

            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));

        }

        return result;

    }

    private static byte toByte(char c) {

        byte b = (byte) "0123456789ABCDEF".indexOf(c);

        return b;

    }

    //转hex字符串转字节数组
    static public byte[] HexStringToByteArr(String inHex)//hex字符串转字节数组
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

    static public byte HexToByte(String inHex)//Hex字符串转byte
    {
        return (byte) Integer.parseInt(inHex, 16);
    }

}
