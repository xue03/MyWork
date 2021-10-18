package com.work.mywork.net;

import android.util.Log;

import com.work.mywork.utils.SystemUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {
    private static volatile HttpManager httpManager;
    private static Retrofit retrofitBuild;

    public static HttpManager getInstance(){
        if (httpManager==null){
            synchronized (HttpManager.class){
                if (httpManager==null){
                    httpManager=new HttpManager();
                }
            }
        }
        return httpManager;
    }
    private static Retrofit initRetrofit(String url){
        retrofitBuild = new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofitBuild;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .addNetworkInterceptor(new NetInterceptor())
                .addInterceptor(new NetInterceptor())
                .callTimeout(10, TimeUnit.SECONDS)
                .build();
        return okHttpClient;

    }

    private static class NetInterceptor implements Interceptor{

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            if (!SystemUtils.checkNetWork()){
                request=request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if(!SystemUtils.checkNetWork()){
                int maxAge = 0;
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control","public ,max-age="+maxAge).build();
            }else{
                int maxStale = 60*60*24*28; //设置缓存数据的保存时间
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control","public, onlyif-cached, max-stale="+maxStale).build();
            }
        }
    }
    private static class LogInterceptor implements Interceptor{

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {

            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.i("interceptor",String.format("Sending request %s on %s%n%s",request.url(),chain.connection(),request.headers()));

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            Log.i("Received:",String.format("Received response for %s in %.1fms%n%s",response.request().url(),(t2-t1)/1e6d,response.headers()));
            if(response.header("session_id") != null){
                //Constant.session_id = response.header("session_id");
            }

            return response;
        }
    }




























}
