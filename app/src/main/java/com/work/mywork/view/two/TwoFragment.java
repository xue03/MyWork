package com.work.mywork.view.two;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.work.mywork.R;
import com.work.mywork.base.BaseFragment;
import com.work.mywork.interfaces.IBasePresenter;

import butterknife.BindView;
import butterknife.OnClick;


public class TwoFragment extends BaseFragment {
    @BindView(R.id.rv)
    RecyclerView rv;

//    @BindView(R.id.btn_tbs)
//    Button openBTF;

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
        return R.layout.fragment_two;
    }

    @OnClick(R.id.rv)
    public void onViewClicked() {

    }
}