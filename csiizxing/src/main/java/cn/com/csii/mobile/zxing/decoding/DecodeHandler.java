//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.decoding;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import cn.com.csii.mobile.zxing.CaptureInterface;
import cn.com.csii.mobile.zxing.camera.CameraManager;
import cn.com.csii.mobile.zxing.camera.PlanarYUVLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.util.Hashtable;

final class DecodeHandler extends Handler {
    private static final String TAG = DecodeHandler.class.getSimpleName();
    private CaptureInterface captureInterface;
    private final MultiFormatReader multiFormatReader;

    DecodeHandler(CaptureInterface captureInterface, Hashtable<DecodeHintType, Object> hints) {
        this.captureInterface = captureInterface;
        this.multiFormatReader = new MultiFormatReader();
        this.multiFormatReader.setHints(hints);
    }

    @Override
    public void handleMessage(Message message) {
        switch(message.what) {
            case 94501001:
                this.decode((byte[])message.obj, message.arg1, message.arg2);
                break;
            case 94501002:
                Looper.myLooper().quit();
        }

    }

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
        byte[] rotatedData = new byte[data.length];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }

        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, height, width);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            rawResult = this.multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException var17) {
            ;
        } finally {
            this.multiFormatReader.reset();
        }

        if(rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(this.captureInterface.getHandler(), 94501004, rawResult);
            Bundle bundle = new Bundle();
            bundle.putParcelable("barcode_bitmap", source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(this.captureInterface.getHandler(), 94501005);
            message.sendToTarget();
        }

    }
}
