package tw.com.a_i_t.IPCamViewer ;

import android.app.Fragment ;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;


public class ConnectConfigBg extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.connect_config_bg, container, false) ;
		MyBanner banner = (MyBanner)view.findViewById(R.id.banner);
		banner.setBackBtnVisiable(false);
		return view ;
	}
}
