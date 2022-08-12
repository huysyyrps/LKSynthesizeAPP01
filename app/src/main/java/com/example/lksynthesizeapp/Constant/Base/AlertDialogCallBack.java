package com.example.lksynthesizeapp.Constant.Base;

/**
 * Created by dell on 2017/4/25.
 */

public interface AlertDialogCallBack {
    void confirm(String name);
    void cancel();
    void save(String name);
    void checkName(String name);
}
