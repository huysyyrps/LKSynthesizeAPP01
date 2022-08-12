package com.example.lksynthesizeapp.Constant.Base;

import android.content.Context;

/**
 * view的基类
 */

public interface BaseView {
    /**
     * 初始化view
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 显示进度框
     */
    void showLoading(String message);

    void onDestroyView();

    /**
     * 隐藏进度框
     */
    void hideLoading();

    /**
     * 提示消息
     * @param message
     */
    void showMessage(String message);

    /**
     * 跳转activity
     * @param targetActivity
     */
    void toActivity(Context context, Class targetActivity);

    /**
     * 跳转activity
     * @param targetActivity
     */
    void toActivity(Class targetActivity);

    /**
     * 跳转activity
     * @param targetActivity
     * @param obj 携带参数
     */
    void toActivity(Class targetActivity, Object obj);

    /**
     * 跳转activity
     * @param targetActivity
     * @param
     */
    void toActivity(Class targetActivity, int state, String flag);

    /**
     * 跳转activity
     * @param targetActivity
     * @param
     */
    void toActivity(Class targetActivity, String message, String flag);

    /**
     * 跳转activity
     * @param targetActivity
     * @param
     */
    void toActivity(Class targetActivity, String message, String flag, int state, String stateFlag);


    /**
     * 打印错误日志
     * @param message
     */

    void showTag(String message);

    /**
     * 展示自定义dialog
     * @param description
     */
    void  showAlertDialg(String description, AlertDialogCallBack alertDialogCallBack);

    /**
     * 不带返回值得activity
     */
    void closeActivity();

    /**
     * 带返回值的关闭activity
     * @param resultCode
     */
    void closeActivity(int resultCode);

//    /**
//     * 弹出只有一个确定按钮的dialog
//     */
//    void showConfirmDialog(String description);
}
