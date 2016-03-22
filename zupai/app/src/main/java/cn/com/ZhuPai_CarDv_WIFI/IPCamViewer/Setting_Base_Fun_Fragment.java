package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class Setting_Base_Fun_Fragment extends Fragment {
	public SettingItem[] items;
	private String mBannerTxt ="";
	private OnItemListener mListener;
	
	public int mFlicker=0;
	public int mFlickerRet=0;
	public int mAWB=0;
	public int mAWBRet=0;
	public int mMTD=0;
	public int mMTDRet=0;
	public int mVideores=0;
	public int mVideoresRet=0;
	public int mImageres=0;
	public int mImageresRet=0;
	public int mEVRet=0;
	public int mVideoClipTime=0;
	public int mSelectedItem = 0;
	public String mFWVersionRet="";
	private static final String TAG="Setting_Base_Fun_Fragment";
	private static Toast mToast = null;
	public boolean mIsGetMenuCommand = true;
	public void refreshData()
	{
		
	}
	private class GetMenuSettingsValues extends AsyncTask <URL, Integer, String> {
		
		protected void onPreExecute() {
			//setWaitingState(true) ;
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandGetMenuSettingsValuesUrl() ;
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
				Log.d(TAG, "result="+result);
				String[] lines;		
				String[] lines_temp = result.split("Camera.Menu.DefaultValue.VideoRes=");

				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mVideoresRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.ImageRes=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mImageresRet = Integer.valueOf(lines[0]); 
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.MTD=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mMTDRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.AWB=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mAWBRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.Flicker=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mFlickerRet = Integer.valueOf(lines[0]);
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.FWversion=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mFWVersionRet = lines[0];
				}
				lines_temp = result.split("Camera.Menu.DefaultValue.EV=");
				if(null != lines_temp && 1 < lines_temp.length)
				{
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					if(lines!=null)
					mEVRet = Integer.valueOf(lines[0]);
				}
				
				lines_temp = result.split("Camera.Menu.DefaultValue.VideoClipTime=");
				if(lines_temp!=null)
				{
					if(lines_temp.length>1)
					{
						lines = lines_temp[1].split(System.getProperty("line.separator")) ;
						if(lines!=null && lines[0].length()>0)
						{
							String minutes = lines[0].substring(0, 1);
							int m =0;
							try
							{
								m= Integer.valueOf(minutes);
							}
							catch(NumberFormatException e)
							{
								Log.e(TAG,"minutes"+e.getMessage());
							}
							switch(m)
							{
							case 1:
								mVideoClipTime = 0;
								break;
							case 2:
								mVideoClipTime = 1;
								break;
							case 3:
								mVideoClipTime = 2;
								break;
							case 5:
								mVideoClipTime = 3;
								break;
							default:
								mVideoClipTime = 0;
								break;
							}
						}
					}
				}

				Log.d(TAG,"mImageresRet="+mImageresRet);
				mHandler.sendMessage(mHandler.obtainMessage());
		
			}
			else if (activity != null) {
				if(mToast ==null)
				{
					mToast = Toast.makeText(activity, activity.getResources().getString(R.string.message_fail_get_info),
						Toast.LENGTH_LONG);
				}
				mToast.show() ;
			}
			//setWaitingState(false) ;
			//setInputEnabled(true) ;
			super.onPostExecute(result) ;
		}
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			
			refreshData();
			super.handleMessage(msg);
		}
	};
	protected class CameraSettingsSendRequest extends CameraCommand.SendRequest {
		
		@Override
		protected void onPreExecute() {
			//setWaitingState(true) ;
			super.onPreExecute() ;
		}

		@Override
		protected void onPostExecute(String result) {
			//setWaitingState(false) ;
			super.onPostExecute(result) ;
		}
	}
	public interface OnItemListener
	{
		public void OnMyClickListener(View view);
	}
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_list_base, container, false) ;
		//by john 2015-11-25
		TextView text= (TextView) view.findViewById(R.id.txt_title);
		text.setText(mBannerTxt);
//		MyBanner banner = (MyBanner)view.findViewById(R.id.banner);
//		banner.setTitile(mBannerTxt);
		if(mIsGetMenuCommand)
		{
			new GetMenuSettingsValues().execute();
		}
		return view;
	}

	public void setBannerTxt(String txt)
	{
		mBannerTxt = txt;
	}

}