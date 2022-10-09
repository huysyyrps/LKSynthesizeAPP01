package com.example.lksynthesizeapp.Constant.Base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.View.StatusBarUtils;


/**
 *
 * activity的基类
 */

public abstract  class BaseActivity extends AppCompatActivity implements BaseView,View.OnClickListener, Header.ClickLister {
    private AlertDialogUtil alertDialogUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        new StatusBarUtils().setWindowStatusBarColor(BaseActivity.this, R.color.black);
        if(isHasHeader()){
            Header header=(Header)this.findViewById(R.id.header);
            header.setClickLister(this);
        }
        initView();
    }

    @Override
    public void initView() {
        initData();
    }

    @Override
    public void showLoading(String message) {
        ProgressDialogUtil.startLoad(this,message);

    }
    @Override
    public void onDestroyView() {
    }

    @Override
    public void hideLoading() {
        ProgressDialogUtil.stopLoad();

    }

    @Override
    public void showMessage(String message) {
        if(!TextUtils.isEmpty(message)){
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toActivity(Context context, Class targetActivity) {
        Intent intent= new Intent(context,targetActivity.getClass());
        context.startActivity(intent);
    }

    @Override
    public void toActivity(Class targetActivity) {
        Intent intent= new Intent(this,targetActivity);
        this.startActivity(intent);
    }

    @Override
    public void toActivity(Class targetActivity, Object obj) {
        Intent intent=new Intent(this,targetActivity);
        Bundle bundle=new Bundle();
        bundle.putSerializable("data",obj.getClass());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showTag(String message) {
        Log.e("Tag",message);
    }

    @Override
    public void showAlertDialg(String description, AlertDialogCallBack alertDialogCallBack) {
        alertDialogUtil= new AlertDialogUtil(this);
        alertDialogUtil.showDialog(description,alertDialogCallBack);
    }

//    @Override
//    public void showConfirmDialog(String description) {
//        alertDialogUtil= new AlertDialogUtil(this);
//        alertDialogUtil.showSmallDialog(description);
//    }

    @Override
    public void onClick(View view) {

    }
    //得到当前界面的布局文件id(由子类实现)
    protected abstract int provideContentViewId();
    /*
        是否有header(布局中是否有自定义header)
        true  有header
        false 没有header
     */
    protected  abstract  boolean isHasHeader();

    /**
     * 右部点击事件
     * @return
     */
    protected  abstract  void rightClient();

    /**
     * header头部的返回点击事件
     */
    @Override
    public void LeftClickLister() {
        finish();
    }

    /**
     * header 头部的右侧点击事件
     */
    @Override
    public void rightClickLister() {
        // finish();
        rightClient();
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void closeActivity(int resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    public void toActivity(Class targetActivity, int state, String flag) {
        Intent intent=new Intent(this,targetActivity);
        intent.putExtra(flag,state);
        startActivity(intent);
    }

    @Override
    public void toActivity(Class targetActivity, String message, String flag) {
        Intent intent=new Intent(this,targetActivity);
        intent.putExtra(flag,message);
        startActivity(intent);
    }

    @Override
    public void toActivity(Class targetActivity, String message, String flag, int state, String flag2) {
        Intent intent=new Intent(this,targetActivity);
        intent.putExtra(flag,message);
        intent.putExtra(flag2,state);
        startActivity(intent);
    }

    @Override
    public void initData() {

    }
}
