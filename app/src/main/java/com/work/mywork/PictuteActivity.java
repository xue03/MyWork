package com.work.mywork;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Date:2021/10/31
 * Description:
 * Author:XueTingTing
 */
public class PictuteActivity extends AppCompatActivity {
    @BindView(R.id.btn_open)
    Button btnOpen;
    private ArrayList<LocalMedia> mediaList;
    private  MediaType media_type_png = MediaType.parse("image/png");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);
        ButterKnife.bind(this);
        mediaList = new ArrayList<>();
    }
    public void okhttp(){
        File file = getExternalCacheDir();
        int cacheSize=10*1024*1024;

        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(14, TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .cache(new Cache(file.getAbsoluteFile(),cacheSize))
                .build();
        MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
        ArrayList<File> fileArrayList = new ArrayList<>();
        for (int i = 0; i < mediaList.size(); i++) {
            File file1 = new File(mediaList.get(i).getPath());
            fileArrayList.add(file1);
        }
        int i=0;
        for (File file2:fileArrayList) {
            if (file2.exists()){
                Log.d("TAG", "okhttp: "+file2.getName());
                body.addFormDataPart("image"+i,file2.getName(),RequestBody.create(media_type_png,file2));
                i++;
            }
        }
        MultipartBody requestBody = body.build();
        Request request = new Request.Builder()
                .url("")
                .post(requestBody)
                .build();
        build.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
    @OnClick(R.id.btn_open)
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_open:
                //参数很多，根据需要添加
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .maxSelectNum(9)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片
                        .isCamera(false)// 是否显示拍照按钮
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                images = PictureSelector.obtainMultipleResult(data);
                mediaList.addAll(images);
                ArrayList<Map<String,String>> list = new ArrayList<>();
                if (mediaList.size()>0){
                    for (int i = 0; i < mediaList.size(); i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        String path = mediaList.get(i).getPath();
                        int height = mediaList.get(i).getHeight();
                        hashMap.put("path",path);
                        hashMap.put("height",height+"");
                        list.add(hashMap);
                       // Log.d("TAG", "onActivityResult: "+path);
                    }
                    Log.d("TAG", "onActivityResult: "+list);
                }
                //上传
                okhttp();
              //  selectList.addAll(images);

                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                adapter.setList(selectList);
//                adapter.notifyDataSetChanged();
            }
        }
    }
}
