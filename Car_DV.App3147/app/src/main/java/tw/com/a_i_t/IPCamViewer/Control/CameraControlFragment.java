package tw.com.a_i_t.IPCamViewer.Control ;

import tw.com.a_i_t.IPCamViewer.MainActivity ;
import tw.com.a_i_t.IPCamViewer.R ;
import android.app.Fragment ;
import android.os.Bundle ;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.view.View.OnTouchListener ;
import android.widget.RelativeLayout ;

public class CameraControlFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.camera_control, container, false) ;

		OnTouchListener onTouch = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.selected_background) ;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					v.setBackgroundResource(R.drawable.group_background) ;
				}
				return false ;
			}
		} ;

		RelativeLayout networkConfigurations = (RelativeLayout) view
				.findViewById(R.id.cameraControlNetworkConfigurations) ;

		networkConfigurations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(CameraControlFragment.this, new NetworkConfigurationsFragment()) ;
			}
		}) ;

		networkConfigurations.setOnTouchListener(onTouch) ;

		RelativeLayout cameraSettings = (RelativeLayout) view.findViewById(R.id.cameraControlCameraSettings) ;

		cameraSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(CameraControlFragment.this, new CameraSettingsFragment()) ;
			}
		}) ;

		cameraSettings.setOnTouchListener(onTouch) ;

		return view ;
	}

}
