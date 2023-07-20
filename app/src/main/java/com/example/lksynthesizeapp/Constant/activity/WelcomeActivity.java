package com.example.lksynthesizeapp.Constant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.Constant.adapter.ImageAdapter;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.View.CircleTextProgressbar;
import com.example.lksynthesizeapp.View.StatusBarUtils;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.tvProgress)
    CircleTextProgressbar tvProgress;
    @BindView(R.id.banner)
    Banner banner;
    private Handler mHandler;

    //    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        mHandler = new Handler();
        //设置状态栏颜色
        new StatusBarUtils().setWindowStatusBarColor(WelcomeActivity.this, R.color.black);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                //你需要跳转的地方的代码
                Intent intent = new Intent(WelcomeActivity.this, DefinedActivity.class);
                startActivity(intent);
                mHandler.removeCallbacks(this);
                finish();
            }
        }, 4000); //延迟6秒跳转

        // 模拟网易新闻跳过。
        tvProgress = (CircleTextProgressbar) findViewById(R.id.tvProgress);
        tvProgress.setProgressLineWidth(4);
        tvProgress.reStart();


        ArrayList<Integer> imgList = BannerData();
        banner.setIndicator(new CircleIndicator(this));
        banner.setLoopTime(1500);
        banner.setAdapter(new ImageAdapter(imgList), true);
    }

    private ArrayList<Integer> BannerData() {
        ArrayList<Integer> bannerList = new ArrayList<Integer>();
        bannerList.add(R.drawable.banner2);
        bannerList.add(R.drawable.banner5);
        bannerList.add(R.drawable.banner6);
        return bannerList;
    }

    @OnClick(R.id.tvProgress)
    public void onClick() {
        //你需要跳转的地方的代码
        Intent intent = new Intent(WelcomeActivity.this, DefinedActivity.class);
        startActivity(intent);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        finish();
    }
}