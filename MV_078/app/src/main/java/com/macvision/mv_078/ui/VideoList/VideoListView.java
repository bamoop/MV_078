package com.macvision.mv_078.ui.VideoList;

import com.macvision.mv_078.base.ISwipeRefreshView;
import com.macvision.mv_078.model.entity.SoulEntity;

import java.util.List;

/**
 * Created by bzmoop on 2016/7/29 0029.
 */
public interface VideoListView<T extends SoulEntity> extends ISwipeRefreshView {

    /**
     * 数据加载成功
     * @param data
    * */
    void fillData(List<T> data);

    /**
     *将数据加载到列表当中
     * @param data
     * */
    void appendMoreDataToView(List<T> data);

    /**
     * 没有更多数据
     * */
    void hasNoMoreData();


}
