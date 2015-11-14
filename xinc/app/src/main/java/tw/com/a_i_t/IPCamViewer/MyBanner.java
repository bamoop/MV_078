package tw.com.a_i_t.IPCamViewer;

import tw.com.a_i_t.IPCamViewer.R;
import android.content.Context;  
import android.content.res.TypedArray;
import android.util.AttributeSet;  
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;  


public class MyBanner extends RelativeLayout {
	 private View mView;
	 private Context mcontext;
	 private ImageButton mback;
	 private TextView  mText;
	 private String mtitle;
	 private static final String TAG="banner";
	 public MyBanner(Context context) 
	 {  
         super(context);
         this.mcontext = context;  
         initLayoutInflater();  
	 }
	public MyBanner(Context context, AttributeSet attrs)
	 {  
		  super(context, attrs);
		  this.mcontext = context;
		  TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.MyBanner);
		  mtitle = a.getString(R.styleable.MyBanner_text);
		  a.recycle();
		  initLayoutInflater();
     }
	
	 public MyBanner(Context context, AttributeSet attrs, int defStyle) 
	 {  
	      super(context, attrs, defStyle);  
	      this.mcontext = context;
		  TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.MyBanner);
		  mtitle = a.getString(R.styleable.MyBanner_text);
		  a.recycle();
	      initLayoutInflater(); 
     }
	private void initLayoutInflater() {  
		 LayoutInflater lInflater = (LayoutInflater) getContext()  
		               .getSystemService(Context.LAYOUT_INFLATER_SERVICE); //  
		 mView = lInflater.inflate(R.layout.banner, this,true);
		 initView();
		} 
	private void initView() 
	 {  
	    	mback = (ImageButton)mView.findViewById(R.id.btn_back);  
	    	mText = (TextView)mView.findViewById(R.id.txt_title);
	    	mText.setText(mtitle);
	    	mback.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d(TAG,"back");
					MainActivity.backToPreFragment();
				}});
	 }
	public void setTitile(String title)
	{
		mtitle = title;
		mText.setText(mtitle);
	}
	public void setBackBtnVisiable(boolean isvisiable)
	{
		if(isvisiable)
		{
			mback.setVisibility(View.VISIBLE);
		}
		else
		{
			mback.setVisibility(View.INVISIBLE);
		}
		
	}
}
