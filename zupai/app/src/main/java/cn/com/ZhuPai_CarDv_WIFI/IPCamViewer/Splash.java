package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import org.videolan.libvlc.LibVlcException ;
import org.videolan.vlc.Util ;

import android.app.Activity ;
import android.os.Bundle ;
import android.view.Window ;

public class Splash extends Activity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;

		// Hides the titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE) ;

		setContentView(R.layout.splash) ;

		if (savedInstanceState == null) {
			
		}
		
		try {
			
//            Context context = VLCApplication.getAppContext();
//            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

//            SharedPreferences.Editor editor = pref.edit() ;
//            editor.putInt("network_caching_value", 2000) ;
//            editor.commit() ;
			
			Util.getLibVlcInstance() ;
		} catch (LibVlcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
