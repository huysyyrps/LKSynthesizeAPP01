package com.example.lksynthesizeapp.Constant.Net;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lksynthesizeapp.R;

public class BaseDialogProgress extends Dialog {
    private TextView tvTitle;
    private TextView tvSize;
    private TextView tvPercent;
    private ProgressBar pb;

    public BaseDialogProgress(Context context) {
        super(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_progress, (ViewGroup)null);
        this.setContentView(view);
        this.initView(view);
    }

    private void initView(View view) {
        this.tvTitle = (TextView)view.findViewById(R.id.tv_title);
        this.tvSize = (TextView)view.findViewById(R.id.tv_size);
        this.tvPercent = (TextView)view.findViewById(R.id.tv_percent);
        this.pb = (ProgressBar)view.findViewById(R.id.pb);
        this.pb.setMax(100);
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.tvTitle.setText(title);
    }

    @SuppressLint({"SetTextI18n"})
    public void setProgress(int progress) {
        this.pb.setProgress(progress);
        this.tvPercent.setText(progress + "%");
    }

    public void setFileSize(String filesizestr) {
        this.tvSize.setText(filesizestr);
    }
}

