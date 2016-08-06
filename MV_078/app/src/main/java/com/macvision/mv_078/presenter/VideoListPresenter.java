package com.macvision.mv_078.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.macvision.mv_078.model.entity.VideoEntity;
import com.macvision.mv_078.model.impl.BusinessTask;
import com.macvision.mv_078.ui.VideoList.VideoContract;
import com.macvision.mv_078.util.GsonUtil;
import com.orhanobut.logger.Logger;

import rx.Subscriber;


/**
 * 首页视图控制器
 * Created by bzmoop on 2016/7/29 0029.
 */
public class VideoListPresenter implements VideoContract.Presenter {
    private BusinessTask mVideoTask;
    private VideoContract.View mVideoView;

    /**
     * 当前页数
     */
    private int mCurrentPage = 1;
    private int mCountOfGetMoreDataEmpty = 0;
    private boolean hasLoadMoreData = false;


    /**
     * 设置每页的长度
     */
    private static final int PAGE_SIZE = 5;

    public VideoListPresenter(VideoContract.View mVideoView) {
        this.mVideoView = mVideoView;
        mVideoTask = new BusinessTask();

    }

    @Override
    public void start(int page, int type,boolean isgetDataMore) {
        if (isgetDataMore)
            getVideoListMore(page, PAGE_SIZE, type);
        else
            getVideoList(page, PAGE_SIZE, type);
    }

    public void getVideoList(int page, int pageSize,int type){
        mVideoTask.getVideoList(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Logger.i( "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Logger.i( "onError: " + e);
                mVideoView.getDataFinish();

            }

            @Override
            public void onNext(String s) {
                Logger.i( "onNext: "+"type="+type+"page="+page+"\\n"+s);

                if(!TextUtils.isEmpty(s)){
                    mVideoView.fillData(GsonUtil.changeGsonToBean(s,VideoEntity.class));
                    if ( GsonUtil.changeGsonToBean(s,VideoEntity.class).getNewslist().size()<PAGE_SIZE ){
                        mVideoView.hasNoMoreData();
                    }
                }mVideoView.getDataFinish();
            }
        },page,pageSize,type);
    }
    public void getVideoListMore(int page, int pageSize,int type){
        mVideoTask.getVideoList(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Logger.i( "appendonCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Logger.i( "appendonError: " + e);
                mVideoView.getDataFinish();

            }

            @Override
            public void onNext(String s) {
                Logger.i( "appendonNext: "+"type="+type+"page="+page+"\\n"+s);
                if(!TextUtils.isEmpty(s)){
                    mVideoView.appendMoreDataToView(GsonUtil.changeGsonToBean(s,VideoEntity.class));
                    if ( GsonUtil.changeGsonToBean(s,VideoEntity.class).getNewslist().size()<PAGE_SIZE ){
                        mVideoView.hasNoMoreData();
                    }
                }
                mVideoView.getDataFinish();
            }
        },page,pageSize,type);
    }



}
