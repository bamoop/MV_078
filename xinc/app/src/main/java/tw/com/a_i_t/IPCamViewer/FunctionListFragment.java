package tw.com.a_i_t.IPCamViewer ;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.net.MalformedURLException;
import java.net.URL;

import tw.com.a_i_t.IPCamViewer.Control.CameraControlFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.FileBrowser;
import tw.com.a_i_t.IPCamViewer.FileBrowser.FileBrowserFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.BrowserSettingFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.LocalFileBrowserFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.CameraStatus;
import tw.com.a_i_t.IPCamViewer.Viewer.MenuViewItem;
import tw.com.a_i_t.IPCamViewer.Viewer.MjpegPlayerFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.StreamPlayerActivity;
import tw.com.a_i_t.IPCamViewer.Viewer.StreamPlayerFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.ViewerSettingFragment ;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment ;
import android.app.ProgressDialog;
import android.content.Context ;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.DhcpInfo ;
import android.net.wifi.WifiManager ;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.view.View.OnTouchListener ;
import android.view.ViewGroup ;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout ;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FunctionListFragment extends Fragment implements OnClickListener {
	private static final String TAG = "FunctionListFragment";
	private int mB = -1;
	private int mlocalB=0;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAIL = 2;
	private static final int MSG_DESTORY = 3;
    public static String mRecordStatus="";
    public static String mRecordmode="";
    private boolean mclicksetting = false;
	public CameraStatus cameraStatus;
	public List<CameraStatus>cameraStatusList=new ArrayList<CameraStatus>();

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
			URL url = CameraCommand.commandCameraRecordUrl() ;
			Log.i("moop","FunctiongListFragment-74");
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
				String[] item=lines_temp[0].split(",");

				String te=",20151030,3003,2015-12-2 17:51:1,1,0,1,1,0,0,1,0,0,0,6,1,NPD_CarDV_WiFi,1234567890,1";
				String [] tt = te.split(",");
				Log.i("--", tt[0]);
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
							new CameraVideoRecord().execute();
							MainActivity.addFragment(FunctionListFragment.this, new SettingFragment()) ;
						}
					})
					.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss() ;
							mclicksetting = false;
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
	///added by eric
	/* Query property of RTSP AV1 */
	private class GetRTPS_AV1 extends AsyncTask<URL, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandQueryAV1Url() ;
			Log.i("moop","请求属性"+url);
			if (url != null) {		
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			String	liveStreamUrl;
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
								dialog.dismiss() ;
							}
						}) ;
				alertDialog.show() ;
				return;
			}
			String gateway = MainActivity.intToIp(dhcpInfo.gateway) ;
			// set http push as default for streaming
			liveStreamUrl = "http://" + gateway + MjpegPlayerFragment.DEFAULT_MJPEG_PUSH_URL ;
//			liveStreamUrl = null;
			if (result != null) {
				String[] lines;
				try {
					String[] lines_temp = result.split("Camera.Preview.RTSP.av=");
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					int av = Integer.valueOf(lines[0]);
					Log.i("moop","请求属4--"+av);
					switch (av) {
					case 1:	// liveRTSP/av1 for RTSP MJPEG+AAC
						liveStreamUrl = "rtsp://" + gateway + MjpegPlayerFragment.DEFAULT_RTSP_MJPEG_AAC_URL ;
						break;
					case 2: // liveRTSP/v1 for RTSP H.264
						liveStreamUrl = "rtsp://" + gateway + MjpegPlayerFragment.DEFAULT_RTSP_H264_URL ;
						break;
					case 3: // liveRTSP/av2 for RTSP H.264+AAC
						liveStreamUrl = "rtsp://" + gateway + MjpegPlayerFragment.DEFAULT_RTSP_H264_AAC_URL ;
						break;
					}
					Log.i("moop","请求属3"+liveStreamUrl);
				} catch (Exception e) {/* not match, for firmware of MJPEG only */}
			}
			else {
				Log.i("moop", "没有拿到视频属性");
			}
			Fragment fragment = StreamPlayerFragment.newInstance(liveStreamUrl) ;
			MainActivity.addFragment(FunctionListFragment.this, fragment) ;
//			Intent intent = new Intent(getActivity(), StreamPlayerActivity.class) ;
//			intent.putExtra("KEY_MEDIA_URL",liveStreamUrl);
//			startActivity(intent) ;
			super.onPostExecute(result) ;
		}
	}
	private Toast mToast;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.function_list, container, false) ;
		view.findViewById(R.id.dv_btn).setOnClickListener( this);
		view.findViewById(R.id.local_btn).setOnClickListener( this);
		view.findViewById(R.id.settings_btn).setOnClickListener( this);
		view.findViewById(R.id.video_btn).setOnClickListener( this);

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


	/*	 MenuViewItem  control= (MenuViewItem) view.findViewById(R.id.settings_btn) ;
		control.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mclicksetting)
				{
					mclicksetting = true;
					new GetRecordStatus().execute();
				}
				//MainActivity.addFragment(FunctionListFragment.this, new SettingFragment()) ;
			}
		}) ;
		control.setOnTouchListener(onTouch) ;
		MenuViewItem preview = (MenuViewItem) view.findViewById(R.id.video_btn) ;
		preview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Button_main","ONCLICK");
				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode ;
				if (engineerMode) {
					MainActivity.addFragment(FunctionListFragment.this, new ViewerSettingFragment()) ;
				} else {
					new GetRTPS_AV1().execute();
				}
			}
		}) ;
		MenuViewItem browser = (MenuViewItem) view.findViewById(R.id.dv_btn) ;
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

		browser.setOnTouchListener(onTouch) ;

		MenuViewItem localAlbum = (MenuViewItem) view.findViewById(R.id.local_btn) ;

		localAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(FunctionListFragment.this, new LocalFileBrowserFragment()) ;
			}
		}) ;

//		localAlbum.setOnTouchListener(onTouch) ;
//		ImageButton help_btn = (ImageButton) view.findViewById(R.id.help) ;
//		help_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				MainActivity.addFragment(FunctionListFragment.this, new HelpFramgment()) ;
//			}
//		});*/
		return view ;
	}

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
				if(!mclicksetting)
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
					Log.i("moop", "Streamplay--827");
				}
				break;
		}
//		mToast.show();
	}

	@Override
	public void onResume() {

	    ////begin added by eric for update record status 
		if(true == MainActivity.getUpdateRecordStatusFlag())
		{
			new MyGetRecordStatus().execute();
		}
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
