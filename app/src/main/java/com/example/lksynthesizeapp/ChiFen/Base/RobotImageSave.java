package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lksynthesizeapp.R;

public class RobotImageSave {
    public void saveImage(Context context, ImageView imageView, String path, String project, String workName, String workCode, Toast toast){
        View view1 = imageView;
        view1.setDrawingCacheEnabled(true);
        view1.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
        if (bitmap != null) {
            boolean backstate = new ImageSave().saveBitmap("/LUKERobotDescImage/"+ project + "/" + workName + "/" + workCode + "/", context, bitmap);
            if (backstate) {
                toast = Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT);
                toast.show();
                Log.e("XXX", "保存成功");
            } else {
                toast = Toast.makeText(context, R.string.save_faile, Toast.LENGTH_SHORT);
                toast.show();
                Log.e("XXX", "保存失败");
            }
        } else {
            System.out.println("bitmap is NULL!");
        }
    }
}
