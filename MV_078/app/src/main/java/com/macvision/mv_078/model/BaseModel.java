package com.macvision.mv_078.model;

import java.io.Serializable;

/**
 * Created by bzmoop on 2016/7/29 0029.
 */
public class BaseModel<T> implements Serializable {
    public String errno;
    public String msg;
    public T data;

    public boolean success() {
        return errno.equals("0");
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "errno='" + errno + '\'' +
                ", msg='" + msg + '\'' +
                ",data=" + data + '}';
    }
}
