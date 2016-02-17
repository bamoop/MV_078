package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View ;


public class Setting_photo_Quality_Fragment extends Setting_Base_Fragment implements Setting_Base_Fragment.OnItemListener {

	private static final String TAG="Setting_photo_Quality_Fragment";
	MainActivity.MyOnTouchListener myOnTouchListener;
	private GestureDetector mGestureDetector;
	private static boolean isINFilebrowser=true;//当前fragment是否打开
	private boolean returnBlankScreen=false;//待机黑屏
	private boolean returnFragment=false;//待机自动退出

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

