package com.macvision.mv_078.presenter;/**
 * Created by bzmoop on 2016/8/12 0012.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.macvision.mv_078.model.entity.FileEntity;
import com.macvision.mv_078.ui.File.FileContract;
import com.macvision.mv_078.util.TaskUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.videopls.venvy.f.V;

/**
 * 作者：LiangXiong on 2016/8/12 0012 13:50
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FilePresenter implements FileContract.Presenter {
    private FileContract.View mFileView;
    Activity context;
    ArrayList<FileEntity> mlist = new ArrayList<>();

    public FilePresenter(FileContract.View mFileView) {
        this.mFileView = mFileView;
    }

    @Override
    public void start(String dir, Activity context) {
        TaskUtils.executeAsyncTask(new AsyncTask<String, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(String... params) {
                @SuppressWarnings("deprecation")
                final Cursor mCursor = context.managedQuery(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA}, null, null,
                        "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");

                return mCursor;
            }

            @Override
            protected void onPostExecute(Cursor mCursor) {
                super.onPostExecute(mCursor);
                if (mCursor.moveToFirst()) {
                    do {
                        FileEntity file = new FileEntity();
                        file.setNaame(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                        file.setPath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                        mlist.add(file);
                        Log.i("moop", "fileName: " + file.getNaame());
                        Log.i("moop", "filePath: " + file.getPath());
                    } while (mCursor.moveToNext());
                }
                if(Integer.parseInt(Build.VERSION.SDK) < 11)
                {
                    mCursor.close();

                }
                mFileView.getDataFinish();
                mFileView.fillData(mlist);
            }
        });


    }

    @Override
    public void delete(File file) {

    }

    @Override
    public void upload(File file) {

    }
}
