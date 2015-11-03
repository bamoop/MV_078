package tw.com.a_i_t.IPCamViewer.FileBrowser ;

import java.io.File ;
import java.text.SimpleDateFormat ;
import java.util.ArrayList ;
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
import android.graphics.Color;
import android.net.Uri ;
import android.os.AsyncTask ;
import android.os.Bundle ;
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
	private List<FileNode> mSelectedFiles = new LinkedList<FileNode>() ;
	private final String TAG="LocalFileBrowserFragment";
	private LocalFileListAdapter mFileListAdapter ;
	//private TextView mFileListTitle ;
	private LinearLayout mOpenButton ;
	private LinearLayout mDeleteButton ;
	private LinearLayout mSharedButton ;
	private LinearLayout btn_video;
	private LinearLayout btn_photo;
	private ImageView btn_back;
	//private TextView mPhontoTxt;
	//private TextView mVideoTxt;
	private Boolean isvideo = true;	/*video is default*/
	private LoadFileListTask MyLoadFileListTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_local_file_list, container, false) ;

		mFileListAdapter = new LocalFileListAdapter(inflater, mFileList) ;

		//mFileListTitle = (TextView) view.findViewById(R.id.browserTitle) ;
		String fileBrowser = getActivity().getResources().getString(R.string.label_file_browser) ;
		//mFileListTitle.setText(fileBrowser + " : " + MainActivity.sAppName) ;

		ListView fileListView = (ListView) view.findViewById(R.id.browserList) ;

		fileListView.setAdapter(mFileListAdapter) ;
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
						TextView tv_filelistname=(TextView) view.findViewById(R.id.fileListName);
						TextView tv_filelisttime=(TextView) view.findViewById(R.id.fileListTime);
						TextView tv_fileListSize=(TextView) view.findViewById(R.id.fileListSize);
						if (checkBox.isChecked()) {
							tv_filelistname.setTextColor(Color.BLACK);
							tv_filelisttime.setTextColor(Color.BLACK);
							tv_fileListSize.setTextColor(Color.BLACK);
						}else {
							tv_filelistname.setTextColor(Color.rgb(184,184,184));
							tv_filelisttime.setTextColor(Color.rgb(184,184,184));
							tv_fileListSize.setTextColor(Color.rgb(184,184,184));

						}

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
				isvideo = true;
				btn_video.setEnabled(false);
				btn_photo.setEnabled(true);
				//mPhontoTxt.setEnabled(true);
				//mVideoTxt.setEnabled(false);
				new LoadFileListTask().execute() ;
			}
		});
		btn_video.setEnabled(false);
		btn_photo = (LinearLayout) view.findViewById(R.id.btn_photo) ;
		btn_photo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				isvideo = false;
				btn_video.setEnabled(true);
				btn_photo.setEnabled(false);
				//mPhontoTxt.setEnabled(false);
				//mVideoTxt.setEnabled(true);
				new LoadFileListTask().execute() ;
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

		mDeleteButton = (LinearLayout) view.findViewById(R.id.browserDeleteButton) ;
		mDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				for (FileNode fileNode : mSelectedFiles) {

					File file = new File(fileNode.mName) ;

					file.delete() ;
				}
				new LoadFileListTask().execute() ;
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
			mFileList.clear() ;
			mFileListAdapter.notifyDataSetChanged() ;

			mSelectedFiles.clear() ;
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
			for (File file : files) {
				String name = file.getName() ;
				String ext = name.substring(name.lastIndexOf(".") + 1) ;
				String attr = (file.canRead() ? "r" : "") + (file.canWrite() ? "w" : "") ;
				long size = file.length() ;
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date(file
						.lastModified())) ;
				Log.i("LocalFileBrowserFragment", "long="+file.lastModified()+"    "+time) ;
				FileNode.Format format = FileNode.Format.all ;

				if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg")) {
					format = FileNode.Format.jpeg;
				} else if (ext.equalsIgnoreCase("avi")) {
					format = FileNode.Format.avi;
				} else if (ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("3gp")) {
					format = FileNode.Format.mov;
				}

				if (format != FileNode.Format.all) {
					if(!isvideo)
					{
						if(format !=FileNode.Format.jpeg)
						{
							continue;
						}
					}
					else
					{
						if(format ==FileNode.Format.jpeg)
						{
							continue;
						}
					}
					try {
						FileNode fileNode = new FileNode(file.getPath(), format, (int) size, attr, time) ;
						fileNode.mModifiedTime =file.lastModified();
						fileList.add(fileNode) ;

					} catch (ModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace() ;
					}
				}
			}
			Log.i("LocalFileBrowserFragment", "file parsed") ;

			Collections.sort(fileList,new Comparator<FileNode>(){
						@Override
						public int compare(FileNode arg0, FileNode arg1) {
							// TODO Auto-generated method stub
							//Log.d("LocalFileBrowserFragment",arg0. + "  - "+ arg1.mModifiedTime);
							long ret = arg0.mModifiedTime - arg1.mModifiedTime;
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
							//return (int) (arg0.mModifiedTime - arg1.mModifiedTime);

						}}
			);

			return fileList ;
		}

		@Override
		protected void onPostExecute(ArrayList<FileNode> result) {

			Log.i("LocalFileBrowserFragment", "post exec") ;


			mFileList.addAll(result) ;
			mFileListAdapter.notifyDataSetChanged() ;
			setWaitingState(false) ;
			super.onPostExecute(result) ;
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
	public void onResume() {

		restoreWaitingIndicator() ;
		//new LoadFileListTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR) ;
		MyLoadFileListTask = new LoadFileListTask();
		MyLoadFileListTask.execute();
		super.onResume() ;
	}

	@Override
	public void onPause() {
		clearWaitingIndicator() ;
		mFileList.clear() ;
		mFileListAdapter.notifyDataSetChanged() ;

		super.onPause() ;
	}
}
