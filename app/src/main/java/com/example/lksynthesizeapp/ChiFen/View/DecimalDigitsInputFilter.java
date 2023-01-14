package com.example.lksynthesizeapp.ChiFen.View;


import android.text.InputFilter;
import android.text.Spanned;

/**
 * 限制输入小数点位数，以及开头不允许输入
 */

public class DecimalDigitsInputFilter implements InputFilter {

    /**
     * 限制小数位数
     */
    private final int decimalDigits;


    public DecimalDigitsInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int dotPos = -1;
        int len = dest.length();
        for (int i = 0; i < len; i++) {
            char c = dest.charAt(i);
            if (c == '.' || c == ',') {
                dotPos = i;
                break;
            }
        }

        if(source.equals(".") && dstart==0 && dend==0){
            return "";
        }

        if (dotPos >= 0) {

            // protects against many dots
            if (source.equals(".") || source.equals(",")) {
                return "";
            }
            // if the text is entered before the dot
            if (dend <= dotPos) {
                return null;
            }
            if (len - dotPos > decimalDigits) {
                return "";
            }
        }

        return null;

    }
}
