package com.work.mywork.view.one;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.work.mywork.R;
import com.work.mywork.activity.ShareBoardActivity;
import com.work.mywork.base.BaseFragment;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.utils.CheckPermission;
import com.work.mywork.utils.ContentsUtil;
import com.work.mywork.utils.LocationUtil;
import com.work.mywork.utils.Permissions;
import com.work.mywork.utils.PictureSelectUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class OneFragment extends BaseFragment {

    @BindView(R.id.btn_toPhoto)
    Button toPhoto;
    @BindView(R.id.img_show)
    ImageView imageView;
    @BindView(R.id.album)
    Button openAlbum;
    @BindView(R.id.save_photo)
    Button savePhoto;
    @BindView(R.id.contents)
    Button contents;
    @BindView(R.id.txt_content)
    TextView textContent;
    @BindView(R.id.location)
    Button btnLocation;
    @BindView(R.id.txt_location)
    TextView txtLocation;
    @BindView(R.id.btn_wheel)
    Button btnWheel;
    @BindView(R.id.btn_share)
    Button btnShare;
    private String path;


    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_one;
    }

    @OnClick({R.id.btn_toPhoto, R.id.album, R.id.contents, R.id.location, R.id.save_photo,R.id.btn_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_toPhoto://拍照+裁剪
                boolean b = CheckPermission.checkPermission(getContext(), getActivity(), Permissions.CAMERA, 100);
                if (b) {
                    Toast.makeText(getContext(), "打开相机", Toast.LENGTH_LONG).show();
                    PictureSelectUtil.takePhoto(getActivity(), this);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.CAMERA, 100);
                }
                break;
            case R.id.album://相册+裁剪
                boolean a = CheckPermission.checkPermission(getContext(), getActivity(), Permissions.STORAGE, 100);
                if (a) {
                    Toast.makeText(getContext(), "打开相册", Toast.LENGTH_LONG).show();
                    PictureSelectUtil.openAlbum(getActivity(), this);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.CAMERA, 100);
                }
                break;
            case R.id.save_photo://保存图片
                if (CheckPermission.checkPermission(getContext(), getActivity(), Permissions.CAMERA, 400)) {

                    byte[] decode = Base64.decode(path, Base64.NO_WRAP);


                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.CAMERA, 400);
                }

                break;
            case R.id.contents://通讯录获取联系人
                if (CheckPermission.checkPermission(getContext(), getActivity(), Permissions.CONTACTS, 200)) {
                    Toast.makeText(getContext(), "打开通讯录", Toast.LENGTH_LONG).show();
                    ContentsUtil.openContent(this);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.CONTACTS, 200);
                }
                break;
            case R.id.location://定位
                if (CheckPermission.checkPermission(getContext(), getActivity(), Permissions.LOCATION, 300)) {
                    if (LocationUtil.isOpenLocationService(getContext())) {
                        Toast.makeText(getContext(), "开始定位", Toast.LENGTH_LONG).show();
                        LocationUtil.getLocation(getContext());
                        SharedPreferences sp = getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
                        String address = sp.getString("address", "");
                        Log.d("TAG", "onClick: " + address);
                        txtLocation.setText(address);
                    } else {
                        LocationUtil.showLocationServiceDialog(getContext());
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.LOCATION, 200);
                }
                break;
            case R.id.btn_wheel:
                break;
            case R.id.btn_share:
                startActivity(new Intent(getActivity(), ShareBoardActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        path = PictureSelectUtil.onActivityResult(getActivity(), this, requestCode, resultCode, data, true);
        Glide.with(getContext()).load(path).into(imageView);
        String[] contacts = ContentsUtil.onActivityResult(getContext(), requestCode, resultCode, data);
        textContent.setText("姓名：" + contacts[0] + "   号码：" + contacts[1]);
    }

}