package com.work.mywork;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.tabs.TabLayout;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.utils.CheckPermission;
import com.work.mywork.view.one.OneFragment;
import com.work.mywork.view.three.ThreeFragment;
import com.work.mywork.view.two.TwoFragment;
import com.work.screenshotedit.ScreenShotEditActivity;
import com.work.screenshotedit.ScreenShotListenManager;

import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.OnClick;

public class AppActivity extends BaseActivity implements ScreenShotListenManager.OnScreenShotListener {
    @BindView(R.id.vp)
    ViewPager viewPager;
    @BindView(R.id.tab)
    TabLayout tabLayout;
    private ScreenShotListenManager screenShotListenManager;
    private  boolean isScreenShotListen;
    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        isScreenShotListen=true;
        screenShotListenManager = ScreenShotListenManager.newInstance(this);
        screenShotListenManager.setListener(this);
        startScreenShotListen();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        titles.add("页面一");
        titles.add("页面二");
        titles.add("页面三");
        fragments.add(new OneFragment());
        fragments.add(new TwoFragment());
        fragments.add(new ThreeFragment());

        viewPager.setAdapter(new AppViewPageAdapter(getSupportFragmentManager(),titles,fragments));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View inflate = LayoutInflater.from(AppActivity.this).inflate(R.layout.tablayout_item, null);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void startScreenShotListen() {
        String[] str=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (CheckPermission.checkPermission(this,this,str,100)){
            screenShotListenManager.startListen();
        }else {
            ActivityCompat.requestPermissions(this,str,100);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_app;
    }

    @Override
    public void onShot(String imagePath) {
        if (isScreenShotListen){
            if (TextUtils.isEmpty(imagePath)){
                return;
            }
            Intent intent = new Intent(this, ScreenShotEditActivity.class);
            intent.putExtra("path",imagePath);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isScreenShotListen=false;
        if (screenShotListenManager!=null){
            screenShotListenManager.stopListen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            startScreenShotListen();
        }
    }
    //    @OnClick({R.id.tab, R.id.toolbar, R.id.vp})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.vp:
//                break;
//            case R.id.toolbar:
//                break;
//            case R.id.tab:
//                break;
//        }
//    }
}
