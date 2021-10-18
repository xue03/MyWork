package com.work.mywork.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.work.mywork.R;


/**
 * Date:2021/8/17
 * Description:
 * Author:XueTingTing
 */
public class CustomDialog extends Dialog {
    private Button btn_ok;
    private Button btn_cancel;
    private TextView txt_title;
    private TextView txt_desc;
    private ImageView img_icon;
    private String titleSrt;
    private String descStr;
    private String imgIcon;

    public void setOnDialogCancelListener(OnDialogCancelListener onDialogCancelListener) {
        this.onDialogCancelListener = onDialogCancelListener;
    }

    public void setOnDialogOklListener(OnDialogOklListener onDialogOklListener) {
        this.onDialogOklListener = onDialogOklListener;
    }

    private OnDialogCancelListener onDialogCancelListener;
    private OnDialogOklListener onDialogOklListener;

    public CustomDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_pop);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    private void initEvent() {

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogOklListener != null) {
                    onDialogOklListener.onDialogOkListener();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogCancelListener != null) {
                    onDialogCancelListener.onDialogCancelListener();
                }
            }
        });

    }

    private void initData() {
        if (titleSrt != null) {
            txt_title.setText(titleSrt);
        }
        if (descStr != null) {
            txt_desc.setText(descStr);
        }
        if (imgIcon != null) {
            Glide.with(getContext()).load(imgIcon).into(img_icon);
        }

    }

    private void initView() {
        txt_title = findViewById(R.id.txt_app_title);
        txt_desc = findViewById(R.id.txt_desc);
        img_icon = findViewById(R.id.iv_app);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_ok = findViewById(R.id.btn_ok);
    }

    public interface OnDialogCancelListener {
        void onDialogCancelListener();
    }

    public interface OnDialogOklListener {
        void onDialogOkListener();
    }

    public void setTitle(String title) {
        if (title != null) {
            titleSrt = title;
        }
    }
    public void setDesc(String desc){
        if (desc!=null){
            descStr=desc;
        }
    }
    public void setIcon(String icon){
        if (icon!=null){
            imgIcon=icon;
        }
    }

    @Override
    public void show() {
        super.show();
        initData();
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams lp = window.getAttributes();
       // lp.width = (int) (getScreenWidth()*0.95);
        lp.y = 1; //设置Dialog距离底部的距离
        window.setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
