package com.macvision.mv_078.ui.fragment;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.support.v4.app.Fragment;

import com.macvision.mv_078.R;
import com.macvision.mv_078.presenter.VideoListPresenter;

/**
 * 作者：LiangXiong on 2016/8/3 0003 18:02
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FragmentTab4 extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_tab4;
    }

    @Override
    protected void initPresenter() {
        mPresenter =new VideoListPresenter(getActivity(),null);

    }

    @Override
    public void initData() {

    }
}
