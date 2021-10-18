package com.work.mywork.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CheckPermission {
    public static boolean checkPermission(Context context, Activity activity, String[] permission, int requestCode) {
        ArrayList<String> permissions = new ArrayList<>();
        for (String per : permission) {
            if (ContextCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {//有权限未通过，需要申请
           // ActivityCompat.requestPermissions((Activity) context, permissions.toArray(new String[permissions.size()]), requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkPermissionSec(Context context, Activity activity, String[] permission, int requestCode) {
        ArrayList<String> permissions = new ArrayList<>();
        for (String per : permission) {
            if (ContextCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per);
            }
        }
        if (!permissions.isEmpty()) {//有权限未通过，需要申请
            //ActivityCompat.requestPermissions((Activity) context, permissions.toArray(new String[permissions.size()]), requestCode);
//            for (String per : permissions) {
//                boolean b = ActivityCompat.shouldShowRequestPermissionRationale(activity, per);
//                if (!b) {
//                    showAlertDialog(context);
//                }
//            }
            showAlertDialog(context);
            return false;
        } else {
            return true;
        }
    }

    public static boolean noPermission(Activity context, String[] permission) {
        boolean isNo = false;
        for (String per : permission) {
            isNo = ActivityCompat.shouldShowRequestPermissionRationale(context, per);
        }
        return isNo;
    }

    public static void showAlertDialog(Context context) {

        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("以禁用权限，请手动授予")
                .setNegativeButton("取消", null)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "去设置", Toast.LENGTH_LONG).show();
                        /*跳转到应用详情，让用户去打开权限*/
                        Intent localIntent = new Intent();
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 9) {
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        } else if (Build.VERSION.SDK_INT <= 8) {
                            localIntent.setAction(Intent.ACTION_VIEW);
                            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                        }
                        context.startActivity(localIntent);

                    }
                }).show();
    }
}
