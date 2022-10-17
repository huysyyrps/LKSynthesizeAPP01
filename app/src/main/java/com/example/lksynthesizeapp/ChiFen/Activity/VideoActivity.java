package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lksynthesizeapp.ChiFen.Module.VideoContract;
import com.example.lksynthesizeapp.ChiFen.Presenter.VideoPresenter;
import com.example.lksynthesizeapp.ChiFen.bean.Video;
import com.example.lksynthesizeapp.ChiFen.bean.VideoLocal;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.BaseRecyclerAdapter;
import com.example.lksynthesizeapp.Constant.Base.BaseViewHolder;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class VideoActivity extends BaseActivity implements VideoContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.header)
    Header header;
    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.tvDelect)
    TextView tvDelect;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    List<VideoLocal> imagePaths = new ArrayList<>();
    List<VideoLocal> selectList = new ArrayList<>();
    BaseRecyclerAdapter baseRecyclerAdapter;
    SharePreferencesUtils sharePreferencesUtils;
    private int startNum = 0;
    private int lastNum = 9;
    private int allNum;
    List<File> fileList;
    String tag = "";
    LoadingDialog loadingDialog;
    private VideoPresenter videoPresenter;
    private String project = "", workName = "", workCode = "", compName = "", device = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        ButterKnife.bind(this);
        loadingDialog = new LoadingDialog(this);
        sharePreferencesUtils = new SharePreferencesUtils();
        compName = "鲁科检测";
        device = "磁探机";
        tag = getIntent().getStringExtra("tag");
        project = sharePreferencesUtils.getString(VideoActivity.this, "project", "");
        workName = sharePreferencesUtils.getString(VideoActivity.this, "workName", "");
        workCode = sharePreferencesUtils.getString(VideoActivity.this, "workCode", "");
        videoPresenter = new VideoPresenter(this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(VideoActivity.this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        baseRecyclerAdapter = new BaseRecyclerAdapter<VideoLocal>(VideoActivity.this, R.layout.album_item, imagePaths) {
            @Override
            public void convert(BaseViewHolder holder, final VideoLocal haveAudio) {
                if (haveAudio.getFile().getName() + "" != null) {
                    holder.setBitmap(R.id.imageView, haveAudio.getBitmap());
                    holder.setVisitionTextView(R.id.tvTime);
                    holder.setText(R.id.tvName, haveAudio.getFile().getName() + "");
                    holder.setText(R.id.tvTime, haveAudio.getTime());
                }
                holder.setOnClickListener(R.id.imageView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VideoActivity.this, SeeImageOrVideoActivity.class);
                        intent.putExtra("path", haveAudio.getFile().getAbsolutePath());
                        intent.putExtra("tag", "video");
                        startActivity(intent);
                    }
                });

                holder.setCheckClickListener(R.id.cbSelect, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectList.contains(haveAudio)) {
                            selectList.remove(haveAudio);
                            if (selectList.size() == 0) {
                                header.setTvTitle("有声视频");
                            } else {
                                header.setTvTitle("有声视频" + "(" + selectList.size() + "/3)");
                            }
                        } else {
                            if (selectList.size() >= 3) {
                                holder.setCheckBoxFalse(R.id.cbSelect);
                                Toast.makeText(VideoActivity.this, "最多只能选择3个视频", Toast.LENGTH_SHORT).show();
                            } else {
                                selectList.add(haveAudio);
                                header.setTvTitle("有声视频" + "(" + selectList.size() + "/3)");
                            }
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(baseRecyclerAdapter);
        smartRefreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                startNum = 0;
                lastNum = 24;
                imagePaths.clear();
                setData(fileList);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (allNum == lastNum) {
                    Toast.makeText(VideoActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                    smartRefreshLayout.finishLoadMore();//结束加载
                } else if (lastNum < allNum) {
                    startNum = lastNum;
                    lastNum += 24;
                    if (lastNum >= allNum) {
                        lastNum = allNum;
                    }
                    setData(fileList);
                }
            }
        });
        loadingDialog.setLoadingText(getResources().getString(R.string.dialog_loding))
//                        .setSuccessText("加载成功")//显示加载成功时的文字
                //.setFailedText("加载失败")
                .setSize(200)
                .setShowTime(1)
                .setInterceptBack(false)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                .setRepeatCount(1)
                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (tag.equals("Local")) {
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKEVideo/" + project + "/" + workName + "/" + workCode + "/");
                } else if (tag.equals("Desc")){
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKEDescVideo/" + project + "/" + workName + "/" + workCode + "/");
                } else if (tag.equals("RobotDesc")){
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKERobotDescVideo/" + project + "/" + workName + "/" + workCode + "/");
                }else if (tag.equals("Robot")){
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKERobotVideo/" + project + "/" + workName + "/" + workCode + "/");
                }
            }
        }).start();
    }

    public void getFilesAllName(String path) {
        fileList = listFileSortByModifyTime(path);
        Collections.reverse(fileList);
        if (fileList.size() != 0) {
            allNum = fileList.size();
            setData(fileList);
        } else {
            handler.sendEmptyMessage(Constant.TAG_TWO);
        }
    }

    private void setData(List<File> list) {
        try {
            if (allNum > 9) {
                for (int i = startNum; i < lastNum; i++) {
                    String longTime = getRingDuring(list.get(i));
                    VideoLocal videoLocal = new VideoLocal();
                    if (longTime != null && !longTime.equals("null")) {
                        videoLocal.setFile(list.get(i));
                        videoLocal.setTime(longTime);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_imageloding);
                        videoLocal.setBitmap(bitmap);
                    }
                    imagePaths.add(videoLocal);
                }
            } else {
                for (int i = startNum; i < fileList.size() + 1; i++) {
                    String longTime = getRingDuring(list.get(i));
                    VideoLocal videoLocal = new VideoLocal();
                    if (longTime != null && !longTime.equals("null")) {
                        videoLocal.setFile(list.get(i));
                        videoLocal.setTime(longTime);
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_imageloding);
                        videoLocal.setBitmap(bitmap);
                    }
                    imagePaths.add(videoLocal);
                }
                lastNum = allNum;
                smartRefreshLayout.finishLoadMore();//结束加载
            }

        } catch (Exception e) {
//            Toast.makeText(HaveAudioActivity.this, e.toString() + "", Toast.LENGTH_SHORT).show();
        }
        smartRefreshLayout.finishLoadMore();//结束加载
        handler.sendEmptyMessage(Constant.TAG_ONE);
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFilesye(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFilesye(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFilesye(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.TAG_ONE:
                    baseRecyclerAdapter.notifyDataSetChanged();
                    loadingDialog.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setImage();
                        }
                    }).start();
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(VideoActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                    loadingDialog.close();
                    break;
                case Constant.TAG_THERE:
                    baseRecyclerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void setImage() {
        for (int i = 0; i < imagePaths.size(); i++) {
            VideoLocal haveAudio = new VideoLocal();
            haveAudio.setFile(imagePaths.get(i).getFile());
            haveAudio.setTime(imagePaths.get(i).getTime());
            haveAudio.setBitmap(getRingBitmap(imagePaths.get(i).getFile()));
            imagePaths.set(i, haveAudio);
        }
        handler.sendEmptyMessage(Constant.TAG_THERE);
    }

    @OnClick({R.id.tvSend, R.id.tvDelect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSend:
                if (selectList.size() == 0) {
                    Toast.makeText(VideoActivity.this, "您还未选择有声视频", Toast.LENGTH_SHORT).show();
                } else {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM); //表单类型
                    for (int i = 0; i < selectList.size(); i++) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), selectList.get(i).getFile());
                        builder.addFormDataPart("file" + i, selectList.get(i).getFile().getName(), requestBody);//"imgfile"+i 后台接收图片流的参数名
                    }
                    builder.addFormDataPart("company", compName);
                    builder.addFormDataPart("project", project);
                    builder.addFormDataPart("device", device);
                    builder.addFormDataPart("workpiece", workName);
                    builder.addFormDataPart("workpiecenum", workCode);
                    builder.addFormDataPart("voice", "audiovideo");
                    List<MultipartBody.Part> parts = builder.build().parts();
                    videoPresenter.getHaveVideo(parts);
                }
                break;
            case R.id.tvDelect:
                if (selectList.size() == 0) {
                    Toast.makeText(this, "请先选择想要删除的文件", Toast.LENGTH_SHORT).show();
                } else {
                    for (VideoLocal haveAudio : selectList) {
                        imagePaths.remove(haveAudio);
                        haveAudio.getFile().delete();
                    }
                    selectList.clear();
                    header.setTvTitle("有声视频");
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(baseRecyclerAdapter);
                }
                break;
        }
    }

    /**
     * 获取视频时长
     *
     * @param mUri
     * @return
     */
    public static String getRingDuring(File mUri) {
        String duration = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri != null) {
                mmr.setDataSource(mUri.getAbsolutePath());
                duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            }
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
        } finally {
            mmr.release();
        }
        if (duration != null) {
            return timeParse(Long.parseLong(duration));
        } else {
            return "null";
        }
    }

    /**
     * 获取视频第一帧图片
     *
     * @param mUri
     * @return
     */
    public static Bitmap getRingBitmap(File mUri) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (mUri != null) {
                mmr.setDataSource(mUri.getAbsolutePath());
                //获得视频第一帧的Bitmap对象
                bitmap = mmr.getFrameAtTime(1000 * 5);
            }
        } catch (Exception ex) {
            Log.e("XXX", ex.toString());
        } finally {
            mmr.release();
        }
        return bitmap;
    }

    /**
     * 将毫秒转换成分钟
     *
     * @param duration
     * @return
     */
    public static String timeParse(long duration) {
        String time = "";

        long minute = duration / 60000;
        long seconds = duration % 60000;

        long second = Math.round((float) seconds / 1000);

        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";

        if (second < 10) {
            time += "0";
        }
        time += second;

        return time;
    }

    @Override
    public void setHaveVideo(Video HaveVideoUp) {
        Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
        selectList.clear();
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(baseRecyclerAdapter);
    }

    @Override
    public void setHaveVideoMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_video;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {
    }

}
