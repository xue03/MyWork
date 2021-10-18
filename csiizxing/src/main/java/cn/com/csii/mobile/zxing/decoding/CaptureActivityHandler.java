//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.decoding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.com.csii.mobile.zxing.CaptureInterface;
import cn.com.csii.mobile.zxing.camera.CameraManager;
import cn.com.csii.mobile.zxing.view.ViewfinderResultPointCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.util.Vector;

public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getSimpleName();
    private CaptureInterface captureInterface;
    private final DecodeThread decodeThread;
    private CaptureActivityHandler.State state;

    public CaptureActivityHandler(CaptureInterface captureInterface, Vector<BarcodeFormat> decodeFormats, String characterSet) {
        this.captureInterface = captureInterface;
        this.decodeThread = new DecodeThread(captureInterface, decodeFormats, characterSet, new ViewfinderResultPointCallback(captureInterface.getViewfinderView()));
        this.decodeThread.start();
        this.state = CaptureActivityHandler.State.SUCCESS;
        CameraManager.get().startPreview();
        this.restartPreviewAndDecode();
    }

    public void handleMessage(Message message) {
        switch(message.what) {
            case 94501003:
                if(this.state == CaptureActivityHandler.State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, 94501003);
                }
                break;
            case 94501004:
                Log.d(TAG, "Got decode succeeded message");
                this.state = CaptureActivityHandler.State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null?null:(Bitmap)bundle.getParcelable("barcode_bitmap");
                this.captureInterface.handleDecode((Result)message.obj, barcode);
                break;
            case 94501005:
                this.state = CaptureActivityHandler.State.PREVIEW;
                CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), 94501001);
        }

    }

    public void quitSynchronously() {
        this.state = CaptureActivityHandler.State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(this.decodeThread.getHandler(), 94501002);
        quit.sendToTarget();

        try {
            this.decodeThread.join();
        } catch (InterruptedException var3) {
            ;
        }

        this.removeMessages(94501004);
        this.removeMessages(94501005);
    }

    private void restartPreviewAndDecode() {
        if(this.state == CaptureActivityHandler.State.SUCCESS) {
            this.state = CaptureActivityHandler.State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), 94501001);
            CameraManager.get().requestAutoFocus(this, 94501003);
            this.captureInterface.drawViewfinder();
        }

    }

    private static enum State {
        PREVIEW,
        SUCCESS,
        DONE;

        private State() {
        }
    }
}
