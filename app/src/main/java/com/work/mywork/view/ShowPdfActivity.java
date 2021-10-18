package com.work.mywork.view;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.work.mywork.R;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date:2021/9/10
 * Description:
 * Author:XueTingTing
 */
public class ShowPdfActivity extends BaseActivity {
    @BindView(R.id.pdfview)
    PDFView pdfview;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        pdfview.fromAsset("privacey.pdf")
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .swipeHorizontal(false)
                .spacing(10)
                .load();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_pdf;
    }
}
