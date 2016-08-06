package com.macvision.mv_078.ui.view;

import com.macvision.mv_078.data.entity.Soul;

import java.util.List;

/**
 * Created by bzmoop on 2016/7/29 0029.
 */
public interface VideoListView<T extends Soul> extends ISwipeRefreshView {

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
