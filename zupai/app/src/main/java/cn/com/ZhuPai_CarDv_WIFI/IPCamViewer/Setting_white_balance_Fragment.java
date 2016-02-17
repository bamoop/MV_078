package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import android.os.Bundle ;
import android.view.View ;


public class Setting_white_balance_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SettingItem item0 = new SettingItem(getResources().getString(R.string.AWB_Auto), 0);
		SettingItem item1 = new SettingItem(getResources().getString(R.string.AWB_Daylight), 1);
		SettingItem item2 = new SettingItem(getResources().getString(R.string.AWB_Cloudy), 2);
		SettingItem item3 = new SettingItem(getResources().getString(R.string.AWB_Fluorescent_W), 3);
		SettingItem item4 = new SettingItem(getResources().getString(R.string.AWB_Fluorescent_N), 4);
		SettingItem item5 = new SettingItem(getResources().getString(R.string.AWB_Fluorescent_D), 5);
		SettingItem item6 = new SettingItem(getResources().getString(R.string.AWB_Incandescent), 6);
	
		
		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3,item4,item5,item6};
		setData(items);
		setBannerTxt(getResources().getString(R.string.label_white_balance));
		setOnItemClieckListener(this);
	}
	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		
		if(item!=null)
		{
			URL url = CameraCommand.commandSetAWBUrl(item.value) ;		
			if (url != null) {
				
				new CameraSettingsSendRequest().execute(url) ;
			}
		}
	}
	public void refreshData()
	{
		mSelectedItem = mAWBRet;
		super.refreshData();
	}
}
