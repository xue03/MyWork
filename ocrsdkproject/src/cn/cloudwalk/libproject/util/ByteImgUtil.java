package cn.cloudwalk.libproject.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * create by sunyue on 2019/12/26
 */
public class ByteImgUtil {

    public static byte[] zoomToSize(Bitmap srcBp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        srcBp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        if (baos.toByteArray().length / 1024 > 1024) {             //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();                                        //重置baos即清空baos
            srcBp.compress(Bitmap.CompressFormat.JPEG, 80, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;                                  //be=1表示不缩放
        if (w > h && w > ww) {                       //如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {                //如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        closeStream(baos);
        newOpts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);    //压缩好比例大小后再进行质量压缩

    }

    /**
     * 功能描述:进行质量压缩  <br>
     * 创建者:lidongdong<br>
     * 创建日期:2015-9-28上午9:30:04<br>
     *
     * @param image
     * @return
     */
    private static byte[] compressImage(Bitmap image) {
        byte[] desBytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
            if (options <= 80) {
                break;
            }
        }
        desBytes = baos.toByteArray();
        return desBytes;
    }

    private static void closeStream(OutputStream os) {

        if (os != null) {

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
