package tw.com.a_i_t.IPCamViewer.Viewer ;

import java.net.URL ;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.videolan.libvlc.EventHandler ;
import org.videolan.libvlc.IVideoPlayer ;
import org.videolan.libvlc.LibVLC ;
import org.videolan.libvlc.LibVlcException ;
import org.videolan.vlc.Util ;
import org.videolan.vlc.VLCApplication ;
import org.videolan.vlc.WeakHandler ;

import tw.com.a_i_t.IPCamViewer.CameraCommand ;
import tw.com.a_i_t.IPCamViewer.CustomDialog;
import tw.com.a_i_t.IPCamViewer.MainActivity ;
import tw.com.a_i_t.IPCamViewer.R ;
import tw.com.a_i_t.IPCamViewer.Viewer.MediaUrlDialog.MediaUrlDialogHandler ;
import android.R.color;
import android.app.Activity ;
import android.app.Fragment ;
import android.app.ProgressDialog ;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.IntentFilter ;
import android.content.SharedPreferences ;
import android.content.res.Configuration ;
import android.graphics.ImageFormat ;
import android.graphics.PixelFormat ;
import android.media.AudioManager ;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Handler ;
import android.os.Message ;
import android.os.SystemClock;
import android.preference.PreferenceManager ;
import android.util.Log ;
import android.view.LayoutInflater ;
import android.view.SurfaceHolder ;
import android.view.SurfaceHolder.Callback ;
import android.view.SurfaceView ;
import android.view.View ;
import android.view.View.OnClickListener;
import android.view.ViewGroup ;
import android.view.ViewGroup.LayoutParams ;
import android.widget.Button ;
import android.widget.FrameLayout ;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StreamPlayerFragment extends Fragment implements IVideoPlayer {

	public final static String TAG = "VLC/VideoPlayerActivity" ;

	private SurfaceView mSurface ;
	private SurfaceHolder mSurfaceHolder ;
	private FrameLayout mSurfaceFrame ;
	private LibVLC mLibVLC ;
	private boolean mRecording ;
	private TextView curdate;
	private Button cameraStopButton;
	private Button cameraRecordButton;
	private Button cameraSnapshotButton;
	private Button findCameraButton;
	private LinearLayout snapshotButton;
	private LinearLayout recordButton;
	private TextView mRecordTxt;
	private ImageButton mSoundControlButton;
	private ImageButton mSoundControlButtonDisable;
	private AudioManager audioManager;
    private static Date mCameraTime ;
    private static long mCameraUptimeMills ;
    private String mTime;
    public static String mRecordStatus="";
    public static String mRecordmode="";
    private boolean mRecordthread = false;
    
	private int mB = -1;
	private int mlocalB=0;
	private static final int MSG_SUCCESS = 1;
	private static final int MSG_FAIL = 2;
	private static final int MSG_DESTORY = 3;
	
	
	private static final int SURFACE_BEST_FIT = 0 ;
	private static final int SURFACE_FIT_HORIZONTAL = 1 ;
	private static final int SURFACE_FIT_VERTICAL = 2 ;
	private static final int SURFACE_FILL = 3 ;
	private static final int SURFACE_16_9 = 4 ;
	private static final int SURFACE_4_3 = 5 ;
	private static final int SURFACE_ORIGINAL = 6 ;
	private int mCurrentSize = SURFACE_BEST_FIT ;

	/**
	 * For uninterrupted switching between audio and video mode
	 */
	private boolean mEndReached ;

	// size of the video
	private int mVideoHeight ;
	private int mVideoWidth ;
	private int mVideoVisibleHeight ;
	private int mVideoVisibleWidth ;
	private int mSarNum ;
	private int mSarDen ;
	
	private boolean mPlaying ;
	ProgressDialog mProgressDialog ;
	private String mMediaUrl ;
	private static final int MSG_VISIABLE_RECORD_BTN =1;
	private static final int MSG_INVISIABLE_RECORD_BTN =2;
	private static final int MSG_REFRESH_MUTE_STATE =3;
	private static final int button_fresh_delaytime = 500; //ms
	private static final String KEY_MEDIA_URL = "mediaUrl" ;
	private static final String MuteOn ="MuteOn";
	private static final String MuteOff ="MuteOff";
	private TimeThread timestampthread;
	private String mMuteStatus = MuteOn;
	private int countTime=0;
	private boolean isVISIBLE=true;
	private boolean isINVISIABLE=true;


/*用来记录handle延时发送的时间，防止延时发送产生重复的指令*/
	public  void isVISIBLEs(){
		countTime=0;
		Timer timer=new Timer();
		TimerTask task=new TimerTask() {
			@Override
				public void run() {
					isVISIBLE=true;
			}
		};timer.schedule(task, 100 * 5);
	}
	public void isINVISIABLEs(){
		countTime=0;
		Timer timer=new Timer();
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				isINVISIABLE=true;
			}
		};timer.schedule(task, 100 * 5);
	}


	private Handler mHandlerUI = new Handler()
	{
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				case MSG_VISIABLE_RECORD_BTN:
					if (isVISIBLE){
						isVISIBLE=false;
					cameraRecordButton.setVisibility(View.VISIBLE);
					Log.d("video","MSG_VISIABLE_RECORD_BTN");
					mHandlerUI.sendEmptyMessageDelayed(MSG_INVISIABLE_RECORD_BTN, button_fresh_delaytime);
						isVISIBLEs();
					}
						break;
				case MSG_INVISIABLE_RECORD_BTN:
					if (isINVISIABLE) {
						Log.d("video", "MSG_INVISIABLE_RECORD_BTN");
						cameraRecordButton.setVisibility(View.INVISIBLE);
						mHandlerUI.sendEmptyMessageDelayed(MSG_VISIABLE_RECORD_BTN, button_fresh_delaytime);
					isINVISIABLEs();
					}
						break;
				case MSG_REFRESH_MUTE_STATE:
						if(mMuteStatus.equals(MuteOff))
						{
							mSoundControlButton.setVisibility(View.VISIBLE);
							mSoundControlButtonDisable.setVisibility(View.GONE);
						}
						else if(mMuteStatus.equals(MuteOn))
						{
							mSoundControlButton.setVisibility(View.GONE);
							mSoundControlButtonDisable.setVisibility(View.VISIBLE);
						}
						else
						{
							mSoundControlButton.setVisibility(View.VISIBLE);
							mSoundControlButtonDisable.setVisibility(View.GONE);
						}
						
						break;
				default:
					break;
			}
		}
	};
	
	public static StreamPlayerFragment newInstance(String mediaUrl) {
		StreamPlayerFragment fragment = new StreamPlayerFragment() ;
		
		Bundle args = new Bundle() ;
		args.putString(KEY_MEDIA_URL, mediaUrl) ;
		fragment.setArguments(args) ;

		return fragment ;
	}
	
   public String checkTime(int i)
	{
	    	mTime = Integer.toString(i);
	    	if (i<10) 
	    	{
	    		mTime = "0" + mTime;
	    	}
	    	return mTime;
	}
	//yining
	private class GetTimeStamp extends AsyncTask<URL, Integer, String> {

		protected void onPreExecute() {
			setWaitingState(true) ;
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandTimeStampUrl() ;
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
				int year = 2015;
				int month = 1;
				int day = 1;
				int hour = 1;
				int minute = 1;
				int second = 1;
				String[] lines;		
				String[] lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.year=");
				if(null != lines_temp && 1 < lines_temp.length)
				{	
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					year = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.month=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					month = Integer.valueOf(lines[0]); 
				}
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.day=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					day = Integer.valueOf(lines[0]); 
				}
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.hour=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					hour= Integer.valueOf(lines[0]); 
				}
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.minute=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					minute = Integer.valueOf(lines[0]); 
				}
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.second=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					second = Integer.valueOf(lines[0]); 
				}
				SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);  
				try {
					String cameraUptimeStr = String.format("%04d-%02d-%02d %02d:%02d:%02d", 
							year, month, day, hour, minute, second) ;
					mCameraTime = format.parse(cameraUptimeStr);  
				    Log.i("GetTimeStamp", cameraUptimeStr);  
				} catch (ParseException e) {  
				    // TODO Auto-generated catch block  
				    e.printStackTrace();  
				}
				mCameraUptimeMills = SystemClock.uptimeMillis() ;

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

	private class GetRecordStatus extends AsyncTask<URL, Integer, String> {
			@Override
			protected void onPreExecute() {
				setWaitingState(true) ;
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
					lines_temp = result.split("Camera.Preview.MJPEG.status.mute=");
					if(null != lines_temp && 1 < lines_temp.length)
					{
						lines = lines_temp[1].split(System.getProperty("line.separator")) ;
						if(lines!=null)
						mMuteStatus = lines[0];
						Log.d(TAG,"mMuteStatus="+mMuteStatus);
					}
					
					mHandlerUI.sendEmptyMessage(MSG_REFRESH_MUTE_STATE);
					mRecordStatusHandler.sendMessage(mRecordStatusHandler.obtainMessage());

				}
				else if (activity != null) {
					//Toast.makeText(activity, activity.getResources().getString(R.string.message_fail_get_info),
							//Toast.LENGTH_LONG).show() ;			
				}
				setWaitingState(false) ;
				setInputEnabled(true) ;
				super.onPostExecute(result) ;

		}
	}	
	
	
	private class CameraVideoRecord extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			setWaitingState(true) ;
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
				if (mRecordmode.equals("Videomode"))
				{
					if(mRecordStatus.equals("Recording"))
					{
						mRecordStatus = "Standby";
						Toast.makeText(activity,
								activity.getResources().getString(R.string.msg_stoprecord),
								Toast.LENGTH_SHORT).show() ;
					}
					else
					{
						mRecordStatus = "Recording";
						Toast.makeText(activity,
								activity.getResources().getString(R.string.msg_recording),
								Toast.LENGTH_SHORT).show() ;
					}
					mRecordStatusHandler.sendMessage(mRecordStatusHandler.obtainMessage());
				}
			}
			else if (activity != null) {
				
				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_failed),
						Toast.LENGTH_SHORT).show() ;	
			}
			setWaitingState(false) ;
			setInputEnabled(true) ;
			super.onPostExecute(result) ;

	}
}	
	
	private class CameraSnapShot extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			setWaitingState(true) ;
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandCameraSnapshotUrl() ;
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			Activity activity = getActivity() ;
			Log.d(TAG, "snapshot response:"+result) ;
			if (result != null && result.equals("709\n?") != true) {			
				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_camer),
						Toast.LENGTH_SHORT).show() ;
			}
			else if (activity != null) {
				
				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_uncamer),
						Toast.LENGTH_SHORT).show() ;	
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
	    if (savedInstanceState == null) {
	    	mRecordthread = true;
	    	new GetTimeStamp().execute();
	    	new GetRecordStatus().execute();
	        // GET CAMERA STATE
	    }
		mMediaUrl = getArguments().getString(KEY_MEDIA_URL) ;

		IntentFilter filter = new IntentFilter() ;
		filter.addAction(VLCApplication.SLEEP_INTENT) ;
		getActivity().registerReceiver(mReceiver, filter) ;

		try {

			mLibVLC = Util.getLibVlcInstance() ;

//			Context context = VLCApplication.getAppContext() ;
//			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context) ;
//			int cacheSize = pref.getInt("network_caching_value", 1000) ;
//
//			mLibVLC.setNetworkCaching(cacheSize) ;

			mRecording = false ;

		} catch (LibVlcException e) {
			Log.d(TAG, "LibVLC initialisation failed") ;
			return ;
		}

		EventHandler em = EventHandler.getInstance() ;
		em.addHandler(eventHandler) ;
		timestampthread = new TimeThread();
		timestampthread.start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new GSetRandomValues().execute();
			}
		}).start();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.preview_player, container, false) ;
		audioManager = (AudioManager) getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;
		curdate = (TextView) view.findViewById(R.id.TimeStampLabel);
		mSurface = (SurfaceView) view.findViewById(R.id.player_surface) ;
		mSurfaceHolder = mSurface.getHolder() ;
		mSurfaceFrame = (FrameLayout) view.findViewById(R.id.player_surface_frame) ;
		String chroma = pref.getString("chroma_format", "") ;
		if (chroma.equals("YV12")) {
			mSurfaceHolder.setFormat(ImageFormat.YV12) ;
		} else if (chroma.equals("RV16")) {
			mSurfaceHolder.setFormat(PixelFormat.RGB_565) ;
			PixelFormat info = new PixelFormat() ;
			PixelFormat.getPixelFormatInfo(PixelFormat.RGB_565, info) ;
		} else {
			mSurfaceHolder.setFormat(PixelFormat.RGBX_8888) ;
			PixelFormat info = new PixelFormat() ;
			PixelFormat.getPixelFormatInfo(PixelFormat.RGBX_8888, info) ;
		}
		mSurfaceHolder.addCallback(mSurfaceCallback) ;

		LibVLC.restart(getActivity()) ;

		snapshotButton = (LinearLayout) view.findViewById(R.id.snapshotButton) ;

		snapshotButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				new CameraSnapShot().execute();
				/*
				File path = MainActivity.getAppDir() ;
				String fileName = MainActivity.getSnapshotFileName() ;

				if (mLibVLC.takeSnapShot(path.getPath() + File.separator + fileName, mVideoWidth,
						mVideoHeight)) {
					long dateTaken = System.currentTimeMillis() ;

					MainActivity.addImageAsApplication(getActivity().getContentResolver(), fileName,
							dateTaken, path.getPath(), fileName) ;
				}
				*/
			}
		}) ;

		final String appRecord = getActivity().getResources().getString(R.string.label_app_record) ;
		final String recording = getActivity().getResources().getString(R.string.recording) ;
		mRecordTxt = (TextView) view.findViewById(R.id.record_txt) ;
		recordButton = (LinearLayout) view.findViewById(R.id.recordButton) ;
		recordButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				new CameraVideoRecord().execute();
				/*
				if (!mRecording) {

					Activity activity = getActivity() ;

					if (activity == null)
						return ;
					
					CustomDialog alertDialog = new CustomDialog.Builder(activity)
					.setTitle(R.string.label_app_record)
					.setCancelable(false)
					.setMessage(R.string.message_save_video)
					.setPositiveButton(R.string.label_ok,new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.dismiss();
							File path = MainActivity.getAppDir() ;
							String fileName = MainActivity.getMJpegFileName() ;
							fileName = "APP_"+fileName;
							if (mLibVLC.toggleRecord(path.getPath(), fileName)) {

								mRecording = !mRecording ;
							}
							if (mRecording) {
								mRecordTxt.setText(recording) ;
							} else {
								mRecordTxt.setText(appRecord) ;
							}
						}
					}).create();
					alertDialog.show() ;
				} else {

					File path = MainActivity.getAppDir() ;
					String fileName = MainActivity.getMJpegFileName() ;
					fileName = "APP_"+fileName;
					if (mLibVLC.toggleRecord(path.getPath(), fileName)) {

						mRecording = !mRecording ;
					}

					if (mRecording) {
						mRecordTxt.setText(recording) ;
					} else {
						mRecordTxt.setText(appRecord) ;
					}
				}
				*/
			}
		}) ;

		findCameraButton = (Button) view.findViewById(R.id.findCameraButton) ;
		findCameraButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				URL url = CameraCommand.commandFindCameraUrl() ;

				if (url != null) {
					new CameraCommand.SendRequest().execute(url) ;
				}
			}
		}) ;
		cameraStopButton = (Button) view.findViewById(R.id.cameraStopButton) ;
		cameraStopButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				//stop cameraRecord
				cameraRecordButton.setVisibility(View.VISIBLE);
				new CameraVideoRecord().execute();
			}
		}) ;
		cameraRecordButton = (Button) view.findViewById(R.id.cameraRecordButton) ;
		cameraRecordButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				cameraStopButton.setVisibility(View.VISIBLE);
				new CameraVideoRecord().execute();

			}
		}) ;
	
		cameraSnapshotButton = (Button) view.findViewById(R.id.cameraSnapshotButton) ;
		cameraSnapshotButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

					new CameraSnapShot().execute();

			}
		}) ;

		cameraRecordButton.setVisibility(View.VISIBLE) ;
		cameraStopButton.setVisibility(View.GONE);
		cameraSnapshotButton.setEnabled(true) ;
		findCameraButton.setEnabled(true);
		
		mSoundControlButton = (ImageButton) view.findViewById(R.id.sound) ;
		mSoundControlButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ȡ��¼��
				/*dufrense delete 2014-10-*/
				URL url = CameraCommand.commandMuteOnUrl() ;
				if (url != null) {
					new CameraCommand.SendRequest().execute(url) ;
					new GetRecordStatus().execute();
				}	

			}
		}) ;
		mSoundControlButtonDisable = (ImageButton) view.findViewById(R.id.sound_disable) ;
		mSoundControlButtonDisable.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//����¼��
				/*dufrense delete 2014-10-12*/
				URL url = CameraCommand.commandMuteOffUrl() ;
				if (url != null) {
					new CameraCommand.SendRequest().execute(url) ;
					new GetRecordStatus().execute();
				}

			}
		}) ;
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC) ;
		mHandlerUI.sendEmptyMessage(MSG_REFRESH_MUTE_STATE);
		return view ;
	}

	@Override
	public void onPause() {
		super.onPause() ;
		
		stop() ;
		
		mSurface.setKeepScreenOn(false) ;
	}

	@Override
	public void onStop() {
		super.onStop() ;
	}

	@Override
	public void onDestroy() {
		super.onDestroy() ;
		getActivity().unregisterReceiver(mReceiver) ;
		if (mLibVLC != null) {
			mLibVLC.stop() ;
			mLibVLC = null ;
		}

		EventHandler em = EventHandler.getInstance() ;
		em.removeHandler(eventHandler) ;
		mRecordthread = false;
		
	}
	
	private void stop() {
		
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			
			mProgressDialog.dismiss() ;
			mProgressDialog = null ;
		}
		
		if (mPlaying == true) {
			mPlaying = false ;
			mLibVLC.stop() ;
		}
	}
	
	public void play(int connectionDelay) {

		Activity activity = getActivity() ;
		if (activity != null) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			View v = layoutInflater.inflate(R.layout.loading_dialog, null);
			mProgressDialog = new ProgressDialog(activity) ;
			//mProgressDialog.setTitle("Connecting to Camera") ;
			mProgressDialog.setCancelable(false) ;
			mProgressDialog.show() ;
			mProgressDialog.setContentView(v);
			Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() { 
		         public void run() { 
		        	if (mPlaying == false && mLibVLC != null && mRecordmode.equals("Videomode"))
		        	{
		        		mPlaying = true ;
		        		mLibVLC.playMRL(mMediaUrl) ;
		    			mEndReached = false ;
		        	}
		        	if (mProgressDialog != null && mProgressDialog.isShowing()) {
		        		mProgressDialog.dismiss() ;
		        		mProgressDialog = null ;
		        	}
		         } 
		    }, connectionDelay) ;
		}		
	}
	
	private void playLiveStream() {
		
		play(MainActivity.sConnectionDelay) ;
	}
	

	@Override
	public void onResume() {
		super.onResume() ;

		playLiveStream() ;
		
		mSurface.setKeepScreenOn(true) ;
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction() ;
			if (action.equalsIgnoreCase(VLCApplication.SLEEP_INTENT)) {
				getActivity().finish() ;
			}
		}
	} ;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen) ;
		super.onConfigurationChanged(newConfig) ;
	}

	@Override
	public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num,
			int sar_den) {
		if (width * height == 0)
			return ;

		// store video size
		mVideoHeight = height ;
		mVideoWidth = width ;
		mVideoVisibleHeight = visible_height ;
		mVideoVisibleWidth = visible_width ;
		mSarNum = sar_num ;
		mSarDen = sar_den ;
		Message msg = mHandler.obtainMessage(SURFACE_SIZE) ;
		mHandler.sendMessage(msg) ;
	}
	private Handler mHandler_ui = new Handler() {
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
	private final Handler mHandler = new VideoPlayerHandler(this) ;
	private static final int SURFACE_SIZE = 1 ;

	private static class VideoPlayerHandler extends WeakHandler<StreamPlayerFragment> {
		public VideoPlayerHandler(StreamPlayerFragment owner) {
			super(owner) ;
		}

		@Override
		public void handleMessage(Message msg) {
			StreamPlayerFragment activity = getOwner() ;
			if (activity == null) // WeakReference could be GC'ed early
				return ;

			switch (msg.what) {

			case SURFACE_SIZE:
				activity.changeSurfaceSize() ;
				break ;
			}
		}
	} ;

	/**
	 * Handle libvlc asynchronous events
	 */
	private final Handler eventHandler = new VideoPlayerEventHandler(this) ;

	private static class VideoPlayerEventHandler extends WeakHandler<StreamPlayerFragment> {
		public VideoPlayerEventHandler(StreamPlayerFragment owner) {
			super(owner) ;
		}

		@Override
		public void handleMessage(Message msg) {
			StreamPlayerFragment activity = getOwner() ;
			if (activity == null)
				return ;

			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerPlaying:
				Log.i(TAG, "MediaPlayerPlaying") ;
				break ;
			case EventHandler.MediaPlayerPaused:
				Log.i(TAG, "MediaPlayerPaused") ;
				break ;
			case EventHandler.MediaPlayerStopped:
				Log.i(TAG, "MediaPlayerStopped") ;
				break ;
			case EventHandler.MediaPlayerEndReached:
				Log.i(TAG, "MediaPlayerEndReached") ;
				activity.endReached() ;
				break ;
			case EventHandler.MediaPlayerVout:
				activity.handleVout(msg) ;
				break ;
			case EventHandler.MediaPlayerPositionChanged:
				// don't spam the logs
				break ;
			case EventHandler.MediaPlayerEncounteredError:
				Log.i(TAG, "MediaPlayerEncounteredError") ;
				activity.encounteredError();
				break ;
			default:
				Log.e(TAG, String.format("Event not handled (0x%x)", msg.getData().getInt("event"))) ;
				break ;
			}
		}
	} ;

	private void endReached() {
		/* Exit player when reach the end */
		mEndReached = true ;
		//getActivity().onBackPressed() ;
		
		//play() ;
	}

	private void encounteredError() {

		if(mRecordmode.equals("Videomode"))
		{
		new MediaUrlDialog(getActivity(), mMediaUrl, new MediaUrlDialogHandler() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}
		}).show() ;
		}
		cameraRecordButton.setVisibility(View.GONE);
		cameraSnapshotButton.setVisibility(View.GONE);
		findCameraButton.setEnabled(false);
		recordButton.setEnabled(false);
		snapshotButton.setEnabled(false);
	}

	private void handleVout(Message msg) {
		if (msg.getData().getInt("data") == 0 && mEndReached) {

			Log.i(TAG, "Video track lost") ;
			
			stop() ;
			playLiveStream() ;
		}
	}

	private void changeSurfaceSize() {
		// get screen size
		int dw = 0;
		int dh = 0;
		if(getActivity() ==null)
			return;
		if(getActivity().getWindow()!=null && getActivity().getWindow().getDecorView()!=null)
		{
			 dw = getActivity().getWindow().getDecorView().getWidth() ;
			 dh = getActivity().getWindow().getDecorView().getHeight() ;
		}
		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ;
		if (dw > dh && isPortrait || dw < dh && !isPortrait) {
			int d = dw ;
			dw = dh ;
			dh = d ;
		}

		// sanity check
		if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
			Log.e(TAG, "Invalid surface size") ;
			return ;
		}

		// compute the aspect ratio
		double ar, vw ;
		double density = (double) mSarNum / (double) mSarDen ;
		if (density == 1.0) {
			/* No indication about the density, assuming 1:1 */
			vw = mVideoVisibleWidth ;
			ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight ;
		} else {
			/* Use the specified aspect ratio */
			vw = mVideoVisibleWidth * density ;
			ar = vw / mVideoVisibleHeight ;
		}

		// compute the display aspect ratio
		double dar = (double) dw / (double) dh ;

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			if (dar < ar)
				dh = (int) (dw / ar) ;
			else
				dw = (int) (dh * ar) ;
			break ;
		case SURFACE_FIT_HORIZONTAL:
			dh = (int) (dw / ar) ;
			break ;
		case SURFACE_FIT_VERTICAL:
			dw = (int) (dh * ar) ;
			break ;
		case SURFACE_FILL:
			break ;
		case SURFACE_16_9:
			ar = 16.0 / 9.0 ;
			if (dar < ar)
				dh = (int) (dw / ar) ;
			else
				dw = (int) (dh * ar) ;
			break ;
		case SURFACE_4_3:
			ar = 4.0 / 3.0 ;
			if (dar < ar)
				dh = (int) (dw / ar) ;
			else
				dw = (int) (dh * ar) ;
			break ;
		case SURFACE_ORIGINAL:
			dh = mVideoVisibleHeight ;
			dw = (int) vw ;
			break ;
		}

		// force surface buffer size
		mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight) ;

		// set display size
		LayoutParams lp = mSurface.getLayoutParams() ;
		lp.width = dw * mVideoWidth / mVideoVisibleWidth ;
		lp.height = dh * mVideoHeight / mVideoVisibleHeight ;
		mSurface.setLayoutParams(lp) ;

		// set frame size (crop if necessary)
		lp = mSurfaceFrame.getLayoutParams() ;
		lp.width = dw ;
		lp.height = dh ;
		mSurfaceFrame.setLayoutParams(lp) ;
		mSurfaceFrame.setBackgroundColor(color.black);
		mSurface.invalidate() ;
	}

	/**
	 * attach and disattach surface to the lib
	 */
	private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (format == PixelFormat.RGBX_8888)
				Log.d(TAG, "Pixel format is RGBX_8888") ;
			else if (format == PixelFormat.RGB_565)
				Log.d(TAG, "Pixel format is RGB_565") ;
			else if (format == ImageFormat.YV12)
				Log.d(TAG, "Pixel format is YV12") ;
			else
				Log.d(TAG, "Pixel format is other/unknown") ;
			mLibVLC.attachSurface(holder.getSurface(), StreamPlayerFragment.this) ;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mLibVLC.detachSurface() ;
		}
	} ;
	
	private Handler mTimeHandler = new Handler() {
		public void handleMessage(Message msg){
			/*
            	mSecond++;
				if(mSecond==60)
				{
					mSecond=0;
					mMinute++;
					if(mMinute==60)
					{
						mHour++;
						mMinute=0;
						if(mHour==24)
						{
							mDay++;
						mHour=0;
						}
					}
				}
				timestamp = checkTime(mYear)+"/"+checkTime(mMonth)+"/"+checkTime(mDay);
				timestamp += " " + checkTime(mHour)+":"+checkTime(mMinute)+":"+checkTime(mSecond);
            	curdate.setText(timestamp);
            	timestamp = " ";
            */
            long timeElapsed = SystemClock.uptimeMillis() - mCameraUptimeMills ;
            	
            Date currentTime = new Date(mCameraTime.getTime() + timeElapsed) ;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.US) ;
            String currentTimeStr = sdf.format(currentTime);
            curdate.setText(currentTimeStr) ;
            super.handleMessage(msg);  
		}
	};
	
	public Handler mRecordStatusHandler = new Handler() {
		public void handleMessage(Message msg){
			
            	if(mRecordStatus.equals("Recording"))
            	{
            		cameraStopButton.setVisibility(View.VISIBLE);
            		mHandlerUI.sendEmptyMessage(MSG_INVISIABLE_RECORD_BTN);
            		mRecordTxt.setText(R.string.recording);
            	}
            	else
            	{
            		mHandlerUI.removeMessages(MSG_INVISIABLE_RECORD_BTN);
            		mHandlerUI.removeMessages(MSG_VISIABLE_RECORD_BTN);
            		cameraStopButton.setVisibility(View.GONE);
            		cameraRecordButton.setVisibility(View.VISIBLE);
            		mRecordTxt.setText(R.string.label_app_record);
            	}
            	if (mRecordmode.equals("NotVideomode"))
            	{
            		
            		CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
            		.setTitle("Mode Error!")
            		.setMessage("Not At Video Mode")
            		.setPositiveButton(R.string.label_ok,new DialogInterface.OnClickListener() {
            			
            			@Override
            			public void onClick(DialogInterface arg0, int arg1) {
            				// TODO Auto-generated method stub
            				arg0.dismiss();
            			}
            		}).create();
            		alertDialog.show() ;		
            	}
            			
            super.handleMessage(msg);  
		}
	};

	private class TimeThread extends Thread{

			boolean mPlaying=true;
			public void run() {
				while(mPlaying)
				{	
					try{				
						Thread.sleep(1000);
					} catch (Exception e){
						e.printStackTrace();
					}
					
					if (mCameraTime == null) {
						
						continue ;
					}
					
					//if(mRecordthread)
						//mRecordStatusHandler.sendMessage(mRecordStatusHandler.obtainMessage());
					
					mTimeHandler.sendMessage(mTimeHandler.obtainMessage());			
				}
			}
			public void stopPlay(){
				mPlaying=false;
			}
	};
	
	public String IsStatusRecording()
	{
		return mRecordStatus;
	}
	
	public void SetRecordStatus(String Status)
	{
		mRecordStatus = Status;
	}
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
			mlocalB = (v^2015) -1;
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
			Message msg =  mHandler_ui.obtainMessage();
			msg.what = what;
			mHandler_ui.sendMessage(msg);
			super.onPostExecute(result) ;
		}
	}
}
