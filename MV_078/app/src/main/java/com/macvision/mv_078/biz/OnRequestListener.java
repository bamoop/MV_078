package com.macvision.mv_078.biz;

import java.util.List;

/**
 * Created by bzmoop on 2016/7/28 0028.
 */
public interface OnRequestListener {
    void onSuccess(List<String> data);
    void onFailed();
}
