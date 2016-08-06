package com.macvision.mv_078.ui.File;

import android.app.Activity;

import com.macvision.mv_078.base.BasePresenter;
import com.macvision.mv_078.base.BaseView;
import com.macvision.mv_078.model.entity.FileEntity;
import com.macvision.mv_078.model.entity.VideoEntity;

import java.io.File;
import java.util.List;

/**
 * Created by bzmoop on 2016/8/12 0012.
 */
public interface FileContract {
    interface View extends BaseView<Presenter> {
        /**
         * 显示数据
         */
        void fillData(List<FileEntity> fileentity);

        /**
         * 数据加载完成
         */
        void getDataFinish();


    }

    interface Presenter extends BasePresenter {
        void start(String dir, Activity context);

        void delete(File file);

        void upload(File file);


    }

}
