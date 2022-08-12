package com.example.lksynthesizeapp.Constant.Base;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lksynthesizeapp.Constant.activity.SendSelectActivity;

public class EditTextLengClient {
    public void textLeng(EditText editText, Context context){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().length() >= 15) {
                    Toast.makeText(context, "当前项最多可以输入15字", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
