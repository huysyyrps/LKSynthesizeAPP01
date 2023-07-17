package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.lksynthesizeapp.ChiFen.Activity.PhotoActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.VideoActivity;
import com.example.lksynthesizeapp.ChiFen.Modbus.BytesHexChange;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.Constant.activity.DefinedActivity;
import com.example.lksynthesizeapp.Constant.activity.SendSelectActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;

public class MainUI {
    public void showPopupMenu(View view,String tag, Context context) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(context, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.dialog, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("图片")){
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("tag",tag);
                    context.startActivity(intent);
                }else if (item.getTitle().equals("视频")){
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("tag",tag);
                    context.startActivity(intent);
//                    Intent intent = new Intent(context, VideoActivity.class);
//                    context.startActivity(intent);
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    public void showPopupMenuMain(View view,String tag, DefinedActivity context) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(context, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.dialog_item, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("图片")){
                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("tag",tag);
                    context.startActivity(intent);
                    context.finish();
                }else if (item.getTitle().equals("视频")){
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("tag",tag);
                    context.startActivity(intent);
                    context.finish();
                }else if (item.getTitle().equals("进入程序")){
                    SharePreferencesUtils sharePreferencesUtils = new SharePreferencesUtils();
                    sharePreferencesUtils.setString(context, "max", "");
                    sharePreferencesUtils.setString(context, "deviceCode", "");
                    sharePreferencesUtils.setString(context, "deviceName", "");
                    sharePreferencesUtils.setString(context, "deviceModel", "");
                    sharePreferencesUtils.setString(context, "wifiName", "");
                    sharePreferencesUtils.setString(context, "haveDescern", "");
                    Intent intent = new Intent(context, SendSelectActivity.class);
                    intent.putExtra("tag",tag);
                    context.startActivity(intent);
                    context.finish();
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    public void showPopupMenu(View view, String tag, Context mainActivity, ModbusCallBack modbusCallBack) {
        BytesHexChange bytesHexChange = BytesHexChange.getInstance();
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mainActivity, view);
        // menu布局
        if (tag.equals("CEControl")){
            popupMenu.getMenuInflater().inflate(R.menu.modbus_ce_dialog, popupMenu.getMenu());
        }else if(tag.equals("Light")){
            popupMenu.getMenuInflater().inflate(R.menu.close_open, popupMenu.getMenu());
        }else if(tag.equals("CHControl")){
            popupMenu.getMenuInflater().inflate(R.menu.close_open, popupMenu.getMenu());
        }else if(tag.equals("Battery")){
            popupMenu.getMenuInflater().inflate(R.menu.close_open, popupMenu.getMenu());
        }else if(tag.equals("BlackOrWhiteLight")){
            popupMenu.getMenuInflater().inflate(R.menu.pxq_dialog, popupMenu.getMenu());
        }else if(tag.equals("MagnetizeRate")){
            popupMenu.getMenuInflater().inflate(R.menu.modbus_rate, popupMenu.getMenu());
        }
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (tag.equals("MagnetizeRate")){
                    String selectData = item.getTitle().toString();
                    modbusCallBack.success(bytesHexChange.addZeroForNum(String.valueOf(Integer.toHexString(Integer.valueOf(selectData))),2).toUpperCase());
                }else {
                    if (item.getTitle().equals("落下")){
                        modbusCallBack.success("0");
                    }else if (item.getTitle().equals("抬起")){
                        modbusCallBack.fail("1");
                    }else if (item.getTitle().equals("开启")){
                        modbusCallBack.fail("1");
                    }else if (item.getTitle().equals("关闭")){
                        modbusCallBack.success("0");
                    }else if (item.getTitle().equals("白光")){
                        modbusCallBack.fail("10");
                    }else if (item.getTitle().equals("黑光")){
                        modbusCallBack.success("01");
                    }
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }
}
