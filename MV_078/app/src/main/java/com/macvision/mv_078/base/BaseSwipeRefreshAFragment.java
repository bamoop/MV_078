package com.macvision.mv_078.base;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.macvision.mv_078.R;
import com.orhanobut.logger.Logger;

/**
 * 作者：LiangXiong on 2016/8/3 0003 16:08
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public abstract class BaseSwipeRefreshAFragment<P extends BasePresenter> extends BaseFragment<P> implements ISwipeRefreshView{

//    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    private void initSwipeLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Logger.i( "下拉刷新: "+prepareRefresh());
                if (prepareRefresh()) {
                    onRefreshStarted();
                } else {
                    //产生一个加载数据的假象
                    hideRefresh();
                }
            }
        });
    }

    /**
     * 检查数据状态
     * @return 返回true表示应该加载数据而不是假象
     */
    protected boolean prepareRefresh(){
        return true;
    }

    /**
     * 加载数据的方法
     */
    protected abstract void onRefreshStarted();



    @Override
    public void getDataFinish() {
        hideRefresh();

    }

    /**
     * 显示错误的View（例如网路加载错误）
     */
    @Override
    public void showErrorView(Throwable throwable) {

    }

    @Override
    public void showRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @CheckResult
    protected boolean isRefreshing(){
        return mSwipeRefreshLayout.isRefreshing();
    }
    @Override
    public void hideRefresh() {
        // 防止刷新消失太快，让子弹飞一会儿. do not use lambda!!
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        },2000);
    }


}
