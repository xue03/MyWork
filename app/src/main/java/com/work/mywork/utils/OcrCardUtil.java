package com.work.mywork.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.work.mywork.interfaces.ResultCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cn.cloudwalk.libproject.OcrCameraActivity;
import cn.cloudwalk.libproject.OcrResultActivity;
import cn.cloudwalk.libproject.Bulider;

/**
 * Date:2021/10/14
 * Description:
 * Author:XueTingTing
 */
public class OcrCardUtil {
    private static final int REQUEST_CODE = 0x1000;
    static Activity activity;
    static int params_ocr_flag = 1;
    static ResultCallBack callBack;
    static Fragment fragment;

    public static void setLicence(String licences, Activity ac, Fragment fragments) {
        if (licences != null) {
            Bulider.licence = licences;
            activity = ac;
            fragment=fragments;
        }
    }

    public static void startIdCardCamera(String flag, ResultCallBack callBacks) {
        callBack = callBacks;
        if (TextUtils.isEmpty(Bulider.licence)) {
            Log.d(OcrCardUtil.class.getSimpleName(), "调用face前请先设定云从科技颁发的licence,设定方法参考face module里的方法:setLicence(String licence)");
            return;
        }
        Log.d(OcrCardUtil.class.getSimpleName(), "start:"+flag);
        Intent intent = new Intent(activity, OcrCameraActivity.class);
        //默认正面
        if (TextUtils.isEmpty(flag)) {
            intent.putExtra("ocr_flag", 1);
        } else if ("front".equals(flag)) {
            intent.putExtra("ocr_flag", 1);
        } else if ("back".equals(flag)) {
            intent.putExtra("ocr_flag", 0);
        } else {
            intent.putExtra("ocr_flag", 1);
        }
        params_ocr_flag = intent.getIntExtra("ocr_flag", -1);
        //intent.putExtra("isImgSave", isImgSave);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void onActivityCardResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.d(OcrCardUtil.class.getSimpleName(), "data==null:");
            callBack.Filed("data==null");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        if (requestCode == REQUEST_CODE && resultCode == activity.RESULT_OK) {
            String filePath = data.getStringExtra("filepath_key");
            String headimg = null;
            if (OcrResultActivity.faceBitmap != null) {
                headimg = Base64.encodeToString(zoomToSize(OcrResultActivity.faceBitmap), Base64.NO_WRAP);
            }
            if (params_ocr_flag == 1) {//正面
                try {
                    jsonObject.put("name", data.getStringExtra("name"));
                    jsonObject.put("sex", data.getStringExtra("sex"));
                    jsonObject.put("birth", data.getStringExtra("birth"));
                    jsonObject.put("address", data.getStringExtra("address"));
                    jsonObject.put("id", data.getStringExtra("id"));
                    jsonObject.put("race", data.getStringExtra("race"));
                    jsonObject.put("header", headimg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (callBack != null) {
                    callBack.Success(jsonObject.toString());
                }
            }
            if (params_ocr_flag == 0) {//反面
                try {
                    jsonObject.put("authority", data.getStringExtra("authority"));
                    jsonObject.put("validdate1", data.getStringExtra("validdate1"));
                    jsonObject.put("validdate2", data.getStringExtra("validdate2"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (callBack != null) {
                    callBack.Success(jsonObject.toString());
                }
            }
            Log.d(OcrCardUtil.class.getSimpleName(), "result:"+jsonObject.toString());

        }
        if (requestCode == REQUEST_CODE && resultCode == activity.RESULT_CANCELED){
            int errorCode = data.getIntExtra("errorCode", 0);
            try {
                jsonObject.put("errorCode",errorCode);
                jsonObject.put("msg","取消操作");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(OcrCardUtil.class.getSimpleName(), "result:"+jsonObject.toString());
            callBack.Success(jsonObject.toString());
        }
    }
    private static byte[] zoomToSize(Bitmap srcBp) {

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
