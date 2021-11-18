package com.work.mywork.splash;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.work.mywork.R;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.splash_viewpage)
    ViewPager splashViewpage;
    @BindView(R.id.indicator)
    LinearLayout indicator;
    private ArrayList<Fragment> fragments;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        fragments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SplashFragment splashFragment = new SplashFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index",i);
            splashFragment.setArguments(bundle);
            fragments.add(splashFragment);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
        layoutParams.rightMargin=2*10;
        for (int i = 0; i < fragments.size(); i++) {
            View view = new View(this);
            view.setId(i);
            view.setBackgroundResource(i==0 ? R.drawable.indicator_fouce : R.drawable.indicator_nomll);
            view.setLayoutParams(layoutParams);
            indicator.addView(view, i);
        }
        splashViewpage.setAdapter(new ViewPageAddapter(getSupportFragmentManager(), fragments));
        splashViewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

              //  indicator.getChildAt(position).setBackgroundResource(R.drawable.indicator_fouce);
                for (int i = 0; i < fragments.size(); i++) {
                    if (position==i){
                        indicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_fouce);
                    }else {
                        indicator.getChildAt(i).setBackgroundResource(R.drawable.indicator_nomll);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }
}