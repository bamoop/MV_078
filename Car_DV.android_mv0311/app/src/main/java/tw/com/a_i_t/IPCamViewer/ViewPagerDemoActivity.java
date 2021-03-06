package tw.com.a_i_t.IPCamViewer;

import java.util.ArrayList;  
import java.util.List;   
import android.app.Activity;  
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;  
import android.support.v4.view.ViewPager.OnPageChangeListener;  
import android.view.View;  
import android.view.Window;
import android.view.View.OnClickListener;  
import android.widget.ImageView;  
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;  
import android.widget.ImageButton; 

public class ViewPagerDemoActivity extends Activity implements OnClickListener, OnPageChangeListener{  
      
    private ViewPager vp;  
    private ViewPagerAdapter vpAdapter;  
    private List<View> views;  
    private ImageButton m_imgBtn;
    private SharedPreferences preferences;
    public static ViewPagerDemoActivity instance = null;
    //引导图片资源  
    private static final int[] pics = { R.drawable.whatsnew_00,  
            R.drawable.whatsnew_01, R.drawable.whatsnew_02 };  
      
    //底部小店图片  
    private ImageView[] dots ;  
    private static final int MSG_finish =1;
    //记录当前选中位置  
    private int currentIndex;  
    /** Called when the activity is first created. */  
    @SuppressWarnings("deprecation")
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        preferences = getSharedPreferences("count",MODE_WORLD_READABLE);
        int count = preferences.getInt("count", 0);
        if (count > 0) {

			Intent intent = new Intent(ViewPagerDemoActivity.this, MainActivity.class) ;
			startActivity(intent) ;
            return;
        }
        Editor editor = preferences.edit();
        editor.putInt("count", ++count);
        editor.commit();
        setContentView(R.layout.welcome_show);  
        
        views = new ArrayList<View>();  
         
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT);  
          
        //初始化引导图片列表  
        for(int i=0; i<pics.length; i++) {  
            ImageView iv = new ImageView(this);  
            iv.setLayoutParams(mParams);  
            iv.setImageResource(pics[i]);
            iv.setScaleType(ScaleType.FIT_XY);
            views.add(iv);  
        }
        vp = (ViewPager) findViewById(R.id.viewpager_welcome);  
        //初始化Adapter  
        vpAdapter = new ViewPagerAdapter(views);  
        vp.setAdapter(vpAdapter);  
        //绑定回调  
        vp.setOnPageChangeListener(this);  
          
        //初始化底部小点  
        initDots();  
        
        //init welcome button
        m_imgBtn = (ImageButton)findViewById(R.id.welcome_button);  
        m_imgBtn.setVisibility(View.INVISIBLE); //INVISIBLE
        m_imgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(ViewPagerDemoActivity.this, MainActivity.class) ;
				startActivity(intent) ;
				//mHandler.sendEmptyMessageDelayed(MSG_finish, 1000);
			}
		}) ;
        instance = this;
          
    }  
      
    private void initDots() {  
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
  
        dots = new ImageView[pics.length];  
  
        //循环取得小点图片  
        for (int i = 0; i < pics.length; i++) {  
            dots[i] = (ImageView) ll.getChildAt(i);  
            dots[i].setEnabled(true);//都设为灰色  
            dots[i].setOnClickListener(this);  
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应  
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态  
    }  
      
    /** 
     *设置当前的引导页  
     */  
    private void setCurView(int position)  
    {  
        if (position < 0 || position >= pics.length) {  
            return;  
        }  
  
        vp.setCurrentItem(position);  
    }  
  
    /** 
     *这只当前引导小点的选中  
     */  
    private void setCurDot(int positon)  
    {  
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {  
            return;  
        }  
        if(positon == pics.length - 1)
        {
        	m_imgBtn.setVisibility(View.VISIBLE);
        }
        else
        {
        	m_imgBtn.setVisibility(View.INVISIBLE);
        }
        
        dots[positon].setEnabled(false);  
        dots[currentIndex].setEnabled(true);  
  
        currentIndex = positon;  
    }  
  
    //当滑动状态改变时调用  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
        // TODO Auto-generated method stub  
          
    }  
  
    //当当前页面被滑动时调用  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
        // TODO Auto-generated method stub  
          
    }  
  
    //当新的页面被选中时调用  
    @Override  
    public void onPageSelected(int arg0) {  
        //设置底部小点选中状态  
        setCurDot(arg0);  
    }  
  
    @Override  
    public void onClick(View v) {  
        int position = (Integer)v.getTag();  
        setCurView(position);  
        setCurDot(position);  
    }  
}  
