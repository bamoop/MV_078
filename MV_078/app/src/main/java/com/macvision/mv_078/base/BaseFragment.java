package com.macvision.mv_078.base;
/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macvision.mv_078.R;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;

/**
 * 作者：LiangXiong on 2016/8/3 0003 13:54
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment {
    public Activity currentContext;
    public View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.init(this.getClass().getCanonicalName());

    }

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentContext = getActivity();
        if (view == null) {
            view = inflater.inflate(getLayout(), null);
            ButterKnife.bind(this, view);
            initView();
        }
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    protected void onInvisible() {

    }

    /**
     * 设置布局文件
     */
    protected abstract int getLayout();

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 延迟加载
     */
    protected abstract void lazyLoad();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
