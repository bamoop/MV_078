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
import android.graphics.Camera;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingFragment extends Fragment implements OnClickListener  {
	private static final String MuteOn ="0";
	private static final String MuteOff ="1";
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
	private ProgressDialog mProgressDialog,mProgressDialogRest ;
	private LinearLayout settings_DV_quality_row,settings_photo_quality_row,settings_videoout_row,settings_frequency_row;
	private Button settings_DV_quality_row_1,settings_DV_quality_row_2,settings_DV_quality_row_3,settings_DV_quality_row_4;
	private Button settings_photo_quality_row_1,settings_photo_quality_row_2,settings_photo_quality_row_3;
	private Button setting_frequency_row_1,setting_frequency_row_2;
	private Button settings_videoout_row_1,settings_videoout_row_2,settings_videoout_row_3;
	private boolean settings_DV_quality_row_open=false;
	private boolean settings_photo_quality_row_open=false;
	private boolean settings_videoout_row_open=false;
	private boolean settings_frequency_row_open=false;
	LinearLayout settings_TV_OUT;
	int version;
	private boolean restore_factory=false;
	private ToggleButton mMutestateSwitcher,mTVOUTSwitcher,mParkcar;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
//		new GetRecordStatus().execute();
//		new GetParkCarValues().execute();
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

	private class CamerRowSettingsSendRequest extends CameraCommand.SendRequest{
		@Override
		protected void onPreExecute() {
			//setWaitingState(true) ;
			super.onPreExecute() ;
		}
		@Override
		protected void onPostExecute(String result) {
			//setWaitingState(false) ;
			super.onPostExecute(result) ;
		}
	}
	/**
	 * 发送指令集
	 * */
	public void settingSendRequest(int ID,int value){
		URL url=null;
		switch (ID){
			case 1:
				url = CameraCommand.commandSetmovieresolutionUrl(value) ;
				break;
			case 2:
				url=CameraCommand.commandSetVideoFragTimeUrl(value) ;
				break;
			case 3:
				url = CameraCommand.commandSetTVOUTSettingsUrl(value);
				break;
			case 4:
				url = CameraCommand.commandSetflickerfrequencyUrl(value);
				break;
			default:
				break;
		}
		if (url != null) {
			new CamerRowSettingsSendRequest().execute(url) ;
		}
	}
	/**
	 * 用于设置界面每一项Button的显示状态
	* */
	public void settingBtnStatus(int ID,int btn_id){
		switch (ID){
			case 1:
				switch (btn_id){
					case 0:
						settings_DV_quality_row_1.setEnabled(false);
						settings_DV_quality_row_2.setEnabled(true);
						settings_DV_quality_row_3.setEnabled(true);
						settings_DV_quality_row_4.setEnabled(true);
						break;
					case 1:
						settings_DV_quality_row_1.setEnabled(true);
						settings_DV_quality_row_2.setEnabled(false);
						settings_DV_quality_row_3.setEnabled(true);
						settings_DV_quality_row_4.setEnabled(true);
						break;
					case 2:
						settings_DV_quality_row_1.setEnabled(true);
						settings_DV_quality_row_2.setEnabled(true);
						settings_DV_quality_row_3.setEnabled(false);
						settings_DV_quality_row_4.setEnabled(true);
						break;
					case 3:
						settings_DV_quality_row_1.setEnabled(true);
						settings_DV_quality_row_2.setEnabled(true);
						settings_DV_quality_row_3.setEnabled(true);
						settings_DV_quality_row_4.setEnabled(false);
						break;
					default:
						break;
				}
					break;
			case 2:
				switch (btn_id){
					case 1:
						settings_photo_quality_row_1.setEnabled(false);
						settings_photo_quality_row_2.setEnabled(true);
						settings_photo_quality_row_3.setEnabled(true);
						break;
					case 2:
						settings_photo_quality_row_1.setEnabled(true);
						settings_photo_quality_row_2.setEnabled(false);
						settings_photo_quality_row_3.setEnabled(true);
						break;
					case 3:
						settings_photo_quality_row_1.setEnabled(true);
						settings_photo_quality_row_2.setEnabled(true);
						settings_photo_quality_row_3.setEnabled(false);
						break;
					default:
						break;
				}
				break;
			case 3:
				switch (btn_id){
					case 0:
						settings_videoout_row_1.setEnabled(false);
						settings_videoout_row_2.setEnabled(true);
						settings_videoout_row_3.setEnabled(true);
						break;
					case 1:
						settings_videoout_row_1.setEnabled(true);
						settings_videoout_row_2.setEnabled(false);
						settings_videoout_row_3.setEnabled(true);
						break;
					case 2:
						settings_videoout_row_1.setEnabled(true);
						settings_videoout_row_2.setEnabled(true);
						settings_videoout_row_3.setEnabled(false);
						break;
					default:
						break;
				}
				break;
			case 4:
				switch (btn_id){
					case 0:
						setting_frequency_row_1.setEnabled(false);
						setting_frequency_row_2.setEnabled(true);
						break;
					case 1:
						setting_frequency_row_1.setEnabled(true);
						setting_frequency_row_2.setEnabled(false);
						break;

					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.settings_DV_quality_row_1:
				settingBtnStatus(1,0);
				settingSendRequest(1,0);
				break;
			case R.id.settings_DV_quality_row_2:
				settingBtnStatus(1,1);
				settingSendRequest(1, 1);
				break;
			case R.id.settings_DV_quality_row_3:
				settingBtnStatus(1,2);
				settingSendRequest(1, 2);
				break;
			case R.id.settings_DV_quality_row_4:
				settingBtnStatus(1,3);
				settingSendRequest(1, 3);
				break;
			case R.id.settings_photo_quality_row_1:
				settingBtnStatus(2,1);
				settingSendRequest(2, 0);
				break;
			case R.id.settings_photo_quality_row_2:
				settingBtnStatus(2,2);
				settingSendRequest(2,2);
				break;
			case R.id.settings_photo_quality_row_3:
				settingBtnStatus(2,3);
				settingSendRequest(2,3);
				break;
			case R.id.settings_videoout_row_1:
				settingBtnStatus(3,0);
				settingSendRequest(3,0);
				break;
			case R.id.settings_videoout_row_2:
				settingBtnStatus(3,1);
				settingSendRequest(3, 1);
				break;
			case R.id.settings_videoout_row_3:
				settingBtnStatus(3,2);
				settingSendRequest(3, 2);
				break;
			case R.id.setting_frequency_row_1:
				settingBtnStatus(4,0);
				settingSendRequest(4, 0);
				break;
			case R.id.setting_frequency_row_2:
				settingBtnStatus(4,1);
				settingSendRequest(4, 1);
				break;
			default:
				break;
		}
	}

	/**
	 * 管理设置界面二级列表的显示与隐藏
	 * @parameter  传入对应row的编号
	 * */
	public void SettingRowStatus(int ID){
		switch (ID){
			case 1:
				if (settings_DV_quality_row_open){
					settings_DV_quality_row.setVisibility(View.GONE);
					settings_DV_quality_row_open=false;
				}else {
					settings_DV_quality_row.setVisibility(View.VISIBLE);
					settings_DV_quality_row_open=true;
				}
				settings_photo_quality_row.setVisibility(View.GONE);
				settings_videoout_row.setVisibility(View.GONE);
				settings_frequency_row.setVisibility(View.GONE);
				settings_photo_quality_row_open=false;
				settings_videoout_row_open=false;
				settings_frequency_row_open=false;
				break;
			case 2:
				if (settings_photo_quality_row_open){
					settings_photo_quality_row.setVisibility(View.GONE);
					settings_photo_quality_row_open=false;
				}else {
					settings_photo_quality_row.setVisibility(View.VISIBLE);
					settings_photo_quality_row_open=true;
				}
				settings_DV_quality_row.setVisibility(View.GONE);
				settings_videoout_row.setVisibility(View.GONE);
				settings_frequency_row.setVisibility(View.GONE);
				settings_DV_quality_row_open=false;
				settings_videoout_row_open=false;
				settings_frequency_row_open=false;
				break;
			case 3:
				if (settings_videoout_row_open){
					settings_videoout_row.setVisibility(View.GONE);
					settings_videoout_row_open=false;
				}else {
					settings_videoout_row.setVisibility(View.VISIBLE);
					settings_videoout_row_open=true;
				}
				settings_DV_quality_row.setVisibility(View.GONE);
				settings_photo_quality_row.setVisibility(View.GONE);
				settings_frequency_row.setVisibility(View.GONE);
				settings_DV_quality_row_open=false;
				settings_photo_quality_row_open=false;
				settings_frequency_row_open=false;
				break;
			case 4:
				if (settings_frequency_row_open){
					settings_frequency_row.setVisibility(View.GONE);
					settings_frequency_row_open=false;
				}else {
					settings_frequency_row.setVisibility(View.VISIBLE);
					settings_frequency_row_open=true;
				}
				settings_DV_quality_row.setVisibility(View.GONE);
				settings_photo_quality_row.setVisibility(View.GONE);
				settings_videoout_row.setVisibility(View.GONE);
				settings_DV_quality_row_open=false;
				settings_photo_quality_row_open=false;
				settings_videoout_row_open=false;
				break;
			default:
				break;
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
				Log.i("mtp", "老设置返回--" + result);
				if (lines_temp!=null&& lines_temp.length>1){
					lines = lines_temp[1].split("\\|") ;

					if (lines!=null){
						Log.i("mtp", "老设置返回-3===" + lines[6]);
						Log.i("mtp", "老设置返回-4===" + lines[4]);
						if(Integer.valueOf(lines[6]) == 1){
							mMutestateSwitcher.setChecked(false);
						}else {
							mMutestateSwitcher.setChecked(true);
						}
					}
				}
			}
			super.onPostExecute(result);
		}
	}

	private class GetCameraSettingStatus extends AsyncTask<URL,Integer,String>{

		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetMenuSettingsValuesUrl();
			if (url != null) {
				return CameraCommand.sendRequest(url);
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			Activity activity = getActivity() ;
			if (result !=null){
				String[]lines;
				String[] lines_temp = result.split("Camera.Menu.DefaultValue.AWB=");//录音状态
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					Message msg = mHandlerUI.obtainMessage();
					msg.what = MSG_REFRESH_MUTE_STATE;
					Log.i("mtp", "设置返回--" + result);

					if(lines!=null){
						if (version!=1){
							msg.obj = lines[0];
							mHandlerUI.sendMessage(msg);
							Log.i("mtp", "录音状态--" + String.valueOf(Integer.valueOf(lines[0])));
						}
					}
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.EV=");//停车监控
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					Message msg = mHandlerUI.obtainMessage();
					msg.what=MSG_REFRESH_PARKCAR_STATE;
					if(lines!=null){
						msg.obj = lines[0];
						mHandlerUI.sendMessage(msg);
						Log.i("mtp","停车监控--"+String.valueOf(Integer.valueOf(lines[0])));
					}
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.VideoRes=");//录像质量
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null) {
						settingBtnStatus(1, Integer.valueOf(lines[0]));
						Log.i("mtp", "录像质量--" + String.valueOf(Integer.valueOf(lines[0])));
					}
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.VideoClipTime=");//视频分段
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null) {
//						settingBtnStatus(2,Integer.valueOf(lines[0]));
						if (lines[0].equals("1MIN")){
							settingBtnStatus(2, 1);
						}else if (lines[0].equals("3MIN")){
							settingBtnStatus(2, 2);
						}else if (lines[0].equals("5MIN")){
							settingBtnStatus(2, 3);
						}
						Log.i("mtp", "视频分段--" + String.valueOf(lines[0]));
					}
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.ImageRes=");//视频输出
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null) {
						if(lines[0].equals("0")){
							settingBtnStatus(3,0);
						}else if(lines[0].equals("1")){
							settingBtnStatus(3,1);
						}else if (lines[0].equals("2")){
							settingBtnStatus(3,2);
						}
						Log.i("mtp", "视频输出--" + String.valueOf(Integer.valueOf(lines[0])));
					}
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.FWversion=");//版本信息
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
						Log.i("mtp","版本信息--"+String.valueOf(lines[0]));
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.Flicker=");//版本信息
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null){
						if(lines[0].equals("0")){
							settingBtnStatus(4,0);
						}else if(lines[0].equals("1")){
							settingBtnStatus(4,1);
						}
					}
						Log.i("mtp","版本信息--"+String.valueOf(lines[0]));
				}
			}
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
						if (restore_factory)
						MainActivity.backToPreFragment();
					}
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_fragment, container, false) ;

		settings_DV_quality_row= (LinearLayout) view.findViewById(R.id.settings_DV_quality_row);
		settings_photo_quality_row= (LinearLayout) view.findViewById(R.id.settings_photo_quality_row);
		settings_videoout_row= (LinearLayout) view.findViewById(R.id.settings_videoout_row);
		settings_frequency_row= (LinearLayout) view.findViewById(R.id.setting_frequency_row);

		settings_DV_quality_row_1= (Button) view.findViewById(R.id.settings_DV_quality_row_1);
		settings_DV_quality_row_2= (Button) view.findViewById(R.id.settings_DV_quality_row_2);
		settings_DV_quality_row_3= (Button) view.findViewById(R.id.settings_DV_quality_row_3);
		settings_DV_quality_row_4= (Button) view.findViewById(R.id.settings_DV_quality_row_4);

		settings_photo_quality_row_1= (Button) view.findViewById(R.id.settings_photo_quality_row_1);
		settings_photo_quality_row_2= (Button) view.findViewById(R.id.settings_photo_quality_row_2);
		settings_photo_quality_row_3= (Button) view.findViewById(R.id.settings_photo_quality_row_3);

		settings_videoout_row_1= (Button) view.findViewById(R.id.settings_videoout_row_1);
		settings_videoout_row_2= (Button) view.findViewById(R.id.settings_videoout_row_2);
		settings_videoout_row_3= (Button) view.findViewById(R.id.settings_videoout_row_3);

		setting_frequency_row_1= (Button) view.findViewById(R.id.setting_frequency_row_1);
		setting_frequency_row_2= (Button) view.findViewById(R.id.setting_frequency_row_2);

		view.findViewById(R.id.settings_DV_quality_row_1).setOnClickListener( this);
		view.findViewById(R.id.settings_DV_quality_row_2).setOnClickListener( this);
		view.findViewById(R.id.settings_DV_quality_row_3).setOnClickListener( this);
		view.findViewById(R.id.settings_DV_quality_row_4).setOnClickListener( this);
		view.findViewById(R.id.settings_photo_quality_row_1).setOnClickListener( this);
		view.findViewById(R.id.settings_photo_quality_row_2).setOnClickListener( this);
		view.findViewById(R.id.settings_photo_quality_row_3).setOnClickListener( this);
		view.findViewById(R.id.settings_videoout_row_1).setOnClickListener( this);
		view.findViewById(R.id.settings_videoout_row_2).setOnClickListener( this);
		view.findViewById(R.id.settings_videoout_row_3).setOnClickListener( this);
		view.findViewById(R.id.setting_frequency_row_1).setOnClickListener( this);
		view.findViewById(R.id.setting_frequency_row_2).setOnClickListener( this);

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
				isINFilebrowser = false;
				MainActivity.addFragment(SettingFragment.this, new NetworkConfigurationsFragment());
			}
		}) ;

		networkConfigurations.setOnTouchListener(onTouch) ;

		mMutestateSwitcher = (ToggleButton) view.findViewById(R.id.tgl_record) ;
		mMutestateSwitcher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ����ť��һ�α����ʱ����Ӧ���¼�
				if (mMutestateSwitcher.isChecked()) {
					if (version==1){
						URL url = CameraCommand.commamdOldMuteOffUrl();
						if (url != null) {
							new CameraCommand.SendRequest().execute(url);
						}
					}else {
					URL url = CameraCommand.commandMuteOffUrl();
					if (url != null) {
						new CameraCommand.SendRequest().execute(url);
					}
					}
				}
				// ����ť�ٴα����ʱ����Ӧ���¼�
				else {
					//ȡ��¼��
					if (version==1){
						URL url = CameraCommand.commamdOldMuteOnUrl();
						if (url != null) {
							new CameraCommand.SendRequest().execute(url);
						}
					}else{
						URL url = CameraCommand.commandMuteOnUrl();
						if (url != null) {
							new CameraCommand.SendRequest().execute(url);
						}
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
				isINFilebrowser = false;
				SettingRowStatus(1);
//				MainActivity.addFragment(SettingFragment.this, new Setting_Quality_Fragment()) ;
			}
		}) ;

		 settings_TV_OUT = (LinearLayout) view.findViewById(R.id.settings_videoout) ;
		settings_TV_OUT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				SettingRowStatus(3);

//				MainActivity.addFragment(SettingFragment.this, new Setting_TV_OUT_Fragment()) ;
			}
		}) ;
		LinearLayout settings_photo_quality = (LinearLayout) view.findViewById(R.id.settings_photo_quality) ;

		settings_photo_quality.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				SettingRowStatus(2);
//				MainActivity.addFragment(SettingFragment.this, new Setting_photo_Quality_Fragment()) ;
			}
		}) ;
		LinearLayout settings_frequency = (LinearLayout) view.findViewById(R.id.settings_frequency) ;

		settings_frequency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				SettingRowStatus(4);
//				MainActivity.addFragment(SettingFragment.this, new Setting_photo_Quality_Fragment()) ;
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
//				MainActivity.addFragment(SettingFragment.this, new Setting_FactoryReset_Fragment()) ;
				if(mProgressDialogRest==null)
				{
				malertDialog = new CustomDialog.Builder(getActivity())
						.setTitle(getActivity().getString(R.string.trip))
						.setMessage(R.string.label_factoryresettrip)
						.setPositiveButton(R.string.ensure,new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mTHandler.removeMessages(1);
								if(mProgressDialog==null)
								{
									LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
									View v1 = layoutInflater.inflate(R.layout.loading_dialog, null);
									TextView cont_txt = (TextView)v1.findViewById(R.id.content_txt);
									cont_txt.setText(R.string.loading);
									((TextView)v1.findViewById(R.id.title_txt)).setText(R.string.factortreset_attaction);
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
									URL url = CameraCommand.commandfactorySettingsUrl() ;
									if (url != null) {
										new CameraCommand.SendRequest().execute(url) ;
									}
									restore_factory=true;
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

		//版本信息
		LinearLayout settings_version = (LinearLayout) view.findViewById(R.id.settings_version) ;

		settings_version.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isINFilebrowser=false;
				MainActivity.addFragment(SettingFragment.this, new Setting_Version_Fragment()) ;
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
		version = pref.getInt(MainActivity.g_version_check,MainActivity.G_UNKNOWN_VERSION);
		if(version ==MainActivity.G_NEW_VERSION)
		{
//			view.findViewById(R.id.tmp).setVisibility(View.GONE);
			settings_sdcardlt.setVisibility(View.GONE);
//			settings_factoryreset.setVisibility(View.GONE);
		}else if(version == 2){
			settings_TV_OUT.setVisibility(View.VISIBLE);
			settings_park.setVisibility(View.VISIBLE);
		}else if(version == 1){
			settings_TV_OUT.setVisibility(View.GONE);
			settings_park.setVisibility(View.GONE);
			settings_sdcardlt.setVisibility(View.GONE);
			settings_factoryreset.setVisibility(View.GONE);
//			settings_factoryreset.setVisibility(View.GONE);
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
		new GetCameraSettingStatus().execute();
		if (version==1) {
			new CameraStatusCustomer().execute();
			 settings_TV_OUT.setVisibility(View.GONE);
		}
		isINFilebrowser=true;
		returnBlankScreen=true;
		settings_DV_quality_row.setVisibility(View.GONE);
		settings_photo_quality_row.setVisibility(View.GONE);
		settings_videoout_row.setVisibility(View.GONE);
		settings_frequency_row.setVisibility(View.GONE);
		settings_photo_quality_row_open=false;
		settings_videoout_row_open=false;
		settings_DV_quality_row_open=false;
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
