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
                //?????????????????????????????????
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                        .maxSelectNum(9)// ????????????????????????
                        .minSelectNum(1)// ??????????????????
                        .imageSpanCount(4)// ??????????????????
                        .selectionMode(PictureConfig.MULTIPLE)// ?????? or ??????PictureConfig.MULTIPLE : PictureConfig.SINGLE
                        .previewImage(true)// ?????????????????????
                        .isCamera(false)// ????????????????????????
                        .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// ????????????????????????

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
                //??????
                okhttp();
              //  selectList.addAll(images);

                //selectList = PictureSelector.obtainMultipleResult(data);

                // ?????? LocalMedia ??????????????????path
                // 1.media.getPath(); ?????????path
                // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                // ????????????????????????????????????????????????????????????????????????????????????
//                adapter.setList(selectList);
//                adapter.notifyDataSetChanged();
            }
        }
    }
}
