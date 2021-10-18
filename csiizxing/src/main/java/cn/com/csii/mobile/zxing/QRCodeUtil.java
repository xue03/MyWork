//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import cn.com.csii.mobile.zxing.create2code.Create2DCode;
import cn.com.csii.mobile.zxing.decoding.DecodeFormatManager;
import cn.com.csii.mobile.zxing.interfaces.DecodeQRCodeResult;
import cn.com.csii.mobile.zxing.util.BitmapLuminanceSource;
import cn.com.csii.mobile.zxing.util.ZxingUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class QRCodeUtil {
    private Activity activity;
    private DecodeQRCodeResult decodeQRCodeResult;
    private QRCodeUtil.ScanQRCodeReceiver receiver;

    public QRCodeUtil(Activity activity) {
        this.activity = activity;
    }

    public Bitmap CreateQRCode(String str) {
        Bitmap bitmap = null;

        try {
            bitmap = Create2DCode.create2DCode(str);
        } catch (WriterException var4) {
            var4.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap CreateQRCode(String str, Bitmap logo) {
        Bitmap bitmap = null;

        try {
            bitmap = Create2DCode.create2DCode(str, logo);
        } catch (WriterException var5) {
            var5.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap CreateQRCode(String str, int size) {
        Bitmap bitmap = null;

        try {
            bitmap = Create2DCode.create2DCode(str, size);
        } catch (WriterException var5) {
            var5.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap CreateQRCode(String str, int size, Bitmap logo) {
        Bitmap bitmap = null;

        try {
            bitmap = Create2DCode.create2DCode(str, size, logo);
        } catch (WriterException var6) {
            var6.printStackTrace();
        }

        return bitmap;
    }

    public String DecodeQRCode(Bitmap bitmap) {
        Result result = null;

        try {
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            Vector<BarcodeFormat> decodeFormats = new Vector();
            if(decodeFormats == null || decodeFormats.isEmpty()) {
                decodeFormats = new Vector();
                decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }

            Hashtable<DecodeHintType, Object> hints = new Hashtable(2);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
            multiFormatReader.setHints(hints);
            int lWidth = bitmap.getWidth();
            int lHeight = bitmap.getHeight();
            System.out.println("图片宽高：" + lWidth + "----" + lHeight);
            BitmapLuminanceSource rSource = new BitmapLuminanceSource(bitmap);
            HybridBinarizer hybridBinarizer = new HybridBinarizer(rSource);
            BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
            result = multiFormatReader.decodeWithState(binaryBitmap);
        } catch (Exception var11) {
            Toast.makeText(this.activity, "二维码解析失败！", Toast.LENGTH_SHORT).show();
        }

        return result == null?"":result.getText();
    }

    public Bitmap getBitmap(Bitmap bitmap) {
        ImageView imageView = new ImageView(this.activity);
        imageView.setLayoutParams(new LayoutParams(400, 400));
        imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        return this.Drawable2Bitmap(imageView.getDrawable());
    }

    public Bitmap Drawable2Bitmap(Drawable d) {
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), d.getOpacity() != PixelFormat.OPAQUE?Config.ARGB_8888:Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        d.draw(canvas);
        return bitmap;
    }

    private Bitmap zoomBitmap(Bitmap bitmap, int scale) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 1.0F / (float)scale;
        float scaleHeight = 1.0F / (float)scale;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    public void DecodeQRCode(DecodeQRCodeResult decodeQRCodeResult) {
        this.decodeQRCodeResult = decodeQRCodeResult;
        this.initScanQRCodeReceiver();
        Intent intent = new Intent(this.activity, CaptureActivity.class);
        this.activity.startActivity(intent);
    }

    public void DecodeQRCode(String scan_type, DecodeQRCodeResult decodeQRCodeResult) {
        this.decodeQRCodeResult = decodeQRCodeResult;
        this.initScanQRCodeReceiver();
        Intent intent = new Intent(this.activity, CaptureActivity.class);
        if(!TextUtils.isEmpty(scan_type)) {
            intent.putExtra("scan_type", scan_type);
        }

        this.activity.startActivity(intent);
    }

    public CaptureFragment getQRCodeFragment(DecodeQRCodeResult decodeQRCodeResult) {
        this.decodeQRCodeResult = decodeQRCodeResult;
        CaptureFragment captrueFragment = new CaptureFragment();
        captrueFragment.setResultListener(decodeQRCodeResult);
        return captrueFragment;
    }

    public void initScanQRCodeReceiver() {
        this.receiver = new QRCodeUtil.ScanQRCodeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ZxingUtil.receiver_action);
//        this.activity.registerReceiver(this.receiver, filter);
        registerLocalReceiver(receiver,filter);
    }

    private  void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(activity).sendBroadcastSync(intent);
    }
    private  void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, filter);
    }

    private  void unregisterLocalReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
    }

    public void destoryScanQRCodeReceiver() {
        try {
            if(this.receiver != null) {
//                this.activity.unregisterReceiver(this.receiver);
                unregisterLocalReceiver(receiver);
            }
        }catch (Exception ex){
        }
    }
    private class ScanQRCodeReceiver extends BroadcastReceiver {
        private ScanQRCodeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ZxingUtil.receiver_action)) {
                if(intent.getExtras() != null) {
                    QRCodeUtil.this.decodeQRCodeResult.result(intent.getExtras().getString("result"));
                }

                QRCodeUtil.this.destoryScanQRCodeReceiver();
            }

        }
    }
}
