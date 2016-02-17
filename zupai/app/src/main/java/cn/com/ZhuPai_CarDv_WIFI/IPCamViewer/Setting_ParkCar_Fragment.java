package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;


public class Setting_ParkCar_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
	private static final String TAG="Setting_ParkCar_Fragment";
	private int mvalue = 0;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SettingItem item0 = new SettingItem(getResources().getString(R.string.label_Off), 0);
		SettingItem item1 = new SettingItem(getResources().getString(R.string.label_High), 1);
		SettingItem item2 = new SettingItem(getResources().getString(R.string.label_Middle), 2);
		SettingItem item3 = new SettingItem(getResources().getString(R.string.label_Low), 3);
		mIsGetMenuCommand = false;
		
		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3};
		setData(items);
		setBannerTxt(getResources().getString(R.string.label_parkcar));
		setOnItemClieckListener(this);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		//View view = inflater.inflate(R.layout.setting_list_base, container, false) ;
		//MyBanner banner = (MyBanner)view.findViewById(R.id.banner);
		//banner.setTitile(mBannerTxt);
		 View view = super.onCreateView(inflater, container, savedInstanceState);
		 new GetParkCarValues().execute();
		 return view;
	}
	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		
		if(item!=null)
		{
			URL url = CameraCommand.commandSetParkingprotectSettingsUrl(item.value) ;		
			if (url != null) {
				
				new CameraSettingsSendRequest().execute(url) ;
			}
		}
	}
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			Log.d(TAG,"mSelectedItem="+mvalue);
			if(mvalue<list_items.size() && mvalue>-1)
			{
				list_items.get(mvalue).setChecked(true);
			}
		}
		};
private class GetParkCarValues extends AsyncTask <URL, Integer, String> {
		
		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetParkingprotectSettingsUrl() ;
			if (url != null) {		
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			//Log.d(TAG, "TimeStamp property "+result) ;
			if (result != null) {
				Log.d(TAG, "result="+result);
				String[] lines;
				String[] lines_temp = result.split("Camera.Cruise.Seq4.Count=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
						mvalue = Integer.valueOf(lines[0]);
				}
				mHandler.sendEmptyMessage(1);
			}
			super.onPostExecute(result) ;
		}
	}
	
	
}
