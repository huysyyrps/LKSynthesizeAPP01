package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.lksynthesizeapp.ChiFen.Activity.PhotoActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.VideoActivity;
import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.R;

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

    public void showPopupMenuLight(View view, Context mainActivity, ModbusCallBack alertDialogCallBack) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mainActivity, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.pxq_dialog, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("白光模式")){
                    alertDialogCallBack.success("");
                }else if (item.getTitle().equals("黑光模式")){
                    alertDialogCallBack.fail("");
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
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mainActivity, view);
        // menu布局
        if (tag.equals("CEControl")){
            popupMenu.getMenuInflater().inflate(R.menu.modbus_ce_dialog, popupMenu.getMenu());
        }else if(tag.equals("SearchlightControl")){
            popupMenu.getMenuInflater().inflate(R.menu.modbus_tzd_dialog, popupMenu.getMenu());
        }else if(tag.equals("CHControl")){
            popupMenu.getMenuInflater().inflate(R.menu.modbus_ch_dialog, popupMenu.getMenu());
        }else if(tag.equals("BatteryControl")){
            popupMenu.getMenuInflater().inflate(R.menu.modbus_battery_dialog, popupMenu.getMenu());
        }
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("磁轭落下")){
                    modbusCallBack.success("");
                }else if (item.getTitle().equals("磁轭抬起")){
                   modbusCallBack.fail("");
                }else if (item.getTitle().equals("探照灯开启")){
                    modbusCallBack.success("");
                }else if (item.getTitle().equals("探照灯关闭")){
                    modbusCallBack.fail("");
                }else if (item.getTitle().equals("开启磁化")){
                    modbusCallBack.success("");
                }else if (item.getTitle().equals("关闭磁化")){
                    modbusCallBack.fail("");
                }else if (item.getTitle().equals("电池阀开启")){
                    modbusCallBack.success("");
                }else if (item.getTitle().equals("电池阀关闭")){
                    modbusCallBack.fail("");
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
