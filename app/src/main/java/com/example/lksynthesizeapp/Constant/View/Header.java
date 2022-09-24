package com.example.lksynthesizeapp.Constant.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.lksynthesizeapp.R;


/**
 * 自定义header头部
 * ①返回按钮   返回文字
 * ②title
 * ③保存按钮   保存文字
 *
 */

public class Header extends LinearLayout {
    public void setClickLister(ClickLister clickLister) {
        this.clickLister = clickLister;
    }

    private ClickLister clickLister;
    private Context context;
    private AttributeSet attrs;
    private String tvLeft;//左面文字

    private String tvTitle;//中间标题
    private String tvRight;//右边的文字
    private int ivleft;//左边的图标
    private int ivRight;//右面的图标
    private boolean isleftTv;//左面文字是否显示
    private boolean isleftIv;//左面图标是否显示
    private boolean isTittle;//中间标题是否显示
    private boolean isRightTv;//右面标题是否显示
    private boolean isRightIv;//右面的图标是否显示

    private TextView tviewLeft;//左面标题
    private TextView tviewTittle;//中间的标题
    private TextView tviewRight;//右面的标题
    private ImageView iviewLeft;//左面的图片
    private ImageView iviewRight;//右面的图片

    public void setTvLeft(String tvLeft) {
        this.tvLeft = tvLeft;
    }

    public void setTvTitle(String tvTitle) {
        tviewTittle.setText(tvTitle);
        this.tvTitle = tvTitle;
    }

    public void setTvRight(String tvRight) {
        this.tvRight = tvRight;
    }

    public void setIvleft(int ivleft) {
        this.ivleft = ivleft;
    }

    public void setIvRight(int ivRight) {
        this.ivRight = ivRight;
    }

    public void setIsleftTv(boolean isleftTv) {
        this.isleftTv = isleftTv;
    }

    public void setIsleftIv(boolean isleftIv) {
        this.isleftIv = isleftIv;
    }

    public void setTittle(boolean tittle) {
        isTittle = tittle;
    }

    public void setRightTv(boolean rightTv) {
        isRightTv = rightTv;
        if(isRightTv){
            tviewRight.setVisibility(VISIBLE);
        }else {
            tviewRight.setVisibility(GONE);
        }
    }

    public void setRightIv(boolean rightIv) {
        isRightIv = rightIv;
        if(isRightIv){
            iviewRight.setVisibility(VISIBLE);
            iviewRight.setImageResource(ivRight);
        }else {
            iviewRight.setVisibility(GONE);
        }
    }

    public Header(Context context) {
        super(context);
        this.context=context;
        initAttributes();
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        initAttributes();
    }

    /**
     * 初始化属性
     */
    private void initAttributes() {
        if(attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Header);
            if (typedArray != null) {
                tvLeft = typedArray.getString(R.styleable.Header_header_title_left);
                tvTitle = typedArray.getString(R.styleable.Header_header_title);
                tvRight = typedArray.getString(R.styleable.Header_header_title_right);
                ivleft = typedArray.getResourceId(R.styleable.Header_header_image_left,R.drawable.ic_headerback);
                ivRight = typedArray.getResourceId(R.styleable.Header_header_image_right,R.drawable.ic_search);
                isleftIv=typedArray.getBoolean(R.styleable.Header_header_is_left_iv_visiable,true);
                isleftTv=typedArray.getBoolean(R.styleable.Header_header_is_left_tv_visiable,false);
                isRightIv=typedArray.getBoolean(R.styleable.Header_header_is_right_iv_visiable,false);
                isRightTv=typedArray.getBoolean(R.styleable.Header_header_is_right_tv_visiable,true);
                isTittle=typedArray.getBoolean(R.styleable.Header_header_is_title_visiable,true);
                typedArray.recycle();
            }
        }
        initView(context);
    }

    /**
     * 初始化view
     * @param context
     */
    public void initView(final Context context){
        LayoutInflater.from(context).inflate(R.layout.activity_head_myview, this, true);
        tviewLeft= (TextView) findViewById(R.id.tv_left);
        tviewTittle= (TextView) findViewById(R.id.tv_tittle);
        tviewRight= (TextView) findViewById(R.id.tv_right);

        iviewLeft=(ImageView) findViewById(R.id.iv_left);
        iviewRight=(ImageView) findViewById(R.id.iv_right);
        if(isleftTv){
            tviewLeft.setVisibility(VISIBLE);
            tviewLeft.setText(tvLeft);
        }else{
            tviewLeft.setVisibility(GONE);
        }
        if(isRightTv){
            tviewRight.setVisibility(VISIBLE);
            tviewRight.setText(tvRight);
        }else{
            tviewRight.setVisibility(GONE);
        }
        if(isTittle){
            tviewTittle.setVisibility(VISIBLE);
            tviewTittle.setText(tvTitle);
        }else{
            tviewTittle.setVisibility(GONE);
        }

        if(isleftIv){
            iviewLeft.setVisibility(VISIBLE);
            iviewLeft.setImageResource(ivleft);
        }else {
            iviewLeft.setVisibility(GONE);
        }

        if(isRightIv){
            iviewRight.setVisibility(VISIBLE);
            iviewRight.setImageResource(ivRight);
        }else {
            iviewRight.setVisibility(GONE);
        }
        iviewLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLister.LeftClickLister();
            }
        });
        iviewRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLister.rightClickLister();
            }
        });
        tviewRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLister.rightClickLister();
            }
        });
    }



    public interface  ClickLister{
        void LeftClickLister();
        void rightClickLister();
    }
}
