package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;

import tw.com.a_i_t.IPCamViewer.Control.CameraControlFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.FileBrowserFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.BrowserSettingFragment ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.LocalFileBrowserFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.MjpegPlayerFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.StreamPlayerFragment ;
import tw.com.a_i_t.IPCamViewer.Viewer.ViewerSettingFragment ;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment ;
import android.content.Context ;
import android.content.DialogInterface;
import android.net.DhcpInfo ;
import android.net.wifi.WifiManager ;
import android.os.AsyncTask;
import android.os.Bundle ;
import android.util.Log;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.view.View.OnTouchListener ;
import android.view.ViewGroup ;
import android.widget.RelativeLayout ;

public class FunctionListFragment extends Fragment {

	/* Query property of RTSP AV1 */
	private class GetRTPS_AV1 extends AsyncTask<URL, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute() ;
		}
		@Override
		protected String doInBackground(URL... params) {
			URL url = CameraCommand.commandQueryAV1Url() ;
			Log.i("moop", String.valueOf(url));
			if (url != null) {		
				return CameraCommand.sendRequest(url) ;
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String result) {
			String	liveStreamUrl;
			WifiManager wifiManager = (WifiManager)
								getActivity().getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
			if (dhcpInfo == null || dhcpInfo.gateway == 0) {
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create() ;
				alertDialog.setTitle(getResources().getString(R.string.dialog_DHCP_error)) ;
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
						getResources().getString(R.string.label_ok),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {

								dialog.dismiss() ;

							}
						}) ;
				alertDialog.show() ;
				return;
			}
			String gateway = MainActivity.intToIp(dhcpInfo.gateway) ;
			// set http push as default for streaming
			liveStreamUrl = "http://" + gateway + MjpegPlayerFragment.DEFAULT_MJPEG_PUSH_URL ;
			if (result != null) {
				String[] lines;
				try {
					String[] lines_temp = result.split("Camera.Preview.RTSP.av1=");
					lines = lines_temp[1].split(System.getProperty("line.separator")) ;
					int av1 = Integer.valueOf(lines[0]);
					if (av1 == 1) {
						// support RTSP audio/video
						liveStreamUrl = "rtsp://" + gateway + MjpegPlayerFragment.DEFAULT_RTSP_URL ;
					}
				} catch (Exception e) {/* not match, for firmware of MJPEG only */}
			}
			Fragment fragment = StreamPlayerFragment.newInstance(liveStreamUrl) ;
			MainActivity.addFragment(FunctionListFragment.this, fragment) ;
			super.onPostExecute(result) ;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.function_list, container, false) ;

		OnTouchListener onTouch = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.selected_background) ;
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					v.setBackgroundResource(0) ;
				}
				return false ;
			}
		} ;

		RelativeLayout control = (RelativeLayout) view.findViewById(R.id.functionListControl) ;

		control.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(FunctionListFragment.this, new CameraControlFragment()) ;
			}
		}) ;

		control.setOnTouchListener(onTouch) ;

		RelativeLayout preview = (RelativeLayout) view.findViewById(R.id.functionListPreview) ;

		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode ;

				if (engineerMode) {
					MainActivity.addFragment(FunctionListFragment.this, new ViewerSettingFragment()) ;
				} else {
					Log.i("moop","134");
					new GetRTPS_AV1().execute();
				}
			}
		}) ;

		preview.setOnTouchListener(onTouch) ;

		RelativeLayout browser = (RelativeLayout) view.findViewById(R.id.functionListBrowser) ;

		browser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean engineerMode = ((MainActivity) getActivity()).mEngineerMode ;

				if (engineerMode) {

					MainActivity.addFragment(FunctionListFragment.this, new BrowserSettingFragment()) ;
				} else {
					Fragment fragment = FileBrowserFragment.newInstance(null, null, null) ;

					MainActivity.addFragment(FunctionListFragment.this, fragment) ;
				}
			}
		}) ;

		browser.setOnTouchListener(onTouch) ;

		RelativeLayout localAlbum = (RelativeLayout) view.findViewById(R.id.functionListLocalAlbum) ;

		localAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.addFragment(FunctionListFragment.this, new LocalFileBrowserFragment()) ;
			}
		}) ;

		localAlbum.setOnTouchListener(onTouch) ;

		return view ;
	}
}
