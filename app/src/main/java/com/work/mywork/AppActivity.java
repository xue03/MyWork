package com.work.mywork;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.utils.CheckPermission;
import com.work.mywork.utils.Permissions;
import com.work.mywork.utils.SystemUtils;
import com.work.mywork.view.one.OneFragment;
import com.work.mywork.view.three.ThreeFragment;
import com.work.mywork.view.two.TwoFragment;

import java.util.ArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.OnClick;

public class AppActivity extends BaseActivity {
    @BindView(R.id.vp)
    ViewPager viewPager;
    @BindView(R.id.tab)
    TabLayout tabLayout;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {

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

    @Override
    protected int getLayout() {
        return R.layout.activity_app;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
