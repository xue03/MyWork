//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.decoding;

import android.os.Handler;
import android.os.Looper;
import cn.com.csii.mobile.zxing.CaptureInterface;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private CaptureInterface captureInterface;
    private final Hashtable<DecodeHintType, Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(CaptureInterface captureInterface, Vector<BarcodeFormat> decodeFormats, String characterSet, ResultPointCallback resultPointCallback) {
        this.captureInterface = captureInterface;
        this.handlerInitLatch = new CountDownLatch(1);
        this.hints = new Hashtable(3);
        if(decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        this.hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if(characterSet != null) {
            this.hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        this.hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            this.handlerInitLatch.await();
        } catch (InterruptedException var2) {
            ;
        }

        return this.handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        this.handler = new DecodeHandler(this.captureInterface, this.hints);
        this.handlerInitLatch.countDown();
        Looper.loop();
    }
}
