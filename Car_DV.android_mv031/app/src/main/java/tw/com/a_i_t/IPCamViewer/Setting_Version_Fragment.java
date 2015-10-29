package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;

import tw.com.a_i_t.IPCamViewer.R ;
import android.app.Fragment ;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



public class Setting_Version_Fragment extends Setting_Base_Fun_Fragment {
	private TextView mTextView;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setBannerTxt(getResources().getString(R.string.FW_Version));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = super.onCreateView(inflater, container, savedInstanceState);
		//View view = inflater.inflate(R.layout.setting_version, container, false) ;
		view.findViewById(R.id.line).setVisibility(View.GONE);
		LinearLayout contentview = (LinearLayout) view.findViewById(R.id.contentview) ;
		
		View view_version = inflater.inflate(R.layout.setting_version, container, false) ;
		contentview.addView(view_version);
		mTextView = (TextView)view_version.findViewById(R.id.hardware_version);
		String pkName = getActivity().getPackageName();
	    String versionName="";
		try {
			versionName = getActivity().getPackageManager().getPackageInfo(
						pkName, 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((TextView)view_version.findViewById(R.id.app_version)).setText(versionName);
		return view;
	}
	public void refreshData()
	{
		mTextView.setText(mFWVersionRet);
	}

}
