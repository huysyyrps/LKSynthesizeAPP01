package com.example.lksynthesizeapp.Constant.Base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lksynthesizeapp.R;


/**
 * Created by Administrator on 2019/4/12.
 * 通用adapter的viewholder
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews; //用来存储控件
    private View mConvertView;
    private Context mContext;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

    /**
     * 提供一个获取ViewHolder的方法
     */
    public static BaseViewHolder getRecyclerHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        BaseViewHolder viewHolder = new BaseViewHolder(context, itemView);
        return viewHolder;
    }

    /**
     * 获取控件
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 给TextView设置setText方法
     */
    public BaseViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置checkBox不可选择
     */
    public BaseViewHolder setCheckBoxFalse(int viewId) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(false);
        return this;
    }

    public BaseViewHolder setCheckBoxTrue(int viewId) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(true);
        return this;
    }


    /**
     * 给TextView设置setText方法
     */
    public BaseViewHolder setText2(int viewId,int viewId1,int viewId2, String text,String type) {
        TextView tv1 = getView(viewId);
        TextView tv2 = getView(viewId1);
        TextView tv3 = getView(viewId2);
        if (type.equals("0")){
            tv1.setText(text);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);
        }

        if (type.equals("1")){
            tv2.setText(text);
            tv2.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);
        }

        if (type.equals("2")){
            tv3.setText(text);
            tv3.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
        }
        return this;
    }

    public BaseViewHolder setText3(int viewId, String text) {
        WebView tv = getView(viewId);
        tv.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);
        return this;
    }

    /**
     * 给ImageView设置setImageResource方法
     */
    public BaseViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * 给Radionbutton设置隐藏控件方法
     */
    public BaseViewHolder setGone(int viewId) {
        RadioButton rb = getView(viewId);
        rb.setVisibility(View.GONE);
        return this;
    }

    public BaseViewHolder setGoneLinearLayout(int viewId) {
        LinearLayout ll = getView(viewId);
        ll.setVisibility(View.GONE);
        return this;
    }
    public BaseViewHolder setVisLinearLayout(int viewId) {
        LinearLayout ll = getView(viewId);
        ll.setVisibility(View.VISIBLE);
        return this;
    }


    /**
     * 给Radionbutton设置隐藏控件方法
     */
    public BaseViewHolder setGoneText(int viewId) {
        TextView rb = getView(viewId);
        rb.setVisibility(View.GONE);
        return this;
    }
    public BaseViewHolder setVisText(int viewId) {
        TextView rb = getView(viewId);
        rb.setVisibility(View.VISIBLE);
        return this;
    }

    public BaseViewHolder setGoneEdit(int viewId) {
        EditText rb = getView(viewId);
        rb.setVisibility(View.GONE);
        return this;
    }
    public BaseViewHolder setVisEdit(int viewId) {
        EditText rb = getView(viewId);
        rb.setVisibility(View.VISIBLE);
        return this;
    }


    /**
     * 设置显示控件方法
     */
    public BaseViewHolder setVisitionImageView(int viewId) {
        ImageView iv = getView(viewId);
        iv.setVisibility(View.VISIBLE);
        return this;
    }

    public BaseViewHolder setVisitionTextView(int viewId) {
        TextView tv = getView(viewId);
        tv.setVisibility(View.VISIBLE);
        return this;
    }

    public BaseViewHolder setGoneTextView(int viewId) {
        TextView tv = getView(viewId);
        tv.setVisibility(View.GONE);
        return this;
    }


    /**
     * 给Radionbutton设置cgeck方法
     */
    public BaseViewHolder setCheck(int viewId) {
        RadioButton rb = getView(viewId);
        rb.setChecked(true);
        return this;
    }

    /**
     * 添加点击事件
     */
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * checkBox选择事件
     * @param viewId
     * @param listener
     * @return
     */
    public BaseViewHolder setCheckClickListener(int viewId, CompoundButton.OnClickListener listener) {
        CheckBox view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseViewHolder setGile(Context context,int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        Drawable drawable=new BitmapDrawable(bitmap);
        Glide.with(context)
                .load(drawable)
                .placeholder(R.color.app_color_f6)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv);
        return this;
    }

    /**
     * 设置图片
     */
    public BaseViewHolder setImage (Context context,int viewId, String path) {
        ImageView iv = getView(viewId);
        Glide.with(context)
                .load(path)
                .placeholder(R.color.app_color_f6)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv);
        return this;
    }



    public BaseViewHolder setResource (int viewId, int path) {
        ImageView iv = getView(viewId);
        iv.setImageResource(path);
        return this;
    }

    public BaseViewHolder setBitmap (int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }


}
