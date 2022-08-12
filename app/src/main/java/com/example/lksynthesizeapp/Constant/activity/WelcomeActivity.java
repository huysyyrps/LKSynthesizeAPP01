package com.example.lksynthesizeapp.Constant.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.View.CircleTextProgressbar;
import com.example.lksynthesizeapp.View.StatusBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.tvProgress)
    CircleTextProgressbar tvProgress;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        mHandler = new Handler();
        //设置状态栏颜色
        new StatusBarUtils().setWindowStatusBarColor(WelcomeActivity.this, R.color.white);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                //你需要跳转的地方的代码
                Intent intent=new Intent(WelcomeActivity.this, DefinedActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); //延迟3.5秒跳转

        // 模拟网易新闻跳过。
        tvProgress = (CircleTextProgressbar) findViewById(R.id.tvProgress);
        tvProgress.setOutLineColor(Color.TRANSPARENT);
        tvProgress.setInCircleColor(Color.parseColor("#AAC6C6C6"));
        tvProgress.setProgressColor(Color.BLUE);
        tvProgress.setProgressLineWidth(3);
        tvProgress.reStart();
    }

}