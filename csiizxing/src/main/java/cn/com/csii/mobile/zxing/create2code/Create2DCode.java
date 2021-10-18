//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.create2code;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.EnumMap;
import java.util.Map;

public class Create2DCode {
    public Create2DCode() {
    }

    public static Bitmap create2DCode(String str) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = (new MultiFormatWriter()).encode(str, BarcodeFormat.QR_CODE, 400, 400, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                if(matrix.get(x, y)) {
                    pixels[y * width + x] = -16777216;
                } else {
                    pixels[y * width + x] = -1;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap create2DCode(String str, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = (new MultiFormatWriter()).encode(str, BarcodeFormat.QR_CODE, size, size, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                if(matrix.get(x, y)) {
                    pixels[y * width + x] = -16777216;
                } else {
                    pixels[y * width + x] = -1;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap create2DCode(String str, int size, Bitmap logo) throws WriterException {
        Bitmap bitmap = create2DCode(str, size);
        return logo2DCode(bitmap, logo, 1);
    }

    public static Bitmap create2DCode(String str, Bitmap logo) throws WriterException {
        Bitmap bitmap = create2DCode(str);
        return logo2DCode(bitmap, logo, 1);
    }

    public static Bitmap logo2DCode(Bitmap code, Bitmap logo, int maptype) {
        if(code == null) {
            return null;
        } else {
            int bgWidth = code.getWidth();
            int bgHeight = code.getHeight();
            int fgWidth = logo.getWidth();
            int fgHeight = logo.getHeight();
            int _left = 0;
            int _top = 0;
            switch(maptype) {
                case 1:
                    _left = (bgWidth - fgWidth) / 2;
                    _top = (bgHeight - fgHeight) / 2;
                    break;
                case 2:
                    _left = bgWidth - fgWidth;
                    _top = 0;
            }

            Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(newmap);
            canvas.drawBitmap(code, 0.0F, 0.0F, (Paint)null);
            canvas.drawBitmap(logo, (float)_left, (float)_top, (Paint)null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return newmap;
        }
    }

    public static Bitmap logo2DCode(Bitmap code, Bitmap logo, Bitmap pic) {
        Bitmap logocode = logo2DCode(code, logo, 1);
        if(logocode == null) {
            return null;
        } else {
            int bgWidth = logocode.getWidth();
            int bgHeight = logocode.getHeight();
            int fgWidth = pic.getWidth();
            Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(newmap);
            canvas.drawBitmap(logocode, 0.0F, 0.0F, (Paint)null);
            canvas.drawBitmap(pic, (float)(bgWidth - fgWidth), 0.0F, (Paint)null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return newmap;
        }
    }
}
