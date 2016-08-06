package com.macvision.mv_078.ui.adapter;/**
 * Created by bzmoop on 2016/8/3 0003.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * 作者：LiangXiong on 2016/8/3 0003 17:53
 * 邮箱：liangxiong.sz@foxmail.com
 * 未婚：QQ294894105
 *
 */
public class FragmentTableAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> mFragmentList;
    private String[] mTitle = null;
    public FragmentTableAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
        super(fm);
        this.mContext = context;
        this.mFragmentList = fragments;
        this.mTitle = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}
