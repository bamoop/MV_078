package com.macvision.mv_078.TestCode;/**
 * Created by bzmoop on 2016/8/12 0012.
 */

import android.content.Context;
import android.telephony.TelephonyManager;

import rx.Subscriber;

/**
 * 作者：LiangXiong on 2016/8/12 0012 11:26
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public abstract class RxSubscriber<T> extends Subscriber<T> {
    @Override
    public void onNext(T t) {
        _onNext(t);
    }
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
//        if (TDevice.getNetworkType() == 0) {
        if (false) {
            _onError("网络不可用");
//        } else if (e instanceof ServerException) {
        } else if (false) {
            _onError(e.getMessage());
        } else {
            _onError("请求失败，请稍后再试...");
        }
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);

}