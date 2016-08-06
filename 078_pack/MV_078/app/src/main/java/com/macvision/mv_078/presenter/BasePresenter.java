package com.macvision.mv_078.presenter;

import android.app.Activity;
import android.content.Context;

import com.macvision.mv_078.core.MainFactory;
import com.macvision.mv_078.core.VideoListService;
import com.macvision.mv_078.ui.view.IBaseView;

/**
 * Created by bzmoop on 2016/7/29 0029.
 */
public abstract class BasePresenter<VV extends IBaseView> implements Presenter{

    protected VV mView;
    /**
     * TODO 这里用是否用Activity带考证
    * */
    protected Context mContext;

    public static final VideoListService mVideoService = MainFactory.getVideoInstance();

    public BasePresenter(Context context, VV view){
        mContext = context;
        mView = view;
    }
    public void init(){
        mView.initView();
    }
    public abstract void release();
}
