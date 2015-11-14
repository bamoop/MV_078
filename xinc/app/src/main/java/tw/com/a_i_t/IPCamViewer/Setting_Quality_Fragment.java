package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;

import tw.com.a_i_t.IPCamViewer.R ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.ViewTag;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode;
import android.app.Fragment ;
import android.os.Bundle ;
import android.util.Log;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;



public class Setting_Quality_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
		
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SettingItem item0 = new SettingItem("1080p 30", 0);
		SettingItem item1 = new SettingItem("720p 30", 1);
		SettingItem item2 = new SettingItem("720p 60", 2);
		SettingItem item3 = new SettingItem("VGA", 3);
		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3};
		setData(items);
		setBannerTxt(getResources().getString(R.string.label_DV_quality));
		setOnItemClieckListener(this);
	}
		/*
		SettingItem item0 = new SettingItem("1080p 30", 0);
		SettingItem item1 = new SettingItem("720p 30", 0);
		SettingItem item2 = new SettingItem("720p 60", 0);
		SettingItem item3 = new SettingItem("VGA", 0);
		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3};*/

	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		
		if(item!=null)
		{
			URL url = CameraCommand.commandSetmovieresolutionUrl(item.value) ;		
			if (url != null) {	
				new CameraSettingsSendRequest().execute(url) ;
			}
		}
	}
	public void refreshData()
	{
		mSelectedItem = mVideoresRet;
		super.refreshData();
	}
}

