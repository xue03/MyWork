package com.work.mywork.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationUtil {


    private static String bestProvider;
    private static String locationAddress;
    private static Address address;

    /**
     * 是否开启定位服务
     */
    public static boolean isOpenLocationService(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void showLocationServiceDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("未开启服务,请开启")
                .setNegativeButton("取消", null)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                }).create()
                .show();
    }

    @SuppressLint("WrongConstant")
    public static void getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        for (int i = 0; i < providers.size(); i++) {
            Log.d("TAG", "getLocation: " + providers.get(i));
        }
        Criteria criteria = new Criteria();
        // 查询精度：高，Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精确
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.ACCURACY_LOW);
        //获取最佳定位
        bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d("TAG", "getLocation: " + bestProvider);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                StringBuilder stringBuilder = new StringBuilder();
                if (location != null) {
                    double longitude = location.getLongitude();//经度
                    double latitude = location.getLatitude();//纬度
                    Geocoder geocoder = new Geocoder(context);
                    List<Address> addresses = new ArrayList<>();
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        for (Address address : addresses) {
                            String countryName = address.getCountryName();//国家
                            String adminArea = address.getAdminArea();//省
                            String locality = address.getLocality();//市
                            String featureName = address.getFeatureName();//区
                            stringBuilder.append("国家：").append(countryName).append(",省：").append(adminArea)
                                    .append(",市：").append(locality).append(",区：").append(featureName)
                                    .append(",经度:").append(address.getLongitude())
                                    .append(",纬度：").append(address.getLatitude());
                            Log.d("TAG", "locationUpdate: " + address.getAddressLine(0));
                            SharedPreferences sp = context.getSharedPreferences("location", Context.MODE_PRIVATE);
                            sp.edit().putString("address", address.getAddressLine(0)).commit();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
