//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;

public final class CameraManager {
    private static final String TAG = CameraManager.class.getSimpleName();
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MAX_FRAME_WIDTH = 480;
    private static CameraManager cameraManager;
    static final int SDK_INT;
    private final CameraConfigurationManager configManager;
    private Camera camera;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private Context context;
    private final boolean useOneShotPreviewCallback;
    private final PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;

    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(VERSION.SDK);
        } catch (NumberFormatException var2) {
            sdkInt = 10000;
        }

        SDK_INT = sdkInt;
    }

    public static void init(Context context) {
        if(cameraManager == null) {
            cameraManager = new CameraManager(context);
        }

    }

    public static CameraManager get() {
        return cameraManager;
    }

    private CameraManager(Context context) {
        this.context = context;
        this.configManager = new CameraConfigurationManager(context);
        this.useOneShotPreviewCallback = Integer.parseInt(VERSION.SDK) > 3;
        this.previewCallback = new PreviewCallback(this.configManager, this.useOneShotPreviewCallback);
        this.autoFocusCallback = new AutoFocusCallback();
    }

    public void openDriver(SurfaceHolder holder) throws IOException {
        if(this.camera == null) {
            this.camera = Camera.open();
            if(this.camera == null) {
                throw new IOException();
            }

            this.camera.setPreviewDisplay(holder);
            if(!this.initialized) {
                this.initialized = true;
                this.configManager.initFromCameraParameters(this.camera);
            }

            this.configManager.setDesiredCameraParameters(this.camera);
            FlashlightManager.enableFlashlight();
        }

    }

    public void closeDriver() {
        if(this.camera != null) {
            this.camera.release();
            this.camera = null;
        }

    }

    public void startPreview() {
        if(this.camera != null && !this.previewing) {
            this.camera.startPreview();
            this.previewing = true;
        }

    }

    public void stopPreview() {
        if(this.camera != null && this.previewing) {
            if(!this.useOneShotPreviewCallback) {
                this.camera.setPreviewCallback((android.hardware.Camera.PreviewCallback)null);
            }

            this.camera.stopPreview();
            this.previewCallback.setHandler((Handler)null, 0);
            this.autoFocusCallback.setHandler((Handler)null, 0);
            this.previewing = false;
        }

    }

    public void requestPreviewFrame(Handler handler, int message) {
        if(this.camera != null && this.previewing) {
            this.previewCallback.setHandler(handler, message);
            if(this.useOneShotPreviewCallback) {
                this.camera.setOneShotPreviewCallback(this.previewCallback);
            } else {
                this.camera.setPreviewCallback(this.previewCallback);
            }
        }

    }

    public void requestAutoFocus(Handler handler, int message) {
        if(this.camera != null && this.previewing) {
            this.autoFocusCallback.setHandler(handler, message);
            this.camera.autoFocus(this.autoFocusCallback);
        }

    }

    public Rect getFramingRect() {
        Point screenResolution = this.configManager.getScreenResolution();
        if(this.framingRect == null) {
            if(this.camera == null) {
                return null;
            }

            int width = screenResolution.x * 3 / 4;
            if(width < this.dip2px(this.context, 240.0F)) {
                width = this.dip2px(this.context, 240.0F);
            } else if(width > this.dip2px(this.context, 480.0F)) {
                width = this.dip2px(this.context, 480.0F);
            }

            System.out.println(width + "------矩形框------" + width);
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - width) / 2;
            this.framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + width);
            Log.d(TAG, "Calculated framing rect: " + this.framingRect);
        }

        return this.framingRect;
    }

    private int dip2px(Context activity, float dipValue) {
        float m = activity.getResources().getDisplayMetrics().density;
        return (int)(dipValue * m + 0.5F);
    }

    public Rect getFramingRectInPreview() {
        if(this.framingRectInPreview == null) {
            Rect rect = new Rect(this.getFramingRect());
            Point cameraResolution = this.configManager.getCameraResolution();
            Point screenResolution = this.configManager.getScreenResolution();
            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            this.framingRectInPreview = rect;
        }

        return this.framingRectInPreview;
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = this.getFramingRectInPreview();
        int previewFormat = this.configManager.getPreviewFormat();
        String previewFormatString = this.configManager.getPreviewFormatString();
        switch(previewFormat) {
            case 16:
            case 17:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
            default:
                if("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
                } else if("yuv422i-yuyv".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
                } else {
                    throw new IllegalArgumentException("Unsupported picture format: " + previewFormat + '/' + previewFormatString);
                }
        }
    }

    public Camera getCamera() {
        return this.camera != null?this.camera:null;
    }
}
