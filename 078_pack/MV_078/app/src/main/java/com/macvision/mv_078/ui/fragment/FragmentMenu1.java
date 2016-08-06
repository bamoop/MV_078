package com.macvision.mv_078.ui.fragment;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.macvision.mv_078.R;
import com.macvision.mv_078.presenter.VideoListPresenter;
import com.macvision.mv_078.ui.adapter.FragmentTableAdapter;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * 作者：LiangXiong on 2016/8/3 0003 16:07
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FragmentMenu1 extends BaseFragment<VideoListPresenter> {
    @Bind(R.id.tab_selector)
    TabLayout mTableLayout;
    @Bind(R.id.vp_news)
    ViewPager mViewPager;
    ArrayList<Fragment> mFragmentlist=new ArrayList<>();
    private String[] mTitle;
    FragmentTableAdapter mFragmentTableAdapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_menu1;
    }

    @Override
    protected void initPresenter() {
        mPresenter =new VideoListPresenter(getActivity(),null);
    }

    @Override
    public void initData() {
        mFragmentlist.add(new FragmentTab1());
        mFragmentlist.add(new FragmentTab2());
        mFragmentlist.add(new FragmentTab3());
        mFragmentlist.add(new FragmentTab4());
        mFragmentlist.add(new FragmentTab5());
        mTitle = new String[]{"标题1","标题2","标题3","标题4","标题5",};
        mFragmentTableAdapter=new FragmentTableAdapter(currentContext,getChildFragmentManager(),mFragmentlist,mTitle);
        mViewPager.setAdapter(mFragmentTableAdapter);
        mTableLayout.setupWithViewPager(mViewPager);
        mTableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }


}
