package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater ;
import android.view.MotionEvent;
import android.view.View ;
import android.view.ViewGroup ;


public class Setting_Quality_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
	private static final String TAG="Setting_Quality_Fragment";
	private String mvalue = "";

	MainActivity.MyOnTouchListener myOnTouchListener;
	private GestureDetector mGestureDetector;
	private static boolean isINFilebrowser=true;//当前fragment是否打开
	private boolean returnBlankScreen=false;//待机黑屏
	private boolean returnFragment=false;//待机自动退出
	SettingItem item0;
	SettingItem item1;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (new MainActivity().M_VERSION > 2){
			item0 = new SettingItem(getResources().getString(R.string.quality_1080), 0);
			item1 = new SettingItem(getResources().getString(R.string.quality_720), 1);
		}else {
			item0 = new SettingItem(getResources().getString(R.string.quality_1080), 0);
			item1 = new SettingItem(getResources().getString(R.string.quality_72060), 1);
		}
		mIsGetMenuCommand = false;
//		SettingItem item2 = new SettingItem("720p 60", 2);
//		SettingItem item3 = new SettingItem("VGA", 3);
//		SettingItem[] items = new SettingItem[]{item0,item1,item2,item3};
		SettingItem[] items = new SettingItem[]{item0,item1};
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
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			//View view = inflater.inflate(R.layout.setting_list_base, container, false) ;
			//MyBanner banner = (MyBanner)view.findViewById(R.id.banner);
			//banner.setTitile(mBannerTxt);
			View view = super.onCreateView(inflater, container, savedInstanceState);
			new GetTVOUTValues().execute();

			mGestureDetector = new GestureDetector(getActivity(),
					new SideIndexGestureListener());
			myOnTouchListener=new MainActivity.MyOnTouchListener() {
				@Override
				public boolean onTouch(MotionEvent ev) {
					boolean result = mGestureDetector.onTouchEvent(ev);
					if (isINFilebrowser){
						sleepHandler.removeMessages(1);
						sleepHandler.sendEmptyMessageDelayed(1, 1000 * MainActivity.TOUNCHTIME);
						return result;
					}
					return result;
				}
			};
			((MainActivity) getActivity())
					.registerMyOnTouchListener(myOnTouchListener);

			return view;
		}
	@Override
	public void OnMyClickListener(View view) {
		// TODO Auto-generated method stub
		SettingItem item = (SettingItem)view.getTag();
		if(item!=null)
		{
			URL url = CameraCommand.commandSetmovieresolutionUrl(item.value) ;		
			if (url != null) {	
				new CameraSettingsSendRequest().execute(url);
			}
		}
	}
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			Log.d(TAG,"mSelectedItem="+mvalue);
			if(mvalue.equals("0"))
			{
				list_items.get(0).setChecked(true);
			}else{
				list_items.get(1).setChecked(true);
			}
		}
	};
	private class GetTVOUTValues extends AsyncTask<URL, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetQualitySettingsUrl() ;
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
				String[] lines_temp = result.split(CameraCommand.PROPERTY_VIDEORES+"=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
						Log.i("moop",lines[0]);
						mvalue =lines[0];
				}
				mHandler.sendEmptyMessage(1);
			}
			super.onPostExecute(result) ;
		}
	}

	@Override
	public void onPause() {
		returnBlankScreen=false;
		super.onPause() ;
	}

	@Override
	public void onResume() {
		isINFilebrowser=true;
		returnBlankScreen=true;
		if (returnFragment){
			MainActivity.backToFristFragment(getActivity());
		}
		sleepHandler.removeMessages(1);
		sleepHandler.sendEmptyMessageDelayed(1, 1000 * MainActivity.TOUNCHTIME);
		super.onResume() ;
	}
	@Override
	public void onDestroy(){
		isINFilebrowser=false;
		sleepHandler.removeMessages(1);
		super.onDestroy();
	}
	private Handler sleepHandler = new Handler()
	{
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				case 1:
					returnFragment();
					break;
			}
		}
	};
	public synchronized void returnFragment(){
		if (isINFilebrowser&&returnBlankScreen){
			Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
//			getFragmentManager().popBackStack();
			MainActivity.backToFristFragment(getActivity());
		}else if (isINFilebrowser&&!returnBlankScreen){
			Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
			returnFragment=true;
			new StartRecord().startRecord();
		}
	}
}

