package com.work.mywork.interfaces;

public interface IBasePresenter<T extends IBaseView> {
    //v层接口的关联
    void attachView(T view);
    //v层接口的取消
    void detachView();
}
