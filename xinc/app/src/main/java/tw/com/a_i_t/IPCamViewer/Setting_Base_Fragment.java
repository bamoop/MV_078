package tw.com.a_i_t.IPCamViewer;

import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class Setting_Base_Fragment extends Setting_Base_Fun_Fragment {
	public SettingItem[] items;
	public String mBannerTxt ="";
	private OnItemListener mListener;
	public ArrayList<ListItem> list_items;
	private static final String TAG="CarSetting";
	
	public void refreshData()
	{
		
		Log.d(TAG,"mImageres="+mImageres);
		Log.d(TAG,"mSelectedItem="+mSelectedItem);
		if(mSelectedItem<list_items.size() && mSelectedItem>-1)
		{
			list_items.get(mSelectedItem).setChecked(true);
		}
	}
	public interface OnItemListener
	{
		public void OnMyClickListener(View view);
	}
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		list_items = new ArrayList<ListItem>();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		LinearLayout contentview = (LinearLayout) view.findViewById(R.id.contentview) ;
		if(items!=null)
		{
			for(int i=0;i<items.length;i++)
			{
				ListItem item = new ListItem(inflater.getContext());
				item.setItemTxt(items[i].name);
				item.setItemTag(items[i]);
				list_items.add(item);
				item.setMyOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						for(int i=0;i<list_items.size();i++)
						{
							if(!list_items.get(i).getItemTag().equals(arg0.getTag()))
							{
								list_items.get(i).setChecked(false);
							}
						}
						if(mListener!=null)
						{
							mListener.OnMyClickListener(arg0);
						}
					}
				});
				contentview.addView(item);
			}
		}
		return view;
	}
	public void setData(SettingItem[] data)
	{
		items = data;
	}

	public void setOnItemClieckListener(OnItemListener item_ls)
	{
		mListener = item_ls;
	}
}