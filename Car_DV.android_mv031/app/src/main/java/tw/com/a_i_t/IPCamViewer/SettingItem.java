package tw.com.a_i_t.IPCamViewer;

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