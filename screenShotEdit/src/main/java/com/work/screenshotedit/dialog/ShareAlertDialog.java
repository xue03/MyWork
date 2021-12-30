package com.work.screenshotedit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.work.screenshotedit.R;

/**
 * Date:2021/12/23
 * Description:自定义分享弹窗
 * Author:XueTingTing
 */
public class ShareAlertDialog extends Dialog {

    private LinearLayout ll_weiFriend;
    private LinearLayout ll_weiFriendCircle;
    private TextView share_cancel;
    private OnShareFriendCircleClickListener onShareFriendCircleClickListener;
    private OnShareFriendsClickListener onShareFriendsClickListener;
    private OnShareCancelClickListener onShareCancelClickListener;

    public void setOnShareCancelClickListener(OnShareCancelClickListener onShareCancelClickListener) {
        this.onShareCancelClickListener = onShareCancelClickListener;
    }

    public void setOnShareFriendCircleClickListener(OnShareFriendCircleClickListener onShareFriendCircleClickListener) {
        this.onShareFriendCircleClickListener = onShareFriendCircleClickListener;
    }

    public void setOnShareFriendsClickListener(OnShareFriendsClickListener onShareFriendsClickListener) {
        this.onShareFriendsClickListener = onShareFriendsClickListener;
    }

    public ShareAlertDialog(@NonNull Context context) {
        super(context, R.style.privacy_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_item);
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
        ll_weiFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareFriendsClickListener!=null){
                    onShareFriendsClickListener.shareFriends();
                }
            }
        });
        ll_weiFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareFriendCircleClickListener!=null){
                    onShareFriendCircleClickListener.shareFriendCircle();
                }
            }
        });
        share_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareCancelClickListener!=null){
                    onShareCancelClickListener.cancelShare();
                }
            }
        });

    }

    public interface OnShareFriendsClickListener {
        void shareFriends();
    }

    public interface OnShareFriendCircleClickListener {
        void shareFriendCircle();
    }
    public interface OnShareCancelClickListener{
        void cancelShare();
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

        ll_weiFriend=findViewById(R.id.ll_weiFriend);
        ll_weiFriendCircle=findViewById(R.id.ll_weiFriendCircle);
        share_cancel=findViewById(R.id.share_cancel);

    }

    @Override
    public void show() {
        super.show();
        initData();
    }
}
