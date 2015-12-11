package tw.com.a_i_t.IPCamViewer ;

import java.io.File ;
import java.net.URL;
import java.text.SimpleDateFormat ;
import java.util.ArrayList;
import java.util.Date ;
import java.util.List;
import java.util.Locale ;

import org.videolan.vlc.VLCApplication ;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.app.Fragment ;
import android.app.FragmentManager ;
import android.app.FragmentManager.BackStackEntry ;
import android.app.FragmentTransaction ;
import android.content.ContentResolver ;
import android.content.ContentValues ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.SharedPreferences ;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration ;
import android.database.Cursor ;
import android.net.DhcpInfo;
import android.net.Uri ;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager ;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.os.Environment ;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager ;
import android.provider.BaseColumns ;
import android.provider.MediaStore ;
import android.provider.MediaStore.Images ;
import android.provider.MediaStore.MediaColumns ;
import android.util.Log ;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu ;
import android.view.MenuItem ;
import android.view.MenuItem.OnMenuItemClickListener ;
import android.view.SubMenu ;
import android.view.View;
import android.view.Window ;
import android.widget.LinearLayout;
import android.widget.Toast;

import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.CameraStatus;
import tw.com.a_i_t.IPCamViewer.Viewer.MjpegPlayerFragment;
import tw.com.a_i_t.IPCamViewer.Viewer.StreamPlayerFragment;

public class MainActivity extends Activity {
	private static Activity s_Activity;
	public static final String g_version_check = "checkversion";
	public static String mRecordStatus="";
	public static String mRecordmode="";
	private static final int MSG_STARTRECORD =1;
	private static final int DELAY_TIME=60*3000;   //60s modify by eric
	private static final int STATUS_RECORD_BACKKEY = 1;
	private static final int STATUS_RECORD_PAUSE = 2;
	private static final int STATUS_REOCORD_NOMAL= 0;
	private int mRecordSenderStatus = STATUS_REOCORD_NOMAL;
	private static final String TAG="ipviewer";
	public static final int G_UNKNOWN_VERSION=0;
	public static final int G_OLD_VERSION=1;
	public static final int G_NEW_VERSION=2;
	private WifiInfo mWifiInfo;

	private WifiManager mWifiManager;

	///begin added by eric for update recording status
	public static boolean mNeedUpdateRecordStatus = false ;
	public static boolean getUpdateRecordStatusFlag()
	{
		return mNeedUpdateRecordStatus;
	}
	public static void setUpdateRecordStatusFlag(boolean recStatusFlag)
	{
		mNeedUpdateRecordStatus = recStatusFlag;
	}

	///end

	public static String intToIp(int addr) {

		return ((addr & 0xFF) + "." + ((addr >>>= 8) & 0xFF) + "." + ((addr >>>= 8) & 0xFF) + "." + ((addr >>>= 8) & 0xFF)) ;
	}

	public static String getSnapshotFileName() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US) ;
		String currentDateandTime = sdf.format(new Date()) ;

		return currentDateandTime + ".jpg" ;
	}

	public static String getMJpegFileName() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US) ;
		String currentDateandTime = sdf.format(new Date()) ;

		return currentDateandTime ;
	}
	public static String sAppName = "" ;

	public static String sAppDir = "" ;

	public static File getAppDir() {
		File appDir = new File(sAppDir) ;
		if (!appDir.exists()) {
			appDir.mkdirs() ;
		}
		return appDir ;
	}

	public static Uri addImageAsApplication(ContentResolver contentResolver, String name, long dateTaken,
											String directory, String filename) {

		String filePath = directory + File.separator + filename ;

		String[] imageProjection = new String[] { "DISTINCT " + BaseColumns._ID, MediaColumns.DATA,
				MediaColumns.DISPLAY_NAME } ;

		String imageSelection = new String(Images.Media.TITLE + "=? AND " + Images.Media.DISPLAY_NAME + "=?") ;

		String[] imageSelectionArgs = new String[] { name, filename } ;

		Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection,
				imageSelection, imageSelectionArgs, null) ;

		if (cursor == null || cursor.getCount() == 0) {

			ContentValues values = new ContentValues(7) ;
			values.put(Images.Media.TITLE, name) ;
			values.put(Images.Media.DISPLAY_NAME, filename) ;
			values.put(Images.Media.DATE_TAKEN, dateTaken) ;
			values.put(Images.Media.MIME_TYPE, "image/jpeg") ;
			values.put(Images.Media.DATA, filePath) ;

			return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ;
		} else {

			int idColumn = cursor.getColumnIndex(MediaColumns._ID) ;

			if (idColumn == -1)
				return null ;

			cursor.moveToFirst() ;

			Long id = cursor.getLong(idColumn) ;

			return Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/"
					+ String.valueOf(id)) ;
		}
	}

	public static void addFragment(Fragment originalFragment, Fragment newFragment) {

		FragmentManager fragmentManager = originalFragment.getActivity().getFragmentManager() ;

		if (fragmentManager.getBackStackEntryCount() > 0) {

			FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(fragmentManager
					.getBackStackEntryCount() - 1) ;

			if (backEntry != null && backEntry.getName().equals(newFragment.getClass().getName()))
				return ;
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

		fragmentTransaction
				//.setCustomAnimations(R.anim.right_in, R.anim.right_out, R.anim.left_in, R.anim.left_out)
				.replace(R.id.mainMainFragmentLayout, newFragment)
				.addToBackStack(newFragment.getClass().getName()).commit() ;
		fragmentManager.executePendingTransactions() ;
	}
	public static void backToFristFragment(Activity activity) {
		FragmentManager fragmentManager = activity.getFragmentManager() ;
		if (fragmentManager.getBackStackEntryCount() == 0)
			return ;

		BackStackEntry rootEntry = fragmentManager.getBackStackEntryAt(0) ;

		if (rootEntry == null)
			return ;

		int rootFragment = rootEntry.getId() ;
		fragmentManager.popBackStack(rootFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE) ;

	}

	public static void backToPreFragment() {
		if(s_Activity == null)
		{
			return;
		}
		FragmentManager fragmentManager = s_Activity.getFragmentManager() ;
		int stack_len = fragmentManager.getBackStackEntryCount();
		if (stack_len == 0)
			return ;

		BackStackEntry preEntry = fragmentManager.getBackStackEntryAt(stack_len-1) ;

		if (preEntry == null)
			return ;
		Log.d(TAG,"preEntry.getName()" + preEntry.getName());
		if(preEntry.getName().contains("StreamPlayerFragment"))
		{
			s_Activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		int rootFragment = preEntry.getId() ;
		fragmentManager.popBackStack(rootFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE) ;

	}

	public boolean mEngineerMode = false ;
	private static Locale sDefaultLocale ;

	private static Locale sSelectedLocale ;

	static {
		sDefaultLocale = Locale.getDefault() ;
	}

	public static Locale getDefaultLocale() {

		return sDefaultLocale ;
	}

	public static void setAppLocale(Locale locale) {

		Locale.setDefault(locale) ;
		sSelectedLocale = locale ;
	}
	public static Locale getAppLocale() {

		return sSelectedLocale == null ? sDefaultLocale : sSelectedLocale ;
	}
	private CustomDialog mdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i("Fragment Activity", "ON CREATE " + savedInstanceState) ;
		Display mDisplay = getWindowManager().getDefaultDisplay();
		if(ViewPagerDemoActivity.instance!=null)
		{
			ViewPagerDemoActivity.instance.finish();
		}
        /*发送同步时间指令*/
//		URL url = CameraCommand.commandCameraTimeSettingsUrl() ;
//		CameraCommand cameraCommand=new CameraCommand();
//		if (url != null) {
//			new CameraCommand.SendRequest().execute(url) ;
//		}

		System.setProperty("http.keepAlive", "false") ;
		super.onCreate(savedInstanceState) ;
		mWifiManager = (WifiManager)this
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;  //added by eric
		if (sSelectedLocale == null) {

			sSelectedLocale = sDefaultLocale ;
		}
		Locale.setDefault(Locale.ENGLISH) ;
		Configuration config = new Configuration() ;
		config.locale = Locale.ENGLISH ;
		getResources().updateConfiguration(config, null) ;

		sAppName = getResources().getString(R.string.app_name) ;

		Locale.setDefault(sSelectedLocale) ;
		config = new Configuration() ;
		config.locale = sSelectedLocale ;
		getResources().updateConfiguration(config, null) ;



		sAppDir = Environment.getExternalStorageDirectory().getPath() + File.separator + sAppName ;
		Log.i("Fragment Activity", sAppDir) ;
		File appDir = new File(sAppDir) ;
		if (!appDir.exists()) {
			appDir.mkdirs() ;
		}

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ;

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS) ;

		setContentView(R.layout.activity_main) ;

		setTitle(getResources().getString(R.string.app_name)) ;

		//getActionBar().setDisplayHomeAsUpEnabled(false) ;

		setProgressBarIndeterminateVisibility(false) ;

		if (savedInstanceState == null) {

			WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(
					Context.WIFI_SERVICE) ;

			Log.i("Wifi Info", wifiManager.getConnectionInfo().toString()) ;

			if (wifiManager.isWifiEnabled() && wifiManager.getConnectionInfo().getNetworkId() != -1) {

				FragmentManager fragmentManager = getFragmentManager() ;
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

				Fragment fragment = new FunctionListFragment() ;
				fragmentTransaction.add(R.id.mainMainFragmentLayout, fragment) ;
				fragmentTransaction.commit() ;

			} else {
				LayoutInflater layoutInflater = LayoutInflater.from(this);
				View myDialogView = layoutInflater.inflate(R.layout.dialog_setting_connet, null);
				LinearLayout btn_open_wifi=(LinearLayout)myDialogView.findViewById(R.id.open_wifi);
				btn_open_wifi.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(android.os.Build.VERSION.SDK_INT > 10) {
							startActivity(new Intent( android.provider.Settings.ACTION_SETTINGS));
						} else {
							startActivity(new Intent( android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					}
				});
				//String title = getResources().getString(R.string.dialog_no_connection_title) ;
				//String message = getResources().getString(R.string.dialog_no_connection_message) ;
				if(mdialog!=null)
				{
					mdialog.dismiss();
				}
				FragmentManager fragmentManager = getFragmentManager() ;
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

				Fragment fragment_bg = new ConnectConfigBg() ;
				fragmentTransaction.add(R.id.mainMainFragmentLayout, fragment_bg) ;
				fragmentTransaction.commit() ;

				mdialog = new CustomDialog.Builder(this)
						.setTitle(R.string.dialog_no_connection_message_title)
						.setContentView(myDialogView)
						.setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								// TODO Auto-generated method stub
								dialog.dismiss();

								FragmentManager fragmentManager = getFragmentManager() ;
								FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

								Fragment fragment = new FunctionListFragment() ;
								fragmentTransaction.replace(R.id.mainMainFragmentLayout, fragment);
								//fragmentTransaction.add(R.id.mainMainFragmentLayout, fragment) ;
								fragmentTransaction.commit() ;
							}
						})
						.create();
				mdialog.setCancelable(false);
				mdialog.show();
						/*
						.setPositiveButton(getResources().getString(R.string.label_ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										// MainActivity.this.finish() ;
										dialog.dismiss() ;

										FragmentManager fragmentManager = getFragmentManager() ;
										FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

										Fragment fragment = new FunctionListFragment() ;
										fragmentTransaction.add(R.id.mainMainFragmentLayout, fragment) ;
										fragmentTransaction.commit() ;

									}
								}).show() ;*/

			}
		}
		s_Activity = this;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int v = pref.getInt(g_version_check,G_UNKNOWN_VERSION);
//		if(v ==G_UNKNOWN_VERSION)
		if(true)
		{
			Log.i("moop","请求版本信息");
			new CameraFWVersion().execute();
			new GetRTPS_AV1().execute();
		}
	}
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
					MainActivity.this.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
			if (dhcpInfo == null || dhcpInfo.gateway == 0) {
				AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create() ;
				alertDialog.setTitle(getResources().getString(R.string.dialog_DHCP_error)) ;
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
						getResources().getString(R.string.label_ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss() ;
							}
						}) ;
//				alertDialog.show() ;
				return;
			}
			String gateway = MainActivity.intToIp(dhcpInfo.gateway) ;
			// set http push as default for streaming
			liveStreamUrl = "http://" + gateway + MjpegPlayerFragment.DEFAULT_MJPEG_PUSH_URL ;
//			liveStreamUrl = null;
			if (result != null) {
				String[] lines;
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(s_Activity);
				SharedPreferences.Editor editor = pref.edit();
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
					editor.putString("liveStreamUrl", liveStreamUrl);
					editor.commit();
				} catch (Exception e) {/* not match, for firmware of MJPEG only */}
			}
			else {
				Log.i("moop", "没有拿到视频属性");
			}

//			Intent intent = new Intent(getActivity(), StreamPlayerActivity.class) ;
//			intent.putExtra("KEY_MEDIA_URL",liveStreamUrl);
//			startActivity(intent) ;
			super.onPostExecute(result) ;
		}
	}
	SubMenu mLanguageSubMenu ;
	String[] mLanguageNames ;

	Locale[] mLocales ;
	SubMenu mNetworkCacheSizeMenu ;

	int[] mCacheSize ;
	SubMenu mConnectionDelayMenu ;
	int[] mConnectionDelay ;

	public static int sConnectionDelay = 1500 ;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		mLanguageNames = new String[] { getResources().getString(R.string.label_default),
				"English",
				getResources().getString(R.string.label_language_TChinese),
				getResources().getString(R.string.label_language_SChinese)} ;

		mLocales = new Locale[] { MainActivity.getDefaultLocale(), Locale.ENGLISH,
				Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE} ;

		//getMenuInflater().inflate(R.menu.main, menu) ;

		mLanguageSubMenu = menu.addSubMenu(0, 0, 0, getResources().getString(R.string.label_language)) ;

		int i = 0 ;
		for (String language : mLanguageNames) {

			MenuItem item = mLanguageSubMenu.add(0, i++, 0, language) ;
			item.setCheckable(true) ;

		}
/*
		mNetworkCacheSizeMenu = menu.addSubMenu(0, 0, 0, "Cache Size") ;
		mCacheSize = new int[] {100, 1000, 1500, 2000, 2500} ;
*/
//		int cacheSize = 100 ;
//        Context context = VLCApplication.getAppContext();
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

		//set value of cache size to VLC settings.
//        SharedPreferences.Editor editor = pref.edit() ;
//        editor.putInt("network_caching_value", 2000) ;//set default value of cache size with 100ms.
//        editor.commit() ;

        /*
         * i = 0 ;
		for (int cacheSize : mCacheSize) {

			MenuItem item = mNetworkCacheSizeMenu.add(0, i++, 0, String.valueOf(cacheSize)) ;
			item.setCheckable(true) ;
		}
		*/
        /*
		mConnectionDelayMenu = menu.addSubMenu(0, 0, 0, "Connection Delay") ;
		mConnectionDelay = new int[] {500, 1000, 1500, 2000, 2500, 3000, 3500, 4000} ;

		i = 0 ;
		for (int connectionDelay : mConnectionDelay) {

			MenuItem item = mConnectionDelayMenu.add(0, i++, 0, String.valueOf(connectionDelay)) ;
			item.setCheckable(true) ;
		}
*/

		return true ;
	}
	public Handler mRecordStatusHandler = new Handler() {
		public void handleMessage(Message msg){

			if (mRecordmode.equals("Videomode"))
			{
				if(!mRecordStatus.equals("Recording"))
				{
					CustomDialog alertDialog = new CustomDialog.Builder(s_Activity)
							.setTitle(getResources().getString(R.string.trip))
							.setMessage(R.string.exit_trip)
							.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
									new CameraVideoRecord().execute();
									Log.i("moop", "481");
									finish();
								}
							})
							.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss() ;
									disconnectWifi();///added by eric for disconnect wifi
									finish();
								}
							}).create();
					alertDialog.show();
					super.handleMessage(msg);
					return;
				}
				else
				{
					disconnectWifi();///added by eric for disconnect wifi
				}
			}
			finish();
			super.handleMessage(msg);
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
			if(mRecordSenderStatus == STATUS_RECORD_BACKKEY)
			{
				mRecordStatusHandler.sendMessage(mRecordStatusHandler.obtainMessage());
			}
			else if(mRecordSenderStatus == STATUS_RECORD_PAUSE)
			{
				if (mRecordmode.equals("Videomode"))
				{
					if(mRecordStatus.equals("Recording"))
					{
						new CameraVideoRecord().execute();
						Log.i("moop", "554");
					}
				}
			}
			super.onPostExecute(result) ;

		}
	}
	private class CameraVideoRecord extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandCameraRecordUrl() ;
			Log.i("moop","MainAvtivity-568");
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			if (STATUS_RECORD_BACKKEY == mRecordSenderStatus)
			{
				disconnectWifi();///added by eric for disconnect wifi
			}

			super.onPostExecute(result) ;

		}
	}
	private class CameraFWVersion extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commanRecordStatusCustomerUrl() ;
			if (url != null) {
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			if (result != null)
			{
				String[] lines;
				String[] lines_temp = result.split("Camera.Menu.DefaultValue.FWversion=");
				if(lines_temp!=null && lines_temp.length>1)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					{
						String fwVersion = lines[0];
						Log.i("moop","版本="+fwVersion);
						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(s_Activity);
						SharedPreferences.Editor editor = pref.edit();
						int value = G_UNKNOWN_VERSION;
						if(fwVersion.startsWith("1"))
						{
							Log.i("moop","版本1");
							value = G_OLD_VERSION;
						}
						else if(fwVersion.startsWith("2"))
						{
							Log.i("moop","版本2");
							value = G_NEW_VERSION;
//							value = 3;
						}else if (fwVersion.startsWith("3")){
							Log.i("moop","版本3");
							value = 3;
						}
						editor.putInt(g_version_check, value);
						editor.commit();
					}
				}

			}

			super.onPostExecute(result) ;

		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if((keyCode == KeyEvent.KEYCODE_BACK) && getFragmentManager().getBackStackEntryCount()==0)
		{
			mRecordSenderStatus = STATUS_RECORD_BACKKEY;
			new GetRecordStatus().execute();
			Log.d("moop", "onKeyDown");
			//disconnectWifi();///added by eric for disconnect wifi
			Log.d(TAG,"onDestoryonDestory");///added by eric for disconnect wifi
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK && getFragmentManager().getBackStackEntryCount()>0)
		{
			backToPreFragment();
			Log.d(TAG,"KeyEvent.KEYCODE_BACK");///added by eric for disconnect wifi
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (mLanguageSubMenu != null) {

			int size = mLanguageSubMenu.size() ;

			for (int i = 0 ; i < size ; i++) {
				MenuItem item = mLanguageSubMenu.getItem(i) ;

				if (i > 0 && getAppLocale().equals(mLocales[i])) {
					item.setChecked(true) ;
				} else {
					item.setChecked(false) ;
				}

				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem checkedItem) {

						int size = mLanguageSubMenu.size() ;

						for (int i = 0 ; i < size ; i++) {
							MenuItem item = mLanguageSubMenu.getItem(i) ;
							if (checkedItem == item && item.isChecked() == false) {
								item.setChecked(true) ;

								setAppLocale(mLocales[i]) ;

								Intent intent = getIntent() ;
								finish() ;
								startActivity(intent) ;
							} else {
								item.setChecked(false) ;
							}
						}
						return true ;
					}
				}) ;
			}
		}
/*		
		if (mNetworkCacheSizeMenu != null) {

			int size = mNetworkCacheSizeMenu.size() ;
			//for JWD request: no delay so set cache size with 100ms.It didnot work with 0ms.
			int cacheSize = 100 ;
            Context context = VLCApplication.getAppContext();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            
            //set value of cache size to VLC settings.
            SharedPreferences.Editor editor = pref.edit() ;
            editor.putInt("network_caching_value", 100) ;//set default value of cache size with 100ms.
            editor.commit() ;

			for (int i = 0 ; i < size ; i++) {
				MenuItem item = mNetworkCacheSizeMenu.getItem(i) ;

				if (mCacheSize[i] == cacheSize) {
					item.setChecked(true) ;
				} else {
					item.setChecked(false) ;
				}

				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem checkedItem) {

						int size = mNetworkCacheSizeMenu.size() ;

						for (int i = 0 ; i < size ; i++) {
							MenuItem item = mNetworkCacheSizeMenu.getItem(i) ;
							if (checkedItem == item && item.isChecked() == false) {
								item.setChecked(true) ;

					            Context context = VLCApplication.getAppContext();
					            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

					            SharedPreferences.Editor editor = pref.edit() ;
					            editor.putInt("network_caching_value", mCacheSize[i]) ;
					            editor.commit() ;

							} else {
								item.setChecked(false) ;
							}
						}
						return true ;
					}
				}) ;
			}
		}
*/		
/*		
		if (mConnectionDelayMenu != null) {

			int size = mConnectionDelayMenu.size() ;

			int connectionDelay = sConnectionDelay ;

			for (int i = 0 ; i < size ; i++) {
				MenuItem item = mConnectionDelayMenu.getItem(i) ;

				if (mConnectionDelay[i] == connectionDelay) {
					item.setChecked(true) ;
				} else {
					item.setChecked(false) ;
				}

				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem checkedItem) {

						int size = mConnectionDelayMenu.size() ;

						for (int i = 0 ; i < size ; i++) {
							MenuItem item = mConnectionDelayMenu.getItem(i) ;
							if (checkedItem == item && item.isChecked() == false) {
								item.setChecked(true) ;
								sConnectionDelay = mConnectionDelay[i] ;

							} else {
								item.setChecked(false) ;
							}
						}
						return true ;
					}
				}) ;
			}
		}
*/

		//menu.findItem(R.id.engineerMode).setChecked(mEngineerMode) ;
		//menu.findItem(R.id.engineerMode).setOnMenuItemClickListener(new OnMenuItemClickListener() {

		//@Override
		//public boolean onMenuItemClick(MenuItem item) {
		//item.setChecked(!item.isChecked()) ;
		//mEngineerMode = item.isChecked() ;
		//return true ;
		//}
		//}) ;

		return super.onPrepareOptionsMenu(menu) ;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//

				// NavUtils.navigateUpFromSameTask(this) ;

				backToFristFragment(this) ;
				return true ;
		}
		return super.onOptionsItemSelected(item) ;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}

	private Handler mTimerHandler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				case MSG_STARTRECORD:
					mRecordSenderStatus = STATUS_RECORD_PAUSE;
					new GetRecordStatus().execute();
					Log.i("moop","MSG_STARTRECORD");
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onPause()
	{
		Log.d(TAG,"on pause");
		//mTimerHandler.sendMessageDelayed(mTimerHandler.obtainMessage(MSG_STARTRECORD),DELAY_TIME);
		super.onPause();
	}

	public void onResume()
	{
		mTimerHandler.removeMessages(MSG_STARTRECORD);
		super.onResume();
	}
	public void onDestory()
	{
		mTimerHandler.removeMessages(MSG_STARTRECORD);
		super.onDestroy();
	}
	private int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	private void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}
}
