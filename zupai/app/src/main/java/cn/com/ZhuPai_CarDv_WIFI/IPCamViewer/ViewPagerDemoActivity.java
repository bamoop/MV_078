package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

public class ViewPagerDemoActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private ImageButton m_imgBtn;
    private SharedPreferences preferences;
    public static ViewPagerDemoActivity instance = null;
    //��ͼƬ��Դ
    private static final int[] pics = { R.drawable.whatsnew_00,
            R.drawable.whatsnew_01, R.drawable.whatsnew_02 };

    //�ײ�С��ͼƬ
    private ImageView[] dots ;
    private static final int MSG_finish =1;
    //��¼��ǰѡ��λ��
    private int currentIndex;
    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_disn);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        preferences = getSharedPreferences("count",MODE_WORLD_READABLE);
        int count = preferences.getInt("count", 0);
//        if (count > 0) {
//            Intent intent = new Intent(ViewPagerDemoActivity.this, MainActivity.class) ;
//            startActivity(intent) ;
//            return;
//        }
        if (true) {
            final Intent intent = new Intent(ViewPagerDemoActivity.this, MainActivity.class) ;
            Timer timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent) ;
                    finish();
                }
            };timer.schedule(task, 1000 * 1);
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("count", ++count);
        editor.commit();
        setContentView(R.layout.welcome_show);
        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        //��ʼ����ͼƬ�б�
        for(int i=0; i<pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(iv);
        }
        vp = (ViewPager) findViewById(R.id.viewpager_welcome);
        //��ʼ��Adapter
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        //�󶨻ص�
        vp.setOnPageChangeListener(this);

        //��ʼ���ײ�С��
        initDots();

        //init welcome button
        m_imgBtn = (ImageButton)findViewById(R.id.welcome_button);
        m_imgBtn.setVisibility(View.INVISIBLE); //INVISIBLE
        m_imgBtn.setOnClickListener(new View.OnClickListener() {
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

        //ѭ��ȡ��С��ͼƬ
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);//����Ϊ��ɫ
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//����Ϊ��ɫ����ѡ��״̬
    }

    /**
     *���õ�ǰ����ҳ
     */
    private void setCurView(int position)
    {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }

    /**
     *��ֻ��ǰ��С���ѡ��
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

    //������״̬�ı�ʱ����
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    //����ǰҳ�汻����ʱ����
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    //���µ�ҳ�汻ѡ��ʱ����
    @Override
    public void onPageSelected(int arg0) {
        //���õײ�С��ѡ��״̬
        setCurDot(arg0);

    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }
}
