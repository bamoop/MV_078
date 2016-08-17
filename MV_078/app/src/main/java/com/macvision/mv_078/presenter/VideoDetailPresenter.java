package com.macvision.mv_078.presenter;/**
 * Created by bzmoop on 2016/8/11 0011.
 */

import android.text.TextUtils;
import android.util.Log;

import com.macvision.mv_078.model.entity.CommentEntity;
import com.macvision.mv_078.model.entity.VideoDetailEntity;
import com.macvision.mv_078.model.entity.VideoEntity;
import com.macvision.mv_078.model.impl.BusinessTask;
import com.macvision.mv_078.ui.VideoDetail.VideoDetailContract;
import com.macvision.mv_078.util.GsonUtil;
import com.orhanobut.logger.Logger;

import rx.Subscriber;

/**
 * 作者：LiangXiong on 2016/8/11 0011 18:06
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class VideoDetailPresenter implements VideoDetailContract.Presenter {
    private BusinessTask mVideoTask;
    private VideoDetailContract.View mVideoView;
    private final static int PAGESIZE = 10;

    public VideoDetailPresenter(VideoDetailContract.View mVideoView) {
        this.mVideoView = mVideoView;
        mVideoTask = new BusinessTask();
    }

    @Override
    public void getData(int videoID) {
//        getVideoDetail(videoID);
    }

    @Override
    public void getComment(int videoid, int page) {
        getCommentList(videoid, page,PAGESIZE);
    }

    public void getVideoDetail(int videoID) {
        mVideoTask.getVideoDetail(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Logger.i("moop" + "视频详情请异常:" + e);
            }

            @Override
            public void onNext(String s) {
                if (!TextUtils.isEmpty(s)) {
                    mVideoView.fillData(GsonUtil.changeGsonToBean(s, VideoDetailEntity.class));
                }
                Logger.i("moop", "评论列表数据:" + s);

            }
        }, videoID);
        mVideoView.getDataFinish();
    }

    public void getCommentList(int videoId, int page, int pageSize) {
        mVideoTask.getVideoCommentList(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if (!TextUtils.isEmpty(s)) {
                    mVideoView.fillCommentData(GsonUtil.changeGsonToBean(s, CommentEntity.class));
                    Log.i("comment", "视频详情请求返回:" + s);
                    mVideoView.getDataFinish();
                }
            }
        }, videoId, page, pageSize);
    }
}
