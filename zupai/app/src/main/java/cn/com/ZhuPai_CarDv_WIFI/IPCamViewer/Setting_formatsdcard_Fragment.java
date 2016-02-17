package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.net.URL;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.annotation.SuppressLint;
import android.app.Fragment ;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater ;
import android.view.MotionEvent;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.Button;
import android.widget.TextView;



public class Setting_formatsdcard_Fragment extends Fragment {
	private CustomDialog malertDialog;
	private ProgressDialog mProgressDialog ;

	MainActivity.MyOnTouchListener myOnTouchListener;
	private GestureDetector mGestureDetector;
	private static boolean isINFilebrowser=true;//当前fragment是否打开
	private boolean returnBlankScreen=false;//待机黑屏
	private boolean returnFragment=false;//待机自动退出


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
		mGestureDetector = new GestureDetector(getActivity(),
				new SideIndexGestureListener());
		myOnTouchListener=new MainActivity.MyOnTouchListener() {
			@Override
			public boolean onTouch(MotionEvent ev) {
				boolean result = mGestureDetector.onTouchEvent(ev);
				if (isINFilebrowser){
					sleepHandler.removeMessages(1);
					sleepHandler.sendEmptyMessageDelayed(1, 1000 * MainActivity.TOUNCHTIME);
					return result;
				}
				return result;
			}
		};
		((MainActivity) getActivity())
				.registerMyOnTouchListener(myOnTouchListener);
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

	@Override
	public void onPause() {
		returnBlankScreen=false;
		super.onPause() ;
	}

	@Override
	public void onResume() {
		isINFilebrowser=true;
		returnBlankScreen=true;
		if (returnFragment){
			MainActivity.backToFristFragment(getActivity());
		}
		sleepHandler.removeMessages(1);
		sleepHandler.sendEmptyMessageDelayed(1, 1000 * MainActivity.TOUNCHTIME);
		super.onResume() ;
	}
	@Override
	public void onDestroy(){
		isINFilebrowser=false;
		sleepHandler.removeMessages(1);
		super.onDestroy();
	}
	private Handler sleepHandler = new Handler()
	{
		public void handleMessage(Message msg){
			switch(msg.what)
			{
				case 1:
					returnFragment();
					break;
			}
		}
	};
	public synchronized void returnFragment(){
		if (isINFilebrowser&&returnBlankScreen){
			getFragmentManager().popBackStack();
		}else if (isINFilebrowser&&!returnBlankScreen){
			returnFragment=true;
			new StartRecord().startRecord();
		}
	}
}
