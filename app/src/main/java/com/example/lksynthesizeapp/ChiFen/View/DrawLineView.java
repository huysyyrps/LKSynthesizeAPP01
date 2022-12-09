package com.example.lksynthesizeapp.ChiFen.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.lksynthesizeapp.ChiFen.bean.PointBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

public class DrawLineView extends ImageView {
    private LinkedList<PointBean> pointLists = new LinkedList<PointBean>();
    private PointBean pointBean = new PointBean(-1, -1, -1, -1);
    private Path mPath = new Path();
    private static int height = 10;
    private static int bottom = 5;
    int bitmapWidth = 0;
    int bitmapHeight = 0;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    boolean tag = false;
    private Paint mBitmapPaint;
    private Paint paint = new Paint() {
        {
            setColor(Color.RED);
            setAntiAlias(true);
            setTextAlign(Align.CENTER);//居中显示
            setStrokeWidth(2.0f);
            setDither(true);
            setFilterBitmap(true);
            setStyle(Style.STROKE);
            setStrokeJoin(Join.ROUND);
            setStrokeCap(Cap.ROUND);
            setTextSize(24);
            setFakeBoldText(false);
            setTypeface(Typeface.DEFAULT);
            setTextAlign(Align.CENTER);//居中显示
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        }
    };
    private CloseActivity closeActivity;
    double scale,scale1;

    public DrawLineView(Context context) {
        super(context);
    }

    public DrawLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    public void clear() {
        if (pointBean != null) {
            pointBean.setStartX(-1);
            pointBean.setStartY(-1);
            pointBean.setEndX(-1);
            pointBean.setEndY(-1);
        }

        if (pointLists != null && pointLists.size() > 0) {
            pointLists.clear();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, paint);
        if (pointLists != null && pointLists.size() > 0) {
            for (int i = 0; i < pointLists.size(); i++) {
                PointBean pb = pointLists.get(i);
                canvas.drawLine(pb.getStartX(), pb.getStartY(), pb.getEndX(), pb.getEndY(), paint);
                drawTrangle(canvas, paint, pb.getStartX(), pb.getStartY(), pb.getEndX(), pb.getEndY(), height, bottom);
                drawTrangle(canvas, paint, pb.getEndX(), pb.getEndY(), pb.getStartX(), pb.getStartY(), height, bottom);
            }
            if (tag) {
                for (int i = 0; i < pointLists.size(); i++) {
                    double a = (pointLists.get(i).getEndX() - pointLists.get(i).getStartX())
                            * (pointLists.get(i).getEndX() - pointLists.get(i).getStartX())
                            + (pointLists.get(i).getEndY() - pointLists.get(i).getStartY())
                            * (pointLists.get(i).getEndY() - pointLists.get(i).getStartY());
                    double length = (double) Math.sqrt(a);
                    Path mPath = new Path();
                    mPath.moveTo(pointLists.get(i).getStartX(), pointLists.get(i).getStartY());
                    mPath.lineTo(pointLists.get(i).getEndX(), pointLists.get(i).getEndY());
                    canvas.drawPath(mPath, paint);
                    BigDecimal bigDecimal = new BigDecimal(length*scale).setScale(2, RoundingMode.HALF_UP);
//                    BigDecimal bigDecimal1 = new BigDecimal(length*scale1).setScale(2, RoundingMode.HALF_UP);
//                    Toast.makeText(MyApplication.getContext(), bigDecimal1.toString(), Toast.LENGTH_SHORT).show();
                    canvas.drawTextOnPath(bigDecimal.toString(), mPath, 0, -15, paint);
                }
            }
        }

        if (pointBean != null && pointBean.getStartX() != -1
                && pointBean.getStartY() != -1 && pointBean.getEndX() != -1
                && pointBean.getEndY() != -1) {
            canvas.drawLine(pointBean.getStartX(), pointBean.getStartY(), pointBean.getEndX(), pointBean.getEndY(), paint);
            drawTrangle(canvas, paint, pointBean.getStartX(), pointBean.getStartY(), pointBean.getEndX(), pointBean.getEndY(), height, bottom);
            drawTrangle(canvas, paint, pointBean.getEndX(), pointBean.getEndY(), pointBean.getStartX(), pointBean.getStartY(), height, bottom);
        }
    }

    /**
     * 绘制三角
     *
     * @param canvas
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param height
     * @param bottom
     */
    private void drawTrangle(Canvas canvas, Paint paintLine, float fromX, float fromY, float toX, float toY, int height, int bottom) {
        try {
            float juli = (float) Math.sqrt((toX - fromX) * (toX - fromX) + (toY - fromY) * (toY - fromY));// 获取线段距离
            float juliX = toX - fromX;// 有正负，不要取绝对值
            float juliY = toY - fromY;// 有正负，不要取绝对值
            float dianX = toX - (height / juli * juliX);
            float dianY = toY - (height / juli * juliY);
            //终点的箭头
            Path path = new Path();
            path.moveTo(toX, toY);// 此点为三边形的起点
            path.lineTo(dianX + (bottom / juli * juliY), dianY - (bottom / juli * juliX));
            path.lineTo(dianX - (bottom / juli * juliY), dianY + (bottom / juli * juliX));
            path.close(); // 使这些点构成封闭的三边形
            canvas.drawPath(path, paintLine);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(event);
                break;
        }
        return true;
    }

    private void onActionDown(MotionEvent event) {
        try {
            if (pointBean == null) {
                pointBean = new PointBean(-1, -1, -1, -1);
            }
            pointBean.setStartX(event.getX());
            pointBean.setStartY(event.getY());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        invalidate();
    }

    private void onActionMove(MotionEvent event) {
        try {
            if (pointBean != null) {
                pointBean.setEndX(event.getX());
                pointBean.setEndY(event.getY());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        invalidate();
    }

    private void onActionUp(MotionEvent event) {
        try {
            if (pointBean != null) {
                pointBean.setEndX(event.getX());
                pointBean.setEndY(event.getY());
                PointBean pb = new PointBean();
                pb.setStartX(pointBean.getStartX());
                pb.setStartY(pointBean.getStartY());
                pb.setEndX(pointBean.getEndX());
                pb.setEndY(pointBean.getEndY());
                pointLists.add(pb);
                pointBean.setStartX(-1);
                pointBean.setStartY(-1);
                pointBean.setEndX(-1);
                pointBean.setEndY(-1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tag = true;
        invalidate();
    }

    /**
     * 撤回
     */
    public void remoke() {
        if (pointLists!=null&&pointLists.size() > 0) {
            pointLists.removeLast();
            invalidate();
        }else {
            setCallback(closeActivity);
            closeActivity.closeThisActivity();
        }
    }

    /**
     * 获取壁画数量
     */
    public String undoNum() {
        if (pointLists.size() > 0) {
           return "open";
        }else {
            return "closr";
        }
    }


    /**
     * 加载图片
     */
    public void loadImage(Bitmap bitmap, int width, int height) {
        bitmapHeight = height;
        bitmapWidth = width;
        //Math.tan(Math.PI/6);30度正切值
        scale = 72/Math.tan(Math.PI/6)/bitmapWidth;
//        scale1 = 72/Math.tan(Math.PI/6)/2400;
        Log.e("TAG1", width+"__________"+height);
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    public interface CloseActivity {
        void closeThisActivity();
    }

    public void setCallback(CloseActivity closeActivity) {
        this.closeActivity = closeActivity;
    }
}