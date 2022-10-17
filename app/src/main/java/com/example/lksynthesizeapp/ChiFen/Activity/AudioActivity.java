package com.example.lksynthesizeapp.ChiFen.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lksynthesizeapp.Constant.Base.AlertDialogCallBack;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.BaseRecyclerAdapter;
import com.example.lksynthesizeapp.Constant.Base.BaseViewHolder;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioActivity extends BaseActivity {

    @BindView(R.id.header)
    Header header;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private MediaPlayer mediaPlayer;
    BaseRecyclerAdapter baseRecyclerAdapter;
    List<String> stringList = new ArrayList<>();
    SharePreferencesUtils sharePreferencesUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        //数据组装
        setData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AudioActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        baseRecyclerAdapter = new BaseRecyclerAdapter<String>(AudioActivity.this, R.layout.audio_item, stringList) {
            @Override
            public void convert(BaseViewHolder holder, final String o) {
                holder.setText( R.id.tvName, o);
                holder.setOnClickListener(R.id.imageView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (o.equals("基础蜂鸣音报警")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.fengming);
                        }
                        if (o.equals("标准女音报警")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.nv);
                        }
                        if (o.equals("标准男音报警")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.nan);
                        }
                        if (o.equals("ami蜂鸣音报警")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.ami);
                        }
                        if (o.equals("电子蜂鸣音1")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.dzy1);
                        }
                        if (o.equals("电子蜂鸣音2")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.dzy2);
                        }
                        if (o.equals("计时器蜂鸣音1")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.jsq1);
                        }
                        if (o.equals("计时器蜂鸣音2")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.jsq2);
                        }
                        if (o.equals("仿电报蜂鸣音")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.db);
                        }
                        if (o.equals("仿电话蜂鸣音")){
                            mediaPlayer = MediaPlayer.create(AudioActivity.this, R.raw.dh);
                        }
                        mediaPlayer.start();
                    }
                });

                holder.setOnClickListener(R.id.linearLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialogUtil(AudioActivity.this).showDialog("您确定将"+o+"\n设置为报警提示音吗", new AlertDialogCallBack() {
                            @Override
                            public void confirm(String name) {
                                if (o.equals("基础蜂鸣音报警")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","fengming");
                                }
                                if (o.equals("标准女音报警")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","nv");
                                }
                                if (o.equals("标准男音报警")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","nan");
                                }
                                if (o.equals("ami蜂鸣音报警")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","ami");
                                }
                                if (o.equals("电子蜂鸣音1")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","dzy1");
                                }
                                if (o.equals("电子蜂鸣音2")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","dzy2");
                                }
                                if (o.equals("计时器蜂鸣音1")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","jsq1");
                                }
                                if (o.equals("计时器蜂鸣音2")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","jsq2");
                                }
                                if (o.equals("仿电报蜂鸣音")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","db");
                                }
                                if (o.equals("仿电话蜂鸣音")){
                                    sharePreferencesUtils.setString(AudioActivity.this,"audio","dh");
                                }

                                Toast.makeText(AudioActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void save(String name) {

                            }

                            @Override
                            public void checkName(String name) {

                            }

                        });
                    }
                });
            }
        };
        recyclerView.setAdapter(baseRecyclerAdapter);
    }

    private void setData() {
        stringList.add("标准男音报警");
        stringList.add("标准女音报警");
        stringList.add("基础蜂鸣音报警");
        stringList.add("ami蜂鸣音报警");
        stringList.add("电子蜂鸣音1");
        stringList.add("电子蜂鸣音2");
        stringList.add("计时器蜂鸣音1");
        stringList.add("计时器蜂鸣音2");
        stringList.add("仿电报蜂鸣音");
        stringList.add("仿电话蜂鸣音");
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_audio;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {

    }
}