package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser ;

import java.io.File ;
import java.text.DateFormat;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date ;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale ;
import java.util.Map;


import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.CustomDialog;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.MainActivity ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.R ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileBrowserModel.ModelException ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileNode ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileNode.Format ;
import android.app.Activity ;
import android.app.Fragment ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.content.SharedPreferences;
import android.net.Uri ;
import android.os.AsyncTask ;
import android.os.Bundle ;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LocalFileBrowserFragment extends Fragment {

	//private ArrayList<FileNode> mFileList = new ArrayList<FileNode>() ;
//	private ArrayList<FileNode> mPhotoFilelist = new ArrayList<FileNode>() ;
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
	//ListView fileListView,filePhotoListView;
	//add by John 2015.11.11
	private static final int ONEDAYS=7;
	private static final int TRIDDAYS=7;
	private static final int SEVENDAYS=7;
	private ExpandableListView photofileExpListview,videofileExpListview;
	private List<FileNode> vlist1 = new ArrayList<FileNode>();
	private List<FileNode> vlist2 = new ArrayList<FileNode>();
	private List<FileNode> vlist3 = new ArrayList<FileNode>();
	private List<FileNode> plist1 = new ArrayList<FileNode>();
	private List<FileNode> plist2 = new ArrayList<FileNode>();
	private List<FileNode> plist3 = new ArrayList<FileNode>();
	private List<FileNode> stackFileList = new ArrayList<FileNode>();
	private Map<String, List<FileNode>> photomap = new HashMap<String, List<FileNode>>();
	private Map<String, List<FileNode>> videomap = new HashMap<String, List<FileNode>>();
	private ArrayList<String> videoParent = new ArrayList<String>();
	private ArrayList<String> photoParent = new ArrayList<String>();
	private LocalFileVideoExpandableAdapter videoexpandableAdapter;
	private LocalFilePhotoExpandableAdapter photoexpandableAdapter;
	private List<String> selectgroupPositionlist=new ArrayList<String>();
	private List<String> selectgroupPositionlistvideo=new ArrayList<String>();
	private boolean nitializationFile=true;
	private int VERSION=0; //版本信息
    Toast mToast;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_local_file_list, container, false) ;
		photofileExpListview= (ExpandableListView) view.findViewById(R.id.expandlist_photo);
		videofileExpListview= (ExpandableListView) view.findViewById(R.id.expandlist_video);

//		videofileExpListview.setGroupIndicator(null);
//		photofileExpListview.setGroupIndicator(null);local_expand_group_bg

		//mFileListAdapter = new LocalFileListAdapter(inflater, mFileList) ;
		//mPhotoFileListAdapter = new LocalFileListAdapter(inflater, mPhotoFilelist) ;
		String fileBrowser = getActivity().getResources().getString(R.string.label_file_browser) ;

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int v = pref.getInt(MainActivity.g_version_check,MainActivity.G_UNKNOWN_VERSION);
		VERSION=v;

		photoexpandableAdapter=new LocalFilePhotoExpandableAdapter(getActivity(),inflater,photomap,photoParent);
		videoexpandableAdapter=new LocalFileVideoExpandableAdapter(getActivity(),inflater,videomap,photoParent);
		photofileExpListview.setAdapter(photoexpandableAdapter);
		videofileExpListview.setAdapter(videoexpandableAdapter);
		this.photoParent.add(getResources().getString(R.string.intraday));
		this.photoParent.add(getResources().getString(R.string.yesterday));
		this.photoParent.add(getResources().getString(R.string.threedaysago));

		selectgroupPositionlistvideo.add( photoParent.get(0));
		selectgroupPositionlistvideo.add( photoParent.get(1));
		selectgroupPositionlistvideo.add( photoParent.get(2));
		selectgroupPositionlist.add(photoParent.get(0));
		selectgroupPositionlist.add(photoParent.get(1));
		selectgroupPositionlist.add(photoParent.get(2));
		photofileExpListview.expandGroup(0);
		videofileExpListview.expandGroup(0);

		photofileExpListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
				final FileNode fileNode = photomap.get(selectgroupPositionlist.get(groupPosition)).get(childPosition);
				if (fileNode != null) {
					ViewTag viewTag = (ViewTag) view.getTag();
					if (viewTag != null) {
						FileNode file = viewTag.mFileNode;
						CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.fileListCheckBox);
						checkBox.setChecked(!checkBox.isChecked());
						file.mSelected = checkBox.isChecked();
						photoexpandableAdapter.notifyDataSetChanged();
						if (file.mSelected)
							mSelectedFiles.add(file);
						else
							mSelectedFiles.remove(file);
						if (mSelectedFiles.size() == 1) {
							mOpenButton.setEnabled(true);
						} else {
							mOpenButton.setEnabled(true);
						}
						if (mSelectedFiles.size() > 0) {
							mDeleteButton.setEnabled(true);
						} else {
//							mDeleteButton.setEnabled(false);
						}
					}
				}
				return false;
			}
		});
		videofileExpListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
				final FileNode fileNode = videomap.get(selectgroupPositionlistvideo.get(groupPosition)).get(childPosition);
				if (fileNode != null) {
					ViewTag viewTag = (ViewTag) view.getTag();
					if (viewTag != null) {
						FileNode file = viewTag.mFileNode;
						CheckedTextView checkBox = (CheckedTextView) view.findViewById(R.id.fileListCheckBox);
						checkBox.setChecked(!checkBox.isChecked());
						file.mSelected = checkBox.isChecked();
						photoexpandableAdapter.notifyDataSetChanged();
						if (file.mSelected)
							mSelectedFiles.add(file);
						else
							mSelectedFiles.remove(file);
						if (mSelectedFiles.size() == 1) {
							mOpenButton.setEnabled(true);
						} else {
							mOpenButton.setEnabled(true);
						}
						if (mSelectedFiles.size() > 0) {
							mDeleteButton.setEnabled(true);
						} else {
//							mDeleteButton.setEnabled(false);
						}
					}
				}
				return false;
			}
		});

		/*fileListView.setOnItemClickListener(new OnItemClickListener() {
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
		}) ;*/
		btn_video = (LinearLayout) view.findViewById(R.id.btn_video) ;
		btn_video.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				filePhotoListView.setVisibility(View.GONE);
//				fileListView.setVisibility(View.VISIBLE);
				photofileExpListview.setVisibility(View.GONE);
				videofileExpListview.setVisibility(View.VISIBLE);
				clsSelect();
				isvideo = true;
//				mSelectedFiles.clear();
//				fileListView.setAdapter(mFileListAdapter);
				photoexpandableAdapter.notifyDataSetChanged();
				videoexpandableAdapter.notifyDataSetChanged();
				btn_video.setEnabled(false);
				btn_photo.setEnabled(true);
				//mPhontoTxt.setEnabled(true);
				//mVideoTxt.setEnabled(false);

			}
		});
		btn_video.setEnabled(false);
		btn_photo = (LinearLayout) view.findViewById(R.id.btn_photo) ;
		btn_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mSelectedFiles.clear();
				clsSelect();

				photofileExpListview.setVisibility(View.VISIBLE);
				videofileExpListview.setVisibility(View.GONE);
//				filePhotoListView.setAdapter(mPhotoFileListAdapter);
//				mPhotoFileListAdapter.notifyDataSetChanged();
//				mFileListAdapter.notifyDataSetChanged();
				photoexpandableAdapter.notifyDataSetChanged();
				videoexpandableAdapter.notifyDataSetChanged();
				if (isfirstclickphotobtn) {
					new LoadFileListTask().execute();
					isfirstclickphotobtn = false;
				}
				isvideo = false;
				btn_video.setEnabled(true);
				btn_photo.setEnabled(false);
				//mPhontoTxt.setEnabled(false);
				//mVideoTxt.setEnabled(true);
			}
		});
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
						try{
							intent.setDataAndType(Uri.fromFile(file), "image/jpeg") ;
							startActivity(intent) ;
						}catch (Exception e){
							Toast.makeText(getActivity(), "打开失败，请更换系统图片默认的打开方式",
									Toast.LENGTH_SHORT).show();
						}
					}
				}else {
					CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
							.setTitle(getResources().getString(R.string.trip))
							.setMessage(getResources().getString(R.string.erroroneopenfile))
							.setCancelable(false)
							.setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									arg0.dismiss();
									return;
								}
							}).create();
					alertDialog.show();
				}
//				mSelectedFiles.clear() ;
//				photoexpandableAdapter.notifyDataSetChanged();
//				mOpenButton.setEnabled(false) ;
//				mDeleteButton.setEnabled(false) ;
			}
		}) ;

		/*删除方法*/
		mDeleteButton = (LinearLayout) view.findViewById(R.id.browserDeleteButton) ;
		mDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                if (mSelectedFiles.size()>0){
                    deleteFile();
                }else {
                    toastUtil(getResources().getString(R.string.pleaseSelectFile));
                }
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
				} else {
					CustomDialog alertDialog = new CustomDialog.Builder(getActivity())
							.setTitle(getResources().getString(R.string.trip))
							.setMessage(getResources().getString(R.string.erroronesharefile))
							.setCancelable(false)
							.setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									arg0.dismiss();
									return;
								}
							}).create();
					alertDialog.show();
				}
			}
		}) ;
		return view ;
	}

	private class LoadFileListTask extends AsyncTask<Integer, Integer, ArrayList<FileNode>> {

		@Override
		protected void onPreExecute() {
			setWaitingState(true) ;
//			mDeleteButton.setEnabled(false) ;
//			mOpenButton.setEnabled(false) ;
			Log.i(TAG, "pre execute") ;
			super.onPreExecute() ;
		}
		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList<FileNode> doInBackground(Integer... params) {

			Log.i(TAG, "background");
			File directory = MainActivity.getAppDir();
			Log.i(TAG, "get app dir dir=" + directory.getAbsolutePath());
			File[] files = directory.listFiles();
			Log.i(TAG, "list file size=" + files.length);
			ArrayList<FileNode> fileList = new ArrayList<FileNode>();
			ArrayList<FileNode> photofileList = new ArrayList<FileNode>();
			int i = 0;
			for (File file : files) {
				i++;
				String name = file.getName();
				String ext = name.substring(name.lastIndexOf(".") + 1);
				String attr = (file.canRead() ? "r" : "") + (file.canWrite() ? "w" : "");
				long size = file.length();
				String time=null;
				//031用下载的时间分类，031以上用文件在机器上创建的时间分类
				if(VERSION > 2){
					Log.i(TAG, "新版本时间分类");
					if (file.getName().length() >= 16){
						time =getFileCreateTime(file.getName());
					}else {
						continue;
					}
				}else {
					Log.i(TAG, "老版本时间分类");
					time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date(file
							.lastModified()));
				}

				Log.i("LocalFileBrowserFragment", "long=" + file.lastModified() + "    " + time);
				FileNode.Format format = FileNode.Format.all;
				if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg")) {
					format = FileNode.Format.jpeg;
				} else if (ext.equalsIgnoreCase("avi")) {
					format = FileNode.Format.avi;
				} else if (ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("3gp")) {
					format = FileNode.Format.mov;
				}
//			//amend by john 2015.11.10
				FileNode fileNode = null;
				try {
					fileNode = new FileNode(file.getPath(), format, (int) size, attr, time);
					//Log.i("LocalFileBrowserFragment", "file.getPath()="+file.getPath()+"   format="+format+"  size"+size+"   attr="+attr+"   time="+time+"    file.lastModified();="+file.lastModified() );
//					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//					String date = sDateFormat.format(new java.util.Date());
//					Log.i("moop", " ;当前系统时间=="+date) ;
					Calendar c = Calendar.getInstance();
					Date date = c.getTime();
					fileNode.mModifiedTime = file.lastModified();
					date.toLocaleString();

					//int calculate= Integer.parseInt(calculateTime(time));
				} catch (ModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (format != FileNode.Format.all) {
//					if (isvideo){
//						if (format !=FileNode.Format.jpeg||format ==FileNode.Format.jpeg){
//							continue;
//						}
//					}
					if (isvideo) {
						if (format != FileNode.Format.jpeg) {
							int calculate = Integer.parseInt(calculateTime(time));
							if (calculate >= 0 && calculate < 1) {
								stackFileList.add(fileNode);
								sortfile(stackFileList, 1);
//								vlist1.add(fileNode);
							} else if (calculate >= 1 && calculate < 5) {
								stackFileList.add(fileNode);
								sortfile(stackFileList, 2);
//								vlist2.add(fileNode);
							} else {
								stackFileList.add(fileNode);
								sortfile(stackFileList, 3);
//								vlist3.add(fileNode);
							}
							continue;
						}
					} else {
						if (format == FileNode.Format.jpeg) {
							int calculate = Integer.parseInt(calculateTime(time));
							if (calculate >= 0 && calculate < 1) {
								stackFileList.add(fileNode);
								sortfile(stackFileList, 4);
//								plist1.add(fileNode);
							} else if (calculate >= 1 && calculate < 5) {
								stackFileList.add(fileNode);
								sortfile(stackFileList, 5);
//								plist2.add(fileNode);
							}  else {
								stackFileList.add(fileNode);
								sortfile(stackFileList, 6);
//								plist3.add(fileNode);
							}
//							photofileList.add(fileNode);
							continue;
						}
					}
				}
			}
			Log.i("LocalFileBrowserFragment", "file parsed");
			photomap.put(getResources().getString(R.string.intraday), plist1);
			photomap.put(getResources().getString(R.string.yesterday), plist2);
			photomap.put(getResources().getString(R.string.threedaysago), plist3);
			videomap.put(getResources().getString(R.string.intraday), vlist1);
			videomap.put(getResources().getString(R.string.yesterday), vlist2);
			videomap.put(getResources().getString(R.string.threedaysago), vlist3);
//			photomap.get(getResources().getString(R.string.intraday)).size();
			/*根据下载到本地的时间由近到远进行排序*/
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
			if (isvideo){
				return fileList;
			}else {
				return photofileList;
			}
		}
		@Override
		protected void onPostExecute(ArrayList<FileNode> result) {
			Log.i(TAG, "post exec");
			photoexpandableAdapter.notifyDataSetChanged();
			videoexpandableAdapter.notifyDataSetChanged();
//			photofileExpListview.expandGroup(0);
//			videofileExpListview.expandGroup(0);
			setWaitingState(false);
			super.onPostExecute(result) ;
		}
	}


	private boolean mWaitingState = false ;
	private boolean mWaitingVisible = false ;

	/**
	 * 从文件名中获取文件创建的时间
	 * @prarm 传入一个文件名，文件名里面包含创建时间
	 * @return 返回创建文件的时间格式
	 * */
	public  String getFileCreateTime(String name){
		name=name.trim();
		String time="";
		String year="";
		String month="";
		String day="";
		String hour="";
		String minute="";
		int j=1;
		if (name != null && !"".equals(name)) {
			for (int i = 0; i < name.length(); i++) {
				if (name.charAt(i) >= 48 && name.charAt(i) <= 57) {
					j++;
					if (j<4){
						year+=name.charAt(i);
					}else if (j<6){
						month+=name.charAt(i);
					}else if(j<8){
						day+=name.charAt(i);
					}else if(j<10){
						hour+=name.charAt(i);
					}else if(j<12){
						minute+=name.charAt(i);
					}
				}
			}
		}
		time="20"+year+"-"+month+"-"+day+" "+hour+":"+minute;
		Log.i(TAG,"文件名截取的时间是:"+time);
		Log.i(TAG,"文件名截取的时间是:year="+year+" month="+month+"day=" +day+"hour="+hour+ "minute=" + minute+"j=" +j);
		Log.i(TAG,"文件名是:"+name);
		return time;
	}

	/**
	 * 获取当前系统时间
	 * @prarm 传入一个需要比较的时间
	 * @return 返回一个传入时间与当前手机时间的差值，单位是 天
	 * */

	public  String calculateTime(String time){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		Log.i(TAG,"24小时时间="+localTime.format("%Y-%m-%d %H:%M:%S"));
		String bjtime = null;
		try{
			Date d1=df.parse(localTime.format("%Y-%m-%d %H:%M:%S"));
			Date d2=df.parse(time);
			long diff = d1.getTime() - d2.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			bjtime= String.valueOf(days);
		}catch(Exception e){
			Log.i(TAG,"Exception"+e);
		}
		return bjtime;
	}
	private void clerSelect(){
//		this.photoParent.add("七天前==");
		photofileExpListview.setAdapter(photoexpandableAdapter);
		photoexpandableAdapter.notifyDataSetChanged();
//		photomap.remove("cle");
//		photoexpandableAdapter.notifyDataSetChanged();

	}
	/*删除操作*/
	private void deleteFile(){
		FileNode fileNode = mSelectedFiles.remove(0);
		File file = new File(fileNode.mName);
		file.delete();
		//photomap.remove(fileNode);
		for (int i=0;i<selectgroupPositionlist.size();i++){
		photomap.get(selectgroupPositionlist.get(i)).remove(fileNode);
		}
		for (int i=0;i<selectgroupPositionlistvideo.size();i++){
		videomap.get(selectgroupPositionlistvideo.get(i)).remove(fileNode);
		}
	//	mFileList.remove(fileNode);
		videoexpandableAdapter.notifyDataSetChanged();
		photoexpandableAdapter.notifyDataSetChanged();
//		photofileExpListview.setAdapter(photoexpandableAdapter);
		if (mSelectedFiles.size()>0){
			deleteFile();
		}else {
			if (mSelectedFiles.size() > 0)
				mDeleteButton.setEnabled(true) ;
			else
				mDeleteButton.setEnabled(false) ;
		}
	}
	private void clsSelect(){

		if (mSelectedFiles.size()>0){
			FileNode fileNode= mSelectedFiles.remove(0);
			fileNode.mSelected=false;
			photoexpandableAdapter.notifyDataSetChanged();
			videoexpandableAdapter.notifyDataSetChanged();
		}
		if(mSelectedFiles.size()>0){
			clsSelect();
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
	/**文件排序
	 * @prarm 传入一个从sd卡读出的文件list，
	 * @prarm position，指示第几个list需要排序。
 	 * */
	private void sortfile(List<FileNode> list,int position)
	{
		switch (position){
			case 1:
				for(int i=list.size()-1;i>=0;i--){
					vlist1.add(list.get(i));
				}
				break;
			case 2:
				for(int i=list.size()-1;i>=0;i--){
					vlist2.add(list.get(i));
				}
				break;
			case 3:
				for(int i=list.size()-1;i>=0;i--){
					vlist3.add(list.get(i));
				}
				break;
			case 4:
				for(int i=list.size()-1;i>=0;i--){
					plist1.add(list.get(i));
				}
				break;
			case 5:
				for(int i=list.size()-1;i>=0;i--){
					plist2.add(list.get(i));
				}
				break;
			case 6:
				for(int i=list.size()-1;i>=0;i--){
					plist3.add(list.get(i));
				}
				break;
		}
		list.clear();

//		if(list.size()>0)
//		{
//			for(int i=len-1;i>=0;i--)
//			for(int i=0;i<list.size();i++)
//			{
//				sFileListVIDEO.add(stackFileListVIDEO.get(i));
//			}
//		}

	}

    public void toastUtil(String msg){
        if (mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(getActivity() ,msg ,Toast.LENGTH_SHORT);
        mToast.show();
    }

	@Override
	public void onDestroy() {
		super.onPause();
		Log.i(TAG, "onDestroy");
		clearWaitingIndicator() ;
		photomap.clear();
		videomap.clear();
//		mFileList.clear() ;
//		mPhotoFilelist.clear();
	}
	@Override
	public void onResume() {
		restoreWaitingIndicator() ;
		//new LoadFileListTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR) ;
		if (nitializationFile){
			MyLoadFileListTask = new LoadFileListTask();
			MyLoadFileListTask.execute();
			nitializationFile=false;
		}
		super.onResume() ;
	}

	@Override
	public void onPause() {
		clsSelect();
		mSelectedFiles.clear();
		super.onPause() ;
	}
	@Override
	public void onStop(){
		super.onStop();
	}
}
