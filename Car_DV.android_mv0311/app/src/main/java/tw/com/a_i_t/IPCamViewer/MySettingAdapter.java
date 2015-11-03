package tw.com.a_i_t.IPCamViewer;
import java.util.ArrayList;


import tw.com.a_i_t.IPCamViewer.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
public class MySettingAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<SettingItem> mFileList;
	private MySettingAdapter mAdapter;
	private int mPosition;
	AdapterClickListener mAdapterClickListener;
	public interface AdapterClickListener
	{
		public void OnClickListener(int position);
	}
	public MySettingAdapter(LayoutInflater inflater,
			ArrayList<SettingItem> fileList) {

		mInflater = inflater;
		mFileList = fileList;
		mAdapter = this;
	}
	public MySettingAdapter(LayoutInflater inflater,
			SettingItem[] fileList) {

		mInflater = inflater;
		mFileList = new ArrayList<SettingItem>();
		for(int i=0;i<fileList.length;i++)
		{
			mFileList.add(fileList[i]);
		}
		mAdapter = this;
	}
	public ArrayList<SettingItem> getData()
	{
		return mFileList;
	}
	public void setMyOnClickListener(AdapterClickListener l)
	{
		mAdapterClickListener = l;
	}
	@Override
	public int getCount() {

		return mFileList == null ? 0 : mFileList.size();
	}

	@Override
	public Object getItem(int position) {

		return mFileList == null ? null : mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	private static class MyViewTag
	{
		public TextView txtview;
		public CheckBox cb;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SettingItem item = (SettingItem)getItem(position);
		if(item ==null)
			return null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.setting_list_item, null);
			MyViewTag viewTag =new MyViewTag();
			viewTag.txtview = (TextView) convertView.findViewById(R.id.content);
			viewTag.cb = (CheckBox) convertView.findViewById(R.id.checkbox);
			viewTag.cb.setTag(position);
			
			viewTag.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					int position =  (Integer) arg0.getTag();
					if(arg1)
					{
						
						for(int i=0;i<getCount();i++)
						{
							if(i!=position)
							{
								mFileList.get(i).value = 0;
							}
							else
							{
								mFileList.get(i).value = 1;
								if(mAdapterClickListener!=null)
								{
									mAdapterClickListener.OnClickListener(position);
								}
								
							}
						}
					}
					else
					{
						mFileList.get(position).value = 0;
					}
					
				}
			});
			viewTag.txtview.setText(item.name);
			convertView.setTag(viewTag);
			
			if(item.value!=0)
			{
				viewTag.cb.setChecked(true);
			}
			else
			{
				viewTag.cb.setChecked(false);
			}
		}
		else
		{
			MyViewTag tag = (MyViewTag)convertView.getTag();
			tag.cb.setTag(position);
			if(item.value!=0)
			{
				tag.cb.setChecked(true);
			}
			else
			{
				tag.cb.setChecked(false);
			}
		}
		return convertView;
	}
}
