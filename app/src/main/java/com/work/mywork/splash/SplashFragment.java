package com.work.mywork.splash;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.work.mywork.R;

public class SplashFragment extends Fragment {

    private RelativeLayout bgimage;
    private Button come;
    private int[] resoultimage= new int[]{R.drawable.yidao1,R.drawable.yidao2,R.drawable.yidao2};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_splash, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        come = inflate.findViewById(R.id.btn_goin);
        bgimage = inflate.findViewById(R.id.rl_splash);
        int index = getArguments().getInt("index", 0);
        bgimage.setBackgroundResource(resoultimage[index]);
        if (index==2){
            come.setVisibility(View.VISIBLE);
        }
    }
}