package tw.com.a_i_t.IPCamViewer ;
import java.util.Random;
import java.net.MalformedURLException;
import java.net.URL;

import tw.com.a_i_t.IPCamViewer.Control.CameraControlFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.FileBrowser;
import tw.com.a_i_t.IPCamViewer.FileBrowser.FileBrowserFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.BrowserSettingFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.LocalFileBrowserFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.MjpegPlayerFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.StreamPlayerFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.ViewerSettingFragment ;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment ;
import android.app.ProgressDialog;
import android.content.Context ;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.DhcpInfo ;
import android.net.wifi.WifiManager ;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
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
public class FunctionListFragment extends Fragment {
	private static final String TAG = "FunctionListFragment";
	private int mB = -1;
	private int mlocalB=0;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAIL = 2;
	private static final int MSG_DESTORY = 3;
    public static String mRecordStatus="";
    public static String mRecordmode="";
    private boolean mclicksetting = false;
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
			if (result != null) {
				String[] lines;
				try {
					String[] lines_temp = result.split("Camera.Preview.RTSP.av=");
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					int av = Integer.valueOf(lines[0]);
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
				} catch (Exception e) {/* not match, for firmware of MJPEG only */}
			}
			Fragment fragment = StreamPlayerFragment.newInstance(liveStreamUrl) ;
			MainActivity.addFragment(FunctionListFragment.this, fragment) ;
			super.onPostExecute(result) ;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.function_list, container, false) ;
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

		Button_main control = (Button_main) view.findViewById(R.id.settings_btn) ;

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

		Button_main preview = (Button_main) view.findViewById(R.id.video_btn) ;

		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Button_main","ONCLICK");
				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode ;
				if (engineerMode) {

					MainActivity.addFragment(FunctionListFragment.this, new ViewerSettingFragment()) ;
				} else {
					
					////modify by eric
					new GetRTPS_AV1().execute();
					/*WifiManager wifiManager = (WifiManager) getActivity().getSystemService(
							Context.WIFI_SERVICE) ;

					DhcpInfo dhcpInfo = wifiManager.getDhcpInfo() ;

					if (dhcpInfo != null && dhcpInfo.gateway != 0) {

						String gateway = MainActivity.intToIp(dhcpInfo.gateway) ;

						String mediaUrl = "http://" + gateway + MjpegPlayerFragment.DEFAULT_MJPEG_PUSH_URL ;
						getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
						Fragment fragment = StreamPlayerFragment.newInstance(mediaUrl) ;
						MainActivity.addFragment(FunctionListFragment.this, fragment) ;
					}
					else
					{
						CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
						.setTitle(getResources().getString(R.string.dialog_DHCP_error))
						.setPositiveButton(R.string.label_ok,new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.dismiss();
							}
						}).create();
						
						alertDialog.show() ;
					}
					*/
				}
			}
		}) ;

		//preview.setOnTouchListener(onTouch) ;

		Button_main browser = (Button_main) view.findViewById(R.id.dv_btn) ;

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

		Button_main localAlbum = (Button_main) view.findViewById(R.id.local_btn) ;

		localAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(FunctionListFragment.this, new LocalFileBrowserFragment()) ;
			}
		}) ;

		localAlbum.setOnTouchListener(onTouch) ;
		ImageButton help_btn = (ImageButton) view.findViewById(R.id.help) ;
		help_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MainActivity.addFragment(FunctionListFragment.this, new HelpFramgment()) ;
			}
		});
		return view ;
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
