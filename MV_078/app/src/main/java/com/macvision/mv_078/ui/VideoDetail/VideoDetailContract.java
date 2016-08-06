package com.macvision.mv_078.ui.VideoDetail;/**
 * Created by bzmoop on 2016/8/11 0011.
 */

import com.macvision.mv_078.base.BasePresenter;
import com.macvision.mv_078.base.BaseView;
import com.macvision.mv_078.model.entity.CommentEntity;
import com.macvision.mv_078.model.entity.VideoDetailEntity;
import com.macvision.mv_078.model.entity.VideoEntity;
import com.macvision.mv_078.ui.VideoList.VideoContract;

/**
 * 作者：LiangXiong on 2016/8/11 0011 18:07
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public interface VideoDetailContract {
    interface View extends BaseView<VideoContract.Presenter> {
        /**填充数据*/
        void fillData(VideoDetailEntity entity);
        void fillCommentData(CommentEntity entity);

        /**数据加载完成*/
        void  getDataFinish();
    }
    interface Presenter extends BasePresenter{
        void getData(int videoID);
        void getComment(int video,int page);
    }
    }
