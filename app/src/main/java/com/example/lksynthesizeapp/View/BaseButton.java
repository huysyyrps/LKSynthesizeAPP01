package com.example.lksynthesizeapp.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.lksynthesizeapp.MyApplication;
import com.example.lksynthesizeapp.R;

public class BaseButton extends AppCompatButton {
    public BaseButton(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public BaseButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BaseButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        //获取默认的颜色值 如果按钮没有设置颜色值 默认为这个颜色
        int cColor = ContextCompat.getColor(context, R.color.theme_color);
        //获取自定义的属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseButton);
        //获取设置的背景色
        int bgColor = typedArray.getColor(R.styleable.BaseButton_bg_color, cColor);
        //获取设置的圆角大小
        int buttonCorner = typedArray.getDimensionPixelSize(R.styleable.BaseButton_bg_corner, 0);
        //设置按钮是否可以点击
        boolean buttonclient = typedArray.getBoolean(R.styleable.BaseButton_bg_client, true);

        //生成圆角图片
        GradientDrawable bgcDrawable = new GradientDrawable();
        //设置图片颜色
        bgcDrawable.setColor(bgColor);
        //设置圆角大小
        bgcDrawable.setCornerRadius(buttonCorner);

        //生成一张半透明的灰色图片 #31000000为遮罩颜色 可自定义
        GradientDrawable bgcDrawable1 = new GradientDrawable();
        bgcDrawable1.setColor(MyApplication.getContext().getColor(R.color.style_red));
        bgcDrawable1.setCornerRadius(buttonCorner);

        //生成一个图层叠加的图片 上面用灰色盖住 模拟变暗效果
        Drawable[] arr = new Drawable[]{bgcDrawable, bgcDrawable1};
        LayerDrawable layerDrawable = new LayerDrawable(arr);

        //设置点击后 变暗效果
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (buttonclient){
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, layerDrawable);
        }
        stateListDrawable.addState(new int[]{}, bgcDrawable);
        setBackground(stateListDrawable);
        typedArray.recycle();
    }
}
