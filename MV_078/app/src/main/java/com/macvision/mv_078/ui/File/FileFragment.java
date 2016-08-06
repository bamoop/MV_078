package com.macvision.mv_078.ui.File;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;

import com.macvision.mv_078.R;
import com.macvision.mv_078.ui.VideoList.FragmentTab1;
import com.macvision.mv_078.ui.adapter.FragmentTableAdapter;
import com.macvision.mv_078.base.BaseFragment;
import com.macvision.mv_078.util.ScreenUtils;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 作者：LiangXiong on 2016/8/3 0003 19:33
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FileFragment extends BaseFragment {
    @Bind(R.id.tab_selector)
    TabLayout mTableLayout;
    @Bind(R.id.vp_file)
    ViewPager mViewPager;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private String[] mTitle;
    FragmentTableAdapter mFragmentTableAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected int getLayout() {
        return R.layout.fragment_file;
    }

    @Override
    protected void initView() {
        initData();
        mFragmentTableAdapter = new FragmentTableAdapter(getChildFragmentManager(), fragmentArrayList, mTitle);
        mViewPager.setAdapter(mFragmentTableAdapter);
        mTableLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTableLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void lazyLoad() {

    }

    private void initData() {
        mTitle = new String[]{"前视频", "小视频", "照片"};
        fragmentArrayList.clear();
        for (int i = 0; i < mTitle.length; i++) {
            Bundle bundle = new Bundle();
            fragmentArrayList.add(FileChildFragment.Tab1Instance(bundle));
        }
    }
}
