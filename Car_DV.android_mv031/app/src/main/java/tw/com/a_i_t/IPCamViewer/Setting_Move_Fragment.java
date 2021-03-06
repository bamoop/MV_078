package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;

import tw.com.a_i_t.IPCamViewer.R ;
import android.app.Fragment ;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.Button;
import android.widget.ListView;



public class Setting_Move_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SettingItem item0 = new SettingItem(getResources().getString(R.string.label_Off), 0);
		SettingItem item1 = new SettingItem(getResources().getString(R.string.label_Low), 1);
		SettingItem item2 = new SettingItem(getResources().getString(R.string.label_Middle), 2);
		SettingItem item3 = new SettingItem(getResources().getString(R.string.label_High), 3);
		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3};
		setData(items);
		setBannerTxt(getResources().getString(R.string.label_motion_detection));
		setOnItemClieckListener(this);
	}
	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		if(item!=null)
		{
			URL url = CameraCommand.commandSetmotiondetectionUrl(item.value) ;		
			if (url != null) {
				new CameraSettingsSendRequest().execute(url) ;
			}
		}
	}
	public void refreshData()
	{
		mSelectedItem = mMTDRet;
		super.refreshData();
	}
}
