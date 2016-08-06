package com.macvision.mv_078.model.impl;

import com.macvision.mv_078.core.VideoListService;
import com.macvision.mv_078.model.VideoData;
import com.macvision.mv_078.util.HttpUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bzmoop on 2016/8/11 0011.
 */
public class BusinessTask {

    public void getVideoList(Subscriber<String> subscriber, int currentPage, int pageSize, int type) {
        HttpUtils.getInstance().initRetrofit().create(VideoListService.class).getVideoListData(currentPage,pageSize, type)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getVideoDetail(Subscriber<String> subscriber, int videoID) {
        HttpUtils.getInstance().initRetrofit().create(VideoListService.class).getVideoDetail(videoID)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getVideoCommentList(Subscriber<String> subscriber, int videoID,int page,int pageSize) {
        HttpUtils.getInstance().initRetrofit().create(VideoListService.class).getCommentList(videoID,page,pageSize)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
