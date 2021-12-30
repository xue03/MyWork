package com.work.screenshotedit.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Date:2021/12/20
 * Description:系统分享（微信、朋友圈）
 * Author:XueTingTing
 */
public class SystemShare {
    private static Context mContext;
    //分享到朋友圈
    public static void shareWXFriendsCircle(Bitmap params, Context context){
        mContext=context;
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/*");
        if (params != null) {
            String fileUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), params, null, null);
            if (TextUtils.isEmpty(fileUrl)) {// 插入系统相册失败时保存文件 upd 2019-04-13 19:00:41
                fileUrl = saveFile(params, 100);
            }
            final Uri fileUri = Uri.parse(fileUrl);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);  //传输图片或者文件 采用流的方式
        } else {
            Log.d("TAG", "openSystenShare: bitmap is null");
        }
        context.startActivity(intent);
    }
    //分享到微信朋友列表
    public static void shareWXFriends(Bitmap params, Context context){
        mContext=context;
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/*");
        if (params != null) {
            String fileUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), params, null, null);
            if (TextUtils.isEmpty(fileUrl)) {// 插入系统相册失败时保存文件 upd 2019-04-13 19:00:41
                fileUrl = saveFile(params, 100);
            }
            final Uri fileUri = Uri.parse(fileUrl);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);  //传输图片或者文件 采用流的方式
        } else {
            Log.d("TAG", "openSystenShare: bitmap is null");
        }
        context.startActivity(intent);
    }

    /**
     * 分享到全部平台
     * @param params
     * @param context
     */
    public static void openSystenShare(Bitmap params, Context context) {
        mContext=context;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
         //分享图片
        if (params != null) {
            String fileUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(), params, null, null);
            if (TextUtils.isEmpty(fileUrl)) {// 插入系统相册失败时保存文件 upd 2019-04-13 19:00:41
                fileUrl = saveFile(params, 100);
            }
            final Uri fileUri = Uri.parse(fileUrl);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);  //传输图片或者文件 采用流的方式
        } else {
            Log.d("TAG", "openSystenShare: bitmap is null");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享到"));
//        Log.d("TAG", "openSystenShare: success");
    }

    /**
     * 保存分享的图片
     * @param bitmap
     * @param quality
     * @return
     */
    private static String saveFile(Bitmap bitmap, int quality) {
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path = getQPATH(mContext, Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + "_share.jpg";
        } else {
            path = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + System.currentTimeMillis() + "_share.jpg";
        }
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                Log.d("TAG", "saveFile: 保存为JPEG");
                out.flush();
                out.close();
            } else if (bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)) {
                Log.d("TAG", "saveFile: 保存为PNG");
                out.flush();
                out.close();
            } else {
                return null;
            }
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
            return null;
        } catch (IOException var6) {
            var6.printStackTrace();
            return null;
        }
        return path;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getQPATH(Context context, String type) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD卡已装入
            File externalFilesDirs[] = context.getExternalFilesDirs(type);
            if (externalFilesDirs.length > 0 && externalFilesDirs[0] != null) {
                return externalFilesDirs[0].getAbsolutePath();
            } else {
                return context.getFilesDir().getAbsolutePath();
            }
        } else {
            // 无sd卡
            File file = context.getExternalFilesDir(type);
            if (file != null && !TextUtils.isEmpty(file.getAbsolutePath())) {
                return file.getAbsolutePath();
            } else {
                return context.getFilesDir().getAbsolutePath();
            }
        }
    }
    private static void addShareIntent(List<Intent> list, ActivityInfo ainfo, Bitmap params) {

        Intent target = new Intent(Intent.ACTION_SEND);
        target.setType("image/*");
        //分享图片
        if (params != null) {
            String fileUrl = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), params, null, null);
            if (TextUtils.isEmpty(fileUrl)) {// 插入系统相册失败时保存文件 upd 2019-04-13 19:00:41
                fileUrl = saveFile(params, 100);
            }
            final Uri fileUri = Uri.parse(fileUrl);
            target.putExtra(Intent.EXTRA_STREAM, fileUri);  //传输图片或者文件 采用流的方式
        } else {
            Log.d("TAG", "openSystenShare: bitmap is null");
        }
        target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.putExtra(Intent.EXTRA_TEXT, "分享看看小可爱");
        target.setPackage(ainfo.packageName);
        target.setClassName(ainfo.packageName, ainfo.name);
        list.add(target);
    }
}
