package com.macvision.mv_078.ui.File;/**
 * Created by bzmoop on 2016/8/15 0015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macvision.mv_078.R;
import com.macvision.mv_078.base.BaseFragment;
import com.macvision.mv_078.model.entity.FileEntity;
import com.macvision.mv_078.presenter.FilePresenter;
import com.macvision.mv_078.ui.adapter.FileAdapter;
import com.macvision.mv_078.ui.adapter.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 作者：LiangXiong on 2016/8/15 0015 16:59
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FileChildFragment extends BaseFragment implements FileContract.View {
    @Bind(R.id.rv_filelist)
    RecyclerView rv_filelist;
    private List<FileEntity> mFileList = new ArrayList<FileEntity>();
    private FilePresenter mPresenter;
    private FileAdapter mFileAdapter;
    private LinearLayoutManager layoutManager;
    @Bind(R.id.right_tv)
    TextView mRightTV;


    public static FileChildFragment Tab1Instance(Bundle bundle) {
        FileChildFragment tab1Fragment = new FileChildFragment();
        tab1Fragment.setArguments(bundle);
        return tab1Fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            Toolbar toolbar = (Toolbar) view.findViewById(R.id.file_toolbar);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.filechild_fragment;
    }

    @Override
    protected void initView() {
        mPresenter = new FilePresenter(this);
        mPresenter.start("", getActivity());
        mFileAdapter = new FileAdapter(getActivity(), mFileList);
        layoutManager = new LinearLayoutManager(getActivity());
        rv_filelist.setLayoutManager(layoutManager);
        rv_filelist.setAdapter(mFileAdapter);
        rv_filelist.addItemDecoration(new RecycleViewDivider(
                currentContext, LinearLayoutManager.VERTICAL, 1,
                getResources().getColor(R.color.theme_fragment_bgColor)));
        initData();
        mRightTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItems();
            }
        });
    }

    private void initData() {
        mPresenter.start("", getActivity());
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void fillData(List<FileEntity> entity) {
        mFileList.addAll(entity);
        mFileAdapter.updateWithClear(entity);
    }

    @Override
    public void getDataFinish() {

    }

    private void setToolbar() {
    }


    private void editItems() {
        if ("编辑".equals(mRightTV.getText().toString())) {
            mRightTV.setText("取消");
            mFileAdapter.openItemAnimation();
        } else if ("取消".equals(mRightTV.getText().toString())) {
            mRightTV.setText("编辑");
            mFileAdapter.closeItemAnimation();
        }
    }
}
