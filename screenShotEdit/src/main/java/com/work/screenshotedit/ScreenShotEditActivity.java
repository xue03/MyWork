package com.work.screenshotedit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.work.screenshotedit.dialog.ChooseDialog;
import com.work.screenshotedit.dialog.ScreenAlertDialog;
import com.work.screenshotedit.dialog.ShareAlertDialog;
import com.work.screenshotedit.utils.BlurBitmapUtil;
import com.work.screenshotedit.utils.SystemShare;


/**
 * Date:2021/12/24
 * Description:截屏编辑页面+分享
 * Author:XueTingTing
 */
public class ScreenShotEditActivity extends AppCompatActivity {
    private String TAG = "WXScreenShotModule";
    private ImageView img_bg;
    private Bitmap bitmap;
    private String imgPath;
    private ScreenAlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_screen_edit);
        imgPath = getIntent().getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(imgPath);
        bitmap = BlurBitmapUtil.blurBitmap(this, bitmap, 20);
        initView();
    }


    private void initView() {
        img_bg = (ImageView) findViewById(R.id.img_bg);
        img_bg.setImageBitmap(bitmap);
        if (imgPath!=null){
           // showChoosDialog(imgPath);
            share(imgPath);
        }
    }
    private void showChoosDialog(final String path){
        final ChooseDialog chooseDialog = new ChooseDialog(this);
        chooseDialog.setOnShareClickListener(new ChooseDialog.OnShareClickListener() {
            @Override
            public void setShare() {
                chooseDialog.dismiss();
                share(path);
            }
        });
        chooseDialog.setOnFeedClickListener(new ChooseDialog.OnFeedClickListener() {
            @Override
            public void setFeed() {
                chooseDialog.dismiss();
                feedback(path);
            }
        });
        Window window = chooseDialog.getWindow();
        window.clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.RIGHT);
        chooseDialog.show();
    }
    public void share(String path){
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        showDialog(1,bitmap);
    }
    public void feedback(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        showDialog(2,bitmap);
    }
    public void showDialog(int mode, Bitmap bitmap){
        alertDialog = new ScreenAlertDialog(this);
        alertDialog.setVisibility(mode);
        alertDialog.setBitmap(bitmap);
        alertDialog.setYesOnclickListener("返回", new ScreenAlertDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                alertDialog.dismiss();
                finish();
            }
        });
        alertDialog.setOnDrawLineClickListener(new ScreenAlertDialog.OnDrawLineClickListener() {
            @Override
            public void setPinLin() {
                alertDialog.setStatueBackground(2);
                alertDialog.setPinLin();
            }
        });
        alertDialog.setOnRepealClickListener(new ScreenAlertDialog.OnRepealClickListener() {
            @Override
            public void setRepeal() {
                alertDialog.setStatueBackground(4);
                alertDialog.setRepeal();
            }
        });
        alertDialog.setOnMosaicClickListener(new ScreenAlertDialog.OnMosaicClickListener() {
            @Override
            public void setMosaic() {
                alertDialog.setStatueBackground(1);
                alertDialog.setMosaic();
            }
        });
        alertDialog.setOnFeedbackClickListener(new ScreenAlertDialog.OnFeedbackClickListener() {
            @Override
            public void setFeedback() {
                //问题反馈
                Bitmap editImage = alertDialog.getEditImage();
//                String toBase64 = Zoom.bitmapToBase64(editImage);
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnShareImgClickListener(new ScreenAlertDialog.OnShareImgClickListener() {
            @Override
            public void setShare() {
                alertDialog.setStatueBackground(3);
                Bitmap editImage = alertDialog.getEditImage();
                showShareDialog(editImage);
                // SystemShare.openSystenShare(editImage, mWXSDKInstance.getContext());
            }
        });
        alertDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
       // window.clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.width = (int) (display.getWidth()); // 设置宽度
        lp.height = (int) (display.getHeight()); // 设置高度
        window.setAttributes(lp);
    }
    private void showShareDialog(final Bitmap bitmap) {
        final ShareAlertDialog shareAlertDialog = new ShareAlertDialog(this);
        shareAlertDialog.setOnShareCancelClickListener(new ShareAlertDialog.OnShareCancelClickListener() {
            @Override
            public void cancelShare() {
                shareAlertDialog.dismiss();
            }
        });
        shareAlertDialog.setOnShareFriendCircleClickListener(new ShareAlertDialog.OnShareFriendCircleClickListener() {
            @Override
            public void shareFriendCircle() {
                shareAlertDialog.dismiss();
                SystemShare.shareWXFriendsCircle(bitmap, ScreenShotEditActivity.this);
            }
        });
        shareAlertDialog.setOnShareFriendsClickListener(new ShareAlertDialog.OnShareFriendsClickListener() {
            @Override
            public void shareFriends() {
                shareAlertDialog.dismiss();
                SystemShare.shareWXFriends(bitmap,ScreenShotEditActivity.this);

            }
        });
        Window window = shareAlertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        shareAlertDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = shareAlertDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        shareAlertDialog.getWindow().setAttributes(lp);
    }

}
