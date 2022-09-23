package com.example.lksynthesizeapp.Constant.Base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lksynthesizeapp.ChiFen.Modbus.ModbusCallBack;
import com.example.lksynthesizeapp.R;


public class AlertDialogUtil {
    private Context context;
    Dialog dialog;

    public AlertDialogUtil(Context context) {
        this.context = context;
    }

    public void showDialog(String description, final AlertDialogCallBack alertDialogCallBack) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_with_show, null, false);
            TextView tv_content = (TextView) view.findViewById(R.id.content);
            TextView tv_yes = (TextView) view.findViewById(R.id.yes);
            TextView tv_no = (TextView) view.findViewById(R.id.no);
            tv_content.setText(description);
            tv_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    alertDialogCallBack.cancel();
                }
            });

            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    alertDialogCallBack.confirm("");
                }
            });
            dialog.getWindow().setContentView(view);
        }
    }

    public void showWifiSetting(Context context,String ssid,String pwd,final DialogCallBack dialogCallBack) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_with_wifisetting, null, false);
            TextView tvSSID = (TextView) view.findViewById(R.id.tvSSID);
            TextView tvPWD = (TextView) view.findViewById(R.id.tvPWD);
            Button btnCapySSID = (Button) view.findViewById(R.id.btnCapySSID);
            Button btnCopyPwd = (Button) view.findViewById(R.id.btnCapyPWD);
            TextView tvSetting = (TextView) view.findViewById(R.id.tvSetting);
            TextView tvCancle = (TextView) view.findViewById(R.id.tvCancle);
            ImageView ivClose = (ImageView) view.findViewById(R.id.ivClose);
            tvSSID.setText(ssid);
            tvPWD.setText(pwd);
            btnCapySSID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(ssid);
                    dialogCallBack.confirm("",dialog);
                }
            });
            btnCopyPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(pwd);
                    dialogCallBack.confirm("",dialog);
                }
            });
            tvSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCallBack.confirm("",dialog);
                }
            });
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    dialogCallBack.cancel();
                }
            });
            dialog.getWindow().setContentView(view);
        }
    }

    public void showWriteDialog(String hint, final ModbusCallBack modbusCallBack) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_with_write, null, false);
            EditText editText = (EditText) view.findViewById(R.id.edittext);
            SpannableString s = new SpannableString(hint);//这里输入自己想要的提示文字
            editText.setHint(s);
            TextView tvYes = (TextView) view.findViewById(R.id.yes);
            TextView tvCover = (TextView) view.findViewById(R.id.cover);
            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getText().toString()!=null){
                        if (!editText.getText().toString().equals("")){
                            dialog.dismiss();
                            modbusCallBack.success(editText.getText().toString());
                        }
                    }else {
                        Toast.makeText(context, "请输入行走距离", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    modbusCallBack.fail("");
                }
            });

            dialog.getWindow().setContentView(view);
        }
    }
    
}
