package com.example.lksynthesizeapp.ChiFen.Robot.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;


/**
 */
public class CircleMenu extends ViewGroup {

    public static final String TAG = "CircleMenu";
    /**
     * 圆盘半径，那么圆心为（mRadius， mRadius）
     **/
    public int mRadius;
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 2f;
    private static final float RADIO_PADDING_LAYOUT = 1 / 20f;
    /**
     * 内边距，默认为mRadius/20
     **/
    public float mPadding = -1;
    private double mStartAngle = 0;
    private OnMenuItemClickListener mListener;
    private ListAdapter mAdapter;
    // 用户输入事件，默认为无用行为，主要用于解决Fling后的按停，此时屏蔽点击事件
    private UserEvent mUserEvent = UserEvent.USELESS_ACTION;

    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
        setClickable(true);

    }


    //依附到窗口上
    @Override
    protected void onAttachedToWindow() {
        if (mAdapter != null) {
            buildMenuItems();
        }
        super.onAttachedToWindow();
    }

    /**
     * 菜单重新布局
     *
     * @param startAngle
     */
    public void relayoutMenu(double startAngle) {
        mStartAngle = startAngle;
        requestLayout();
    }

    // 构建菜单项
    @SuppressLint("NewApi")
    private void buildMenuItems() {
        if (mAdapter.getCount() <= 0) {
            return;
        }
        for (int i = 0; i < mAdapter.getCount(); i++) {
            CircleItemView itemView = (CircleItemView) mAdapter.getView(i, null, this);
            final int position = i;
            itemView.setClickable(true);
            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mUserEvent == UserEvent.FLING) {
                        // 正在飞转时，接收用户的点击事件，则视为停止飞转动作，而屏蔽点击事件
                        mUserEvent = UserEvent.USELESS_ACTION;
                        return;
                    }
                    // 非飞转时响应点击事件
                    if (mListener != null) {
                        mListener.onClick(v, position);
                    }

                }
            });
            addView(itemView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量自身
        // measureMyself(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 测量子View
        measureChildViews();

    }

    private void measureChildViews() {
        if (mAdapter.getCount() <= 0) {
            return;
        }

        // 获取半径,
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight()) / 2;
        final int count = getChildCount();
        // 取mRadius/2为Item宽度
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        int childMode = MeasureSpec.EXACTLY;
        int makeMeasureSpec = -1;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            // 设置为正方形
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        // 取mRadius/10为默认内边距
        if (mPadding == -1) {
            mPadding = RADIO_PADDING_LAYOUT * mRadius;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter.getCount() <= 0) {
            return;
        }
        final int childCount = getChildCount();
        int left, top, halfDiagonal;
        // 限制Item的宽高
        int itemWidth = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        float angleDelay = 360 / childCount;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            mStartAngle %= 360;
            // 取Item对角线的一半为Item中心到圆盘圆周的距离
            halfDiagonal = (int) (itemWidth / Math.sqrt(2)) - 30;
            float distanceFromCenter = mRadius - halfDiagonal - mPadding;
            left = mRadius
                    + (int) Math.round(distanceFromCenter * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * itemWidth);
            top = mRadius
                    + (int) Math.round(distanceFromCenter * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * itemWidth);
            // 重新Layout
            child.layout(left, top, left + itemWidth, top + itemWidth);
            mStartAngle += angleDelay;
        }

    }


    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
    }


    public void setOnItemClickListener(OnMenuItemClickListener listener) {
        this.mListener = listener;
    }

    public double getmStartAngle() {
        return mStartAngle;
    }

    public float getmPadding() {
        return mPadding;
    }

    public void setmPadding(float mPadding) {
        this.mPadding = mPadding;
    }

    /**
     * Item点击事件监听器
     *
     * @author SergioPan
     */
    public interface OnMenuItemClickListener {

        public void onClick(View view, int position);
    }

}
