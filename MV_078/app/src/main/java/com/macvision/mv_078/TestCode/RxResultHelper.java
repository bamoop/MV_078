package com.macvision.mv_078.TestCode;/**
 * Created by bzmoop on 2016/8/12 0012.
 */

import android.util.Log;

import com.macvision.mv_078.model.BaseModel;
import com.orhanobut.logger.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Rx处理服务器返回
 * 作者：LiangXiong on 2016/8/12 0012 11:21
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class RxResultHelper {
    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<BaseModel<T>, T> handleResult() {
        return new Observable.Transformer<BaseModel<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseModel<T>> tObservable) {
                return tObservable.flatMap(new Func1<BaseModel<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(BaseModel<T> result) {
                        Logger.i("result from network : " + result);
                        if (result.success()) {
                            return createData(result.data);
                        } else {
                            return Observable.error(new Throwable(result.msg));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    /**
     * 创建成功的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

    }
}
