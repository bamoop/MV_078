package com.macvision.mv_078.ui.VideoList;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.macvision.mv_078.R;
import com.macvision.mv_078.ui.adapter.FragmentTableAdapter;
import com.macvision.mv_078.base.BaseFragment;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 作者：LiangXiong on 2016/8/3 0003 16:07
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FragmentMenu1 extends BaseFragment {
    @Bind(R.id.tab_selector)
    TabLayout mTableLayout;
    @Bind(R.id.vp_video)
    ViewPager mViewPager;
    ArrayList<Fragment> mFragmentlist = new ArrayList<>();
    private String[] mTitle;
    FragmentTableAdapter mFragmentTableAdapter;

    private void initData() {
        mTitle = new String[]{"热门", "风景", "事故", "精选", "曝光台",};
        mFragmentlist.clear();
        for (int i = 0; i < mTitle.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", i + 1);
            mFragmentlist.add(FragmentTab1.Tab1Instance(bundle));
        }
    }

    @Override
    public void initView() {
        initData();
        mFragmentTableAdapter = new FragmentTableAdapter(getChildFragmentManager(), mFragmentlist, mTitle);
        mViewPager.setAdapter(mFragmentTableAdapter);
        mTableLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(5);
        mTableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_menu1;
    }
}
