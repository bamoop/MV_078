package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser;

import java.util.ArrayList ;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.R ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileNode ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.BaseAdapter ;
import android.widget.CheckedTextView ;
import android.widget.ImageView ;
import android.widget.TextView ;

public class FileListVideoAdapter extends BaseAdapter {

	private LayoutInflater mInflater ;
	private ArrayList<FileNode> mFileList ;
	
	public FileListVideoAdapter(LayoutInflater inflater, ArrayList<FileNode> fileList) {
		
		mInflater = inflater ;
		mFileList = fileList ;
	}
	
	@Override
	public int getCount() {
		
		return mFileList == null ? 0 : mFileList.size() ;
	}

	@Override
	public Object getItem(int position) {

		return mFileList == null ? null : mFileList.get(position) ;
	}

	@Override
	public long getItemId(int position) {
		
		return position ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewTag viewTag ;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.local_filelist_video_row, null) ;

			viewTag = new ViewTag((ImageView) convertView.findViewById(R.id.fileListThumbnail),
					(TextView) convertView.findViewById(R.id.fileListName),
					(TextView) convertView.findViewById(R.id.fileListTime),
					(TextView) convertView.findViewById(R.id.fileListSize), mFileList.get(position),
					(CheckedTextView) convertView.findViewById(R.id.fileListCheckBox)) ;
			convertView.setTag(viewTag) ;
		} else {
			viewTag = (ViewTag) convertView.getTag() ;
		}
		viewTag.mFileNode = mFileList.get(position) ;
		String filename = viewTag.mFileNode.mName.substring(viewTag.mFileNode.mName.lastIndexOf("/") + 1) ;
		viewTag.mFilename.setText(filename) ;
		viewTag.mTime.setText(viewTag.mFileNode.mTime) ;
		viewTag.setSize(viewTag.mFileNode.mSize) ;
		viewTag.mCheckBox.setChecked(viewTag.mFileNode.mSelected) ;
		return convertView ;
	}
}
