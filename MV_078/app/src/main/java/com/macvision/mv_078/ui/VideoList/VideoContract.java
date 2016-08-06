package com.macvision.mv_078.ui.VideoList;/**
 * Created by bzmoop on 2016/8/11 0011.
 */

import com.macvision.mv_078.base.BasePresenter;
import com.macvision.mv_078.base.BaseView;
import com.macvision.mv_078.model.entity.VideoEntity;

/**
 * 作者：LiangXiong on 2016/8/11 0011 11:28
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public interface VideoContract {
    interface View extends BaseView<Presenter>{
            /**显示数据*/
            void fillData(VideoEntity entity);

            /**没有更多数据*/
            void hasNoMoreData();

            /**添加更多数据*/
            void appendMoreDataToView(VideoEntity entity);

            /**数据加载完成*/
            void  getDataFinish();
    }
    interface Presenter extends BasePresenter{
        void start(int page,int type,boolean isgetDataMore);
    }

}
