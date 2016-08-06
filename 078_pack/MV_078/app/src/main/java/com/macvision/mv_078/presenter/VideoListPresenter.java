package com.macvision.mv_078.presenter;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.macvision.mv_078.data.BaseData;
import com.macvision.mv_078.data.VideoData;
import com.macvision.mv_078.data.entity.VideoMain;
import com.macvision.mv_078.ui.view.IBaseView;
import com.macvision.mv_078.ui.view.VideoListView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 首页视图控制器
 * Created by bzmoop on 2016/7/29 0029.
 */
public class VideoListPresenter extends BasePresenter<VideoListView> {

    /**
     *当前页数
     */
    private int mCurrentPage = 1;
    private int mCountOfGetMoreDataEmpty = 0;
    private boolean hasLoadMoreData = false;


    /**
     * test数据
     */
    private int mStart=0, mLimit = 5;
    /**
     * 设置每页的长度
     */
    private static final int PAGE_SIZE = 10;

    public VideoListPresenter(Activity context, VideoListView view) {
        super(context, view);
    }

    /**
     * 重置页数
     */
    public void restCurrentPage(){
        mCurrentPage = 1;
    }

    /**
     *只有当前只加载了第一页 那么下拉刷新才应该去执行数据请求，如果加载的页数超过两页
     * 则不去执行重新加载的数据请求，此时的刷新为假刷新，不去请求数据。这是一种良好的用户体验
     */
    public boolean shouldRefill(){
        return mCurrentPage <= 2;
    }
    /**
     * 重新加载数据，历史数据将会清零
     */
    public void getData() {
        mVideoService.getVideoListData(mStart, mLimit)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .map(new Func1<VideoData, VideoData>() {
                    @Override
                    public VideoData call(VideoData videoData) {
                        return videoData;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoData>() {
                    @Override
                    public void onCompleted() {
                        Log.d("moop","onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("moop","onError:"+e.toString());
                    }
                    @Override
                    public void onNext(VideoData videoMains) {
                        Log.d("moop","onNext"+videoMains.data.size());
                        Log.d("moop","onNext"+videoMains.msg);
//                        Log.d("moop","文件路径"+Environment.getExternalStorageDirectory());
                        if (videoMains.data.isEmpty()){
                            getData();
                        }else {
                              mCountOfGetMoreDataEmpty = 0;
                              mView.fillData(videoMains.data);
                        }
                        mView.getDataFinish();

                    }
                });


    }
    public void getDataMore(){
        mVideoService.getVideoListData(mStart+5, mLimit+5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .map(new Func1<VideoData, VideoData>() {
                    @Override
                    public VideoData call(VideoData videoData) {
                        return videoData;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoData>() {
                    @Override
                    public void onCompleted() {
                        Log.d("moop","onCompleted");
                        hasLoadMoreData = true;
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("moop","onError:"+e.toString());
                    }
                    @Override
                    public void onNext(VideoData videoMains) {
                        if (videoMains.data.isEmpty()){
                             mCountOfGetMoreDataEmpty +=1;
                            if (mCountOfGetMoreDataEmpty>=5){
                                mView.hasNoMoreData();
                            } else{
                                getDataMore();
                            }
                        }else {
                            mCountOfGetMoreDataEmpty = 0;
                        }
                        mView.getDataFinish();
                    }
                });

    }

    @Override
    public void release() {

    }

    @Override
    public void attachView(IBaseView baseView) {

    }

    @Override
    public void detachView() {

    }

    public boolean shouldRefillData(){
        return !hasLoadMoreData;
    }
}
