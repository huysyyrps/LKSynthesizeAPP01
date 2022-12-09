package com.example.lksynthesizeapp.ChiFen.View;

import android.annotation.SuppressLint;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.lksynthesizeapp.ChiFen.Activity.SeeImageOrVideoActivity;
import com.example.lksynthesizeapp.ChiFen.bean.PointBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;


public class ScaleImageView extends AppCompatImageView implements View.OnTouchListener {
    public static final String TAG = "ScaleImageView";
    public static final float SCALE_MAX = 5.0f; //最大的缩放比例
    private static final float SCALE_MIN = 1.0f;
    private double oldDist = 0;
    private double moveDist = 0;
    private float downX1 = 0;
    private float downX2 = 0;
    private float downY1 = 0;
    private float downY2 = 0;

    boolean tag = false;
    float scale, touchScale = 1;
    private Paint mBitmapPaint;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    int heightPixels, widthPixels;
    private static int height = 10;
    private static int bottom = 5;
    private Path mPath = new Path();
    private LinkedList<PointBean> pointLists = new LinkedList<PointBean>();
    private PointBean pointBean = new PointBean(-1, -1, -1, -1);

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pointerCount = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (pointerCount == 2) {
                    downX1 = 0;
                    downY1 = 0;
                    downX2 = 0;
                    downY2 = 0;
                }
                if (pointerCount == 1) {
                    onActionUp(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 2) {
                    float x1 = event.getX(0);
                    float x2 = event.getX(1);
                    float y1 = event.getY(0);
                    float y2 = event.getY(1);

                    double changeX1 = x1 - downX1;
                    double changeX2 = x2 - downX2;
                    double changeY1 = y1 - downY1;
                    double changeY2 = y2 - downY2;

                    if (getScaleX() > 1) { //滑动
                        float lessX = (float) ((changeX1) / 2 + (changeX2) / 2);
                        float lessY = (float) ((changeY1) / 2 + (changeY2) / 2);
                        setSelfPivot(-lessX, -lessY);
                        Log.d(TAG, "此时为滑动");
                    }
                    //缩放处理
                    moveDist = spacing(event);
                    double space = moveDist - oldDist;
                    touchScale = (float) (getScaleX() + space / v.getWidth());
                    if (touchScale < SCALE_MIN) {
                        setScale(SCALE_MIN);
                    } else if (touchScale > SCALE_MAX) {
                        setScale(SCALE_MAX);
                    } else {
                        setScale(touchScale);
                    }
                }
                if (pointerCount == 1) {
                    onActionMove(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointBean = null;//防止单指双指冲突
                if (pointerCount == 2) {
                    downX1 = event.getX(0);
                    downX2 = event.getX(1);
                    downY1 = event.getY(0);
                    downY2 = event.getY(1);
                    Log.d(TAG, "ACTION_POINTER_DOWN 双指按下 downX1=" + downX1 + " downX2="
                            + downX2 + "  downY1=" + downY1 + " downY2=" + downY2);
                    oldDist = spacing(event); //两点按下时的距离
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (event.getHistorySize() > 1) {
                    long time = event.getEventTime() - event.getHistoricalEventTime(event.getHistorySize() - 1);
                    Log.e("XXX", event.getEventTime() + "------" + event.getHistoricalEventTime(event.getHistorySize() - 1));
                }
                onActionDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointBean = null;
                Log.d(TAG, "ACTION_POINTER_UP");
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 触摸使用的移动事件
     *
     * @param lessX
     * @param lessY
     */
    private void setSelfPivot(float lessX, float lessY) {
        float setPivotX = 0;
        float setPivotY = 0;
        setPivotX = getPivotX() + lessX;
        setPivotY = getPivotY() + lessY;
        if (setPivotX < 0 && setPivotY < 0) {
            setPivotX = 0;
            setPivotY = 0;
        } else if (setPivotX > 0 && setPivotY < 0) {
            setPivotY = 0;
            if (setPivotX > getWidth()) {
                setPivotX = getWidth();
            }
        } else if (setPivotX < 0 && setPivotY > 0) {
            setPivotX = 0;
            if (setPivotY > getHeight()) {
                setPivotY = getHeight();
            }
        } else {
            if (setPivotX > getWidth()) {
                setPivotX = getWidth();
            }
            if (setPivotY > getHeight()) {
                setPivotY = getHeight();
            }
        }
        setPivot(setPivotX, setPivotY);
    }

    /**
     * 计算两个点的距离
     *
     * @param event
     * @return
     */
    private double spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    /**
     * 平移画面，当画面的宽或高大于屏幕宽高时，调用此方法进行平移
     *
     * @param x
     * @param y
     */
    public void setPivot(float x, float y) {
        setPivotX(x);
        setPivotY(y);
    }

    /**
     * 设置放大缩小
     *
     * @param scale
     */
    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    /**
     * 初始化比例，也就是原始比例
     */
    public void setInitScale() {
        setScaleX(1.0f);
        setScaleY(1.0f);
        setPivot(getWidth() / 2, getHeight() / 2);
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

    @SuppressLint("ResourceAsColor")
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
                    BigDecimal bigDecimal = new BigDecimal(length * scale / touchScale).setScale(2, RoundingMode.HALF_UP);
//                    BigDecimal bigDecimal1 = new BigDecimal(length*scale1).setScale(2, RoundingMode.HALF_UP);
//                    Toast.makeText(MyApplication.getContext(), bigDecimal1.toString(), Toast.LENGTH_SHORT).show();
                    canvas.drawTextOnPath(bigDecimal.toString() + "mm", mPath, 0, -15, paint);
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

    /**
     * 加载图片
     */
    public void loadImage(SeeImageOrVideoActivity seeImageOrVideoActivity, Bitmap bitmap, int width, int height) {
        DisplayMetrics dm = new DisplayMetrics();
        seeImageOrVideoActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        heightPixels = dm.heightPixels;
        widthPixels = dm.widthPixels;
        Bitmap resized1 = Bitmap.createScaledBitmap(bitmap, widthPixels, heightPixels, true);
        //Math.tan(Math.PI/6);30度正切值
        if (widthPixels > width) {
            scale = (float) (72 / Math.tan(Math.PI / 6) / widthPixels * (widthPixels / width));
        } else {
            scale = (float) (72 / Math.tan(Math.PI / 6) / widthPixels);
        }

//        scale1 = 72/Math.tan(Math.PI/6)/2400;
        Log.e("TAG1", width + "__________" + height);
        mBitmap = resized1.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
//        invalidate();
    }

    /**
     * 撤回
     *
     * @param seeImageOrVideoActivity
     */
    public void remoke(SeeImageOrVideoActivity seeImageOrVideoActivity) {
        if (pointLists != null && pointLists.size() > 0) {
            pointLists.removeLast();
            invalidate();
        } else {
            seeImageOrVideoActivity.finish();
        }
    }
}
