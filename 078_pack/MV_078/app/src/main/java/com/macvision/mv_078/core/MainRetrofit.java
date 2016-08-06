package com.macvision.mv_078.core;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by bzmoop on 2016/7/29 0029.
 */
public class MainRetrofit {

    final VideoListService mService ;
    //统一日期格式请求
    final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").serializeNulls().create();

    MainRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException
            {
                Logger.d("TAG1","请求信息"+chain.request());
                Request request = chain.request();
                Logger.json("TAG1"+"json:"+chain.proceed(request));

                long t1 = System.nanoTime();
                //请求地址以及参数
                String requestLog = String.format("Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers());

                if(request.method().compareToIgnoreCase("post")==0){
//                    requestLog ="\n"+requestLog+"\n"+bodyToString(request);
                }
                Log.d("TAG","request"+"\n"+requestLog);

                Response response = chain.proceed(request);
                long t2 = System.nanoTime();

                String responseLog = String.format("Received response for %s in %.1fms%n%s",
                        response.request().url(), (t2 - t1) / 1e6d, response.headers());

                String bodyString = response.body().string();
                Logger.json("TAG"+bodyString);

                Log.d("TAG","response"+"\n"+responseLog+"\n"+bodyString);

                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), bodyString))
                        .build();
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callFactory(client)
                .baseUrl(MainFactory.HOST)
                .build();
        mService = retrofit.create(VideoListService.class);
    }
    public VideoListService getmService(){
        return mService;
    }
    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }}
}
