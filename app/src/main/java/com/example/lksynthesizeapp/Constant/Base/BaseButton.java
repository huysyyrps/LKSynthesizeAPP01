package com.example.lksynthesizeapp.Constant.Base;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.lksynthesizeapp.R;

public class BaseButton extends AppCompatButton {
    public BaseButton(Context context) {
        super(context);
    }

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        //获取默认的颜色值 如果按钮没有设置颜色值 默认为这个颜色
        int defaultColor = ContextCompat.getColor(context, R.color.theme_color);
        //获取自定义的属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseButton);
        //获取设置的背景色
        int bgColor = typedArray.getColor(R.styleable.BaseButton_bg_color, defaultColor);
        //获取设置的圆角大小
        int buttonCorner = typedArray.getDimensionPixelSize(R.styleable.BaseButton_bg_corner, 0);
//
//        //生成圆角图片
//        GradientDrawable bgcDrawable = new GradientDrawable();
//        //设置图片颜色
//        bgcDrawable.setColor(bgColor);
//        //设置圆角大小
//        bgcDrawable.setCornerRadius(buttonCorner);
//
//        //生成一张半透明的灰色图片 #31000000为遮罩颜色 可自定义
//        GradientDrawable bgcDrawable1 = new GradientDrawable();
//        bgcDrawable1.setColor(Color.parseColor("#ff4c41"));
//        bgcDrawable1.setCornerRadius(buttonCorner);
//
//        //生成一个图层叠加的图片 上面用灰色盖住 模拟变暗效果
//        Drawable[] arr = {bgcDrawable, bgcDrawable1};
//        LayerDrawable layerDrawable = new LayerDrawable(arr);
//
//        //设置点击后 变暗效果
//        StateListDrawable stateListDrawable = new StateListDrawable();
//        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, layerDrawable);
//        stateListDrawable.addState(new int[]{}, bgcDrawable);
//        setBackground(stateListDrawable);
//        typedArray.recycle();
    }
}
