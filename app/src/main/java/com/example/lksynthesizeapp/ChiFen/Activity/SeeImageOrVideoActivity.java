package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.lksynthesizeapp.ChiFen.View.CustomerVideoView;
import com.example.lksynthesizeapp.Constant.Base.AlertDialogUtil;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeeImageOrVideoActivity extends BaseActivity implements View.OnClickListener {
    String fileName = "";
    String path = "";
    SharePreferencesUtils sharePreferencesUtils;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.videoView)
    CustomerVideoView videoView;
    AlertDialogUtil alertDialogUtil;


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
        String tag = getIntent().getStringExtra("tag");
        if (tag.equals("photo")) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            int start = path.lastIndexOf("/");
            int end = path.lastIndexOf(".");
            if (start != -1 && end != -1) {
                fileName = path.substring(start + 1, end);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options); //此时返回 bm 为空
            options.inJustDecodeBounds = false; //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            bitmap = BitmapFactory.decodeFile(path, options);
            imageView.setImageBitmap(bitmap);
        } else if (tag.equals("video")) {
            imageView.setVisibility(View.GONE);
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

}
