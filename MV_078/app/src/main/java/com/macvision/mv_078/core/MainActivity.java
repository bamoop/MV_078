package com.macvision.mv_078.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;


import com.macvision.mv_078.R;
import com.macvision.mv_078.base.BaseActivity;
import com.macvision.mv_078.ui.VideoList.FragmentMenu1;
import com.macvision.mv_078.ui.CloudDog.FragmentMenu2;
import com.macvision.mv_078.ui.Device.FragmentMenu3;
import com.macvision.mv_078.ui.File.FileFragment;
import com.macvision.mv_078.ui.Person.FragmentMenu5;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @Bind({R.id.tab_community ,R.id.tab_clouddog,R.id.tab_device,R.id.tab_file,R.id.tab_person})
    List<RadioButton> mTabs;
    private Fragment mTab01;
    private Fragment mTab02;
    private Fragment mTab03;
    private Fragment mTab04;
    private Fragment mTab05;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSelect(0);
    }
    @OnClick({R.id.tab_community ,R.id.tab_clouddog,R.id.tab_device,R.id.tab_file,R.id.tab_person})
    void bottomClick(View view) {
        resetImgs();
        switch (view.getId()) {
            case R.id.tab_community:
                setSelect(0);
                break;
            case R.id.tab_clouddog:
                setSelect(1);
                break;
            case R.id.tab_device:
                setSelect(2);
                break;
            case R.id.tab_file:
                setSelect(3);
                break;
            case R.id.tab_person:
                setSelect(4);
                break;
        }
    }
    private void setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        // 把图片设置为亮的
        // 设置内容区域
        switch (i)
        {
            case 0:
                if (mTab01 == null)
                {
                    mTab01 = new FragmentMenu1();
                    transaction.add(R.id.id_content, mTab01);
                } else
                {
                    transaction.show(mTab01);
                }
                mTabs.get(0).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_comprehensive_pressed_icon, 0, 0);
                break;
            case 1:
                if (mTab02 == null)
                {
                    mTab02 = new FragmentMenu2();transaction.add(R.id.id_content, mTab02);
                } else
                {
                    transaction.show(mTab02);

                }
                mTabs.get(1).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_move_pressed_icon, 0, 0);
                break;
            case 2:
                if (mTab03 == null)
                {
                    mTab03 = new FragmentMenu3();
                    transaction.add(R.id.id_content, mTab03);
                } else
                {
                    transaction.show(mTab03);
                }
                mTabs.get(2).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_found_pressed_icon, 0, 0);
                break;
            case 3:
                if (mTab04 == null)
                {
                    mTab04 = new FileFragment();
                    transaction.add(R.id.id_content, mTab04);
                } else
                {
                    transaction.show(mTab04);
                }
                mTabs.get(3).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_me_pressed_icon, 0, 0);
                break;

            case 4:
                if (mTab05 == null)
                {
                    mTab05 = new FragmentMenu5();
                    transaction.add(R.id.id_content, mTab05);
                } else
                {
                    transaction.show(mTab05);
                }
                mTabs.get(4).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_me_pressed_icon, 0, 0);
                break;

            default:
                break;
        }

        transaction.commit();
    }

    /**
     * 切换图片至暗色
     */
    private void resetImgs()
    {
        mTabs.get(0).setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_comprehensive_icon, 0, 0);
        mTabs.get(1).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_move_icon, 0, 0);
        mTabs.get(2).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_found_icon, 0, 0);
        mTabs.get(3).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_me_icon, 0, 0);
        mTabs.get(4).setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.tab_me_icon, 0, 0);
    }

    private void hideFragment(FragmentTransaction transaction)
    {
        if (mTab01 != null)
        {
            transaction.hide(mTab01);
        }
        if (mTab02 != null)
        {
            transaction.hide(mTab02);
        }
        if (mTab03 != null)
        {
            transaction.hide(mTab03);
        }
        if (mTab04 != null)
        {
            transaction.hide(mTab04);
        }
        if (mTab05 != null)
        {
            transaction.hide(mTab05);
        }
    }
}
