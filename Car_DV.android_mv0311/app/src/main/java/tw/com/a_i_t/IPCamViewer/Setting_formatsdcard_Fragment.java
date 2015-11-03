package tw.com.a_i_t.IPCamViewer ;

import java.net.MalformedURLException;
import java.net.URL;

import tw.com.a_i_t.IPCamViewer.R ;
import android.annotation.SuppressLint;
import android.app.Fragment ;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.Button;
import android.widget.TextView;



public class Setting_formatsdcard_Fragment extends Fragment {
	private CustomDialog malertDialog;
	private ProgressDialog mProgressDialog ;
	@SuppressLint("HandlerLeak")
	private Handler mTHandler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what)
			{
			default:
				if(malertDialog!=null)
				{
					malertDialog.dismiss();
					malertDialog = null;
				}
				if(mProgressDialog!=null)
				{
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				break;
			}
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_formatsdcard, container, false) ;

		Button btn = (Button) view
				.findViewById(R.id.format_btn) ;

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(malertDialog==null)
				{
					malertDialog = new CustomDialog.Builder(getActivity())
					.setTitle(getActivity().getString(R.string.warnnig))
					.setMessage(R.string.format_attaction)
					.setPositiveButton(R.string.ensure,new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mTHandler.removeMessages(1);
							if(mProgressDialog==null)
							{
								LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
								View v1 = layoutInflater.inflate(R.layout.loading_dialog, null);
								TextView cont_txt = (TextView)v1.findViewById(R.id.content_txt);
								cont_txt.setText(R.string.label_formatbtn);
								((TextView)v1.findViewById(R.id.title_txt)).setText(R.string.warnnig);
								mProgressDialog = new ProgressDialog(getActivity()) ;
								//mProgressDialog.setTitle("Connecting to Camera") ;
								mProgressDialog.setCancelable(false) ;
								mProgressDialog.show() ;
								mProgressDialog.setContentView(v1);
							}
							else
							{
								mProgressDialog.show() ;
							}

							try {
								URL url = CameraCommand.commandformatsdcardSettingsUrl() ;
								if (url != null) {
									new CameraCommand.SendRequest().execute(url) ;
								}
								mTHandler.sendEmptyMessageDelayed(1,10*1000);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace() ;
							}
						}
					})
					.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mTHandler.removeMessages(1);
							dialog.dismiss() ;
							malertDialog = null;
						}
					}).create();
				}
				malertDialog.show() ;
			}
		}) ;
		return view;
	}
}
