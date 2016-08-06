package com.macvision.mv_078.ui.fragment;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import com.macvision.mv_078.R;
import com.macvision.mv_078.presenter.VideoListPresenter;

/**
 * 作者：LiangXiong on 2016/8/3 0003 19:33
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FragmentMenu5 extends BaseFragment {

    @Override
    protected int getLayout() {
        return R.layout.fragment_menu5;
    }

    @Override
    protected void initPresenter() {
        mPresenter =new VideoListPresenter(getActivity(),null);

    }

    @Override
    public void initData() {

    }
}
