package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ListItem extends LinearLayout {
	 private Context mcontext;
	 private TextView  mText;
	 private String mtxt;
	 private CheckBox mCheckbox;
	 private LinearLayout mlayout;
	 private View.OnClickListener mListener;
	 private static final String TAG="ListItem";
	 public ListItem(Context context) 
	 {  
         super(context);
         this.mcontext = context;  
         initLayoutInflater();  
	 }
	public ListItem(Context context, AttributeSet attrs)
	 {  
		  super(context, attrs);
		  this.mcontext = context;
		  
		  initLayoutInflater();
     }
	
	 public ListItem(Context context, AttributeSet attrs, int defStyle) 
	 {  
	      super(context, attrs, defStyle);  
	      this.mcontext = context;
	      initLayoutInflater(); 
     }
	private void initLayoutInflater() 
	{  
		 LayoutInflater.from(mcontext).inflate(R.layout.setting_list_item, this, true);
		 initView();
	} 
	private void initView() 
	 {  
	    	mText = (TextView)findViewById(R.id.content);
	    	mText.setText(mtxt);
	    	mlayout = (LinearLayout)findViewById(R.id.content_layout);
	    	mCheckbox = (CheckBox)findViewById(R.id.checkbox);
	    	
	    	mlayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mCheckbox.setChecked(!mCheckbox.isChecked());
					if(mListener!=null)
						mListener.onClick(arg0);
				}
			});
	 }
	 public void setChecked(boolean isChecked)
	 {
		 mCheckbox.setChecked(isChecked);
	 }
	 public boolean isChecked()
	 {
		 return mCheckbox.isChecked();
	 }
	 public void setItemTxt(String txt)
	 {
		 mtxt = txt;
		 mText.setText(mtxt);
	 }
	public void setMyOnClickListener(View.OnClickListener listen)
	{
		mListener = listen;
		super.setOnClickListener(null);
	}
	public void setItemTag(Object obj)
	{
		mlayout.setTag(obj);
	}
	public Object getItemTag()
	{
		return mlayout.getTag();
	}
	
}
