package com.example.lksynthesizeapp.ChiFen.Base;

import android.graphics.Color;
import android.graphics.Paint;

public class MyPaint {
    Paint linePaint;
    Paint textbgpaint;
    Paint textpaint;
    Paint headTextpaint;
    public Paint getLinePaint(){
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.RED);
        return linePaint;
    }

    public Paint getTextpaint(){
        textpaint = new Paint();
        textpaint.setColor(Color.RED);
        textpaint.setTextSize(26);
        textpaint.setStrokeWidth(2);
        textpaint.setTextAlign(Paint.Align.LEFT);
        return textpaint;
    }

    public Paint getHeadTextpaint(){
        headTextpaint = new Paint();
        headTextpaint.setColor(Color.RED);
        headTextpaint.setTextSize(26);
        headTextpaint.setStrokeWidth(2);
        headTextpaint.setTextScaleX(0.8F);
        headTextpaint.setAntiAlias(true);//抗锯齿
//        Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
//        headTextpaint.setTypeface( font );
//        headTextpaint.setTextAlign(Paint.Align.LEFT);
        return headTextpaint;
    }

    public Paint getTextbgpaint(){
        textbgpaint = new Paint();
        textbgpaint.setColor(Color.WHITE);
        textbgpaint.setStyle(Paint.Style.FILL);
        return textbgpaint;
    }
}
