package com.macvision.mv_078.base;/**
 * Created by bzmoop on 2016/8/1 0001.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;

/**
 * 作者：LiangXiong on 2016/8/1 0001 16:34
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceStat ) {
        super.onCreate(savedInstanceStat);
        Logger.init(this.getClass().getCanonicalName());
        getSupportActionBar().hide();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
