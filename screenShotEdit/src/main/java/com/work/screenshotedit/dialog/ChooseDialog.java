package com.work.screenshotedit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.work.screenshotedit.R;


/**
 * Date:2021/12/20
 * Description:
 * Author:XueTingTing
 */
public class ChooseDialog extends Dialog {
    private Context mcontext;
    private LinearLayout btn_feed;
    private LinearLayout btn_share;
    private LinearLayout ll;
    private OnShareClickListener onShareClickListener;
    private OnFeedClickListener onFeedClickListener;

    public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    public void setOnFeedClickListener(OnFeedClickListener onFeedClickListener) {
        this.onFeedClickListener = onFeedClickListener;
    }

    public ChooseDialog(@NonNull Context context) {
        super(context, R.style.Theme_Dialog);
        this.mcontext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_item);
        //setCancelable(false);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareClickListener!=null){
                    onShareClickListener.setShare();
                }
            }
        });
        btn_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFeedClickListener!=null){
                    onFeedClickListener.setFeed();
                }
            }
        });

    }
    public interface OnShareClickListener{
        void setShare();
    }
    public interface OnFeedClickListener{
        void setFeed();
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {


    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        ll=findViewById(R.id.ll);
        btn_feed=findViewById(R.id.btn_feed);
        btn_share=findViewById(R.id.btn_share);

    }

    @Override
    public void show() {
        super.show();
        initData();
    }
}
