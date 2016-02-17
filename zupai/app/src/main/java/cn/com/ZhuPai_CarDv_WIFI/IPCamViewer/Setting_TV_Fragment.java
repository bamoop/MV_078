package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import android.os.Bundle ;
import android.view.View ;


public class Setting_TV_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SettingItem item0 = new SettingItem(getResources().getString(R.string.Flicker_50), 0);
		SettingItem item1 = new SettingItem(getResources().getString(R.string.Flicker_60), 1);
		SettingItem[] items = new SettingItem[]{item0,item1};
		setData(items);
		setBannerTxt(getResources().getString(R.string.label_flicker_frequency));
		setOnItemClieckListener(this);
	}
	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		
		if(item!=null)
		{
			URL url = CameraCommand.commandSetflickerfrequencyUrl(item.value) ;		
			if (url != null) {
				
				new CameraSettingsSendRequest().execute(url) ;
			}
		}
	}
	public void refreshData()
	{
		mSelectedItem = mFlickerRet;
		super.refreshData();
	}
	
}
