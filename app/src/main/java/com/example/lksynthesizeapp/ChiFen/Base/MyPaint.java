package com.example.lksynthesizeapp.ChiFen.Base;

import android.graphics.Color;
import android.graphics.Paint;

public class MyPaint {
    Paint linePaint;
    Paint textbgpaint;
    Paint textpaint;
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
        textpaint.setTextSize(13);
        textpaint.setTextAlign(Paint.Align.LEFT);
        return textpaint;
    }

    public Paint getTextbgpaint(){
        textbgpaint = new Paint();
        textbgpaint.setColor(Color.WHITE);
        textbgpaint.setStyle(Paint.Style.FILL);
        return textbgpaint;
    }
}
