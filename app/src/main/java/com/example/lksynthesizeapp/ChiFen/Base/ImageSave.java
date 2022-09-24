package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageSave {
    /**
     * 保存图片方法
     */
    public boolean saveBitmap(String local, String project, String workName, String workCode, Context context, Bitmap bitmap) {
        getNowDate();
        try {
            String dir = Environment.getExternalStorageDirectory() + local + project + "/" + workName + "/" + workCode + "/";//图片保存的文件夹名
            File file = new File(Environment.getExternalStorageDirectory() + local + project + "/" + workName + "/" + workCode + "/");
            //如果不存在  就mkdirs()创建此文件夹
            if (!file.exists()) {
                file.mkdirs();
            }
            //将要保存的图片文件
            File mFile = new File(dir + getNowDate());
            FileOutputStream outputStream = new FileOutputStream(mFile);     //构建输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);  //compress到输出outputStream
            Uri uri = Uri.fromFile(mFile);                                  //获得图片的uri
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)); //发送广播通知更新图库，这样系统图库可以找到这张图片
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存图片方法
     */
    public boolean saveBitmap(String local, Context context, Bitmap bitmap) {
        getNowDate();
        try {
            String dir = Environment.getExternalStorageDirectory() + local + "/" ;//图片保存的文件夹名
            File file = new File(Environment.getExternalStorageDirectory() + local + "/");
            //如果不存在  就mkdirs()创建此文件夹
            if (!file.exists()) {
                file.mkdirs();
            }
            //将要保存的图片文件
            File mFile = new File(dir + getNowDate());
            FileOutputStream outputStream = new FileOutputStream(mFile);     //构建输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);  //compress到输出outputStream
            Uri uri = Uri.fromFile(mFile);                                  //获得图片的uri
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)); //发送广播通知更新图库，这样系统图库可以找到这张图片
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前时间,用来给文件夹命名
     */
    private String getNowDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return format.format(new Date()) + ".png";
    }
}
