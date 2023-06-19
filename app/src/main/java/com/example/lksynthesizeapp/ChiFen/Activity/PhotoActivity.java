package com.example.lksynthesizeapp.ChiFen.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.lksynthesizeapp.ChiFen.Module.PhotoContract;
import com.example.lksynthesizeapp.ChiFen.Presenter.PhotoPresenter;
import com.example.lksynthesizeapp.ChiFen.bean.PhotoAttachments;
import com.example.lksynthesizeapp.ChiFen.bean.SavePhotoBack;
import com.example.lksynthesizeapp.ChiFen.bean.UpPhoto;
import com.example.lksynthesizeapp.Constant.Base.BaseActivity;
import com.example.lksynthesizeapp.Constant.Base.BaseRecyclerAdapter;
import com.example.lksynthesizeapp.Constant.Base.BaseViewHolder;
import com.example.lksynthesizeapp.Constant.Base.Constant;
import com.example.lksynthesizeapp.Constant.View.Header;
import com.example.lksynthesizeapp.R;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PhotoActivity extends BaseActivity implements PhotoContract.View {

    @BindView(R.id.header)
    Header header;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.tvDelect)
    TextView tvDelect;

    List<String> imagePaths = new ArrayList<>();
    List<String> selectList = new ArrayList<>();
    BaseRecyclerAdapter baseRecyclerAdapter;
    SharePreferencesUtils sharePreferencesUtils;
    private PhotoPresenter photoPresenter;
    private String project = "", workName = "", workCode = "", compName = "", device = "";
    private int startNum = 0;
    private int lastNum = 24;
    private int allNum;
    File[] files;
    int photoNum = 0;
    List<File> fileListData;
    LoadingDialog loadingDialog;
    List<UpPhoto> sendPhotoDataList = new ArrayList<>();
    List<PhotoAttachments> photoAttachmentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        ButterKnife.bind(this);
        sharePreferencesUtils = new SharePreferencesUtils();
        compName = "鲁科检测";
        device = "磁探机";
        project = sharePreferencesUtils.getString(PhotoActivity.this, "project", "");
        workName = sharePreferencesUtils.getString(PhotoActivity.this, "workName", "");
        workCode = sharePreferencesUtils.getString(PhotoActivity.this, "workCode", "");
        String tag = getIntent().getStringExtra("tag");
        header.setTvTitle("图库");
        photoPresenter = new PhotoPresenter(this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PhotoActivity.this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        baseRecyclerAdapter = new BaseRecyclerAdapter<String>(PhotoActivity.this, R.layout.album_item, imagePaths) {
            @Override
            public void convert(BaseViewHolder holder, final String o) {
                holder.setImage(PhotoActivity.this, R.id.imageView, o);
                holder.setText(R.id.tvName, getFileName(o));
                holder.setOnClickListener(R.id.imageView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PhotoActivity.this, SeeImageOrVideoActivity.class);
                        intent.putExtra("path", o);
                        intent.putExtra("tag", "photo");
                        startActivity(intent);
                    }
                });

                holder.setCheckClickListener(R.id.cbSelect, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectList.contains(o)) {
                            selectList.remove(o);
                            if (selectList.size() == 0) {
                                header.setTvTitle("图库");
                            } else {
                                header.setTvTitle("图库" + "(" + selectList.size() + "/9)");
                            }
                        } else {
                            if (selectList.size() >= 9) {
                                holder.setCheckBoxFalse(R.id.cbSelect);
                                Toast.makeText(PhotoActivity.this, "最多只能选择9张图片", Toast.LENGTH_SHORT).show();
                            } else {
                                selectList.add(o);
                                String[] strarray = o.split("/");
                                int leng = strarray.length;
                                compName = strarray[leng - 5];
                                project = strarray[leng - 4];
                                device = strarray[leng - 3];
                                workName = strarray[leng - 2];
                                header.setTvTitle("图库" + "(" + selectList.size() + "/9)");
                                holder.setCheckBoxTrue(R.id.cbSelect);
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
                setData(fileListData);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (allNum == lastNum) {
                    Toast.makeText(PhotoActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                    smartRefreshLayout.finishLoadMore();//结束加载
                } else if (lastNum < allNum) {
                    startNum = lastNum;
                    lastNum += 24;
                    if (lastNum >= allNum) {
                        lastNum = allNum;
                    }
                    setData(fileListData);
                }
            }
        });
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText(getResources().getString(R.string.device_connect))
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
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKEImage/" + project + "/" + workName + "/" + workCode + "/");
                } else if (tag.equals("Desc")){
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKEDescImage/" + project + "/" + workName + "/" + workCode + "/");
                }else if (tag.equals("RobotDesc")){
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKERobotDescImage/"+ project + "/" + workName + "/" + workCode + "/");
                }else if (tag.equals("Robot")){
                    getFilesAllName(Environment.getExternalStorageDirectory() + "/LUKERobotImage/"+ project + "/" + workName + "/" + workCode + "/");
                }
            }
        }).start();
    }

    public void getFilesAllName(String path) {
        imagePaths.clear();
        fileListData = listFileSortByModifyTime(path);
        Collections.reverse(fileListData);
        if (fileListData.size() != 0) {
            allNum = fileListData.size();
            setData(fileListData);
        } else {
            handler.sendEmptyMessage(Constant.TAG_TWO);
        }
    }

    private void setData(List<File> fileListData) {
        try {
            if (allNum > 24) {
                for (int i = startNum; i < lastNum; i++) {
                    if (checkIsImageFile(fileListData.get(i).getPath()) && !fileListData.get(i).getPath().equals("null")) {
                        imagePaths.add(fileListData.get(i).getPath());
                    }
                }
            } else {
                for (int i = 0; i < fileListData.size(); i++) {
                    if (checkIsImageFile(fileListData.get(i).getPath()) && fileListData.get(i).getPath() != null) {
                        Bitmap bitmap = null;
                        bitmap = BitmapFactory.decodeFile(fileListData.get(i).getPath());
                        if (bitmap != null) {
                            Log.e("XXX", fileListData.get(i).getPath());
                            imagePaths.add(fileListData.get(i).getPath());
                        }
                    }
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

    @Override
    public void onResume() {
        super.onResume();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.TAG_ONE:
                    baseRecyclerAdapter.notifyDataSetChanged();
                    loadingDialog.close();
                    break;
                case Constant.TAG_TWO:
                    Toast.makeText(PhotoActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                    loadingDialog.close();
                    break;
            }
        }
    };

    /**
     * 判断是否是照片
     */
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (fileEnd.equals("png")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    @OnClick({R.id.tvSend, R.id.tvDelect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSend:
//                selectList.clear();
                sendPhotoDataList.clear();
                photoNum = 0;
                if (selectList.size() == 0) {
                    Toast.makeText(PhotoActivity.this, "您还未选择图片", Toast.LENGTH_SHORT).show();
                } else {
                    sendPhoto(photoNum);

                }
                break;
            case R.id.tvDelect:
                if (selectList.size() == 0) {
                    Toast.makeText(this, "请先选择想要删除的文件", Toast.LENGTH_SHORT).show();
                } else {
                    for (String path : selectList) {
                        imagePaths.remove(path);
                        File file = new File(path);
                        file.delete();
                    }
                    selectList.clear();
                    header.setTvTitle("图库");
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(baseRecyclerAdapter);
                }
                break;
        }
    }

    private void sendPhoto(int photoNum){
        File  photoFile = new File(selectList.get(photoNum));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", photoFile.getName(), requestFile);
        photoPresenter.getPhoto(part);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_photo;
    }

    @Override
    protected boolean isHasHeader() {
        return true;
    }

    @Override
    protected void rightClient() {

    }


    @Override
    public void setPhoto(UpPhoto photoUp) {
        sendPhotoDataList.add(photoUp);
        photoNum++;
        if (photoNum<selectList.size()){
            sendPhoto(photoNum);
        }else {
            for (int i = 0; i < sendPhotoDataList.size(); i++) {
                PhotoAttachments photoAttachments = new PhotoAttachments();
                photoAttachments.setFileName(sendPhotoDataList.get(i).getFileName());
                photoAttachments.setName(sendPhotoDataList.get(i).getOriginalFilename());
                photoAttachmentsList.add(photoAttachments);
            }
            Gson gson = new Gson();
            String s = gson.toJson(photoAttachmentsList);
            s = s.replace("\"","\'");
            Log.e("TAG",s);
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("code",workCode);
            params.put("project", project);
            params.put("workpiece", workName);
            params.put("type", 0);
            params.put("attachments", s);
//            Gson gson = new Gson();
            String s1 = gson.toJson(params);
            Log.e("TAG",s1);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(params));
            photoPresenter.getsavePhoto(requestBody);
        }
//        header.setTvTitle("图库");
//        Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void setPhotoMessage(String message) {
        selectList.clear();
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(baseRecyclerAdapter);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void savePhoto(SavePhotoBack savePhotoBack) {
        if (savePhotoBack.getMsg().equals("操作成功")){
            selectList.clear();
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(baseRecyclerAdapter);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void savePhotoMessage(String message) {
        selectList.clear();
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(baseRecyclerAdapter);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        baseRecyclerAdapter.notifyDataSetChanged();
    }


    /**
     * 从路径中提取文件名
     *
     * @param pathandname
     * @return
     */
    public String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.length();
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return "null";
        }

    }

}