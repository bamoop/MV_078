package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;

import tw.com.a_i_t.IPCamViewer.Control.NetworkConfigurationsFragment;
import android.app.Activity;
import android.app.Fragment ;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.view.View.OnTouchListener ;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingFragment extends Fragment {
	private static final String MuteOn ="MuteOn";
	private static final String MuteOff ="MuteOff";
	private static final int MSG_REFRESH_MUTE_STATE =1;
	private ToggleButton mMutestateSwitcher;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		new GetRecordStatus().execute();
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
							if(status.equalsIgnoreCase(MuteOn))
							{
								mMutestateSwitcher.setChecked(false);
								Log.i("moop", "if,mMutestateSwitcher");

							}
							else if(status.equalsIgnoreCase(MuteOff))
							{
								Log.i("moop", "if,nononono");
								mMutestateSwitcher.setChecked(true);

							}
						}
					break;
				default:
					break;
			}
		}
	};
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
				//Toast.makeText(activity, activity.getResources().getString(R.string.message_fail_get_info),
						//Toast.LENGTH_LONG).show() ;			
			}
			super.onPostExecute(result) ;

	}
}	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_fragment, container, false) ;

		OnTouchListener onTouch = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
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

				MainActivity.addFragment(SettingFragment.this, new NetworkConfigurationsFragment());
			}
		}) ;

		networkConfigurations.setOnTouchListener(onTouch) ;

		mMutestateSwitcher = (ToggleButton) view.findViewById(R.id.tgl_record) ;
		mMutestateSwitcher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ����ť��һ�α����ʱ����Ӧ���¼�
				if (mMutestateSwitcher.isChecked()) {
					Log.i("moop", "1");
					URL url = CameraCommand.commandMuteOffUrl() ;
					if (url != null) {
						new CameraCommand.SendRequest().execute(url) ;
				}
				}
				// ����ť�ٴα����ʱ����Ӧ���¼�
				else {
					Log.i("moop", "2");
					Log.i("moop","if,no--button");
					//ȡ��¼��
					URL url = CameraCommand.commandMuteOnUrl() ;
					if (url != null) {
						new CameraCommand.SendRequest().execute(url) ;
				}
			}}
		});

		//time setting
		LinearLayout time_setting = (LinearLayout) view.findViewById(R.id.time_setting) ;

		time_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_Time_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_DV_quality = (LinearLayout) view.findViewById(R.id.settings_DV_quality) ;

		settings_DV_quality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_Quality_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_photo_quality = (LinearLayout) view.findViewById(R.id.settings_photo_quality) ;

		settings_photo_quality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_photo_Quality_Fragment()) ;
			}
		}) ;	
		
		LinearLayout settings_move = (LinearLayout) view.findViewById(R.id.settings_move) ;

		settings_move.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_Move_Fragment()) ;
			}
		}) ;
		
		
		
		LinearLayout settings_TV = (LinearLayout) view.findViewById(R.id.settings_TV) ;

		settings_TV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_TV_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_white_balance = (LinearLayout) view.findViewById(R.id.settings_white_balance) ;

		settings_white_balance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_white_balance_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_exposure = (LinearLayout) view.findViewById(R.id.settings_exposure) ;

		settings_exposure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_Exposure_Fragment()) ;
			}
		}) ;
		LinearLayout settings_lockprotect = (LinearLayout) view.findViewById(R.id.settings_lockProtect) ;

		settings_lockprotect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_lockprotect_Fragment()) ;
			}
		}) ;
		LinearLayout settings_formatsdcard = (LinearLayout) view.findViewById(R.id.settings_formatsdcard) ;

		settings_formatsdcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_formatsdcard_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_park = (LinearLayout) view.findViewById(R.id.settings_parkcar) ;

		settings_park.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_ParkCar_Fragment()) ;
			}
		}) ;
		
		
		LinearLayout settings_TV_OUT = (LinearLayout) view.findViewById(R.id.settings_videoout) ;

		settings_TV_OUT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_TV_OUT_Fragment()) ;
			}
		}) ;
		
		LinearLayout settings_version = (LinearLayout) view.findViewById(R.id.settings_version) ;

		settings_version.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(SettingFragment.this, new Setting_Version_Fragment()) ;
			}
		}) ;
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int v = pref.getInt(MainActivity.g_version_check,MainActivity.G_UNKNOWN_VERSION);
		if(v != MainActivity.G_NEW_VERSION)
		{
			view.findViewById(R.id.tmp).setVisibility(View.GONE);
			settings_TV_OUT.setVisibility(View.GONE);
			settings_park.setVisibility(View.GONE);
			view.findViewById(R.id.settings_parkcar_line).setVisibility(View.GONE);
			view.findViewById(R.id.settings_videoout_line).setVisibility(View.GONE);
		}
		return view ;
		//CameraSettingsFragment
	}
	
	@Override
	public void onPause() {

		////begin added by eric for update record status
		MainActivity.setUpdateRecordStatusFlag(true);
		///end
		super.onPause() ;
	}

}
