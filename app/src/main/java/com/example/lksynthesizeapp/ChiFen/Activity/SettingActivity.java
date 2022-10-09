package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lksynthesizeapp.ChiFen.bean.Setting;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.BaseRecyclerAdapter;
import com.example.lksynthesizeapp.Constant.Base.BaseViewHolder;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.Base.ProgressDialogUtil;
import com.example.lksynthesizeapp.Constant.Net.SSHCallBack;
import com.example.lksynthesizeapp.Constant.Net.SSHExcuteCommandHelper;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    String address,tag;
    String toastData = "";
    BaseRecyclerAdapter baseRecyclerAdapter;
    SharePreferencesUtils sharePreferencesUtils;
    List<Setting> settingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        address = getIntent().getStringExtra("address");
        tag = getIntent().getStringExtra("tag");
        //数据组装
        setData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SettingActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        baseRecyclerAdapter = new BaseRecyclerAdapter<Setting>(SettingActivity.this, R.layout.setting_item, settingList) {
            @Override
            public void convert(BaseViewHolder holder, final Setting setting) {
                holder.setText( R.id.tvTitle, setting.getTitle());
                if (setting.getTitle().equals("软件版本")){
                    holder.setGoneImage( R.id.ivGo);
                }
                holder.setImage( SettingActivity.this, R.id.imageView,setting.getImagePath());
                holder.setOnClickListener(R.id.linearLayout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (setting.getTitle().equals("报警音设置")){
                            startActivity(new Intent(SettingActivity.this,AudioActivity.class));
                            finish();
                        }
                        if (setting.getTitle().equals("设备重启")){
                            ShowDialog("uci set mjpg-streamer.core.fps=30", "uci commit", "/etc/init.d/mjpg-streamer restart");
                        }
                        if (setting.getTitle().equals("程序后台运行")){
                            startActivity(new Intent(SettingActivity.this,KeepActivity.class));
                            finish();
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(baseRecyclerAdapter);
    }

    private void setData() {
        Setting setting = new Setting();
        setting.setTitle("软件版本");
        setting.setImagePath(R.drawable.ic_appversion);
        settingList.add(setting);


        if (tag.equals("desc")){
            Setting setting1 = new Setting();
            setting1.setTitle("报警音设置");
            setting1.setImagePath(R.drawable.ic_audio);
            settingList.add(setting1);
        }

        Setting setting2 = new Setting();
        setting2.setTitle("设备重启");
        setting2.setImagePath(R.drawable.ic_restart);
        settingList.add(setting2);

        Setting setting3 = new Setting();
        setting3.setTitle("版本检测");
        setting3.setImagePath(R.drawable.ic_version);
        settingList.add(setting3);

        Setting setting4 = new Setting();
        setting4.setTitle("程序后台运行");
        setting4.setImagePath(R.drawable.ic_version);
        settingList.add(setting4);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {

    }

    /**
     * 重启服务刷新视频
     *
     * @param data1
     */
    private void ShowDialog(String data1, String data2, String data3) {
        try {
            ProgressDialogUtil.startLoad(this, "重启中");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SSHExcuteCommandHelper.writeBefor(address, data1, new SSHCallBack() {
                        @Override
                        public void confirm(String data) {
                            SSHExcuteCommandHelper.writeBefor(address, data2, new SSHCallBack() {
                                @Override
                                public void confirm(String data) {
                                    SSHExcuteCommandHelper.writeBefor(address, data3, new SSHCallBack() {
                                        @Override
                                        public void confirm(String data) {
                                            handlerSetting.sendEmptyMessage(Constant.TAG_ONE);
                                        }

                                        @Override
                                        public void error(String s) {
                                            toastData = s;
                                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                        }
                                    });
                                }

                                @Override
                                public void error(String s) {
                                    toastData = s;
                                    handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                                }
                            });
                        }

                        @Override
                        public void error(String s) {
                            toastData = s;
                            handlerSetting.sendEmptyMessage(Constant.TAG_TWO);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler handlerSetting = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TAG_ONE:
                    Toast.makeText(SettingActivity.this, "重启成功", Toast.LENGTH_SHORT).show();
                    ProgressDialogUtil.stopLoad();
                    address = "http://" + address + ":8080";
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(SettingActivity.this, toastData, Toast.LENGTH_LONG).show();
                    ProgressDialogUtil.stopLoad();
                    break;
            }
        }
    };
}