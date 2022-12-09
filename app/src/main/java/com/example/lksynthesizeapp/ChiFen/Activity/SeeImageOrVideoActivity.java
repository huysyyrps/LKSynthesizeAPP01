package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lksynthesizeapp.ChiFen.Base.ImageSave;
import com.example.lksynthesizeapp.ChiFen.View.CustomerVideoView;
import com.example.lksynthesizeapp.ChiFen.View.DrawLineView;
import com.example.lksynthesizeapp.ChiFen.View.ScaleImageView;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SeeImageOrVideoActivity extends BaseActivity implements View.OnClickListener, DrawLineView.CloseActivity {
    String fileName = "";
    String path = "";
    SharePreferencesUtils sharePreferencesUtils;
    @BindView(R.id.drawLineView)
    ScaleImageView drawLineView;
    @BindView(R.id.videoView)
    CustomerVideoView videoView;
    @BindView(R.id.rbRemoke)
    RadioButton rbRemoke;
    @BindView(R.id.rbSave)
    RadioButton rbSave;
    @BindView(R.id.linBar)
    RelativeLayout linBar;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    AlertDialogUtil alertDialogUtil;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        ButterKnife.bind(this);

        alertDialogUtil = new AlertDialogUtil(this);
        sharePreferencesUtils = new SharePreferencesUtils();
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        path = getIntent().getStringExtra("path");
        Log.e("XXX", path);
        String tag = getIntent().getStringExtra("tag");
        if (tag.equals("photo")) {
            drawLineView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            int start = path.lastIndexOf("/");
            int end = path.lastIndexOf(".");
            if (start != -1 && end != -1) {
                fileName = path.substring(start + 1, end);
                String filepath = path.substring(0, start);
                Log.e("XXX", fileName + "--" + filepath);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(path, options);
            drawLineView.loadImage(this, bitmap, bitmap.getWidth(), bitmap.getHeight());
            linBar.setVisibility(View.VISIBLE);
        } else if (tag.equals("video")) {
            drawLineView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            setupVideo();
        }
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_see_image_or_video;
    }

    @Override
    protected boolean isHasHeader() {
        return false;
    }

    @Override
    protected void rightClient() {

    }


    private void setupVideo() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaybackVideo();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stopPlaybackVideo();
                return true;
            }
        });

        try {
            Uri uri = Uri.parse(path);
            videoView.setVideoURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying()) {
            videoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.canPause()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaybackVideo();
    }

    private void stopPlaybackVideo() {
        try {
            videoView.stopPlayback();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.rbRemoke, R.id.rbSave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbSave:
                drawLineView.setInitScale();
                linBar.setVisibility(View.GONE);
                View view1 = frameLayout;
                view1.setDrawingCacheEnabled(true);
                view1.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
                File file = new File(path);
                if (bitmap != null) {
                    boolean backstate = new ImageSave().saveBitmapFile(file,this, bitmap);
                    if (backstate) {
                        linBar.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                        Log.e("XXX", "保存成功");
                    } else {
                        linBar.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                        Log.e("XXX", "保存失败");
                    }
                }
                break;
            case R.id.rbRemoke:
                drawLineView.remoke(this);
                break;
        }
    }

    @Override
    public void closeThisActivity() {
        finish();
    }
}
