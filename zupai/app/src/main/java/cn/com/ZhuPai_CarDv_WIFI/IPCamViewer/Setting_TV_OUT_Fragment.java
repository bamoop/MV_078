package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.TextView;


public class Setting_TV_OUT_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {
	private static final String TAG="Setting_TV_OUT_Fragment";
	private int mvalue = 0;
	private ProgressDialog mProgressDialog ;
	View views;
	MainActivity.MyOnTouchListener myOnTouchListener;
	private GestureDetector mGestureDetector;
	private static boolean isINFilebrowser=true;//当前fragment是否打开
	private boolean returnBlankScreen=false;//待机黑屏
	private boolean returnFragment=false;//待机自动退出
	SettingItem item0,item1,item2;
	SettingItem[] items;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (new MainActivity().M_VERSION > 2) {
			 item0 = new SettingItem(getResources().getString(R.string.label_Off), 0);
			 item1 = new SettingItem(getResources().getString(R.string.label_Nsystem), 1);
			 item2 = new SettingItem(getResources().getString(R.string.label_Psystem), 2);
			mIsGetMenuCommand = false;
			items = new SettingItem[]{item0,item1,item2};

		} else {
			item0 = new SettingItem(getResources().getString(R.string.label_off), 0);
			item1 = new SettingItem(getResources().getString(R.string.label_on), 1);
			mIsGetMenuCommand = false;
			items = new SettingItem[]{item0,item1};
		}


		setData(items);
		setBannerTxt(getResources().getString(R.string.label_video_out));
		setOnItemClieckListener(this);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		//View view = inflater.inflate(R.layout.setting_list_base, container, false) ;
		//MyBanner banner = (MyBanner)view.findViewById(R.id.banner);
		//banner.setTitile(mBannerTxt);

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
		 View view = super.onCreateView(inflater, container, savedInstanceState);
		 new GetTVOUTValues().execute();
		 return view;
	}
	@Override
	public void OnMyClickListener(View view) {
		views=view;
		SettingItem item = (SettingItem)views.getTag();
		if(item!=null)
		{
			isSnapshotButtonsinterval();
			showProgress();
			mHandlerUI.sendEmptyMessageDelayed(1, 3000);
		}
//		isSnapshotButtonsinterval();
		// TODO Auto-generated method stub

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
private class GetTVOUTValues extends AsyncTask <URL, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetTVOUTSettingsUrl() ;
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
				String[] lines_temp = result.split(CameraCommand.PROPERTY_TVOUT+"=");
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
	void showProgress() {
		Activity activity = getActivity();
		if(mProgressDialog==null)
		{
			mProgressDialog = new ProgressDialog(activity) ;
			mProgressDialog.setCancelable(true) ;
		}
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View v = layoutInflater.inflate(R.layout.loading_dialog, null);
		TextView t= (TextView) v.findViewById(R.id.title_txt);
		t.setText(R.string.loading_tip);
		TextView tv= (TextView) v.findViewById(R.id.content_txt);
		t.setText(R.string.running);
		mProgressDialog.show() ;
		mProgressDialog.setCancelable(false);
		mProgressDialog.setContentView(v);
	}
	private Handler mHandlerUI=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					mProgressDialog.dismiss();
			}
		}
	};
	Timer timer=new Timer();

	/*限制视频制式点击时间间隔*/
	public  void isSnapshotButtonsinterval(){
		int countTime=0;
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				SettingItem item = (SettingItem)views.getTag();
				URL url = CameraCommand.commandSetTVOUTSettingsUrl(item.value) ;
				if (url != null) {
					new CameraSettingsSendRequest().execute(url) ;
				}
			}
		};timer.schedule(task, 1000 * 3);
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
			Log.i(TAG, "触摸无操作，屏幕亮直接返回上一面--");
//			MainActivity.backToPreFragment();
			MainActivity.backToFristFragment(getActivity());
		}else if (isINFilebrowser&&!returnBlankScreen){
			Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
			returnFragment=true;
			new StartRecord().startRecord();
		}
	}
}
