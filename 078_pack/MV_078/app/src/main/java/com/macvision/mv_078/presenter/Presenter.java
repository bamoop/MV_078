package com.macvision.mv_078.presenter;

import com.macvision.mv_078.ui.view.IBaseView;

/**
 * Created by bzmoop on 2016/8/3 0003.
 */
public interface Presenter<V extends IBaseView> {
    void attachView(V baseView);
    void detachView();
}
