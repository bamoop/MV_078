package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;  
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Button_main extends LinearLayout {
	 private Context mcontext;
	 private ImageButton mback;
	 private TextView  mText;
	 private String mButtonText;
	 private LinearLayout button;
	 private int pic_background_id =0;
	 private static final String TAG="Button_main";
	 public Button_main(Context context) 
	 {  
         super(context);
         this.mcontext = context;  
         initLayoutInflater();  
	 }
	public Button_main(Context context, AttributeSet attrs)
	 {  
		  super(context, attrs);
		  this.mcontext = context;
		  TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.Main_button);
		  mButtonText = a.getString(R.styleable.Main_button_txt);
		  pic_background_id = a.getResourceId(R.styleable.Main_button_picblackground, 0);
		  a.recycle();
		  initLayoutInflater();
     }
	
	 public Button_main(Context context, AttributeSet attrs, int defStyle) 
	 {  
	      super(context, attrs, defStyle);  
	      this.mcontext = context;
		  TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.Main_button);
		  mButtonText = a.getString(R.styleable.Main_button_txt);
		  pic_background_id = a.getResourceId(R.styleable.Main_button_picblackground, 0);
		  a.recycle();
	      initLayoutInflater(); 
     }
	private void initLayoutInflater() 
	{  
		 //LayoutInflater lInflater = (LayoutInflater) getContext()  
		 //              .getSystemService(Context.LAYOUT_INFLATER_SERVICE); //  
		 LayoutInflater.from(mcontext).inflate(R.layout.button_main, this, true);
		
		 initView();
	} 
	private void initView() 
	 {  
	    	mback = (ImageButton)findViewById(R.id.btn);
	    	if(pic_background_id!=0)
	    	{
	    		mback.setBackgroundResource(pic_background_id);
	    	}
	    	mText = (TextView)findViewById(R.id.txt);
	    	mText.setText(mButtonText);
	    	Log.d(TAG,"mButtonText="+mButtonText);
	    	button = (LinearLayout)findViewById(R.id.main_button);
	    	button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d(TAG,"xxxxxxxxxxx");
				}});
	 }
	public void setOnClickListener(View.OnClickListener listen)
	{
		button.setOnClickListener(listen);
		super.setOnClickListener(null);
	}

	
}
