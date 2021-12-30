package com.work.screenshotedit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.work.screenshotedit.DoodleView;
import com.work.screenshotedit.R;


/**
 * Date:2021/12/17
 * Description:自定义截屏编辑弹窗
 * Author:XueTingTing
 */
public class ScreenAlertDialog extends Dialog {
    private Bitmap bitmap;
    private int MODE;
    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    private OnMosaicClickListener onMosaicClickListener;
    private OnFeedbackClickListener onFeedbackClickListener;
    private LinearLayout ll_back;
    private LinearLayout ll_recall;
    private LinearLayout ll_recall_no;

    public void setOnFeedbackClickListener(OnFeedbackClickListener onFeedbackClickListener) {
        this.onFeedbackClickListener = onFeedbackClickListener;
    }

    private DoodleView img4;
    private LinearLayout ll_mosaic;
    private LinearLayout ll_sign;
    private LinearLayout ll_share;
    private LinearLayout ll_feedback;

    public void setOnShareImgClickListener(OnShareImgClickListener onShareImgClickListener) {
        this.onShareImgClickListener = onShareImgClickListener;
    }

    private OnShareImgClickListener onShareImgClickListener;

    public void setOnMosaicClickListener(OnMosaicClickListener onMosaicClickListener) {
        this.onMosaicClickListener = onMosaicClickListener;
    }

    public void setOnRepealClickListener(OnRepealClickListener onRepealClickListener) {
        this.onRepealClickListener = onRepealClickListener;
    }

    private OnRepealClickListener onRepealClickListener;


    private OnDrawLineClickListener onDrawLineClickListener;//划线监听
    private Context mcontext;

    public ScreenAlertDialog(@NonNull Context context) {
        super(context, R.style.screen_dialog);
        this.mcontext = context;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }

    public void setOnDrawLineClickListener(OnDrawLineClickListener onDrawLineClickListener) {
        this.onDrawLineClickListener = onDrawLineClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_item);
        //按空白处不能取消动画
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    public void setStatueBackground(int mode) {
        if (mode == 1) {//马赛克
            ll_mosaic.setBackgroundResource(R.drawable.shape_screen_bg);
            ll_sign.setBackgroundResource(R.drawable.shape_screen_bg_no);
        }
        if (mode == 2) {//涂鸦
            ll_sign.setBackgroundResource(R.drawable.shape_screen_bg);
            ll_mosaic.setBackgroundResource(R.drawable.shape_screen_bg_no);
        }
        if (mode == 3) {//分享
            ll_mosaic.setBackgroundResource(R.drawable.shape_screen_bg_no);
            ll_sign.setBackgroundResource(R.drawable.shape_screen_bg_no);
        }
        if (mode == 4) {//撤销
            ll_mosaic.setBackgroundResource(R.drawable.shape_screen_bg_no);
            ll_sign.setBackgroundResource(R.drawable.shape_screen_bg_no);

        }

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        ll_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDrawLineClickListener != null) {
                    onDrawLineClickListener.setPinLin();
                }
            }
        });
        ll_recall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRepealClickListener != null) {
                    onRepealClickListener.setRepeal();
                }
            }
        });
        ll_mosaic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onMosaicClickListener != null) {
                    onMosaicClickListener.setMosaic();
                }
            }
        });
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareImgClickListener != null) {
                    onShareImgClickListener.setShare();
                }
            }
        });
        ll_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onFeedbackClickListener != null) {
                    onFeedbackClickListener.setFeedback();
                }
            }
        });


    }

    public void setVisibility(int mode) {
        MODE = mode;

    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (bitmap != null) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) img4.getLayoutParams();
            params.dimensionRatio = "W," + bitmap.getWidth() + ":" + bitmap.getHeight();
            img4.setOriginBitmap(bitmap);
        }
        if (MODE == 1) {
            ll_share.setVisibility(View.VISIBLE);
            ll_feedback.setVisibility(View.GONE);
        }
        if (MODE == 2) {
            ll_share.setVisibility(View.GONE);
            ll_feedback.setVisibility(View.VISIBLE);
        }
        //int revertPath = img4.getRevertPath();
        img4.setDoodleCallback(new DoodleView.DoodleCallback() {
            @Override
            public void onRevertStateChanged(boolean canRevert) {
                if (canRevert) {
                    ll_recall.setVisibility(View.VISIBLE);
                    ll_recall_no.setVisibility(View.GONE);
                } else {
                    ll_recall.setVisibility(View.GONE);
                    ll_recall_no.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
//        View statubar =findViewById(R.id.status_bar_bg);
//        statubar.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getStatusBarHeight(getContext())));
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        img4 = (DoodleView) findViewById(R.id.img4);
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        ll_mosaic = (LinearLayout) findViewById(R.id.ll_mosaic);
        ll_sign = (LinearLayout) findViewById(R.id.ll_sign);
        ll_feedback = (LinearLayout) findViewById(R.id.ll_feedback);
        ll_recall = findViewById(R.id.ll_recall);
        ll_recall_no = findViewById(R.id.ll_recall_no);


    }

    /**
     * 设置bitmap
     *
     * @param map
     */
    public void setBitmap(Bitmap map) {
        bitmap = map;
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

    public interface OnDrawLineClickListener {
        void setPinLin();
    }

    public interface OnRepealClickListener {
        void setRepeal();
    }

    public interface OnMosaicClickListener {
        void setMosaic();
    }

    public interface OnShareImgClickListener {
        void setShare();
    }

    public interface OnFeedbackClickListener {
        void setFeedback();
    }

    public void setMosaic() {
        img4.setEditable(true);
        img4.setMode(DoodleView.MODE.MOSAIC_MODE);
    }

    public void setPinLin() {
        if (img4 != null) {
            img4.setEditable(true);
            img4.setMode(DoodleView.MODE.DOODLE_MODE);
        }
    }

    public void setRepeal() {
        if (img4 != null) {
            img4.setMode(DoodleView.MODE.REVER);
            int revertPath = img4.revertPath();
        }
    }

    @Override
    public void show() {
        super.show();
        initData();
    }

    public Bitmap getEditImage() {
        Bitmap bitmapFromView = createBitmapFromView(img4);
        return bitmapFromView;
    }

    public Bitmap createBitmapFromView(View view) {
        //是ImageView直接获取
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            canvas.setBitmap(null);
        }
        return bitmap;
    }

}
