package com.macvision.mv_078.model.entity;/**
 * Created by bzmoop on 2016/8/12 0012.
 */

/**
 * 作者：LiangXiong on 2016/8/12 0012 16:07
 * 邮箱：liangxiong.sz@foxmail.com
 * QQ  ：294894105
 */
public class FileEntity {
    String naame;
    String path;
    String size;
    String format;
    private boolean isChecked;

    public String getNaame() {
        return naame;
    }

    public void setNaame(String naame) {
        this.naame = naame;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
