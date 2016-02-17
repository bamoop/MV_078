package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model;

/**
 * Created by bzmoop on 2015/12/2 0002.
 */
public class CameraStatus {
    public String fWversion;//固件版本
    public String timeStamp;//固件生成时间
    public String statusRecord;//录像状态
    public String soundIndicator;//声音开关状态
    public String menuSD;//SD卡是否正常状态
    public String menuAWB;//白平衡
    public String menuVideoRes;//视频类型
    public String videoClipTime;//视频分段
    public String menuImageRes ;//图像质量
    public String menuMTD;//移动侦测
    public String menuFlicker;//刷新频率
    public String menuEV;//曝光值
    public String menuVidemodel;//视频模式
    public String apSSID;//wifi名称
    public String possword;//wifi密码
    public String sys;   //制式


    public String getfWversion() {
        return fWversion;
    }

    public void setfWversion(String fWversion) {
        this.fWversion = fWversion;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatusRecord() {
        return statusRecord;
    }

    public void setStatusRecord(String statusRecord) {
        this.statusRecord = statusRecord;
    }

    public String getSoundIndicator() {
        return soundIndicator;
    }

    public void setSoundIndicator(String soundIndicator) {
        this.soundIndicator = soundIndicator;
    }

    public String getMenuSD() {
        return menuSD;
    }

    public void setMenuSD(String menuSD) {
        this.menuSD = menuSD;
    }

    public String getMenuAWB() {
        return menuAWB;
    }

    public void setMenuAWB(String menuAWB) {
        this.menuAWB = menuAWB;
    }

    public String getMenuVideoRes() {
        return menuVideoRes;
    }

    public void setMenuVideoRes(String menuVideoRes) {
        this.menuVideoRes = menuVideoRes;
    }

    public String getVideoClipTime() {
        return videoClipTime;
    }

    public void setVideoClipTime(String videoClipTime) {
        this.videoClipTime = videoClipTime;
    }

    public String getMenuImageRes() {
        return menuImageRes;
    }

    public void setMenuImageRes(String menuImageRes) {
        this.menuImageRes = menuImageRes;
    }

    public String getMenuMTD() {
        return menuMTD;
    }

    public void setMenuMTD(String menuMTD) {
        this.menuMTD = menuMTD;
    }

    public String getMenuFlicker() {
        return menuFlicker;
    }

    public void setMenuFlicker(String menuFlicker) {
        this.menuFlicker = menuFlicker;
    }

    public String getMenuEV() {
        return menuEV;
    }

    public void setMenuEV(String menuEV) {
        this.menuEV = menuEV;
    }

    public String getMenuVidemodel() {
        return menuVidemodel;
    }

    public void setMenuVidemodel(String menuVidemodel) {
        this.menuVidemodel = menuVidemodel;
    }

    public String getApSSID() {
        return apSSID;
    }

    public void setApSSID(String apSSID) {
        this.apSSID = apSSID;
    }

    public String getPossword() {
        return possword;
    }

    public void setPossword(String possword) {
        this.possword = possword;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }


}
