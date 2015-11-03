package tw.com.a_i_t.IPCamViewer ;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tw.com.a_i_t.IPCamViewer.R ;
import android.app.Fragment ;
import android.os.Bundle ;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.View.OnClickListener ;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;



public class HelpFramgment extends Fragment {
	
    private static final int[] pics = { R.drawable.help1, 
    	R.drawable.help2,R.drawable.help3,R.drawable.help4,R.drawable.help5,R.drawable.help6,
    	R.drawable.help7}; 
    private List<View> views; 
    private ViewPager vp;  
    private ViewPagerAdapter vpAdapter;  
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.help_framgment, container, false) ;
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT);
        views = new ArrayList<View>();  
        //初始化引导图片列表  
        for(int i=0; i<pics.length; i++) {  
            ImageView iv = new ImageView(getActivity());  
            iv.setLayoutParams(mParams);  
            iv.setImageResource(pics[i]);
            iv.setScaleType(ScaleType.FIT_XY);
            views.add(iv);  
        }
        vp = (ViewPager) view.findViewById(R.id.viewpager_help);  
        //初始化Adapter  
        vpAdapter = new ViewPagerAdapter(views);  
        vp.setAdapter(vpAdapter);  
        //绑定回调  
        //vp.setOnPageChangeListener(this); 
		
		
		return view;
	}
}
