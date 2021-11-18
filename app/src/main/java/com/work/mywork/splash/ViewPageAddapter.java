package com.work.mywork.splash;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.work.mywork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2021/11/17
 * Description:
 * Author:XueTingTing
 */
public class ViewPageAddapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;

    public ViewPageAddapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
