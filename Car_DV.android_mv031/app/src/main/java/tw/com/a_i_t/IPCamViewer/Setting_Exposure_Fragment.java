package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;

import tw.com.a_i_t.IPCamViewer.R ;
import android.app.Activity;
import android.app.Fragment ;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;



public class Setting_Exposure_Fragment extends Setting_Base_Fun_Fragment {
	private SeekBar mseekBar;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setBannerTxt(getResources().getString(R.string.label_exposure));
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout contentview = (LinearLayout) view.findViewById(R.id.contentview) ;
		View seekbar = inflater.inflate(R.layout.setting_exposure, container,false);
		contentview.addView(seekbar);
		mseekBar = (SeekBar) seekbar
				.findViewById(R.id.ExposureseekBar1) ;
		mseekBar.setMax(12);
		mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int pos, boolean arg2) {
				// TODO Auto-generated method stub
				URL url = CameraCommand.commandSetEVUrl(pos) ;		
				if (url != null) {
					new CameraSettingsSendRequest().execute(url) ;
				}
			}
		});
		return view;
	}
	public void refreshData()
	{
		mseekBar.setProgress(mEVRet);
	}
}
