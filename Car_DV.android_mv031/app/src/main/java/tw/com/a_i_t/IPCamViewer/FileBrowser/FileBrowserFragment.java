package tw.com.a_i_t.IPCamViewer.FileBrowser ;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.net.HttpURLConnection ;
import java.net.MalformedURLException ;
import java.net.URL ;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList ;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Locale ;

import tw.com.a_i_t.IPCamViewer.CameraCommand ;
import tw.com.a_i_t.IPCamViewer.CustomDialog;
import tw.com.a_i_t.IPCamViewer.MainActivity ;
import tw.com.a_i_t.IPCamViewer.R ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode.Format;
import tw.com.a_i_t.IPCamViewer.CameraCommand;
import tw.com.a_i_t.IPCamViewer.RTPullListView;

import android.app.Activity ;
import android.app.AlertDialog ;
import android.app.Fragment ;
import android.app.Notification ;
import android.app.NotificationManager ;
import android.app.PendingIntent ;
import android.app.ProgressDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.net.DhcpInfo ;
import android.net.Uri ;
import android.net.wifi.WifiManager ;
import android.net.wifi.WifiManager.WifiLock ;
import android.os.AsyncTask ;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager ;
import android.util.Log ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.view.ViewGroup ;
import android.webkit.MimeTypeMap ;
import android.widget.AdapterView ;
import android.widget.AdapterView.OnItemClickListener ;
import android.widget.Button ;
import android.widget.CheckedTextView ;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView ;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView ;
import android.widget.Toast;

public class FileBrowserFragment extends Fragment {

	public final static String TAG = "FileBrowserFragment" ;
	public static final String DEFAULT_PATH = "/cgi-bin/Config.cgi" ;
	public static final String DEFAULT_DIR = "DCIM" ;
    public static String mRecordStatus="";
    public static String mRecordmode="";
	private ProgressDialog mProgDlg;
	private int	mTotalFile;
    ProgressDialog mProgressDialog =null;
	private boolean mCancelDelete;
	private static final int MSG_SHOWDIALOG = 1;
	private static final int MSG_DISMISSDIALOG = 2;
	private static final int MSG_GETFILELIST = 3;
	private static final int G_TRYMAXTIMES = 5;
	private static final int G_TRYTIME = 5000;  //5s
	private int mTryTimes = G_TRYMAXTIMES;
	private FileNode.Format m_fileformat = FileNode.Format.all;
	private static final int FILEREADBLOCK_SIZE= 1024 * 1000;
	private static final int BUTTON_NONE = 0;
	private static final int BUTTON_DOWNLOAD = 1;
	private static final int BUTTON_OPEN = 2;
	private int mCurrentButton = BUTTON_NONE;

	private static final int LOAD_MORE_SUCCESS = 3;//点击加载更多成功
	private static final int LOAD_NEW_INFO = 5;    //加载新信息
	private ProgressBar moreProgressBar;
	FileBrowser rowsers=null;
	private boolean isstatusVideo; //判断是照片还是视频加载更多
	private boolean isstatusPhoto=false; //判断是照片还是视频加载更多
	private boolean isfirstPhoto=true;//判断第一次点击照片选项

	private int mycompare(FileNode arg0, FileNode arg1)
	{ 
		try
		{
			Date  d0= DateFormat.getDateTimeInstance().parse(arg0.mTime);
			Date  d1= DateFormat.getDateTimeInstance().parse(arg1.mTime);
			long ret = d0.compareTo(d1);
			if( ret < 0)
			{
				return 1;
			}
			else if(ret >0)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
		catch(ParseException e)
		{
			return -1;
		}
	}
	/*排序文件*/
	private void sortfile()
	{
		sFileListVIDEO.clear();
		sFileListJPG.clear();
		int len = stackFileListVIDEO.size();
		int len_pic = stackFileListJPG.size();
		if(len>0)
		{
//			for(int i=len-1;i>=0;i--)//倒序
			for(int i=0;i<len;i++)
			{
				sFileListVIDEO.add(stackFileListVIDEO.get(i));
			}
		}
		
		if(len_pic>0)
		{
//			for(int i=len_pic-1;i>=0;i--) //倒序
			for(int i=0;i<len_pic;i++)
			{
				sFileListJPG.add(stackFileListJPG.get(i));
			}
		}
		/*
		Collections.sort(sFileListJPG,new Comparator<FileNode>(){
			@Override
			public int compare(FileNode arg0, FileNode arg1) {
				// TODO Auto-generated method stub
				//Log.d("LocalFileBrowserFragment",arg0. + "  - "+ arg1.mModifiedTime);
				return mycompare(arg0,arg1);
			}}
			);
		Collections.sort(sFileListVIDEO,new Comparator<FileNode>(){
			@Override
			public int compare(FileNode arg0, FileNode arg1) {
				// TODO Auto-generated method stub
				//Log.d("LocalFileBrowserFragment",arg0. + "  - "+ arg1.mModifiedTime);
				return mycompare(arg0,arg1);
			}}
			);
			*/
	}
	///added by eric for stop record when downloading
	//下载时添加由eric停止记录
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
			super.onPostExecute(result) ;

		}
	}
	//重新得到记录状态
	private class BackGetRecordStatus extends AsyncTask<URL, Integer, String> {
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
					new CameraVideoRecord().execute();
				}
			}
			
			super.onPostExecute(result) ;

		}
	}
	//得到记录状态
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

			if (mRecordmode.equals("Videomode"))
			{
				if(mRecordStatus.equals("Recording"))
				{
					mRecordStatusHandler.sendMessage(mRecordStatusHandler.obtainMessage());
				}
				else
				{
					if(BUTTON_DOWNLOAD == mCurrentButton)
					{
						downloadFile(getActivity(), mIp) ;
					}
					else if (BUTTON_OPEN == mCurrentButton)
					{
						///modify by eric
						FileNode fileNode = sSelectedFiles.get(0);
						if (fileNode.mFormat == Format.mov
							||fileNode.mFormat == Format.mp4
							||fileNode.mFormat == Format.avi) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							// CarDV WiFi Support Video container is 3GP (.MOV) 
							// For HTTP File Streaming 
							intent.setDataAndType(Uri.parse("http://" + mIp + fileNode.mName), "video/3gp") ;
							startActivity(intent);

							// For HTML5 Video Streaming 
//							String filename = fileNode.mName.replaceAll("/", "\\$");
//							Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//										Uri.parse("http://"+mIp+"/cgi-bin/Config.cgi?action=play&property=" + filename));
//							startActivity(browserIntent);
//							mSaveButton.setEnabled(false) ;
//							mDeleteButton.setEnabled(false) ;
//							mOpenButton.setEnabled(false) ;
						}
						else if(fileNode.mFormat ==Format.jpeg)
						{
							Intent intent = new Intent(Intent.ACTION_VIEW);
							// CarDV WiFi Support Video container is 3GP (.MOV) 
							// For HTTP File Streaming 
							intent.setDataAndType(Uri.parse("http://" + mIp + fileNode.mName), "image/jpeg") ;
							startActivity(intent);
						}
					}
				}
			}
			
			super.onPostExecute(result) ;

		}
	}
	/*继续下载任务*/
	private class ContiunedDownloadTask extends AsyncTask<FileBrowser, Integer, FileBrowser> {

		@Override
		protected FileBrowser doInBackground(FileBrowser... browsers) {
			browsers[0].retrieveFileList(mDirectory, m_fileformat, false) ;
			return browsers[0] ;
		}

		@Override
		protected void onPostExecute(FileBrowser result) {

			Activity activity = getActivity() ;
			Log.d(TAG,"onPostExecute()");
			if (activity == null)
			{
				Log.d(TAG,"onPostExecute() activity == null");
				return ;
			}
			if (activity != null) {
				List<FileNode> fileList = result.getFileList() ;
				for(int i=0;i<fileList.size();i++)
				{
					if(fileList.get(i).mFormat == FileNode.Format.avi
							||fileList.get(i).mFormat == FileNode.Format.mov
							||fileList.get(i).mFormat == FileNode.Format.mp4)
					{
						//sFileListVIDEO.add(fileList.get(i));
						stackFileListVIDEO.add(fileList.get(i));
					}
					else if(fileList.get(i).mFormat ==FileNode.Format.jpeg)
					{
						stackFileListJPG.add(fileList.get(i));
					}
				}
				sortfile();
				mFileListJPGAdapter.notifyDataSetChanged();
				mFileListVIDEOAdapter.notifyDataSetChanged();
				//mFileListView.setSelection(0);
				myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
				if (isstatusVideo){
					Log.i("moop","视频请求一次");
					isstatusVideo=false;
					mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
				}
				if(isstatusPhoto)
				  {
					  isstatusPhoto=true;
					  if (!result.isCompleted() && sFileListJPG.size() <6) {
							new ContiunedDownloadTask().execute(rowsers);
						Log.i("moop","照片请求——————");
				}else {
						  Log.i("moop","结束照片请求");
						  isstatusPhoto=false;
				   	 mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
				}
				}
				//判断
				if (!result.isCompleted() && fileList.size() != 0) {

				} else {
					mFileListView.setSelection(0);//返回diy
					Toast.makeText(getActivity(), "没有更多了",
							Toast.LENGTH_SHORT).show();

					//mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
					setWaitingState(false) ;
				}
				Log.i("moop","fileList.size="+fileList.size());
				Log.i("moop","sFileListJPG.size="+sFileListJPG.size());
				Log.i("moop","sFileListVIDEO.size="+sFileListVIDEO.size());
				Log.i("moop","stackFileListVIDEO.size="+stackFileListVIDEO.size());
				Log.i("moop","stackFileListJPG.size="+stackFileListJPG.size());

			}
		}
	}
	/*下载文件列表的任务*/
	private class DownloadFileListTask extends AsyncTask<FileBrowser, Integer, FileBrowser> {

		@Override
		protected void onPreExecute() {
			setWaitingState(true) ;
			sFileListJPG.clear();
			sFileListVIDEO.clear();
			stackFileListJPG.clear();
			stackFileListVIDEO.clear();
			mFileListJPGAdapter.notifyDataSetChanged();
			mFileListVIDEOAdapter.notifyDataSetChanged();
			sSelectedFiles.clear() ;
			mSaveButton.setEnabled(false) ;
			mOpenButton.setEnabled(false) ;
			mDeleteButton.setEnabled(false) ;
			//mFileListTitle.setText(mFileBrowser + " : " + mReading + " " + mDirectory) ;
			super.onPreExecute() ;
		}

		@Override
		protected FileBrowser doInBackground(FileBrowser... browsers) {
			//发送第一次请求
			browsers[0].retrieveFileList(mDirectory, m_fileformat, true) ;
			return browsers[0] ;
		}
		@Override
		protected void onPostExecute(FileBrowser result) {
			Activity activity = getActivity() ;
			if (activity != null) {
				List<FileNode> fileList = result.getFileList() ;
				if(!result.mIsError)
				{
					Log.d(TAG,"onPostExecute() !IsError");
					mTryTimes = G_TRYMAXTIMES;
					//mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
				}
				else if(fileList.size()>0)
				{
					Log.d(TAG,"onPostExecute() size()>0");
					mTryTimes = G_TRYMAXTIMES;
				//	mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
				}
				else
				{
					//try again!
					Log.d(TAG,"error! try again");
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_GETFILELIST),G_TRYTIME);
					
				}
				for(int i=0;i<fileList.size();i++)
				{
					if(fileList.get(i).mFormat == FileNode.Format.avi
						||fileList.get(i).mFormat == FileNode.Format.mov
						||fileList.get(i).mFormat == FileNode.Format.mp4)
					{
						//sFileListVIDEO.add(fileList.get(i));
						stackFileListVIDEO.add(fileList.get(i));
					}
					else if(fileList.get(i).mFormat ==FileNode.Format.jpeg)
					{
						stackFileListJPG.add(fileList.get(i));
					}
				}
				sortfile();
				mFileListJPGAdapter.notifyDataSetChanged();
				mFileListVIDEOAdapter.notifyDataSetChanged();
//				mFileListView.setSelection(0);
				//mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
				Log.i("moop","fileList.size="+fileList.size());
				Log.i("moop","sFileListJPG.size="+sFileListJPG.size());
				Log.i("moop","sFileListVIDEO.size="+sFileListVIDEO.size());
				Log.i("moop","stackFileListVIDEO.size="+stackFileListVIDEO.size());
				Log.i("moop","stackFileListJPG.size="+stackFileListJPG.size());
				if (!result.isCompleted() && sFileListVIDEO.size() < 6 ) {
					new ContiunedDownloadTask().execute(result);
					mHandler.sendEmptyMessage(MSG_SHOWDIALOG);
					Log.i("moop", "第一次，视频不足六个，继续请求" );
				}
				if (!result.isCompleted() && fileList.size() > 0) {
					rowsers=result;
					mFileListVIDEOAdapter.notifyDataSetChanged();
					mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
					//mFileListTitle.setText(mFileBrowser + " : " + mReading + " " + mDirectory + " ("
					//		+ sFileList.size() + " " + mItems + ")") ;

					//new ContiunedDownloadTask().execute(result) ;
				} else {
					//mFileListTitle.setText(mFileBrowser + " : " + mDirectory + " (" + sFileList.size() + " "
					//		+ mItems + ")") ;
					mHandler.sendEmptyMessage(MSG_DISMISSDIALOG);
					setWaitingState(false) ;
				}
			}
		}

	}

	private String mIp ;
	private String mPath ;
	private String mDirectory ;
	private static final String KEY_IP = "ip" ;
	private static final String KEY_PATH = "path" ;
	private static final String KEY_DIRECTORY = "directory" ;
	private static ArrayList<FileNode> sFileListJPG = new ArrayList<FileNode>() ;
	private static ArrayList<FileNode> sFileListVIDEO = new ArrayList<FileNode>() ;
	private static ArrayList<FileNode> stackFileListVIDEO = new ArrayList<FileNode>();//堆栈视频文件列表
	private static ArrayList<FileNode> stackFileListJPG = new ArrayList<FileNode>();//堆栈图片文件列表
	private static List<FileNode> sSelectedFiles = new LinkedList<FileNode>() ;
	private static FileListAdapter mFileListJPGAdapter ;
	private static FileListAdapter mFileListVIDEOAdapter ;
	private RTPullListView mFileListView,jpgFileListview ;
	//private TextView mFileListTitle ;
	private LinearLayout mSaveButton ;
	private LinearLayout mDeleteButton ;
	private LinearLayout mOpenButton ;
	private String mFileBrowser ;
	private String mReading ;
	private String mItems ;
	private LinearLayout btn_video;
	private LinearLayout btn_photo;
	private Boolean isvideo = true;	/*video is default*/
	private static String gDownloadStr;
	private static String gpleasewait;
	private static String gcancel;
	
	///added by eric for show stop recording dialog

	public Handler mRecordStatusHandler = new Handler() {
		public void handleMessage(Message msg){
			
			if (mRecordmode.equals("Videomode"))
			{
				if(mRecordStatus.equals("Recording"))
				{
					CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
					.setTitle(getResources().getString(R.string.trip))
					.setMessage(R.string.sd_browser_stoprecord)
					.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.dismiss();
							new CameraVideoRecord().execute();
							
							if(BUTTON_DOWNLOAD == mCurrentButton)
							{
								downloadFile(getActivity(), mIp) ;
							}
							else if (BUTTON_OPEN == mCurrentButton)
							{
								///modify by eric
								FileNode fileNode = sSelectedFiles.get(0);
								if (fileNode.mFormat == Format.mov
									||fileNode.mFormat == Format.mp4
									||fileNode.mFormat == Format.avi) {
									Intent intent = new Intent(Intent.ACTION_VIEW);
									// CarDV WiFi Support Video container is 3GP (.MOV) 
									// For HTTP File Streaming 
									intent.setDataAndType(Uri.parse("http://" + mIp + fileNode.mName), "video/3gp") ;
									startActivity(intent);

									// For HTML5 Video Streaming 
//									String filename = fileNode.mName.replaceAll("/", "\\$");
//									Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//												Uri.parse("http://"+mIp+"/cgi-bin/Config.cgi?action=play&property=" + filename));
//									startActivity(browserIntent);
//									mSaveButton.setEnabled(false) ;
//									mDeleteButton.setEnabled(false) ;
//									mOpenButton.setEnabled(false) ;
								}
								else if(fileNode.mFormat ==Format.jpeg)
								{
									Intent intent = new Intent(Intent.ACTION_VIEW);
									// CarDV WiFi Support Video container is 3GP (.MOV) 
									// For HTTP File Streaming 
									intent.setDataAndType(Uri.parse("http://" + mIp + fileNode.mName), "image/jpeg") ;
									startActivity(intent);
								}
							}
						}
					})
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss() ;
							//disconnectWifi();///added by eric for disconnect wifi
							//finish();
						}
					}).create();
					alertDialog.show();
					super.handleMessage(msg);  
					return;
				}
				
			}
			//finish();
            super.handleMessage(msg);  
		}
	};
	
	public static FileBrowserFragment newInstance(String ip, String url, String directory) {

		FileBrowserFragment fragment = new FileBrowserFragment() ;

		Bundle args = new Bundle() ;

		if (ip != null)
			args.putString(KEY_IP, ip) ;

		if (url != null)
			args.putString(KEY_PATH, url) ;

		if (directory != null)
			args.putString(KEY_DIRECTORY, directory) ;

		fragment.setArguments(args) ;

		return fragment ;
	}

	private static int sNotificationCount = 0 ;
	private static DownloadTask sDownloadTask = null ;//卸载任务

	/*下载任务*/
	private static class DownloadTask extends AsyncTask<URL, Long, Boolean> {

		String mFileName ;
		Context mContext ;
		WifiLock mWifiLock ;
		String mIp ;
		boolean mCancelled ;
		PowerManager.WakeLock mWakeLock ;

		private ProgressDialog mProgressDialog ;

		DownloadTask(Context context) {

			mContext = context ;
		}

		@Override
		protected void onPreExecute() {

			Log.i("DownloadTask", "onPreExecute") ;

			WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE) ;
			mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "DownloadTask") ;
			mWifiLock.acquire() ;

			PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE) ;
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DownloadTask") ;
			mWakeLock.acquire() ;

			mCancelled = false ;
			showProgress(mContext) ;

			super.onPreExecute() ;
		}

		@Override
		protected Boolean doInBackground(URL... urls) {

			Log.i("DownloadTask", "doInBackground " + urls[0]) ;

			try {
				mIp = urls[0].getHost() ;

				HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection() ;
				urlConnection.setRequestMethod("GET") ;
				urlConnection.setConnectTimeout(3000) ;
				urlConnection.setReadTimeout(10000) ;
				urlConnection.setUseCaches(false) ;
				urlConnection.setDoInput(true) ;
				urlConnection.connect() ;
				InputStream inputStream = urlConnection.getInputStream() ;

				mFileName = urls[0].getFile().substring(urls[0].getFile().lastIndexOf(File.separator) + 1) ;
				File appDir = MainActivity.getAppDir() ;
				File file = new File(appDir, mFileName) ;

				Log.i("Path", appDir.getPath() + File.separator + mFileName) ;

				if (file.exists()) {
					file.delete() ;
				}

				file.createNewFile() ;
				FileOutputStream fileOutput = new FileOutputStream(file) ;

				byte[] buffer = new byte[FILEREADBLOCK_SIZE] ;
				int bufferLength = 0 ;
				try {
					Log.i(TAG,"begin download");
					while ((bufferLength = inputStream.read(buffer)) > 0) {
						publishProgress(Long.valueOf(urlConnection.getContentLength()), file.length()) ;
						fileOutput.write(buffer, 0, bufferLength) ;
						
						if (mCancelled) {
							urlConnection.disconnect();
							/*begin delete file when cancel download files*/
							try
							{
								fileOutput.close();
							}
							catch(IOException exio)
							{
								
							}
							file.delete();
							/*end delete file when cancel download files*/
							break ;
						}
					}
				} finally {
					//Log.i("DownloadTask", "doInBackground close START") ;
					//inputStream.close() ;
					//fileOutput.close() ;
					//Log.i("DownloadTask", "doInBackground disconnect START") ;
					//urlConnection.disconnect() ;
					//Log.i("DownloadTask", "doInBackground disconnect FINISHED") ;
					/*begin leak mem*/
					if(!mCancelled)
					{
						try
						{
							fileOutput.close();
						}
						catch(IOException exio)
						{
							
						}
					}
					/*end leak mem*/
				}
				if (mCancelled && file.exists()) {
					file.delete() ;
					return false ;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace() ;
				return false ;
			}

			return true ;
		}

		@Override
		protected void onProgressUpdate(Long... values) {

			if (mProgressDialog != null) {
				int max = values[0].intValue() ;
				int progress = values[1].intValue() ;
				String unit = "Bytes" ;
				mProgressDialog.setTitle(gDownloadStr + mFileName) ;
				
				if (max > 1024) {
					max /= 1024 ;
					progress /= 1024 ;
					unit = "KB" ;
				}
				
				if (max > 1024) {
					max /= 1024 ;
					progress /= 1024 ;
					unit = "MB" ;
				}
				mProgressDialog.setMax(max) ;
				mProgressDialog.setProgress(progress) ;
				mProgressDialog.setProgressNumberFormat("%1d/%2d " + unit) ;
			}
			super.onProgressUpdate(values) ;
		}

		private void cancelDownload() {

			mCancelled = true ;
			
			for (FileNode fileNode : sSelectedFiles) {
				fileNode.mSelected = false ;
			}
			sSelectedFiles.clear() ;
			mFileListJPGAdapter.notifyDataSetChanged();
			mFileListVIDEOAdapter.notifyDataSetChanged() ;
		}

		@Override
		protected void onCancelled() {

			Log.i("DownloadTask", "onCancelled") ;

			if (mProgressDialog != null) {
				mProgressDialog.dismiss() ;
				mProgressDialog = null ;
			}
			sDownloadTask = null ;

			mWakeLock.release() ;
			mWifiLock.release() ;

			super.onCancelled() ;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			Log.i("DownloadTask", "onPostExecute " + mFileName + " " + (mCancelled ? "CANCELLED"
					: result ? "SUCCESS" : "FAIL")) ;

			if (mProgressDialog != null) {
				mProgressDialog.dismiss() ;
				mProgressDialog = null ;
			}
			sDownloadTask = null ;

			mWakeLock.release() ;
			mWifiLock.release() ;

			if (mContext != null) {
				String ext = mFileName.substring(mFileName.lastIndexOf(".") + 1).toLowerCase(Locale.US) ;
				int	nIcon;
				if (ext.equalsIgnoreCase("jpg")) {
					nIcon = R.drawable.type_photo;
				} else {
					nIcon = R.drawable.type_video;
				}
				if (!result) {
					if (!mCancelled) {
						Notification notification = new Notification.Builder(mContext)
								.setContentTitle(mFileName).setSmallIcon(nIcon)
								.setContentText("Download Failed").getNotification() ;

						NotificationManager notificationManager = (NotificationManager) mContext
								.getSystemService(Context.NOTIFICATION_SERVICE) ;

						notification.flags |= Notification.FLAG_AUTO_CANCEL ;

						notificationManager.notify(sNotificationCount++, notification) ;
					} else {
						Notification notification = new Notification.Builder(mContext)
								.setContentTitle(mFileName).setSmallIcon(nIcon)
								.setContentText("Download Cancelled").getNotification() ;

						NotificationManager notificationManager = (NotificationManager) mContext
								.getSystemService(Context.NOTIFICATION_SERVICE) ;

						notification.flags |= Notification.FLAG_AUTO_CANCEL ;

						notificationManager.notify(sNotificationCount++, notification) ;

					}
				} else {

					Uri uri = Uri.parse("file://" + MainActivity.sAppDir + File.separator + mFileName) ;

					String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ;

					Log.i("MIME", ext + "  ==>  " + mimeType) ;

					Log.i("Path", uri.toString()) ;

					Intent intent = new Intent(Intent.ACTION_VIEW) ;
					intent.setDataAndType(uri, mimeType) ;

					PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0) ;
					Notification notification = new Notification.Builder(mContext).setContentTitle(mFileName)
							.setSmallIcon(nIcon).setContentText("Download Completed")
							.setContentIntent(pIntent).getNotification() ;

					NotificationManager notificationManager = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE) ;

					notification.flags |= Notification.FLAG_AUTO_CANCEL ;

					notificationManager.notify(sNotificationCount++, notification) ;
				}
			}
			if (!mCancelled)
				downloadFile(mContext, mIp) ;
			
			super.onPostExecute(result) ;
		}

		void showProgress(Context context) {
			mContext = context ;
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss() ;
			}
			mProgressDialog = new ProgressDialog(mContext) ;
			mProgressDialog.setTitle(R.string.downloading) ;
			mProgressDialog.setMessage(gpleasewait) ;
			mProgressDialog.setCancelable(false) ;
			mProgressDialog.setMax(100) ;
			mProgressDialog.setProgress(0) ;
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) ;
			mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, gcancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancelDownload() ;
							dialog.dismiss() ;
						}
					}) ;
			mProgressDialog.show() ;
		}

		void hideProgress() {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss() ;
				mProgressDialog = null ;
			}
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		mIp = getArguments().getString(KEY_IP) ;

		if (mIp == null) {
			WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE) ;

			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo() ;

			if (dhcpInfo != null && dhcpInfo.gateway != 0) {

				mIp = MainActivity.intToIp(dhcpInfo.gateway) ;
			}
		}
		gDownloadStr = getString(R.string.downloading);
		gpleasewait = getString(R.string.pleasewait);
		gcancel = getString(R.string.label_cancel);
		mPath = getArguments().getString(KEY_PATH) ;
		
		if (mPath == null) {
			mPath = DEFAULT_PATH ;
		}

		mDirectory = getArguments().getString(KEY_DIRECTORY) ;

		if (mDirectory == null) {

			mDirectory = DEFAULT_DIR ;
		}
//		
	}
/*下载文件*/
	private static void downloadFile(final Context context, String ip) {
		if (sSelectedFiles.size() == 0) {
			return ;
		}
		FileNode fileNode = sSelectedFiles.remove(0) ;
		fileNode.mSelected = false ;
		mFileListJPGAdapter.notifyDataSetChanged() ;
		mFileListVIDEOAdapter.notifyDataSetChanged() ;
		final String filename = fileNode.mName.substring(fileNode.mName.lastIndexOf("/") + 1) ;
		final String urlString = "http://" + ip + fileNode.mName ;

		File appDir = MainActivity.getAppDir() ;
		final File file = new File(appDir, filename) ;

		Log.i("Path", appDir.getPath() + File.separator + filename) ;

		if (file.exists()) {
			CustomDialog alertDialog = new CustomDialog.Builder(context)
			.setTitle(filename)
			.setMessage(R.string.message_overwrite_file)
			.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					file.delete() ;
					try {
						sDownloadTask = new DownloadTask(context) ;
						sDownloadTask.execute(new URL(urlString)) ;
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace() ;
					}
					dialog.dismiss() ;
				}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
				}
			}).create();
			alertDialog.show() ;

		} else {
			try {
				sDownloadTask = new DownloadTask(context) ;
				sDownloadTask.execute(new URL(urlString)) ;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace() ;
			}
		}
	}

	private class CameraDeleteFile extends AsyncTask<URL, Integer, String> {
		@Override
		protected void onPreExecute() {
			setWaitingState(true);
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			FileNode fileNode = sSelectedFiles.get(0) ;
			///added by eric
			if(fileNode.mName.contains("SOS"))
			{
				return "next" ;
			}
			URL url = CameraCommand.commandSetdeletesinglefileUrl(fileNode.mName);
			if (url != null) {
				return CameraCommand.sendRequest(url);
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			Activity activity = getActivity() ;
			FileNode fileNode = sSelectedFiles.remove(0);
			Log.d(TAG, "delete file response:"+result) ;
			if (result != null && result.equals("709\n?") != true) {	
				
				fileNode.mSelected = false ;
				sFileListJPG.remove(fileNode);
				sFileListVIDEO.remove(fileNode);
				if(result.equals("next"))
				{
					mFileListJPGAdapter.notifyDataSetChanged() ;
					mFileListVIDEOAdapter.notifyDataSetChanged();
					mProgDlg.setMessage("Can not delete " + fileNode.mName);
				}
				else
				{
					mFileListJPGAdapter.notifyDataSetChanged() ;
					mFileListVIDEOAdapter.notifyDataSetChanged();
					//mFileListTitle.setText(mFileBrowser + " : " + mDirectory + " (" + sFileList.size()
					//		+ " " + mItems + ")") ;
					mProgDlg.setMessage("Please wait, deleteing " + fileNode.mName);
					mProgDlg.setProgress(mTotalFile - sSelectedFiles.size()) ;
				}
				
				if (sSelectedFiles.size() > 0 && !mCancelDelete) {
					new CameraDeleteFile().execute();
				} else {
					if (mProgDlg != null) {
						mProgDlg.dismiss() ;
						mProgDlg= null ;
					}
					//mFileListTitle.setText(mFileBrowser + " : " + mDirectory + " (" + sFileList.size()
					//		+ " " + mItems + ")") ;
					setWaitingState(false);
				}
			}
			else if (activity != null) {
				Toast.makeText(activity,
						activity.getResources().getString(R.string.message_command_failed),
						Toast.LENGTH_SHORT).show();
				setWaitingState(false);
			}
			super.onPostExecute(result);
		}
	}
	
	DataSetObserver mDataSetobserver = new DataSetObserver() {
		   @Override   
		   public void onChanged()
		   {
			   mFileListJPGAdapter.notifyDataSetChanged();
			   mFileListVIDEOAdapter.notifyDataSetChanged();
			   super.onChanged();
		   }
		   @Override   
		   public void onInvalidated() 
		   {   // TODO Auto-generated method stub   super.onInvalidated();  
			   super.onInvalidated();
		   }
	};
	
	public void OnDestory()
	{
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mHandler.removeMessages(MSG_GETFILELIST);
		
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.browser, container, false) ;
		mFileListJPGAdapter = new FileListAdapter(inflater, sFileListJPG) ;
		mFileListVIDEOAdapter = new FileListAdapter(inflater, sFileListVIDEO) ;
		//mFileListAdapter.registerDataSetObserver(mDataSetobserver);
		mFileBrowser = getActivity().getResources().getString(R.string.label_file_browser) ;
		mReading = getActivity().getResources().getString(R.string.label_reading) ;
		mItems = getActivity().getResources().getString(R.string.label_items) ;

		//mFileListTitle = (TextView) view.findViewById(R.id.browserTitle) ;
		//mFileListTitle.setText(mFileBrowser + " : " + mDirectory) ;

		mSaveButton = (LinearLayout) view.findViewById(R.id.browserDownloadButton) ;
		mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				///downloadFile(getActivity(), mIp) ;///modify by eric for show stop record dialog first
				mCurrentButton = BUTTON_DOWNLOAD;
				new GetRecordStatus().execute();
				mSaveButton.setEnabled(false) ;
				mDeleteButton.setEnabled(false) ;
				mOpenButton.setEnabled(false) ;
			}
		}) ;

		mDeleteButton = (LinearLayout) view.findViewById(R.id.browserDeleteButton) ;
		mDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTotalFile = sSelectedFiles.size();
				if (mTotalFile > 0) {
					mProgDlg = new ProgressDialog(getActivity());
					mProgDlg.setCancelable(false);
					mProgDlg.setMax(mTotalFile) ;
					mProgDlg.setProgress(0);
					mProgDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) ;
					mProgDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mCancelDelete = true;
								}
							}) ;
					mProgDlg.setTitle("Delete file in Camera");
					mProgDlg.setMessage("Please wait ...");
					mCancelDelete = false;
					mProgDlg.show();
					new CameraDeleteFile().execute();
				}
//				mSaveButton.setEnabled(false) ;
//				mDeleteButton.setEnabled(false) ;
//				mOpenButton.setEnabled(false) ;
			}
		}) ;
		btn_video = (LinearLayout) view.findViewById(R.id.btn_video) ;
		btn_video.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFileListView.setVisibility(View.VISIBLE);
				jpgFileListview.setVisibility(View.GONE);
				isvideo = true;
				btn_video.setEnabled(false);
				btn_photo.setEnabled(true);
				//m_fileformat = FileNode.Format.all;
				mFileListView.setAdapter(mFileListVIDEOAdapter);
				mFileListVIDEOAdapter.notifyDataSetChanged();
//				mFileListView.setSelection(0);

			}
		});
		btn_video.setEnabled(false);
		btn_photo = (LinearLayout) view.findViewById(R.id.btn_photo) ;
		btn_photo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mFileListView.setVisibility(View.GONE);
				jpgFileListview.setVisibility(View.VISIBLE);
				isvideo = false;
				btn_video.setEnabled(true);
				btn_photo.setEnabled(false);
				if (isfirstPhoto){
					isfirstPhoto=false;
					isstatusPhoto=true;
				mHandler.sendEmptyMessage(MSG_SHOWDIALOG);
				new ContiunedDownloadTask().execute(rowsers);
					isfirstPhoto=false;
					Log.i("moop", "请求照片");}

				//m_fileformat = FileNode.Format.jjpeg;
				jpgFileListview.setAdapter(mFileListJPGAdapter);
				mFileListJPGAdapter.notifyDataSetChanged();
			}
		});
		mOpenButton = (LinearLayout) view.findViewById(R.id.browserOpenButton) ;
		mOpenButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentButton = BUTTON_OPEN;
				new GetRecordStatus().execute();
				///modify by eric
				/*FileNode fileNode = sSelectedFiles.get(0);
				if (fileNode.mFormat == Format.mov
					||fileNode.mFormat == Format.mp4
					||fileNode.mFormat == Format.avi) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					 CarDV WiFi Support Video container is 3GP (.MOV) 
					 For HTTP File Streaming 
					intent.setDataAndType(Uri.parse("http://" + mIp + fileNode.mName), "video/3gp") ;
					startActivity(intent);

					 For HTML5 Video Streaming 
//					String filename = fileNode.mName.replaceAll("/", "\\$");
//					Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//								Uri.parse("http://"+mIp+"/cgi-bin/Config.cgi?action=play&property=" + filename));
//					startActivity(browserIntent);
//					mSaveButton.setEnabled(false) ;
//					mDeleteButton.setEnabled(false) ;
//					mOpenButton.setEnabled(false) ;
				}
				else if(fileNode.mFormat ==Format.jpeg)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					 CarDV WiFi Support Video container is 3GP (.MOV) 
					 For HTTP File Streaming 
					intent.setDataAndType(Uri.parse("http://" + mIp + fileNode.mName), "image/jpeg") ;
					startActivity(intent);
				}*/
			}
		}) ;
		//SD卡文件界面listview
		mFileListView = (RTPullListView) view.findViewById(R.id.browserList) ;
		jpgFileListview= (RTPullListView) view.findViewById(R.id.browserList_jpg);
		initVideoListview();
		initJPGListView();
		
		mFileListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE) ;
		jpgFileListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE) ;

		if(isvideo)
		{
			mFileListView.setAdapter(mFileListVIDEOAdapter);
		}
		else
		{
			jpgFileListview.setAdapter(mFileListJPGAdapter);
		}
		/*SD卡文件listview点击事件*/
		mFileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ViewTag viewTag = (ViewTag) view.getTag() ;
				if (viewTag != null) {
					FileNode file = viewTag.mFileNode ;
					CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.fileListCheckBox) ;
					checkBox.setChecked(!checkBox.isChecked()) ;
					file.mSelected = checkBox.isChecked() ;

					if (file.mSelected)
						sSelectedFiles.add(file) ;
					else
						sSelectedFiles.remove(file) ;

					if (sSelectedFiles.size() > 0) {
						if(sSelectedFiles.size() < 2) {
							mOpenButton.setEnabled(true);
						}
							/*
							if (sSelectedFiles.get(0).mFormat == Format.mov) {
								mOpenButton.setEnabled(true);
							}
						} else {
							mOpenButton.setEnabled(false) ;
						}*/
						mSaveButton.setEnabled(true) ;
						mDeleteButton.setEnabled(true) ;
					} else {
						mOpenButton.setEnabled(false) ;
						mSaveButton.setEnabled(false) ;
						mDeleteButton.setEnabled(false) ;
					}
				}
			}
		}) ;
		/*SD卡文件listview点击事件*/
		jpgFileListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ViewTag viewTag = (ViewTag) view.getTag() ;
				if (viewTag != null) {
					FileNode file = viewTag.mFileNode ;
					CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.fileListCheckBox) ;
					checkBox.setChecked(!checkBox.isChecked()) ;
					file.mSelected = checkBox.isChecked() ;

					if (file.mSelected)
						sSelectedFiles.add(file) ;
					else
						sSelectedFiles.remove(file) ;

					if (sSelectedFiles.size() > 0) {
						if(sSelectedFiles.size() < 2) {
							mOpenButton.setEnabled(true);
						}
							/*
							if (sSelectedFiles.get(0).mFormat == Format.mov) {
								mOpenButton.setEnabled(true);
							}
						} else {
							mOpenButton.setEnabled(false) ;
						}*/
						mSaveButton.setEnabled(true) ;
						mDeleteButton.setEnabled(true) ;
					} else {
						mOpenButton.setEnabled(false) ;
						mSaveButton.setEnabled(false) ;
						mDeleteButton.setEnabled(false) ;
					}
				}
			}
		}) ;
		return view ;
	}

	private boolean mWaitingState = false ;
	private boolean mWaitingVisible = false ;
	private void showWattingDialog()
	{
		Activity activity = getActivity();
		if(mProgressDialog==null)
		{
			mProgressDialog = new ProgressDialog(activity) ;
			mProgressDialog.setCancelable(true) ;
		}
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View v = layoutInflater.inflate(R.layout.loading_dialog, null);
		mProgressDialog.show() ;
		mProgressDialog.setContentView(v);
	}
	private void dismissdialog()
	{
		if(mProgressDialog!=null)
		{
    		mProgressDialog.dismiss() ;
    		mProgressDialog = null ;
		}
	}
	private void setWaitingState(boolean waiting) {

		mFileListView.setClickable(!waiting) ;

		if (mWaitingState != waiting) {

			mWaitingState = waiting ;
			setWaitingIndicator(mWaitingState, mWaitingVisible) ;
		}
	}

	private void setWaitingIndicator(boolean waiting, boolean visible) {

		if (!visible)
			return ;

		Activity activity = getActivity() ;

		if (activity != null) {
			activity.setProgressBarIndeterminate(true) ;
			activity.setProgressBarIndeterminateVisibility(waiting) ;
		}
	}
	public void initVideoListview(){
		//添加listview底部获取更多按钮（可自定义）
		Activity activity = getActivity();
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView =(RelativeLayout) view.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
		mFileListView.addFooterView(footerView);
		//获取跟多监听器
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isstatusVideo=true;
				moreProgressBar.setVisibility(View.VISIBLE);
				new ContiunedDownloadTask().execute(rowsers);
				mHandler.sendEmptyMessage(MSG_SHOWDIALOG);
				//myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
			}
		});

	}
	private void initJPGListView() {
		//添加listview底部获取更多按钮（可自定义）
		Activity activity = getActivity();
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView =(RelativeLayout) view.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
		jpgFileListview.addFooterView(footerView);
		//获取跟多监听器
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				moreProgressBar.setVisibility(View.VISIBLE);
//				new ContiunedDownloadTask().execute(rowsers);
				Log.d("ssss", "图片点击事件");
				new ContiunedDownloadTask().execute(rowsers);
				jpgFileListview.setSelection(0);
				mHandler.sendEmptyMessage(MSG_SHOWDIALOG);
				//myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
			}
		});
	}

	//点击加载更多listview结果处理
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case LOAD_MORE_SUCCESS:
					if(!rowsers.mIsError){
						mFileListView.setSelectionfoot();
						moreProgressBar.setVisibility(View.GONE);
					}
					break;
				case LOAD_NEW_INFO:
					mFileListVIDEOAdapter.notifyDataSetChanged();
					mFileListView.setSelection(0);
					mFileListView.onRefreshComplete();
					break;
				default:
					break;
			}
		}

	};

	/*清除等待指示*/
	private void clearWaitingIndicator() {
		mWaitingVisible = false ;
		setWaitingIndicator(false, true) ;
	}

	private void restoreWaitingIndicator() {

		mWaitingVisible = true ;
		setWaitingIndicator(mWaitingState, true) ;
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what)
			{
			case MSG_SHOWDIALOG:
					showWattingDialog();
					break;
			case MSG_DISMISSDIALOG:
					dismissdialog();
				mFileListView.setSelection(0);
					break;
			case MSG_GETFILELIST:
					try {
						Log.d(TAG,"MSG_GETFILELIST mTryTimes="+mTryTimes);
						mTryTimes --;
						if(mTryTimes > 0)
						{
							new DownloadFileListTask().execute(new FileBrowser(new URL("http://" + mIp + mPath),
								FileBrowser.COUNT_MAX)) ;
						}
						else
						{
							Log.d(TAG,"MSG_GETFILELIST dismiss dialog");
							dismissdialog();
						}
					} catch (MalformedURLException e) {
						e.printStackTrace() ;
					}
					break;
			default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public void onResume() {

		restoreWaitingIndicator() ;
		if (sDownloadTask != null) {
			sDownloadTask.showProgress(getActivity()) ;
		} else {
			try {
				mTryTimes = G_TRYMAXTIMES;
				new DownloadFileListTask().execute(new FileBrowser(new URL("http://" + mIp + mPath),
						FileBrowser.COUNT_MAX)) ;
				showWattingDialog();
			} catch (MalformedURLException e) {
				e.printStackTrace() ;
			}
		}
	    ////begin added by eric for update record status 
		MainActivity.setUpdateRecordStatusFlag(false);
		///end
		super.onResume() ;
	}

	@Override
	public void onPause() {
		clearWaitingIndicator() ;

		if (sDownloadTask != null) {

			sDownloadTask.hideProgress() ;
		}
		////begin added by eric for update record status 
		if(BUTTON_OPEN == mCurrentButton || BUTTON_DOWNLOAD == mCurrentButton)
		{
			MainActivity.setUpdateRecordStatusFlag(true);
		}
		///end
		super.onPause() ;
	}
}
