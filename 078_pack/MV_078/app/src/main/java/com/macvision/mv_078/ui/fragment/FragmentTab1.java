package com.macvision.mv_078.ui.fragment;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.macvision.mv_078.R;
import com.macvision.mv_078.data.entity.VideoMain;
import com.macvision.mv_078.presenter.VideoListPresenter;
import com.macvision.mv_078.ui.adapter.MainVideoListAdapter;
import com.macvision.mv_078.ui.view.VideoListView;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;

/**
 * 作者：LiangXiong on 2016/8/3 0003 19:33
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FragmentTab1 extends BaseSwipeRefreshAFragment<VideoListPresenter> implements VideoListView<VideoMain>,MainVideoListAdapter.IClickMainItem {

    @Bind(R.id.rv_mainvideo)
    RecyclerView rv_mainvideo;
    MainVideoListAdapter mAdapter;


    @Override
    protected int getLayout() {
        return R.layout.fragment_tab1;
    }

    @Override
    protected void initPresenter() {
        mPresenter=new VideoListPresenter(currentContext,this);
    }


    @Override
    public void initData() {
        initRecycleView();
//        rv_mainvideo.notifyAll();
        getData();


    }

    private void getData(){
        mPresenter.getData();
    }

    @Override
    protected void onRefreshStarted() {
        getData();
    }

    @Override
    public void fillData(List<VideoMain> data) {
        mAdapter.update(data);
        rv_mainvideo.notifyAll();
    }

    @Override
    public void appendMoreDataToView(List<VideoMain> data) {
        mAdapter.update(data);
    }

    @Override
    public void hasNoMoreData() {
    //没有更多数据
    }

    @Override
    public void showEmptyView() {

    }

    @Override
    protected boolean prepareRefresh() {
        return mPresenter.shouldRefillData();
    }

    private void initRecycleView(){
        final LinearLayoutManager layoutManager = new LinearLayoutManager(currentContext);
        rv_mainvideo.setLayoutManager(layoutManager);
        mAdapter = new MainVideoListAdapter(currentContext);
        mAdapter.setClickItem(this);
        rv_mainvideo.setAdapter(mAdapter);
    }

    @Override
    public void onClickItemVideo(VideoMain video, View view) {
        Logger.d("FragmentTab1","点击事件");
    }
}
