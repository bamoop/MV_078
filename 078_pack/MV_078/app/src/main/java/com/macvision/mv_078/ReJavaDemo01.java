package com.macvision.mv_078;


import android.graphics.Bitmap;

import com.macvision.mv_078.core.VideoListService;
import com.macvision.mv_078.data.VideoData;
import com.macvision.mv_078.data.entity.VideoMain;

import java.io.File;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bzmoop on 2016/7/26 0026.
 */
public class ReJavaDemo01 {
    File[] folders;

  public void a(){
      Observable.from(folders)
              .flatMap(new Func1<File, Observable<File>>() {
                  @Override
                  public Observable<File> call(File file) {
                      return Observable.from(file.listFiles());
                  }
              })
              .filter(new Func1<File, Boolean>() {
                  @Override
                  public Boolean call(File file) {
                      return file.getName().endsWith(".png");
                  }
              })
                .map(new Func1<File, Bitmap>() {
                    @Override
                    public Bitmap call(File file) {
                        return null;
                    }
                })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Action1<Bitmap>() {
                  @Override
                  public void call(Bitmap bitmap) {
                  }
              });
  }
    public void requestVideo(){
      /*  Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //新的配置
                .baseUrl("")
                .build();
        VideoListService videoListService = retrofit.create(VideoListService.class);
        videoListService.getVideoListData(0, 5)                 //获取Observable对象
                .subscribeOn(Schedulers.newThread())            //请求在新的线程中执行
                .observeOn(Schedulers.io())                     //请求完成后在IO线程中执行
                .doOnNext(new Action1<VideoData>() {
                    @Override
                    public void call(VideoData VideoData) {
                        //这里可以有返回值
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())      //最后在主线程中执行
                .subscribe(new Subscriber<VideoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败后调用
                    }

                    @Override
                    public void onNext(VideoMain videoMain) {
                        //请求结束后调用
                    }
                });*/
    }

}
