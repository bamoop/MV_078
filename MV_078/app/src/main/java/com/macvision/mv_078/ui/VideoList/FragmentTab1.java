package com.macvision.mv_078.ui.VideoList;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macvision.mv_078.R;
import com.macvision.mv_078.base.BaseFragment;
import com.macvision.mv_078.model.entity.VideoEntity;
import com.macvision.mv_078.presenter.VideoListPresenter;
import com.macvision.mv_078.ui.VideoDetail.VideoDetails_Activiey;
import com.macvision.mv_078.ui.adapter.MainVideoListAdapter;
import com.macvision.mv_078.ui.adapter.RecycleViewDivider;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：LiangXiong on 2016/8/3 0003 19:33
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FragmentTab1 extends BaseFragment implements VideoContract.View,MainVideoListAdapter.IClickMainItem {

    @Bind(R.id.rv_mainvideo)
    RecyclerView rv_mainvideo;
    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager layoutManager;
    private VideoListPresenter mPresenter;
    MainVideoListAdapter mAdapter;

    /**
     * 有更多的数据
     */
    private boolean mHasMoreData = true;

    public  int currentPage = 1;

    private List<VideoEntity.VideolistEntity> mDataList;

    private int type = 0;

    public static FragmentTab1 Tab1Instance(Bundle bundle) {
        FragmentTab1 tab1Fragment = new FragmentTab1();
        tab1Fragment.setArguments(bundle);
        return tab1Fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_tab1;
    }

    @Override
    protected void initView() {

        type = getArguments().getInt("type");
        mPresenter = new VideoListPresenter(this);

        mDataList = new ArrayList<>();
        mAdapter = new MainVideoListAdapter(getActivity(), mDataList);
        mAdapter.setClickItem(this);
        layoutManager = new LinearLayoutManager(getActivity());
        rv_mainvideo.setLayoutManager(layoutManager);
        rv_mainvideo.setAdapter(mAdapter);
        rv_mainvideo.addItemDecoration(new RecycleViewDivider(
                currentContext, LinearLayoutManager.VERTICAL, 1,
                getResources().getColor(R.color.theme_fragment_bgColor)));
        initSwipeLayout();
        initEvent();
        Logger.i("type"+type);

    }

    private void initSwipeLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                onRefreshStarted();
                currentPage = 1;
                showRefresh();
                mPresenter.start(currentPage, type, false);
                 mHasMoreData = true;
                Logger.i("onRefresh: 下拉刷新");
            }
        });
        rv_mainvideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isBottom =
                        layoutManager.findLastCompletelyVisibleItemPosition() >= mAdapter.getItemCount() - 1;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom && mHasMoreData) {
                    mPresenter.start(currentPage, type, true);
                    Logger.i("onScrolled: onScrolled"+ "page=" + currentPage);
                    showRefresh();
                }
            }
        });
    }

    /**
     * 隐藏刷新动画
     */
    public void hideRefresh() {
        // 防止刷新消失太快，让子弹飞一会儿. do not use lambda!!
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1000);
    }

    /**
     * 显示刷新动画
     */
    public void showRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    private void initEvent() {
        mPresenter.start(currentPage, type, false);
    }

    @Override
    protected void lazyLoad() {

    }



    @Override
    public void fillData(VideoEntity entity) {
        currentPage++;
        if (currentPage == 1) {
            mDataList.clear();
        }
        mAdapter.updateWithClear(entity.getNewslist());

    }

    @Override
    public void hasNoMoreData() {
        mHasMoreData = false;
    }

    @Override
    public void appendMoreDataToView(VideoEntity entity) {
        currentPage++;
        mAdapter.update(entity.getNewslist());
    }

    @Override
    public void getDataFinish() {
        hideRefresh();
    }

    @Override
    public void onClickItemVideo(View view, VideoEntity.VideolistEntity videoEntity) {
        Intent intent = new Intent(getActivity(), VideoDetails_Activiey.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("VideoEntity", videoEntity);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
