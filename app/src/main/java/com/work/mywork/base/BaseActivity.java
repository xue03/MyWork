package com.work.mywork.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.interfaces.IBaseView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView{
    protected P mPresenter;
    Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        //状态栏沉浸

        bind = ButterKnife.bind(this);
        initView();
        mPresenter=setPresenter();
        if (mPresenter!=null){
            mPresenter.attachView(this);
            initData();
        }

    }

    protected abstract void initData();

    protected abstract P setPresenter();

    protected abstract void initView();

    protected abstract int getLayout();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null){
            bind.unbind();
        }
        if (mPresenter!=null){
            mPresenter.detachView();
        }
    }
}
