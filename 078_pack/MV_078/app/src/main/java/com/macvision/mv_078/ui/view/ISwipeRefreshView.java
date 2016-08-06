package com.macvision.mv_078.ui.view;

/**
 * ListVie列表刷新
 * Created by bzmoop on 2016/7/29 0029.
 */
public interface ISwipeRefreshView extends IBaseView{
    /**
     *加载数据完成
     * */
    void getDataFinish();

    void showEmptyView();

    void showErrorView(Throwable throwable);

    void showrefresh();

    void hideRefresh();

}
