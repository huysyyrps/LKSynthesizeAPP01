package com.example.lksynthesizeapp.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.lksynthesizeapp.R;

public class BaseLinlayout extends LinearLayout {
    private String title;
    private String version;
    private Boolean versionShow;
    private int leftIcon;
    private int rightIcon;

    public BaseLinlayout(Context context) {
        super(context);
        initView(context, null);
    }

    public BaseLinlayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public BaseLinlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public BaseLinlayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawerLin);
            if (typedArray != null) {
                title = typedArray.getString(R.styleable.DrawerLin_drawer_title);
                version = typedArray.getString(R.styleable.DrawerLin_version);
                versionShow = typedArray.getBoolean(R.styleable.DrawerLin_version_show,false);
                leftIcon = typedArray.getResourceId(
                        R.styleable.DrawerLin_drawer_image_left,
                        R.drawable.ic_setting
                );
                rightIcon = typedArray.getResourceId(
                        R.styleable.DrawerLin_drawer_image_right,
                        R.drawable.ic_right_arrow
                );
                typedArray.recycle();
            }
        }
        initView(context);
    }
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.drawer_item, this, true);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvVersion = findViewById(R.id.tvVersion);
        ImageView ivleft = findViewById(R.id.icLeft);
        ImageView ivRight = findViewById(R.id.ivRight);
        tvTitle.setText(title);
        tvVersion.setText(version);
        ivleft.setImageResource(leftIcon);
        ivRight.setImageResource(rightIcon);
    }
}
