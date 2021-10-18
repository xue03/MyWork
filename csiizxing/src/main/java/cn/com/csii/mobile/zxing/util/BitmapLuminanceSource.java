//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.util;

import android.graphics.Bitmap;
import com.google.zxing.LuminanceSource;

public class BitmapLuminanceSource extends LuminanceSource {
    private byte[] bitmapPixels;

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, this.getWidth(), 0, 0, this.getWidth(), this.getHeight());

        for(int i = 0; i < data.length; ++i) {
            this.bitmapPixels[i] = (byte)data[i];
        }

    }


    @Override
    public byte[] getMatrix() {
        return this.bitmapPixels;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        System.arraycopy(this.bitmapPixels, y * this.getWidth(), row, 0, this.getWidth());
        return row;
    }
}
