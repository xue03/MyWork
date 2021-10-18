package com.work.mywork.base;

import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.interfaces.IBaseView;

import java.lang.ref.WeakReference;

public class BasePresenter<V extends IBaseView> implements IBasePresenter {
    protected V mView;
    WeakReference<V> weakReference;
    @Override
    public void attachView(IBaseView view) {
        weakReference= (WeakReference<V>) new WeakReference<>(view);
        mView=weakReference.get();
    }

    @Override
    public void detachView() {
        mView=null;
    }
}
