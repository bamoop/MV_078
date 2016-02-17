package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.net.URL;

import android.os.Bundle;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.FileBrowserFragment ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.BrowserSettingFragment ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.LocalFileBrowserFragment ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.CameraStatus;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.Viewer.StreamPlayerActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment ;
import android.content.Context ;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo ;
import android.net.wifi.WifiManager ;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.view.View.OnTouchListener ;
import android.view.ViewGroup ;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FunctionListFragment extends Fragment  {
	private static final String TAG = "FunctionListFragment";
	private int mB = -1;
	private int mlocalB=0;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAIL = 2;
	private static final int MSG_DESTORY = 3;
	private static final int MSG_NOSDCARD = 4;
    public static String mRecordStatus="";
    public static String mRecordmode="";
    private boolean mclicksetting = false;
	public CameraStatus cameraStatus;
	public List<CameraStatus>cameraStatusList=new ArrayList<CameraStatus>();
	public boolean isSDCardWarning=true;
	public boolean isshowtoast=true;
	private GetSDCardStatus getSDCardStatus;
	private LoopToast loopToast;
	int version=0;
	boolean firstgetscard=true;
    public boolean isIntoSetting = true;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new MyGetRecordStatus().execute();
//
		/*
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new GSetRandomValues().execute();
			}
		}).start();*/
	}

	///added by eric for stop record when downloading
	private class CameraVideoSendRecordCmd extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url;
			if (version > 2){
				url = CameraCommand.commandCameraStartRecordUrl() ;
				Log.i(TAG,"新版本——发送录像指令"+version);
			}else{
				url= CameraCommand.commandCameraRecordUrl() ;
				Log.i(TAG,"老版本——发送录像指令"+version);
			}
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result) ;

		}
	}
	private class MyGetRecordStatus extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandRecordStatusUrl() ;
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				String[] lines;
				String[] lines_temp = result.split("Camera.Preview.MJPEG.status.record=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mRecordStatus = lines[0];
				}
				lines_temp = result.split("Camera.Preview.MJPEG.status.mode=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mRecordmode = lines[0];
				}
			}
			else
			{
				mRecordmode ="";
				mRecordStatus = "";
			}

			if (mRecordmode.equals("Videomode"))
			{
				if(!mRecordStatus.equals("Recording"))
				{
					new CameraVideoSendRecordCmd().execute();
				}
			}

			super.onPostExecute(result) ;

	}
}
	//add john 2015-12-3
	private class CameraStatusCustomer extends AsyncTask<URL,Integer,String>{

		@Override
		protected String doInBackground(URL... params) {
			URL url=CameraCommand.commandRecordStatusCustomerUrl();
			if (url!=null){
				return CameraCommand.sendRequest(url);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result!=null){
				String[] lines;
				String[] lines_temp=result.split("Camera.Preview.MJPEG.status.customer=weioa.com");

				if (lines_temp!=null&& lines_temp.length>1){
					lines = lines_temp[1].split(",") ;
					if (lines!=null){
						cameraStatus.setfWversion(lines[0]);
						cameraStatus.setTimeStamp(lines[1]);
						cameraStatus.setStatusRecord(lines[2]);
						cameraStatus.setSoundIndicator(lines[3]);
						cameraStatus.setMenuSD(lines[4]);
						cameraStatus.setMenuAWB(lines[5]);
						cameraStatus.setMenuVideoRes(lines[6]);
						cameraStatus.setVideoClipTime(lines[7]);
						cameraStatus.setMenuImageRes(lines[8]);
						cameraStatus.setMenuMTD(lines[9]);
						cameraStatus.setMenuFlicker(lines[10]);
						cameraStatus.setMenuEV(lines[11]);
						cameraStatus.setMenuVidemodel(lines[12]);
						cameraStatus.setApSSID(lines[13]);
						cameraStatus.setPossword(lines[14]);
						cameraStatus.setSys(lines[15]);
						cameraStatusList.add(cameraStatus);
					}
				}
			}
			super.onPostExecute(result);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what)
			{
			case MSG_SUCCESS:
					break;
			case MSG_FAIL:
				CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
				.setTitle(getResources().getString(R.string.verify))
				.setMessage(R.string.verify_error)
				.setPositiveButton(R.string.label_ok,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.dismiss();
						Activity act = getActivity();
						if(act!=null)
							act.finish();
					}
				}).create();

				alertDialog.show() ;
				mHandler.sendEmptyMessageDelayed(MSG_DESTORY, 5000);
					break;
			case MSG_DESTORY:
				Activity act = getActivity();
				if(act!=null)
					act.finish();
					break;
			case MSG_NOSDCARD:

//					CustomDialog alertDialoga = new CustomDialog.Builder(getActivity())
//							.setTitle(R.string.trip)
//							.setMessage(R.string.SDCaedWarning)
//							.setCancelable(false)
//							.setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int arg1) {
//									isSDCardWarning=true;
////									getSDCardStatus.start();
//									dialog.dismiss();
//								}
//							}).create();
//					alertDialoga.show();
				Toast toast = new Toast(getActivity());
//				toast.makeText(getActivity(),
//						getActivity().getResources().getString(R.string.SDCaedWarning),
//						Toast.LENGTH_SHORT) ;
//				toast.setGravity(Gravity.TOP | Gravity.CENTER, 0,0);
//				toast.show();
				toast = Toast.makeText(getActivity(),
						getActivity().getResources().getString(R.string.SDCaedWarning), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
					break;
			default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	private int getRandomvalue()
	{
		Random random=new Random();
		int v = random.nextInt();
		if(v<0)
		{
			v=-v;
		}
		return v;
	}
	private class GSetRandomValues extends AsyncTask <URL, Integer, String>
	{
		protected void onPreExecute() {
			int v = getRandomvalue();
			mlocalB = (v^2014) -1;
			URL url = CameraCommand.commandSetRandomValueUrl(v) ;
			if (url != null) {
				CameraCommand.sendRequest(url) ;
			}
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetRandomValuesUrl() ;
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			boolean flag = false;
			int what = 0;
			if (result != null) {
				Log.d(TAG, "result="+result);
				String[] lines;
				String[] lines_temp = result.split("Camera.Cruise.Seq1.Count=");
				if(lines_temp!=null && lines_temp.length>1)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					{
						try
						{
							mB = Integer.valueOf(lines[0]);
						}
						catch(NumberFormatException e)
						{
							Log.e(TAG,"NumberFormatException="+lines[0]);
						}
					}
					if(mlocalB==mB)
					{
						flag = true;
					}
				}
			}

			if(flag)
			{
				what = MSG_SUCCESS;
			}
			else
			{
				what = MSG_FAIL;
			}
			Message msg =  mHandler.obtainMessage();
			msg.what = what;
			mHandler.sendMessage(msg);
			super.onPostExecute(result) ;
		}
	}
	public Handler mRecordStatusHandler = new Handler() {
		public void handleMessage(Message msg){
			if (mRecordmode.equals("Videomode"))
			{
				if(mRecordStatus.equals("Recording"))
				{
					//���ܴ�
					CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
					.setTitle(getResources().getString(R.string.trip))
					.setMessage(R.string.main_setting_warnning)
					.setPositiveButton(R.string.main_enter,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.dismiss();
							mclicksetting = false;
							if (new MainActivity().M_VERSION <= 2){
								new CameraVideoRecord().execute();
								Log.i(TAG,"设置-老版本暂停录像");
							}else {
								Log.i(TAG,"设置-新版本暂停录像");
								new StartRecord().stopRecord();
							}
							MainActivity.addFragment(FunctionListFragment.this, new SettingFragment()) ;
						}
					})
					.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss() ;
//							mclicksetting = true;
						}
					}).create();
					alertDialog.setCancelable(false);
					alertDialog.show();
					super.handleMessage(msg);

					return;
				}
			}
			mclicksetting = false;
			MainActivity.addFragment(FunctionListFragment.this, new SettingFragment()) ;
            super.handleMessage(msg);
		}
	};
	private class CameraVideoRecord extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {

			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandCameraRecordUrl() ;

			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			Activity activity = getActivity() ;
			Log.d(TAG, "Video record response:"+result) ;
			if (result != null && result.equals("709\n?") != true) {
				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_succeed),
						Toast.LENGTH_SHORT).show() ;
				if (mRecordmode.equals("Videomode"))
				{
					if(mRecordStatus.equals("Recording"))
						mRecordStatus = "Standby";
					else
						mRecordStatus = "Recording";
				}
			}
			else if (activity != null) {

				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_failed),
						Toast.LENGTH_SHORT).show() ;
			}
			super.onPostExecute(result) ;

	}
}
	private class GetRecordStatus extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandRecordStatusUrl() ;
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				String[] lines;
				String[] lines_temp = result.split("Camera.Preview.MJPEG.status.record=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mRecordStatus = lines[0];
				}else {
                    Log.i(TAG,"mRecordStatus=null");
                }
				lines_temp = result.split("Camera.Preview.MJPEG.status.mode=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mRecordmode = lines[0];
				}
			}

			mRecordStatusHandler.sendMessage(mRecordStatusHandler.obtainMessage());
			super.onPostExecute(result) ;

	}
}

	private Toast mToast;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.function_list, container, false) ;
//		view.findViewById(R.id.dv_btn).setOnClickListener( this);
//		view.findViewById(R.id.local_btn).setOnClickListener( this);
//		view.findViewById(R.id.settings_btn).setOnClickListener( this);
//		view.findViewById(R.id.video_btn).setOnClickListener( this);

		OnTouchListener onTouch = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.selected_background) ;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					v.setBackgroundResource(0) ;
				}
				return false ;
			}
		} ;


		ImageView control= (ImageView) view.findViewById(R.id.settings_btn) ;
		control.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mclicksetting)
				{
//					mclicksetting = true;
					new GetRecordStatus().execute();
				}
				//MainActivity.addFragment(FunctionListFragment.this, new SettingFragment()) ;
			}
		}) ;
//		control.setOnTouchListener(onTouch) ;
		ImageView preview = (ImageView) view.findViewById(R.id.video_btn) ;
		preview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Button_main","ONCLICK");
				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode;
				WifiManager wifiManager = (WifiManager)
						getActivity().getSystemService(Context.WIFI_SERVICE);
				DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
				if (dhcpInfo == null || dhcpInfo.gateway == 0) {
					AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create() ;
					alertDialog.setTitle(getResources().getString(R.string.dialog_DHCP_error)) ;
					alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
							getResources().getString(R.string.label_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.dismiss();
								}
							}) ;
					alertDialog.show() ;
					return;
				}
				if (engineerMode) {
//					MainActivity.addFragment(FunctionListFragment.this, new ViewerSettingFragment()) ;
				} else {
//					new GetRTPS_AV1().execute();
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String liurl=null;
					liurl = pref.getString("liveStreamUrl", "");
					Intent intent = new Intent(getActivity(), StreamPlayerActivity.class) ;
					intent.putExtra("KEY_MEDIA_URL",liurl);
					startActivity(intent) ;
				}
			}
//		mToast.show();

		}) ;
		ImageView browser = (ImageView) view.findViewById(R.id.dv_btn) ;
		browser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode ;
				if (engineerMode) {
					MainActivity.addFragment(FunctionListFragment.this, new BrowserSettingFragment()) ;
				} else {
					Fragment fragment = FileBrowserFragment.newInstance(null, null, null) ;

					MainActivity.addFragment(FunctionListFragment.this, fragment) ;
				}
			}
		}) ;

//		browser.setOnTouchListener(onTouch) ;

		ImageView localAlbum = (ImageView) view.findViewById(R.id.local_btn) ;

		localAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(FunctionListFragment.this, new LocalFileBrowserFragment()) ;
			}
		}) ;

//		localAlbum.setOnTouchListener(onTouch) ;
		RelativeLayout help_btn = (RelativeLayout) view.findViewById(R.id.help) ;
		help_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MainActivity.addFragment(FunctionListFragment.this, new HelpFramgment()) ;
			}
		});
		return view ;
	}

	/*
	@Override
	public void onClick(View v) {
		if(null != mToast) {
			mToast.cancel();
		}
		switch(v.getId()) {
			case R.id.dv_btn:
				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode ;
				if (engineerMode) {
					MainActivity.addFragment(FunctionListFragment.this, new BrowserSettingFragment()) ;
				} else {
					Fragment fragment = FileBrowserFragment.newInstance(null, null, null) ;

					MainActivity.addFragment(FunctionListFragment.this, fragment) ;
				}
				break;

			case R.id.local_btn:
				MainActivity.addFragment(FunctionListFragment.this, new LocalFileBrowserFragment()) ;
				break;

			case R.id.settings_btn:
				if(isIntoSetting)
				{
					mclicksetting = true;
					new GetRecordStatus().execute();
					break;
				}
				MainActivity.addFragment(FunctionListFragment.this, new SettingFragment()) ;
				break;
			case R.id.video_btn:
				WifiManager wifiManager = (WifiManager)
						getActivity().getSystemService(Context.WIFI_SERVICE);
				DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
				if (dhcpInfo == null || dhcpInfo.gateway == 0) {
					AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create() ;
					alertDialog.setTitle(getResources().getString(R.string.dialog_DHCP_error)) ;
					alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
							getResources().getString(R.string.label_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.dismiss();
								}
							}) ;
				alertDialog.show() ;
					return;
				}
				engineerMode = ((MainActivity) getActivity()).mEngineerMode;
				if (engineerMode) {
					MainActivity.addFragment(FunctionListFragment.this, new ViewerSettingFragment()) ;
				} else {
//					new GetRTPS_AV1().execute();
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String liurl=null;
					liurl = pref.getString("liveStreamUrl", "");
					Intent intent = new Intent(getActivity(), StreamPlayerActivity.class) ;
				    intent.putExtra("KEY_MEDIA_URL",liurl);
			        startActivity(intent) ;
				}
				break;
		}
//		mToast.show();
	}*/

	//added by john
	/**
	 * 请求SD卡状态的AsyncTask
	 * */
	private class GetSDCard extends AsyncTask<URL, Integer, String>{

		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commadGetSDCardWarningUrl();
			if (url!=null){
				return CameraCommand.sendRequest(url);
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result){
			if (result != null){
				String [] lines;
				String [] lines_temp = result.split(CameraCommand.PROPERTY_SDWARNING+"=");
				Log.i(TAG,"GETSDCardstatus="+result);
				if (null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator"));
					if (lines!=null&&!lines[0].equals(0)){
						if (lines[0].equals("CARD IN!")){
							Log.i(TAG,"有卡="+lines[0]);
							isSDCardWarning=true;
							isshowtoast=true;
                            isIntoSetting=true;
						} else if (lines[0].equals("NO CARD!")){
							isSDCardWarning=false;
							isshowtoast=false;
                            isIntoSetting=false;
							Log.i(TAG,"没卡="+lines[0]);
						}
					}
				}
			}else {
				Log.i(TAG,"result=null");
				isshowtoast=true;
			}
		}
	}

	private class GetSDCardStatus extends Thread{
		public synchronized void run(){
			while (true){
				try {
//					if (!isSDCardWarning){
//						mHandler.sendEmptyMessage(MSG_NOSDCARD);
//					}
					new GetSDCard().execute();
					Log.i(TAG, "查询~~");
					Thread.sleep(MainActivity.GETSDSTATUSTIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}
	private class LoopToast extends Thread{
		public synchronized void run(){
			while (true){
				try {
					if (!isshowtoast){
						mHandler.sendEmptyMessage(MSG_NOSDCARD);
						Log.i(TAG, "请插卡");
					}
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public void onResume() {
		version=new MainActivity().M_VERSION;
		Log.i("moop","版本号"+ String.valueOf(version));
		if (firstgetscard){
			firstgetscard=false;
		if(version > 2)
		{
			getSDCardStatus = new GetSDCardStatus();
			getSDCardStatus.start();
		}
		loopToast = new LoopToast();
		loopToast.start();
		}
	    ////begin added by eric for update record status
		if(true == MainActivity.getUpdateRecordStatusFlag())
		{
			new MyGetRecordStatus().execute();
		}
//		new GetSDCard().execute();
		///end
		super.onResume() ;
	}

	@Override
	public void onPause() {

			MainActivity.setUpdateRecordStatusFlag(false);

		///end
		super.onPause() ;
	}
}
