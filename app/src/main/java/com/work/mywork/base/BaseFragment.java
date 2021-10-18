package com.work.mywork.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.interfaces.IBaseView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment <P extends IBasePresenter> extends Fragment implements IBaseView {
    protected P mPresenter;
    private Unbinder bind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayout(), container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = ButterKnife.bind(this,view);
        initView();
        mPresenter=setPresenter();
        if (mPresenter!=null){
            mPresenter.attachView(this);
            initData();
        }
    }

    protected abstract P setPresenter();

    protected abstract void initData();

    protected abstract void initView();

    protected abstract int getLayout();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.detachView();
        }
        if (bind!=null){
            bind.unbind();
        }
    }
}
