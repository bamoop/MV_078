package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.Control.NetworkConfigurationsFragment;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment ;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.view.View.OnTouchListener ;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingFragment extends Fragment {
	private static final String MuteOn ="MuteOn";
	private static final String MuteOff ="MuteOff";
	private static final int MSG_REFRESH_MUTE_STATE =1;
	private static final int MSG_REFRESH_PARKCAR_STATE =2;
	public final static String TAG = "SettingFragment" ;

	private static boolean isINFilebrowser=true;//当前fragment是否打开
	private boolean returnBlankScreen=false;//待机黑屏
	private boolean returnFragment=false;//待机自动退出
	MainActivity.MyOnTouchListener myOnTouchListener;
	private GestureDetector mGestureDetector;
	public static String mRecordStatus="";
	public static String mRecordmode="";

	private CustomDialog malertDialog;
	private ProgressDialog mProgressDialog ;

	private ToggleButton mMutestateSwitcher,mTVOUTSwitcher,mParkcar;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		new GetRecordStatus().execute();
		new GetParkCarValues().execute();
	}
	private Handler mHandlerUI = new Handler()
	{
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				case MSG_REFRESH_MUTE_STATE:
						String status = (String)msg.obj;
						if(status!=null)
						{
							Log.i("moop","status="+status);
							if(status.equalsIgnoreCase(MuteOn))
							{
								mMutestateSwitcher.setChecked(false);
							}
							else if(status.equalsIgnoreCase(MuteOff))
							{
								mMutestateSwitcher.setChecked(true);
							}
						}
					break;
				case MSG_REFRESH_PARKCAR_STATE:
					String parkcarstatus = (String)msg.obj;
					Log.i("moop","parkcarstatus="+parkcarstatus);
					if (parkcarstatus!=null){
						if (parkcarstatus.equalsIgnoreCase("0")){
							mParkcar.setChecked(false);
						}else if(!parkcarstatus.equalsIgnoreCase("0"))
						{
							mParkcar.setChecked(true);
						}
					}
				default:
					break;
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
				String[] lines;
				String[] lines_temp = result.split("Camera.Cruise.Seq4.Count=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					Message msg = mHandlerUI.obtainMessage();
					msg.what=MSG_REFRESH_PARKCAR_STATE;
					if(lines!=null)
						msg.obj = lines[0];
					mHandlerUI.sendMessage(msg);
				}
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
			Activity activity = getActivity() ;
			//Log.d(TAG, "TimeStamp property "+result) ;
			if (result != null) {
				String[] lines;		
				String[] lines_temp = result.split("Camera.Preview.MJPEG.status.mute=");
				if(lines_temp!=null && lines_temp.length>1)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					Log.i("moop","result="+result);
					Message msg = mHandlerUI.obtainMessage();
					msg.what = MSG_REFRESH_MUTE_STATE;
					if(lines!=null)
					{
						msg.obj = lines[0];
					}
					   mHandlerUI.sendMessage(msg);
					
				}
			}
			else if (activity != null) {
				Toast.makeText(activity, activity.getResources().getString(R.string.message_fail_get_info),
						Toast.LENGTH_LONG).show() ;
			}
			super.onPostExecute(result) ;

	}
}

	@SuppressLint("HandlerLeak")
	private Handler mTHandler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				default:
					if(malertDialog!=null)
					{
						malertDialog.dismiss();
						malertDialog = null;
					}
					if(mProgressDialog!=null)
					{
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_fragment, container, false) ;

		OnTouchListener onTouch = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i(TAG,"onTouch");
			 /*
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.selected_background) ;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					v.setBackgroundResource(R.drawable.group_background) ;
				}
				return false ;
			}*/
				return false;
			}
		} ;

		LinearLayout networkConfigurations = (LinearLayout) view
				.findViewById(R.id.cameraControlNetworkConfigurations) ;

		networkConfigurations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new NetworkConfigurationsFragment()) ;
			}
		}) ;

		networkConfigurations.setOnTouchListener(onTouch) ;

		mMutestateSwitcher = (ToggleButton) view.findViewById(R.id.tgl_record) ;
		mMutestateSwitcher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ����ť��һ�α����ʱ����Ӧ���¼�
				if (mMutestateSwitcher.isChecked()) {
					URL url = CameraCommand.commandMuteOffUrl();
					if (url != null) {
						new CameraCommand.SendRequest().execute(url);
					}
				}
				// ����ť�ٴα����ʱ����Ӧ���¼�
				else {
					//ȡ��¼��
					URL url = CameraCommand.commandMuteOnUrl();
					if (url != null) {
						new CameraCommand.SendRequest().execute(url);
					}
				}
			}
		});
		mParkcar = (ToggleButton) view.findViewById(R.id.tgl_parkcar) ;
		mParkcar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ����ť��һ�α����ʱ����Ӧ���¼�
				if (mParkcar.isChecked()) {
					URL url = CameraCommand.commandParkCerOnUrl();
					if (url != null) {
						new CameraCommand.SendRequest().execute(url);
					}
				}
				else {
					URL url = CameraCommand.commandParkCerOffUrl();
					if (url != null) {
						new CameraCommand.SendRequest().execute(url);
					}
				}
			}
		});

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


		LinearLayout settings_DV_quality = (LinearLayout) view.findViewById(R.id.settings_DV_quality) ;

		settings_DV_quality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new Setting_Quality_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_photo_quality = (LinearLayout) view.findViewById(R.id.settings_photo_quality) ;

		settings_photo_quality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new Setting_photo_Quality_Fragment()) ;
			}
		}) ;	
		
		LinearLayout settings_move = (LinearLayout) view.findViewById(R.id.settings_move) ;

		settings_move.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new Setting_Move_Fragment()) ;
			}
		}) ;
		

		LinearLayout settings_lockprotect = (LinearLayout) view.findViewById(R.id.settings_lockProtect) ;

		settings_lockprotect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isINFilebrowser=false;

				MainActivity.addFragment(SettingFragment.this, new Setting_lockprotect_Fragment()) ;
			}
		}) ;

		LinearLayout settings_factoryreset = (LinearLayout) view.findViewById(R.id.setting_factoryreset) ;

		settings_factoryreset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.addFragment(SettingFragment.this, new Setting_FactoryReset_Fragment()) ;
			}
		}) ;
		//格式化储存卡
		LinearLayout settings_formatsdcard = (LinearLayout) view.findViewById(R.id.settings_formatsdcard) ;

		settings_formatsdcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
//				MainActivity.addFragment(SettingFragment.this, new Setting_formatsdcard_Fragment()) ;
				if(malertDialog==null)
				{
					malertDialog = new CustomDialog.Builder(getActivity())
							.setTitle(getActivity().getString(R.string.trip))
							.setMessage(R.string.format_attaction)
							.setPositiveButton(R.string.ensure,new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mTHandler.removeMessages(1);
									if(mProgressDialog==null)
									{
										LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
										View v1 = layoutInflater.inflate(R.layout.loading_dialog, null);
										TextView cont_txt = (TextView)v1.findViewById(R.id.content_txt);
										cont_txt.setText(R.string.label_formatbtn);
										((TextView)v1.findViewById(R.id.title_txt)).setText(R.string.warnnig);
										mProgressDialog = new ProgressDialog(getActivity()) ;
										//mProgressDialog.setTitle("Connecting to Camera") ;
										mProgressDialog.setCancelable(false) ;
										mProgressDialog.show() ;
										mProgressDialog.setContentView(v1);
									}
									else
									{
										mProgressDialog.show() ;
									}

									try {
										URL url = CameraCommand.commandformatsdcardSettingsUrl() ;
										if (url != null) {
											new CameraCommand.SendRequest().execute(url) ;
										}
										mTHandler.sendEmptyMessageDelayed(1,10*1000);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace() ;
									}
									dialog.dismiss() ;
								}
							})
							.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mTHandler.removeMessages(1);
									dialog.dismiss() ;
									malertDialog = null;
								}
							}).create();
				}
				malertDialog.show() ;
			}
		}) ;
		//停车监控
		LinearLayout settings_park = (LinearLayout) view.findViewById(R.id.settings_parkcar) ;
		
		LinearLayout settings_TV_OUT = (LinearLayout) view.findViewById(R.id.settings_videoout) ;
		settings_TV_OUT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new Setting_TV_OUT_Fragment()) ;
			}
		}) ;
		//版本信息
		LinearLayout settings_version = (LinearLayout) view.findViewById(R.id.settings_version) ;

		settings_version.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isINFilebrowser=false;

				MainActivity.addFragment(SettingFragment.this, new Setting_Version_Fragment()) ;
			}
		}) ;

		LinearLayout settings_help = (LinearLayout) view.findViewById(R.id.settings_help) ;
		settings_help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new HelpFramgment()) ;
			}
		}) ;

		LinearLayout settings_sdcardlt = (LinearLayout) view.findViewById(R.id.settings_sdcard_lifetime) ;
		settings_sdcardlt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_SDcardLifttime_fragment()) ;
			}
		}) ;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int v = pref.getInt(MainActivity.g_version_check,MainActivity.G_UNKNOWN_VERSION);
		if(v ==MainActivity.G_NEW_VERSION)
		{
//			view.findViewById(R.id.tmp).setVisibility(View.GONE);
			settings_sdcardlt.setVisibility(View.GONE);
			settings_factoryreset.setVisibility(View.GONE);
		}else if(v == 2){
			settings_TV_OUT.setVisibility(View.VISIBLE);
			settings_park.setVisibility(View.VISIBLE);

		}else if(v == 1){
			settings_TV_OUT.setVisibility(View.GONE);
			settings_park.setVisibility(View.GONE);
			settings_sdcardlt.setVisibility(View.GONE);
			settings_factoryreset.setVisibility(View.GONE);
		}
		return view ;
	}
	
	@Override
	public void onPause() {
		////begin added by eric for update record status 
		MainActivity.setUpdateRecordStatusFlag(true);
		returnBlankScreen=false;
		///end
		super.onPause() ;
	}

	@Override
	public  void onResume(){
		isINFilebrowser=true;
		returnBlankScreen=true;
		if (returnFragment){
			getFragmentManager().popBackStack();
		}
		sleepHandler.removeMessages(1);
		sleepHandler.sendEmptyMessageDelayed(1, 1000 * MainActivity.TOUNCHTIME);
		super.onResume();
	}

	@Override
	public void onDestroy(){
		isINFilebrowser=false;
		sleepHandler.removeMessages(1);
		Log.i("moop","settingonDestory");
		super.onDestroy();
	}
	private Handler sleepHandler = new Handler()
	{
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				case 1:
						returnFragment();
//					MainActivity.addFragment(SettingFragment.this, new FunctionListFragment()) ;
					break;

			}
		}
	};
	public synchronized void returnFragment(){
		if (isINFilebrowser&&returnBlankScreen){
			Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
			getFragmentManager().popBackStack();
		}else if (isINFilebrowser&&!returnBlankScreen){
			Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
			returnFragment=true;
//			new BackGetRecordStatus().execute();
			new StartRecord().startRecord();
		}
	}
}
