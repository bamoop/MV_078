package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import android.app.Fragment ;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.Button;



public class Setting_Time_Fragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_time, container, false) ;

		Button btn = (Button) view
				.findViewById(R.id.synctime_btn) ;

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				URL url = CameraCommand.commandCameraTimeSettingsUrl() ;
				if (url != null) {
					new CameraCommand.SendRequest().execute(url) ;
				}
			}
		}) ;
		return view;
	}
}
