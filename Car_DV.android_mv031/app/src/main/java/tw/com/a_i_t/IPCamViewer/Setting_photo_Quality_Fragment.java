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



public class Setting_photo_Quality_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
		
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String minutes = getString(R.string.minutes);
		SettingItem item0 = new SettingItem("1"+minutes, 0);
		SettingItem item1 = new SettingItem("2"+minutes, 1);
		SettingItem item2 = new SettingItem("3"+minutes, 2);
		SettingItem item3 = new SettingItem("5"+minutes, 3);
		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3};
		setData(items);
		setBannerTxt(getResources().getString(R.string.label_image_resolution));
		setOnItemClieckListener(this);
	}

	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		
		if(item!=null)
		{
			URL url = CameraCommand.commandSetVideoFragTimeUrl(item.value) ;		
			if (url != null) {	
				new CameraSettingsSendRequest().execute(url) ;
			}
		}
	}
	public void refreshData()
	{
		mSelectedItem = mVideoClipTime;
		super.refreshData();
	}
}

