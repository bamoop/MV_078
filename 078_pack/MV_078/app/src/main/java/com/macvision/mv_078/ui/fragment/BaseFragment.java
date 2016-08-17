package com.macvision.mv_078.ui.fragment;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macvision.mv_078.presenter.BasePresenter;

import butterknife.ButterKnife;

/**
 * 作者：LiangXiong on 2016/8/3 0003 13:54
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment {

    /**
     * 设置presenter
     */
    protected P mPresenter;

    public Activity currentContext;
    public View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentContext = getActivity();
        if (view == null) {
            view = inflater.inflate(getLayout(), null);
            ButterKnife.bind(this, view);
            initPresenter();
            checkPresenterIsNull();
            initData();
        }

        return view;
    }

    /**
     * 设置Fragment布局文件
     */
    protected abstract int getLayout();

    /**
     * 实例化Presenter
     */
    protected  abstract void initPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 如果Presenter是空的就抛出异常
     */
    private void checkPresenterIsNull(){
        if(mPresenter == null){
            throw new IllegalStateException("please init mPresenter in initPresenter() method ");
        }
    }
    public abstract void initData();
}
