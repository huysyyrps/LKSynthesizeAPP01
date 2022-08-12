package com.example.lksynthesizeapp.ChiFen.Robot.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * @filename 文件名：CircleItemView.java
 * @description 描 述：Item项自定义控件，主要是为了实现Item的onFling
 * @author 作 者：SergioPan
 * @date 时 间：2017-2-11
 * @Copyright 版 权：塘朗山源代码，版权归塘朗山所有。
 */
public class CircleItemView extends LinearLayout {

	public static final String TAG = "CircleItemView";
	private CircleMenu mParent;

	public CircleItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClickable(true);

	}

}
