package com.macvision.mv_078.util;/**
 * Created by bzmoop on 2016/8/9 0009.
 */

import android.os.AsyncTask;
import android.os.Build;

/**
 * 作者：LiangXiong on 2016/8/9 0009 16:15
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class TaskUtils {

    @SafeVarargs public static <Params, Progress, Result> void executeAsyncTask(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
        else {
            task.execute(params);
        }
    }
}
