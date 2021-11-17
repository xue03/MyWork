package com.work.mywork.app;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

public class MyApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //环信初始化
        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey("1431210929107274#kefuchannelapp98428");
        options.setTenantId("98428");
        if (!ChatClient.getInstance().init(this, options)) {
            return;
        }
        UIProvider.getInstance().init(this);
        //友盟初始化
        UMConfigure.init(this, "611a02e41fee2e303c23c22a", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

        // 微信设置 需到开发平台注册应用获取appid
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setWXFileProvider("com.work.mywork.fileprovider");
        // QQ设置
        PlatformConfig.setQQZone("101830139", "5d63ae8858f1caab67715ccd6c18d7a5");
        PlatformConfig.setQQFileProvider("com.work.mywork.fileprovider");
        // 新浪微博设置
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
        PlatformConfig.setSinaFileProvider("com.work.mywork.fileprovider");
    }
}
