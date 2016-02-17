package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

public class SettingItem
{
	public String name;
	public int value;
	public SettingItem(String pname,int pvalue)
	{
		name = pname;
		value = pvalue;
	}
	public SettingItem()
	{
		name = "";
		value = 0;
	}
}