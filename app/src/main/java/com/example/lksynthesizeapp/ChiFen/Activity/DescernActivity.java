package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lksynthesizeapp.ChiFen.Base.BottomUI;
import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.YoloV5Ncnn;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DescernActivity extends AppCompatActivity implements View.OnClickListener {
    private String url;
    private ImageView imageView;
    private RadioButton rbAlbum, rbBack;
    private TextView tvCompName, tvWorkName, tvWorkCode, tvFPX;
    private Bitmap bmp = null;
    private Thread mythread;
    private YoloV5Ncnn yolov5ncnn = new YoloV5Ncnn();
    URL videoUrl;
    HttpURLConnection conn;
    Paint paint;
    Paint textbgpaint;
    Paint textpaint;
    private MediaPlayer mediaPlayer;
    long currentTme = 0, currentTme1 = 0;
    public boolean runing = true;
    public static String project = "", workName = "", workCode = "", address = "";
    public boolean isFirst = true;
    public long saveTime = 0;
    public long currentTmeTime = 0;
    public static final int TIME = 3000;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不息屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏底部按钮
        new BottomUI().hideBottomUIMenu(this.getWindow());
        setContentView(R.layout.activity_descern);
        mediaPlayer = MediaPlayer.create(DescernActivity.this, R.raw.fengming);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);

        textbgpaint = new Paint();
        textbgpaint.setColor(Color.WHITE);
        textbgpaint.setStyle(Paint.Style.FILL);

        textpaint = new Paint();
        textpaint.setColor(Color.RED);
        textpaint.setTextSize(13);
        textpaint.setTextAlign(Paint.Align.LEFT);
//        w = getWindowManager().getDefaultDisplay().getWidth();
//        h = getWindowManager().getDefaultDisplay().getHeight();
//        scanw = w/364;
//        scanh = h/237;
        boolean ret_init = yolov5ncnn.Init(getAssets());
        if (!ret_init) {
            Log.e("MainActivity", "yolov5ncnn Init failed");
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        tvCompName = findViewById(R.id.tvCompName);
        tvWorkName = findViewById(R.id.tvWorkName);
        tvWorkCode = findViewById(R.id.tvWorkCode);
        rbAlbum = findViewById(R.id.rbAlbum);
        rbBack = findViewById(R.id.rbBack);
//        tvFPX = findViewById(R.id.tvFPX);
        rbAlbum.setOnClickListener(this);
        rbBack.setOnClickListener(this);
        Intent intent = getIntent();
        project = intent.getStringExtra("project");
        workName = intent.getStringExtra("etWorkName");
        workCode = intent.getStringExtra("etWorkCode");
        if (!project.trim().equals("")) {
            tvCompName.setText(project);
        }
        if (!workName.trim().equals("")) {
            tvWorkName.setText(workName);
        }
        if (!workCode.trim().equals("")) {
            tvWorkCode.setText(workCode);
        }

        address = getIntent().getStringExtra("address");
        if (address!=null){
            url = "http://" + address + ":8080?action=snapshot";
            mythread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runing) {
                        draw();
                        currentTme = System.currentTimeMillis();
                    }
                }
            });
            mythread.start();
        }else {
            Toast.makeText(this, "IP为空,请等待连接", Toast.LENGTH_SHORT).show();
            finish();
        }

        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //创建一个URL对象
            videoUrl = new URL(url);
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = (HttpURLConnection) videoUrl.openConnection();
            //设置输入流
            conn.setDoInput(true);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            bmp = BitmapFactory.decodeStream(inputstream);
//            bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
//            bitmap = imageScale(bitmap, 364,237);
            YoloV5Ncnn.Obj[] objects = yolov5ncnn.Detect(bmp, false);
            showObjects(objects);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
        } finally {
        }
    }

    /**
     * 调整图片大小
     *
     * @param bitmap 源
     * @param dst_w  输出宽度
     * @param dst_h  输出高度
     * @return
     */
    public Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
        return dstbmp;
    }


    private void showObjects(YoloV5Ncnn.Obj[] objects) {
        if (objects == null) {
            imageView.setImageBitmap(bmp);
//            currentTme1 = System.currentTimeMillis();
//            Log.e("XXX",(currentTme1-currentTme)+"");
            return;
        }

        // draw objects on bitmap
        Bitmap rgba = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(rgba);
        for (int i = 0; i < objects.length; i++) {
            canvas.drawRect(objects[i].x, objects[i].y, objects[i].x + objects[i].w, objects[i].y + objects[i].h, paint);
            // draw filled text inside image
            {
                String text = objects[i].label + " = " + String.format("%.1f", objects[i].prob * 100) + "%";

                float text_width = textpaint.measureText(text) + 10;
                float text_height = -textpaint.ascent() + textpaint.descent() + 10;

                float x = objects[i].x;
                float y = objects[i].y - text_height;
                if (y < 0)
                    y = 0;
                if (x + text_width > rgba.getWidth())
                    x = rgba.getWidth() - text_width;
//                canvas.drawRect(x, y, x + text_width, y + text_height, textbgpaint);
                canvas.drawText(text, x, y - textpaint.ascent(), textpaint);
            }
        }
        if (objects.length != 0) {
            mediaPlayer.start();
            if (isFirst) {
                saveImageToGallery(DescernActivity.this, rgba);
                saveTime = System.currentTimeMillis();
                isFirst = false;
            } else {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                } else {
                    currentTmeTime = System.currentTimeMillis();
                    if (currentTmeTime - saveTime > 3000) {
                        saveImageToGallery(DescernActivity.this, rgba);
                        saveTime = currentTmeTime;
                    }
                }
            }
        }
        imageView.setImageBitmap(rgba);
//        currentTme1 = System.currentTimeMillis();
//        int timeData = (int) (currentTme1-currentTme);
//        Log.e("XXXXX",currentTme1+"------------"+currentTme);
//        Log.e("XXXXX",currentTme1-currentTme+"");
//        Log.e("XXXXX",timeData+"");
//        int fpsNum = 1000/timeData+2;
//        Log.e("XXXXX",1000/timeData+"");
//        Message message = new Message();
//        message.what = Constant.TAG_ONE;
//        message.obj = fpsNum;
//        handlerSetting.sendMessage(message);
    }


    public static void saveImageToGallery(Context context, Bitmap bmp) {
        boolean backstate = new ImageSave().saveBitmap("/LUKEDecsImage/", project, workName, workCode, context, bmp);
        if (backstate) {
            Log.e("XXX", "保存成功");
        } else {
            Log.e("XXX", "保存失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mythread != null) {
            mythread.interrupt();
        }
        runing = false;
    }

    //    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (rbAlbum.getVisibility()==View.VISIBLE){
//                    rbAlbum.setVisibility(View.GONE);
//                }else {
//                    rbAlbum.setVisibility(View.VISIBLE);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            rbAlbum.setVisibility(View.GONE);
//                        }
//                    },3000);
//                }
//                break;
//        }
//
//        return true;
//    }
    @Override
    protected void onRestart() {
        super.onRestart();
        new BottomUI().hideBottomUIMenu(this.getWindow());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rbAlbum:
                Intent intent = new Intent(this, PhotoActivity.class);
                intent.putExtra("tag", "Descern");
                startActivity(intent);
                break;
            case R.id.rbBack:
                if (mythread != null) {
                    mythread.interrupt();
                }
                runing = false;
                finish();
                break;
        }
    }

//    Handler handlerSetting = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case Constant.TAG_ONE:
//                    int fpsNum = (int) msg.obj;
//                    tvFPX.setText(fpsNum+"");
//                    break;
//            }
//        }
//    };
}
