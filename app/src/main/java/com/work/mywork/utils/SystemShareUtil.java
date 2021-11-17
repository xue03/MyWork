package com.work.mywork.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.work.mywork.interfaces.ResultCallBack;

/**
 * Date:2021/10/20
 * Description:
 * Author:XueTingTing
 */
public class SystemShareUtil {
    public static void shareText(Fragment fragment, String string) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        // 文本形式的数据内容
        sendIntent.putExtra(Intent.EXTRA_TEXT, string);
        // 指定发送内容的类型
        sendIntent.setType("text/plain");
        fragment.startActivity(Intent.createChooser(sendIntent, "选择要分享到的平台"));
    }
//    public static void shareImage(Fragment fragment, Bitmap bitmap){
//        Intent intent = new Intent();
//
//        intent.setAction(Intent.ACTION_SEND);
//
//    }
}
