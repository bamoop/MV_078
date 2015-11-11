package tw.com.a_i_t.IPCamViewer.FileBrowser ;

import java.io.File ;
import java.text.DateFormat;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date ;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale ;


import tw.com.a_i_t.IPCamViewer.MainActivity ;
import tw.com.a_i_t.IPCamViewer.R ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileBrowserModel.ModelException ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode.Format ;
import android.app.Activity ;
import android.app.AlertDialog ;
import android.app.Fragment ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.net.Uri ;
import android.os.AsyncTask ;
import android.os.Bundle ;
import android.text.format.Time;
import android.util.Log ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap ;
import android.widget.AdapterView ;
import android.widget.AdapterView.OnItemClickListener ;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView ;
import android.widget.TextView ;

public class LocalFileBrowserFragment extends Fragment {

	private ArrayList<FileNode> mFileList = new ArrayList<FileNode>() ;
	private ArrayList<FileNode> mPhotoFilelist = new ArrayList<FileNode>() ;
	private List<FileNode> mSelectedFiles = new LinkedList<FileNode>() ;
	private final String TAG="LocalFileBrowserFragment";
	private LocalFileListAdapter mFileListAdapter ;
	private LocalFileListAdapter mPhotoFileListAdapter ;
	//private TextView mFileListTitle ;
	private LinearLayout mOpenButton ;
	private LinearLayout mDeleteButton ;
	private LinearLayout mSharedButton ;
	private LinearLayout btn_video;
	private LinearLayout btn_photo;
	private boolean isfirstclickphotobtn=true;
	private boolean isfirstclickvideobtn=true;
	private ImageView btn_back;
	//private TextView mPhontoTxt;
	//private TextView mVideoTxt;
	private Boolean isvideo = true;	/*video is default*/
	private LoadFileListTask MyLoadFileListTask;
	ListView fileListView,filePhotoListView;
	//add by john 2015.11.11
	private static final int ONEDAYS=7;
	private static final int TRIDDAYS=7;
	private static final int SEVENDAYS=7;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_local_file_list, container, false) ;

		mFileListAdapter = new LocalFileListAdapter(inflater, mFileList) ;
		mPhotoFileListAdapter = new LocalFileListAdapter(inflater, mPhotoFilelist) ;

		//mFileListTitle = (TextView) view.findViewById(R.id.browserTitle) ;
		String fileBrowser = getActivity().getResources().getString(R.string.label_file_browser) ;
		//mFileListTitle.setText(fileBrowser + " : " + MainActivity.sAppName) ;
		

		
		 fileListView = (ListView) view.findViewById(R.id.browserList) ;
		filePhotoListView = (ListView) view.findViewById(R.id.browserPhotoList) ;

		fileListView.setAdapter(mFileListAdapter) ;
		filePhotoListView.setAdapter(mPhotoFileListAdapter) ;
		fileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				final FileNode fileNode = mFileList.get(position) ;
				if (fileNode != null) {

					ViewTag viewTag = (ViewTag) view.getTag() ;
					if (viewTag != null) {

						FileNode file = viewTag.mFileNode ;

						CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.fileListCheckBox) ;
						checkBox.setChecked(!checkBox.isChecked()) ;
						file.mSelected = checkBox.isChecked() ;

						if (file.mSelected)
							mSelectedFiles.add(file) ;
						else
							mSelectedFiles.remove(file) ;

						if (mSelectedFiles.size() == 1) {
							mOpenButton.setEnabled(true) ;
						} else {
							mOpenButton.setEnabled(false) ;
						}
						
						if (mSelectedFiles.size() > 0) {
							mDeleteButton.setEnabled(true) ;
						} else {
							mDeleteButton.setEnabled(false) ;
						}
						
					}
				}
			}
		}) ;
		filePhotoListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				final FileNode fileNode = mPhotoFilelist.get(position) ;
				if (fileNode != null) {

					ViewTag viewTag = (ViewTag) view.getTag() ;
					if (viewTag != null) {

						FileNode file = viewTag.mFileNode ;

						CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.fileListCheckBox) ;
						checkBox.setChecked(!checkBox.isChecked()) ;
						file.mSelected = checkBox.isChecked() ;

						if (file.mSelected)
							mSelectedFiles.add(file) ;
						else
							mSelectedFiles.remove(file) ;

						if (mSelectedFiles.size() == 1) {
							mOpenButton.setEnabled(true) ;
						} else {
							mOpenButton.setEnabled(false) ;
						}

						if (mSelectedFiles.size() > 0) {
							mDeleteButton.setEnabled(true) ;
						} else {
							mDeleteButton.setEnabled(false) ;
						}

					}
				}
			}
		}) ;
		btn_video = (LinearLayout) view.findViewById(R.id.btn_video) ;
		btn_video.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				filePhotoListView.setVisibility(View.GONE);
				fileListView.setVisibility(View.VISIBLE);
				isvideo = true;
//				mSelectedFiles.clear();
				fileListView.setAdapter(mFileListAdapter);
				mFileListAdapter.notifyDataSetChanged();
				mFileListAdapter.notifyDataSetChanged();
				Log.i("moop", "视频点击");
				btn_video.setEnabled(false);
				btn_photo.setEnabled(true);
				//mPhontoTxt.setEnabled(true);
				//mVideoTxt.setEnabled(false);

			}
		});
		btn_video.setEnabled(false);
		btn_photo = (LinearLayout) view.findViewById(R.id.btn_photo) ;
		btn_photo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				mSelectedFiles.clear();
				filePhotoListView.setAdapter(mPhotoFileListAdapter);
				mPhotoFileListAdapter.notifyDataSetChanged();
				mFileListAdapter.notifyDataSetChanged();

				Log.i("moop", "图片点击");

				if (isfirstclickphotobtn){
					new LoadFileListTask().execute() ;
					isfirstclickphotobtn=false;
				}
				filePhotoListView.setVisibility(View.VISIBLE);
				fileListView.setVisibility(View.GONE);
				isvideo = false;
				btn_video.setEnabled(true);
				btn_photo.setEnabled(false);
				//mPhontoTxt.setEnabled(false);
				//mVideoTxt.setEnabled(true);
			}
		});
		/*
		btn_back = (ImageView) view.findViewById(R.id.btn_back) ;
		btn_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d("banner","err back");
			}
		});*/
		//mPhontoTxt = (TextView) view.findViewById(R.id.txt_photo) ;
		//mVideoTxt = (TextView) view.findViewById(R.id.txt_video) ;
		mOpenButton = (LinearLayout) view.findViewById(R.id.browserOpenButton) ;
		mOpenButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSelectedFiles.size() == 1) {
					FileNode fileNode = mSelectedFiles.get(0) ;
					File file = new File(fileNode.mName) ;
					Intent intent = new Intent(Intent.ACTION_VIEW) ;
					if (fileNode.mFormat == Format.mov || fileNode.mFormat == Format.avi) {
						/* CarDV WiFi Support Video is 3GP (.MOV) */
						intent.setDataAndType(Uri.fromFile(file), "video/3gp");
						startActivity(intent) ;
					} //else if (fileNode.mFormat == Format.avi) {
						/* call self player */
						//VideoPlayerActivity.start(getActivity(), "file://"+ fileNode.mName);
					//} 
				     else if (fileNode.mFormat == Format.jpeg) {
						intent.setDataAndType(Uri.fromFile(file), "image/jpeg") ;
						startActivity(intent) ;
					}
				}
				mSelectedFiles.clear() ;
				mOpenButton.setEnabled(false) ;
				mDeleteButton.setEnabled(false) ;
			}
		}) ;
		/*删除方法*/
		mDeleteButton = (LinearLayout) view.findViewById(R.id.browserDeleteButton) ;
		mDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteFile();
			//	new LoadFileListTask().execute() ;
			}
		}) ;
		mSharedButton = (LinearLayout) view.findViewById(R.id.browserSharedButton) ;
		mSharedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedFiles.size() == 1) {
					FileNode fileNode = mSelectedFiles.get(0) ;
					File file = new File(fileNode.mName) ;
					Intent intent = new Intent(Intent.ACTION_SEND) ;
					Uri u = Uri.fromFile(file);
					if (fileNode.mFormat == Format.jpeg ) {
						intent.setType("image/*");
					} else{
						intent.setType("video/*") ;
					}
					intent.putExtra(Intent.EXTRA_STREAM, u); 
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(Intent.createChooser(intent, getString(R.string.label_shared))); 
				}
			}
		}) ;
		return view ;
	}

	private class LoadFileListTask extends AsyncTask<Integer, Integer, ArrayList<FileNode>> {

		@Override
		protected void onPreExecute() {

			setWaitingState(true) ;

			mDeleteButton.setEnabled(false) ;
			mOpenButton.setEnabled(false) ;

			Log.i(TAG, "pre execute") ;

			super.onPreExecute() ;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList<FileNode> doInBackground(Integer... params) {

			Log.i(TAG, "background") ;
			File directory = MainActivity.getAppDir() ;
			Log.i(TAG, "get app dir dir="+directory.getAbsolutePath()) ;
			File[] files = directory.listFiles() ;
			Log.i(TAG, "list file size="+files.length) ;

			ArrayList<FileNode> fileList = new ArrayList<FileNode>() ;
			ArrayList<FileNode> photofileList = new ArrayList<FileNode>() ;
			int i=0;
			for (File file : files) {
				i++;
				String name = file.getName() ;
				String ext = name.substring(name.lastIndexOf(".") + 1) ;
				String attr = (file.canRead() ? "r" : "") + (file.canWrite() ? "w" : "") ;
				long size = file.length() ;
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date(file
						.lastModified())) ;
				Log.i("LocalFileBrowserFragment", "long="+file
						.lastModified()+"    "+time) ;
				FileNode.Format format = FileNode.Format.all ;

				if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg")) {
					format = FileNode.Format.jpeg;
					Log.i("moop", "264format = FileNode.Format.jpeg;") ;
				} else if (ext.equalsIgnoreCase("avi")) {
					format = FileNode.Format.avi;
					Log.i("moop", "267format = FileNode.Format.avi;") ;
				} else if (ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("3gp")) {
					format = FileNode.Format.mov;
					Log.i("moop", "270format = FileNode.Format.mov;") ;
				}
//			//amend by john 2015.11.10
				FileNode fileNode=null;
				try {
					fileNode = new FileNode(file.getPath(), format, (int) size, attr, time) ;
					//Log.i("LocalFileBrowserFragment", "file.getPath()="+file.getPath()+"   format="+format+"  size"+size+"   attr="+attr+"   time="+time+"    file.lastModified();="+file.lastModified() );
//					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//					String date = sDateFormat.format(new java.util.Date());
//					Log.i("moop", " ;当前系统时间=="+date) ;
					Calendar c   =   Calendar.getInstance();
					Date   date   =   c.getTime();
					fileNode.mModifiedTime =file.lastModified();
					date.toLocaleString();
					Log.i("moop", "当前系统时间---" + date.toLocaleString());
					Log.i("moop", "文件创建时间---" + time);
					Log.i("moop", "比较出来的时间=" + calculateTime(time)) ;
					int calculate= Integer.parseInt(calculateTime(time));
					if (calculate>=0&&calculate<1){
						Log.i("moop", "一天内");
					}else if(calculate>=1&&calculate<3){
						Log.i("moop", "一天前");
					}else if(calculate>=3&&calculate<7){
						Log.i("moop", "三天前");
					}else {
						Log.i("moop", "七天前");
					}
				} catch (ModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace() ;
				}
				if (format != FileNode.Format.all) {
//					if (isvideo){
//						if (format !=FileNode.Format.jpeg||format ==FileNode.Format.jpeg){
//							continue;
//						}
//					}
					if(isvideo)
					{
						if(format !=FileNode.Format.jpeg)
						{
							fileList.add(fileNode) ;
							Log.i("moop", "图片278format !=FileNode.Format.jpeg") ;
							continue;
						}
					}
					else
					{
						if(format ==FileNode.Format.jpeg)
						{
							photofileList.add(fileNode);
							Log.i("moop", "”视频“286format ==FileNode.Format.jpeg") ;
							continue;
						}
					}

				}
			}
			Log.i("moop", "i=" + i);
			Log.i("LocalFileBrowserFragment", "file parsed") ;

			/*根据下载到本地的时间有近到远进行排序*/
			Collections.sort(fileList, new Comparator<FileNode>() {
						@Override
						public int compare(FileNode arg0, FileNode arg1) {
							// TODO Auto-generated method stub
							//Log.d("LocalFileBrowserFragment",arg0. + "  - "+ arg1.mModifiedTime);
							long ret = arg0.mModifiedTime - arg1.mModifiedTime;
							if (ret < 0) {
								return 1;
							} else if (ret > 0) {
								return -1;
							} else {
								return 0;
							}
							//return (int) (arg0.mModifiedTime - arg1.mModifiedTime);

						}
					}
			);
			Log.i("moop1", "fileList.size==;" + fileList.size()) ;
			if (isvideo){
				return fileList;
			}else {
				return photofileList;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<FileNode> result) {

			Log.i("LocalFileBrowserFragment", "post exec");

			Log.i("moop", "mFileList.addAll(result) ;");
			if (isvideo){
				mFileList.addAll(result) ;
			}else {
				mPhotoFilelist.addAll(result);
			}
			mFileListAdapter.notifyDataSetChanged();
			mPhotoFileListAdapter.notifyDataSetChanged();
			setWaitingState(false);
			super.onPostExecute(result) ;
		}
	}
	
	private boolean mWaitingState = false ;
	private boolean mWaitingVisible = false ;
	/*获取当前系统时间*/
	public  String calculateTime(String time){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		Log.i("moop","24小时时间="+localTime.format("%Y-%m-%d %H:%M:%S"));
		String bjtime = null;
		try{
			Date d1=df.parse(localTime.format("%Y-%m-%d %H:%M:%S"));
			Date d2=df.parse("2015-11-04 16:44");
			long diff = d1.getTime() - d2.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			bjtime= String.valueOf(days);
		}catch(Exception e){
			Log.i("moop","Exception"+e);
		}
		return bjtime;
	}
	/*删除操作*/
	private void deleteFile(){
		FileNode fileNode = mSelectedFiles.remove(0);
		File file = new File(fileNode.mName);
		file.delete();
		mPhotoFilelist.remove(fileNode);
		mFileList.remove(fileNode);
		mPhotoFileListAdapter.notifyDataSetChanged();
		mFileListAdapter.notifyDataSetChanged();
		if (mSelectedFiles.size()>0){
			deleteFile();
		}
	}

	private void setWaitingState(boolean waiting) {

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

	private void clearWaitingIndicator() {

		mWaitingVisible = false ;
		setWaitingIndicator(false, true) ;
	}

	private void restoreWaitingIndicator() {

		mWaitingVisible = true;
		setWaitingIndicator(mWaitingState, true) ;
	}
	@Override
	public void onDestroy() {
		super.onPause() ;
		Log.i("moop","onDestroy");
		clearWaitingIndicator() ;
		mFileList.clear() ;
		mPhotoFilelist.clear();
		mFileListAdapter.notifyDataSetChanged() ;
		mPhotoFileListAdapter.notifyDataSetChanged();
	}
	@Override
	public void onResume() {
		restoreWaitingIndicator() ;
		//new LoadFileListTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR) ;
		MyLoadFileListTask = new LoadFileListTask();
		MyLoadFileListTask.execute();
		super.onResume() ;
	}

	@Override
	public void onPause() {
		super.onPause() ;
	}
	@Override
	public void onStop(){
		super.onStop();
	}
}
