//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.util.AttributeSet;
import android.view.View;
import cn.com.csii.mobile.zxing.camera.CameraManager;
import cn.com.csii.mobile.zxing.util.ZxingUtil;
import com.google.zxing.ResultPoint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class ViewfinderView extends View {
    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 255;
    private Paint paint;
    private Bitmap resultBitmap;
    private int maskColor;
    private int resultColor;
    private int laserColor;
    private int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private float i = 0.0F;
    private Context context;
    private Bitmap bitmapTop;
    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;
    private Bitmap bitmapBottom;
    private Bitmap bitmapLine;

    public ViewfinderView(Context context) {
        super(context);
        this.context = context;
        this.init();
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.init();
    }

    public void init() {
        this.paint = new Paint();
        this.maskColor = 1610612736;
        this.resultColor = -1342177280;
        this.laserColor = -65536;
        this.resultPointColor = -1056964864;
        this.scannerAlpha = 0;
        this.possibleResultPoints = new HashSet(5);
        this.bitmapTop = ZxingUtil.getInstance().getAssetsBitmap(this.context, "code_top.png");
        this.bitmapLeft = ZxingUtil.getInstance().getAssetsBitmap(this.context, "code_left.png");
        this.bitmapRight = ZxingUtil.getInstance().getAssetsBitmap(this.context, "code_right.png");
        this.bitmapBottom = ZxingUtil.getInstance().getAssetsBitmap(this.context, "code_bottom.png");
        this.bitmapLine = ZxingUtil.getInstance().getAssetsBitmap(this.context, "code_line.png");
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if(frame != null) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            this.paint.setColor(this.resultBitmap != null?this.resultColor:this.maskColor);
            canvas.drawRect(0.0F, 0.0F, (float)width, (float)frame.top, this.paint);
            canvas.drawRect(0.0F, (float)frame.top, (float)frame.left, (float)(frame.bottom + 1), this.paint);
            canvas.drawRect((float)(frame.right + 1), (float)frame.top, (float)width, (float)(frame.bottom + 1), this.paint);
            canvas.drawRect(0.0F, (float)(frame.bottom + 1), (float)width, (float)height, this.paint);
            if(this.resultBitmap != null) {
                this.paint.setAlpha(255);
                canvas.drawBitmap(this.resultBitmap, (float)frame.left, (float)frame.top, this.paint);
            } else {
                if(this.bitmapTop != null) {
                    canvas.drawBitmap(this.bitmapTop, (Rect)null, new RectF((float)frame.left, (float)frame.top, (float)frame.right, (float)(frame.top + 6)), (Paint)null);
                }

                if(this.bitmapLeft != null) {
                    canvas.drawBitmap(this.bitmapLeft, (Rect)null, new RectF((float)frame.left, (float)frame.top, (float)(frame.left + 6), (float)frame.bottom), (Paint)null);
                }

                if(this.bitmapRight != null) {
                    canvas.drawBitmap(this.bitmapRight, (Rect)null, new RectF((float)(frame.right - 6), (float)frame.top, (float)frame.right, (float)frame.bottom), (Paint)null);
                }

                if(this.bitmapBottom != null) {
                    canvas.drawBitmap(this.bitmapBottom, (Rect)null, new RectF((float)frame.left, (float)(frame.bottom - 6), (float)frame.right, (float)frame.bottom), (Paint)null);
                }

                this.paint.setColor(this.laserColor);
                this.paint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
                this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
                if(this.bitmapLine != null) {
                    canvas.drawBitmap(this.bitmapLine, (Rect)null, new RectF((float)(frame.left + 2), this.i + (float)frame.top, (float)(frame.right - 1), this.i + 4.0F + (float)frame.top), (Paint)null);
                }

                Rect targetRect = new Rect(frame.left, frame.top + frame.height() + 40, frame.left + frame.width(), frame.top + frame.height() + 80);
                Paint paintT = new Paint(1);
                paintT.setStrokeWidth(3.0F);
                paintT.setTextSize((float)this.dip2px(this.context, 14.0F));
                String testString = "请将扫描框对准二维码，即可自动扫描";
                paintT.setColor(0);
                canvas.drawRect(targetRect, paintT);
                paintT.setColor(-1);
                FontMetricsInt fontMetrics = paintT.getFontMetricsInt();
                int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                paintT.setTextAlign(Align.CENTER);
                canvas.drawText(testString, (float)targetRect.centerX(), (float)baseline, paintT);
                Rect previewFrame = CameraManager.get().getFramingRectInPreview();
                float scaleX = (float)frame.width() / (float)previewFrame.width();
                float scaleY = (float)frame.height() / (float)previewFrame.height();
                Collection<ResultPoint> currentPossible = this.possibleResultPoints;
                Collection<ResultPoint> currentLast = this.lastPossibleResultPoints;
                ResultPoint point;
                Iterator var16;
                if(currentPossible.isEmpty()) {
                    this.lastPossibleResultPoints = null;
                } else {
                    this.possibleResultPoints = new HashSet(5);
                    this.lastPossibleResultPoints = currentPossible;
                    this.paint.setAlpha(255);
                    this.paint.setColor(this.resultPointColor);
                    var16 = currentPossible.iterator();

                    while(var16.hasNext()) {
                        point = (ResultPoint)var16.next();
                        canvas.drawCircle((float)(frame.left + (int)(point.getX() * scaleX)), (float)(frame.top + (int)(point.getY() * scaleY)), 3.0F, this.paint);
                    }
                }

                if(currentLast != null) {
                    this.paint.setAlpha(127);
                    this.paint.setColor(this.resultPointColor);
                    var16 = currentLast.iterator();

                    while(var16.hasNext()) {
                        point = (ResultPoint)var16.next();
                        canvas.drawCircle((float)frame.left + point.getX(), (float)frame.top + point.getY(), 3.0F, this.paint);
                    }
                }

                this.postInvalidateDelayed(100L, frame.left, frame.top, frame.right, frame.bottom);
                if(this.i < (float)(frame.top + frame.height() - 2)) {
                    this.i += 10.0F;
                    if(this.i > (float)(frame.height() - 2)) {
                        this.i = 0.0F;
                    }

                    this.invalidate();
                }
            }

        }
    }

    private int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * m + 0.5F);
    }

    public void drawViewfinder() {
        this.resultBitmap = null;
        this.invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        this.invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        this.possibleResultPoints.add(point);
    }

    public Bitmap getViewScreenShot(View view) {
        Bitmap obmp = null;
        view.setDrawingCacheEnabled(true);
        if(view.getDrawingCache() != null) {
            obmp = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        }

        return obmp;
    }
}
