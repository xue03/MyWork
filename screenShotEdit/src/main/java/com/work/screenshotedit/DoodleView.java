package com.work.screenshotedit;

import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;

/**
 * Date:2021/12/16
 * Description:自定义编辑页面（画笔、马赛克、撤销）
 * Author:XueTingTing
 */
@SuppressLint("AppCompatCustomView")
public class DoodleView extends ImageView implements LifecycleObserver {
    private static final String TAG = "DoodleView";
    private int mViewWidth, mViewHeight;

    private float mValidRadius = 4;
    /**
     * 暂时的涂鸦画笔
     */
    private Paint mTempPaint;
    /**
     * 暂时的涂鸦路径
     */
    private Path mTempPath;
    /**
     * 暂时的马赛克路径
     */
    private Path mTempMosaicPath;
    private Paint mTempMosaicPaint;
    private Paint mMosaicPaint;
    /**
     * 画笔的颜色
     */
    private int mPaintColor = Color.RED;
    /**
     * 画笔的粗细
     */
    private int mPaintWidth = 5;

    private Paint mBitmapPaint;
    private Bitmap mMoasicBitmap;
    private Bitmap mOriginBitmap;

    private MODE mMode = MODE.NONE;

    /**
     * 是否可编辑
     */
    private boolean mIsEditable = false;
    /**
     * 总路径
     */
    private ArrayList<MODE> mPath = new ArrayList<>();
    /**
     * 涂鸦的路径
     */
    private ArrayList<DrawPathBean> mDoodlePath = new ArrayList<>();
    /**
     * 马赛克路径
     */
    private ArrayList<DrawPathBean> mMosaicPath = new ArrayList<>();

    private float mStartX, mStartY;
    private float mMoveX, mMoveY;

    /**
     * 区分点击和滑动
     */
    private float mDelaX, mDelaY;
    private DoodleCallback doodleCallback;

    public void setDoodleCallback(DoodleCallback doodleCallback) {
        this.doodleCallback = doodleCallback;
    }

    public DoodleView(Context context) {
        super(context);
        init();
        autoBindLifecycle(context);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        autoBindLifecycle(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewWidth <= 0 || mViewHeight <= 0) {
            return;
        }

        // 画原始图
        if (mOriginBitmap != null) {
            canvas.drawBitmap(mOriginBitmap, 0, 0, mBitmapPaint);
        }
        // 画马赛克
        drawMosaicPath(canvas);
        // 再画涂鸦
        drawDoodlePath(canvas);
    }

    /**
     * 画涂鸦内容
     */
    private void drawDoodlePath(Canvas canva) {
        if (mDoodlePath.size() > 0) {
            for (DrawPathBean pathBean : mDoodlePath) {
                canva.drawPath(pathBean.path, pathBean.paint);
            }
        }
        if (mTempPath != null && mTempPaint != null) {
            canva.drawPath(mTempPath, mTempPaint);
        }
    }

    /**
     * 画马赛克内容
     */
    private void drawMosaicPath(Canvas canva) {
        if (mMoasicBitmap != null) {
            // 保存图层
            int layerCount = canva.saveLayer(0, 0, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG);
            if (mMosaicPath.size() > 0) {
                for (DrawPathBean mosaicPath : mMosaicPath) {
                    canva.drawPath(mosaicPath.path, mosaicPath.paint);
                }
            }
            if (mTempMosaicPath != null && mTempMosaicPaint != null) {
                canva.drawPath(mTempMosaicPath, mTempMosaicPaint);
            }
//             进行图层的合并
            canva.drawBitmap(mMoasicBitmap, 0, 0, mMosaicPaint);
            canva.restoreToCount(layerCount);
        }
    }

    /**
     * 设置原始的截图
     *
     * @param originBitmap drawable
     */
    public void setOriginBitmap(@NonNull Bitmap originBitmap) {
        mOriginBitmap = originBitmap;
        initOriginBitmap();
    }

    private void init() {
        setMode(mMode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mViewWidth = w;
            mViewHeight = h;
            initOriginBitmap();
        }
    }

    private void initOriginBitmap() {
        if (mOriginBitmap != null && mViewHeight > 0 && mViewWidth > 0) {
            mOriginBitmap = Bitmap.createScaledBitmap(mOriginBitmap, mViewWidth, mViewHeight, true);
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            makeMosaicBitmap();
            postInvalidate();
        }
    }

    private void setModePaint(MODE mode) {
        if (mode == MODE.DOODLE_MODE) {
            mTempPaint = new Paint();
            mTempPaint.setAntiAlias(true);
            mTempPaint.setColor(mPaintColor);
            mTempPaint.setStyle(Paint.Style.STROKE);
            mTempPaint.setStrokeWidth(mPaintWidth);
            mTempPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempPaint.setStrokeJoin(Paint.Join.ROUND);
        } else if (mode == MODE.MOSAIC_MODE) {

            mTempMosaicPaint = new Paint();
            mTempMosaicPaint.setAntiAlias(true);
            mTempMosaicPaint.setDither(true);
            mTempMosaicPaint.setStyle(Paint.Style.STROKE);
            mTempMosaicPaint.setTextAlign(Paint.Align.CENTER);
            mTempMosaicPaint.setStrokeCap(Paint.Cap.ROUND);
            mTempMosaicPaint.setStrokeJoin(Paint.Join.ROUND);
            mTempMosaicPaint.setStrokeWidth(mPaintWidth * 3);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsEditable) {
            mMoveX = event.getX();
            mMoveY = event.getY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mStartX = mMoveX;
                mStartY = mMoveY;
                mDelaX = 0;
                mDelaY = 0;
                // 正常的画图操作
                touchDownNormalPath();
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                mDelaX += Math.abs(mMoveX - mStartX);
                mDelaY += Math.abs(mMoveY - mStartY);
                touchMoveNormalDraw();
                postInvalidate();
                return true;
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (mDelaX < mValidRadius && mDelaY < mValidRadius) {
                    mTempPath = null;
                    mTempPaint = null;
                    mTempMosaicPath = null;
                    mTempMosaicPaint = null;
                    postInvalidate();
                    if (doodleCallback != null) {
                        doodleCallback.onRevertStateChanged(getCurrentPathSize(mMode));
                    }
                    return false;
                }
                // 非点击，正常Up
                if (mMode == MODE.DOODLE_MODE) {
                    // 把path加到队列中
                    DrawPathBean pathBean = new DrawPathBean(mTempPath, mTempPaint, mMode);
                    mDoodlePath.add(pathBean);
                    mPath.add(mMode);
                } else if (mMode == MODE.MOSAIC_MODE) {
                    // 把path加到队列中
                    DrawPathBean pathBean = new DrawPathBean(mTempMosaicPath, mTempMosaicPaint, mMode);
                    mMosaicPath.add(pathBean);
                    mPath.add(mMode);
                    Log.d(TAG, "onTouchEvent: 非点击MOSAIC_MODE");

                }
                mTempPath = null;
                mTempPaint = null;
                mTempMosaicPath = null;
                mTempMosaicPaint = null;
                postInvalidate();
                if (doodleCallback != null) {
                    doodleCallback.onRevertStateChanged(getCurrentPathSize(mMode));
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 按下时，初始化绘图参数
     */
    private void touchDownNormalPath() {
        if (mMode == MODE.DOODLE_MODE) {
            // 设置对应mode的画笔
            setModePaint(mMode);
            mTempPath = new Path();
            mTempPath.moveTo(mStartX, mStartY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            setModePaint(mMode);
            mTempMosaicPath = new Path();
            mTempMosaicPath.moveTo(mStartX, mStartY);
        }
    }


    /**
     * 移动时，绘制路径或者图形
     */
    private void touchMoveNormalDraw() {
        // 使用队列中最后一条path进行操作
        if (mMode == MODE.DOODLE_MODE) {
            mTempPath.lineTo(mMoveX, mMoveY);
        } else if (mMode == MODE.MOSAIC_MODE) {
            mTempMosaicPath.lineTo(mMoveX, mMoveY);
        }
    }

    /**
     * 撤销操作
     *
     * @return 撤销后剩余可以撤销的步骤
     */
    public int revertPath() {
        mMode = MODE.REVER;
        int size = 0;
        Log.d(TAG, "revertPath: mPath:" + mPath.size());
        size = mPath.size();
        if (size > 0) {
            MODE lastMode = mPath.get(size - 1);
            if (lastMode == MODE.DOODLE_MODE && mDoodlePath.size() > 0) {
                mDoodlePath.remove(mDoodlePath.size() - 1);
            } else if (lastMode == MODE.MOSAIC_MODE && mMosaicPath.size() > 0) {
                mMosaicPath.remove(mMosaicPath.size() - 1);
            }
            mPath.remove(size - 1);
            if (doodleCallback != null) {
                doodleCallback.onRevertStateChanged(mPath.size() > 0);
            }
        }

        postInvalidate();
        return size;
    }

    /**
     * 获取指定模式下，是否可撤销
     *
     * @param mode mode
     * @return boolean
     */
    public boolean getCurrentPathSize(MODE mode) {
        boolean result = false;
        if (mode == MODE.DOODLE_MODE || mode == MODE.MOSAIC_MODE) {
            if (mPath.size() > 0) {
                result = true;
            }
        }
        return result;
    }

    public interface DoodleCallback {
        void onRevertStateChanged(boolean canRevert);
    }

    /**
     * 获取马赛克的bitmap
     */
    public Bitmap makeMosaicBitmap() {
        mMosaicPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMosaicPaint.setFilterBitmap(false);
        mMosaicPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (mMoasicBitmap != null) {
            return mMoasicBitmap;
        }

        int w = Math.round(mViewWidth / 16f);
        int h = Math.round(mViewHeight / 16f);

        if (mOriginBitmap != null) {
            // 先创建小图
            mMoasicBitmap = Bitmap.createScaledBitmap(mOriginBitmap, w, h, false);
            // 再把小图放大
            mMoasicBitmap = Bitmap.createScaledBitmap(mMoasicBitmap, mViewWidth, mViewHeight, false);
        }
        return mMoasicBitmap;
    }

    public enum MODE {
        NONE, DOODLE_MODE, MOSAIC_MODE, REVER;
    }

    /**
     * 设置是否可编辑
     *
     * @param editable 能否编辑
     */
    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
    }

    public void setMode(MODE mode) {
        this.mMode = mode;
        if (doodleCallback != null) {
            doodleCallback.onRevertStateChanged(getCurrentPathSize(mMode));
        }
    }

    /**
     * 记录画笔和画图的路径，主要用来撤销画图的操作
     */
    class DrawPathBean {
        public Path path;
        public Paint paint;
        public MODE mode;

        DrawPathBean(Path path, Paint paint, MODE mode) {
            this.paint = paint;
            this.path = path;
            this.mode = mode;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void clear() {
        if (mOriginBitmap != null && !mOriginBitmap.isRecycled()) {
            mOriginBitmap.recycle();
            mOriginBitmap = null;
        }
        if (mMoasicBitmap != null && !mMoasicBitmap.isRecycled()) {
            mMoasicBitmap.recycle();
            mMoasicBitmap = null;
        }
    }

    private void autoBindLifecycle(Context context) {
        if (context == null) {
            return;
        }
        if (context instanceof AppCompatActivity) {
            // 宿主是activity
            AppCompatActivity activity = (AppCompatActivity) context;
            ((AppCompatActivity) activity).getLifecycle().addObserver(this);
            return;
        }
        // 宿主是fragment
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(this);
            return;
        }
    }

//    public static int convertDpToPixel(float dp) {
//        DisplayMetrics metrics = MyApplication.mContext.getResources().getDisplayMetrics();
//        return (int) (metrics.density * dp + 0.5f);
//    }
}
