package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.Control ;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.CameraCommand;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.R ;
import android.app.Activity;
import android.app.Fragment ;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.ArrayAdapter ;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner ;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask ;
public class CameraSettingsFragment extends Fragment {

	private int mFlicker=0;
	private int mFlickerRet=0;
	private int mAWB=0;
	private int mAWBRet=0;
	private int mMTD=0;
	private int mMTDRet=0;
	private int mVideores=0;
	private int mVideoresRet=0;
	private int mImageres=0;
	private int mImageresRet=0;
	private int mEVRet=0;
	private String mFWVersionRet="";
	private boolean MenuRefresh=false;
	private Spinner videoResolution;
	private Spinner imageResolution;
	private Spinner motiondetection;
	private Spinner flickerfrequency;
	private Spinner AWB;
	//private Spinner deletefiles;
	private SeekBar exposure;
	private ArrayAdapter<String> videoResolutionAdapter;
	private ArrayAdapter<String> imageResolutionAdapter;
	private ArrayAdapter<String> motiondetectionAdapter; 
	private ArrayAdapter<String> flickerfrequencyAdapter;
	private ArrayAdapter<String> AWBAdapter;
	//private ArrayAdapter<String> deletefilesAdapter;
	private View view;
	private TextView FWVersion;
	private class CameraSettingsSendRequest extends CameraCommand.SendRequest {
	
		@Override
		protected void onPreExecute() {
			setWaitingState(true) ;
			super.onPreExecute() ;
		}

		@Override
		protected void onPostExecute(String result) {
			setWaitingState(false) ;
			super.onPostExecute(result) ;
		}
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			
			videoResolution.setSelection(mVideoresRet,true);	
			videoResolution.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView parent, View view, int pos, long id){
						
						URL url = CameraCommand.commandSetmovieresolutionUrl(pos) ;		
						if (url != null) {	
							new CameraSettingsSendRequest().execute(url) ;
						}
				}
				public void onNothingSelected(AdapterView parent) {
				} 
			}); 
			imageResolution.setSelection(mImageresRet,true);
			imageResolution.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView parent, View view, int pos, long id){					

						URL url = CameraCommand.commandSetimageresolutionUrl(pos) ;		
						if (url != null) {
							new CameraSettingsSendRequest().execute(url) ;
						}

						
					}
				public void onNothingSelected(AdapterView parent) {
				} 
			});  
			motiondetection.setSelection(mMTDRet,true);
			motiondetection.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView parent, View view, int pos, long id){					

					URL url = CameraCommand.commandSetmotiondetectionUrl(pos) ;		
					if (url != null) {
						
						new CameraSettingsSendRequest().execute(url) ;

					}
				}
				public void onNothingSelected(AdapterView parent) {
				} 
			}); 
			flickerfrequency.setSelection(mFlickerRet,true);
			flickerfrequency.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView parent, View view, int pos, long id){					
					
					URL url = CameraCommand.commandSetflickerfrequencyUrl(pos) ;		
					if (url != null) {
						
						new CameraSettingsSendRequest().execute(url) ;
					}
				}
				public void onNothingSelected(AdapterView parent) {
				} 
			});
			
			AWB.setSelection(mAWBRet,true);
			AWB.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView parent, View view, int pos, long id){					
					
					URL url = CameraCommand.commandSetAWBUrl(pos) ;		
					if (url != null) {
						
						new CameraSettingsSendRequest().execute(url) ;
					}
				}
				public void onNothingSelected(AdapterView parent) {
				} 
			});
			exposure.setProgress(mEVRet);
			FWVersion.setText(mFWVersionRet);		
		}
	};

	private class GetMenuSettingsValues extends AsyncTask <URL, Integer, String> {
		
		protected void onPreExecute() {
			setWaitingState(true) ;
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetMenuSettingsValuesUrl() ;
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
				String[] lines_temp = result.split("Camera.Menu.DefaultValue.VideoRes=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mVideoresRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.ImageRes=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mImageresRet = Integer.valueOf(lines[0]); 
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.MTD=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mMTDRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.AWB=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mAWBRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.Flicker=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mFlickerRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.FWversion=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mFWVersionRet = lines[0];
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.EV=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mEVRet = Integer.valueOf(lines[0]);
				}
				mHandler.sendMessage(mHandler.obtainMessage());
		
			}
			else if (activity != null) {
				Toast.makeText(activity, activity.getResources().getString(R.string.message_fail_get_info),
						Toast.LENGTH_LONG).show() ;			
			}
			setWaitingState(false) ;
			setInputEnabled(true) ;
			super.onPostExecute(result) ;
		}
	}
	
	
	private List<View> mViewList = new LinkedList<View>() ;

	private void setInputEnabled(boolean enabled) {

		for (View view : mViewList) {

			view.setEnabled(enabled) ;
		}
	}

	private boolean mWaitingState = false ;
	private boolean mWaitingVisible = false ;

	private void setWaitingState(boolean waiting) {

		if (mWaitingState != waiting) {

			mWaitingState = waiting ;
			setWaitingIndicator(mWaitingState, mWaitingVisible) ;
		}
	}

	private void setWaitingIndicator(boolean waiting, boolean visible) {

		if (!visible)
			return ;

		setInputEnabled(!waiting) ;

		Activity activity = getActivity() ;

		if (activity != null) {
			activity.setProgressBarIndeterminate(true) ;
			activity.setProgressBarIndeterminateVisibility(waiting) ;
		}
	}

	private void clearWaitingIndicator() {

		mWaitingVisible = false ;
		setWaitingIndicator(false, true) ;
	}

	private void restoreWaitingIndicator() {

		mWaitingVisible = true ;
		setWaitingIndicator(mWaitingState, true) ;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	    view = inflater.inflate(R.layout.camera_settings, container, false) ;
		videoResolution = (Spinner) view.findViewById(R.id.cameraSettingsVideoResolutionSpinner) ;

		videoResolutionAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item,
				new String[] { "1080p 30", "720p 30", "720p 60", "VGA" }) ;
		videoResolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;
		videoResolution.setAdapter(videoResolutionAdapter) ;
		
		imageResolution = (Spinner) view.findViewById(R.id.cameraSettingsImageResolutionSpinner) ;

		imageResolutionAdapter = new ArrayAdapter<String>(getActivity(),
		android.R.layout.simple_spinner_item, new String[] { "14M" , "12M" , "8M", "5M", "3M", "2M", "1.2M" , "VGA" }) ;
		imageResolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;
		imageResolution.setAdapter(imageResolutionAdapter) ;
		
		motiondetection = (Spinner) view.findViewById(R.id.cameraSettingsmotiondetectionSpinner) ;
		motiondetectionAdapter = new ArrayAdapter<String>(getActivity(),
		android.R.layout.simple_spinner_item, new String[] { getResources().getString(R.string.label_Off), getResources().getString(R.string.label_Low), 
			getResources().getString(R.string.label_Middle), getResources().getString(R.string.label_High) }) ;
		motiondetectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;
		motiondetection.setAdapter(motiondetectionAdapter) ;
		
		flickerfrequency = (Spinner) view.findViewById(R.id.cameraSettingsflickerSpinner) ;
		flickerfrequencyAdapter = new ArrayAdapter<String>(getActivity(),
		android.R.layout.simple_spinner_item, new String[] { getResources().getString(R.string.Flicker_50), getResources().getString(R.string.Flicker_60) }) ;
		flickerfrequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;
		flickerfrequency.setAdapter(flickerfrequencyAdapter) ;
		
		AWB = (Spinner) view.findViewById(R.id.cameraSettingswhitebalanceSpinner) ;
		AWBAdapter = new ArrayAdapter<String>(getActivity(),
		android.R.layout.simple_spinner_item, new String[] { getResources().getString(R.string.AWB_Auto), 
		getResources().getString(R.string.AWB_Daylight), getResources().getString(R.string.AWB_Cloudy) , 
		getResources().getString(R.string.AWB_Fluorescent_W) , getResources().getString(R.string.AWB_Fluorescent_N),
		getResources().getString(R.string.AWB_Fluorescent_D) , getResources().getString(R.string.AWB_Incandescent)}) ;
		AWBAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;
		AWB.setAdapter(AWBAdapter) ;
		
		
		
		exposure = (SeekBar) view.findViewById(R.id.ExposureseekBar1) ;
		exposure.setMax(12);
		exposure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int pos, boolean arg2) {
				// TODO Auto-generated method stub
				URL url = CameraCommand.commandSetEVUrl(pos) ;		
				if (url != null) {		
					new CameraSettingsSendRequest().execute(url) ;
				}
			}
		});
		
		FWVersion = (TextView) view.findViewById(R.id.FWVersionNum);
		
		Button CameraTimeSettings = (Button) view.findViewById(R.id.SyncDevice) ;

		CameraTimeSettings.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				URL url = CameraCommand.commandCameraTimeSettingsUrl() ;
				if (url != null) {
					new CameraCommand.SendRequest().execute(url) ;
				}

			}
		}) ;
		return view ;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		new GetMenuSettingsValues().execute();
	}
	
	}




