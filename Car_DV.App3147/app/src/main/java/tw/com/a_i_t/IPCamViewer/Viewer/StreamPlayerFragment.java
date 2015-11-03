package tw.com.a_i_t.IPCamViewer.Viewer ;

import java.io.File ;
import java.net.URL ;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.videolan.libvlc.EventHandler ;
import org.videolan.libvlc.IVideoPlayer ;
import org.videolan.libvlc.LibVLC ;
import org.videolan.libvlc.LibVlcException ;
import org.videolan.libvlc.Media ;
import org.videolan.vlc.Util ;
import org.videolan.vlc.VLCApplication ;
import org.videolan.vlc.WeakHandler ;

import tw.com.a_i_t.IPCamViewer.CameraCommand ;
import tw.com.a_i_t.IPCamViewer.MainActivity ;
import tw.com.a_i_t.IPCamViewer.R ;
import tw.com.a_i_t.IPCamViewer.Viewer.MediaUrlDialog.MediaUrlDialogHandler ;
import android.app.Activity ;
import android.app.AlertDialog ;
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
import android.view.ViewGroup ;
import android.view.ViewGroup.LayoutParams ;
import android.widget.Button ;
import android.widget.FrameLayout ;
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
	private Button cameraRecordButton;
	private Button cameraSnapshotButton;
	private Button findCameraButton;
	private Button snapshotButton;
	private Button recordButton;
    private static Date mCameraTime ;
    private static long mCameraUptimeMills ;
    private String mTime;
    public static String mRecordStatus="";
    public static String mRecordmode="";
    private boolean mRecordthread = false;
	
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

	private static final String KEY_MEDIA_URL = "mediaUrl" ;
	private TimeThread timestampthread;
	
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
				String[] lines;		
				String[] lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.year=");
				lines = lines_temp[1].split(System.getProperty("line.separator")) ;
				int year = Integer.valueOf(lines[0]);
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.month=");
				lines = lines_temp[1].split(System.getProperty("line.separator")) ;
				int month = Integer.valueOf(lines[0]); 
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.day=");
				lines = lines_temp[1].split(System.getProperty("line.separator")) ;
				int day = Integer.valueOf(lines[0]); 
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.hour=");
				lines = lines_temp[1].split(System.getProperty("line.separator")) ;
				int hour= Integer.valueOf(lines[0]); 
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.minute=");
				lines = lines_temp[1].split(System.getProperty("line.separator")) ;
				int minute = Integer.valueOf(lines[0]); 
				lines_temp = result.split("Camera.Preview.MJPEG.TimeStamp.second=");
				lines = lines_temp[1].split(System.getProperty("line.separator")) ;
				int second = Integer.valueOf(lines[0]); 
				
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
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mRecordStatus = lines[0];
					lines_temp = result.split("Camera.Preview.MJPEG.status.mode=");
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					mRecordmode = lines[0];
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
				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_succeed),
						Toast.LENGTH_SHORT).show() ;
				if (mRecordmode.equals("Videomode"))
				{
					if(mRecordStatus.equals("Recording"))
						mRecordStatus = "Standby";
					else
						mRecordStatus = "Recording";
					
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
						activity.getResources().getString(R.string.message_command_succeed),
						Toast.LENGTH_SHORT).show() ;
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
	    	/* query camera time in camera and to show on preview window */
	    	new GetTimeStamp().execute();
	    	/* query video status (recording or not) in camera */
	    	new GetRecordStatus().execute();
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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.preview_player, container, false) ;

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

		snapshotButton = (Button) view.findViewById(R.id.snapshotButton) ;

		snapshotButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				File path = MainActivity.getAppDir() ;
				String fileName = MainActivity.getSnapshotFileName() ;

				if (mLibVLC.takeSnapShot(path.getPath() + File.separator + fileName, mVideoWidth,
						mVideoHeight)) {
					long dateTaken = System.currentTimeMillis() ;

					MainActivity.addImageAsApplication(getActivity().getContentResolver(), fileName,
							dateTaken, path.getPath(), fileName) ;
				}
			}
		}) ;

		final String appRecord = getActivity().getResources().getString(R.string.label_app_record) ;
		final String stopRecord = getActivity().getResources().getString(R.string.label_stop_record) ;

		recordButton = (Button) view.findViewById(R.id.recordButton) ;
		recordButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!mRecording) {

					Activity activity = getActivity() ;

					if (activity == null)
						return ;

					new AlertDialog.Builder(activity)
							.setTitle(activity.getResources().getString(R.string.message_start_recording))
							.setMessage(
									activity.getResources().getString(R.string.message_save_video) + " \""
											+ MainActivity.sAppName + "\"")
							.setPositiveButton(activity.getResources().getString(R.string.label_ok),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {

											File path = MainActivity.getAppDir() ;
											String fileName = MainActivity.getMJpegFileName() ;

											if (mLibVLC.toggleRecord(path.getPath(), fileName)) {

												mRecording = !mRecording ;
											}

											if (mRecording) {
												recordButton.setText(stopRecord) ;
											} else {
												recordButton.setText(appRecord) ;
											}
										}
									}).show() ;
				} else {

					File path = MainActivity.getAppDir() ;
					String fileName = MainActivity.getMJpegFileName() ;

					if (mLibVLC.toggleRecord(path.getPath(), fileName)) {

						mRecording = !mRecording ;
					}

					if (mRecording) {
						recordButton.setText(stopRecord) ;
					} else {
						recordButton.setText(appRecord) ;
					}
				}
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

		cameraRecordButton = (Button) view.findViewById(R.id.cameraRecordButton) ;
		cameraSnapshotButton = (Button) view.findViewById(R.id.cameraSnapshotButton) ;

		cameraRecordButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				new CameraVideoRecord().execute();

			}
		}) ;

		cameraSnapshotButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
					new CameraSnapShot().execute();
					
			}
		}) ;

		cameraRecordButton.setEnabled(true) ;
		cameraSnapshotButton.setEnabled(true) ;
		findCameraButton.setEnabled(true);
		
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC) ;
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
			
			mProgressDialog = new ProgressDialog(activity) ;
			
			mProgressDialog.setTitle("Connecting to Camera") ;
			mProgressDialog.setCancelable(false) ;
			mProgressDialog.show() ;
			
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
		cameraRecordButton.setEnabled(false);
		cameraSnapshotButton.setEnabled(false);
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
		int dw = getActivity().getWindow().getDecorView().getWidth() ;
		int dh = getActivity().getWindow().getDecorView().getHeight() ;

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
            		cameraRecordButton.setText(getActivity().getResources().getString(R.string.label_camera_stop_record));
            	else
            		cameraRecordButton.setText(getActivity().getResources().getString(R.string.label_camera_record));

            	if (mRecordmode.equals("NotVideomode"))
            	{
            		
            		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create() ;

					alertDialog.setTitle("Mode Error!") ;
					alertDialog.setMessage("Not At Video Mode") ;	
					alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
							"OK",			
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int id) {					
														dialog.dismiss() ;
												}									
							}) ;
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
}
