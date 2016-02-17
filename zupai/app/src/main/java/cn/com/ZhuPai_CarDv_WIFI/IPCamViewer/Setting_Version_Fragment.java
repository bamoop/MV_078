package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater ;
import android.view.MotionEvent;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.LinearLayout;
import android.widget.TextView;



public class Setting_Version_Fragment extends Setting_Base_Fun_Fragment {
	private TextView mTextView;
	private static final String TAG="Setting_Version_Fragment";
	MainActivity.MyOnTouchListener myOnTouchListener;
	private GestureDetector mGestureDetector;
	private static boolean isINFilebrowser=true;//当前fragment是否打开
	private boolean returnBlankScreen=false;//待机黑屏
	private boolean returnFragment=false;//待机自动退出

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setBannerTxt(getResources().getString(R.string.FW_Version));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = super.onCreateView(inflater, container, savedInstanceState);
		//View view = inflater.inflate(R.layout.setting_version, container, false) ;

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
		view.findViewById(R.id.line).setVisibility(View.GONE);
		LinearLayout contentview = (LinearLayout) view.findViewById(R.id.contentview) ;
		
		View view_version = inflater.inflate(R.layout.setting_version, container, false) ;
		contentview.addView(view_version);
		mTextView = (TextView)view_version.findViewById(R.id.hardware_version);
		String pkName = getActivity().getPackageName();
	    String versionName="";
		try {
			versionName = getActivity().getPackageManager().getPackageInfo(
						pkName, 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((TextView)view_version.findViewById(R.id.app_version)).setText(versionName);
		return view;
	}
	public void refreshData()
	{
		mTextView.setText(mFWVersionRet);
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
