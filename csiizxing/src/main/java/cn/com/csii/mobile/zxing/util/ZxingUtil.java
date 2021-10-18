//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.io.IOException;

public class ZxingUtil {
    public static String receiver_action = "com.csii.zxing.scan_qrcode";
    public static final int decode = 94501001;
    public static final int quit = 94501002;
    public static final int auto_focus = 94501003;
    public static final int decode_succeeded = 94501004;
    public static final int decode_failed = 94501005;
    public static final int preview_view = 1;
    private static ZxingUtil utils = new ZxingUtil();

    public ZxingUtil() {
    }

    public static ZxingUtil getInstance() {
        if(utils == null) {
            utils = new ZxingUtil();
            return utils;
        } else {
            return utils;
        }
    }

    private static Drawable getImageDrawable(Context context, String imageName) {
        int id = RUtil.getInstance().getDrawableId(context, imageName);
        return id != 0?context.getResources().getDrawable(id):null;
    }

    public StateListDrawable getRadioButtonBg(Context context, String upImgName, String downImgName) {
        Drawable upImg = this.getAssetsDrawable(context, upImgName);
        Drawable downImg = this.getAssetsDrawable(context, downImgName);
        return newSelector(upImg, downImg);
    }

    public Drawable getDrawable(Bitmap bitmap, Context context) {
        ImageView aImageView = new ImageView(context);
        aImageView.setImageBitmap(bitmap);
        return aImageView.getDrawable();
    }

    private static StateListDrawable newSelector(Drawable normal, Drawable pressed) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{16842919}, pressed);
        bg.addState(new int[]{16842908}, pressed);
        bg.addState(new int[]{16842912}, pressed);
        bg.addState(new int[0], normal);
        return bg;
    }

    public Bitmap getAssetsBitmap(Context context, String name) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(name));
        } catch (IOException var4) {
            return null;
        }
    }

    public Drawable getAssetsDrawable(Context context, String name) {
        Drawable drawable = null;

        try {
            drawable = this.getDrawable(BitmapFactory.decodeStream(context.getAssets().open(name)), context);
            return drawable;
        } catch (IOException var5) {
            return null;
        }
    }

    public View getScanView(Context context) {
        RelativeLayout rLayout = new RelativeLayout(context);
        LayoutParams params = new LayoutParams(-1, -1);
        rLayout.setLayoutParams(params);
        SurfaceView surfaceView = new SurfaceView(context);
        LayoutParams sparams = new LayoutParams(-2, -2);
        sparams.addRule(13);
        surfaceView.setLayoutParams(sparams);
        surfaceView.setId(1);
        return rLayout;
    }
}
