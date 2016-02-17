package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.SideIndexGestureListener;

import android.app.Fragment ;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle ;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater ;
import android.view.MotionEvent;
import android.view.View ;
import android.view.ViewGroup ;
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

    private static final String TAG="HelpFramgment";
    MainActivity.MyOnTouchListener myOnTouchListener;
    private GestureDetector mGestureDetector;
    private static boolean isINFilebrowser=true;//当前fragment是否打开
    private boolean returnBlankScreen=false;//待机黑屏
    private boolean returnFragment=false;//待机自动退出

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.help_framgment, container, false) ;

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


        return view;
    }
    public static Bitmap readBitMap(Context context,int resID){
        BitmapFactory.Options opt=new BitmapFactory.Options();
        opt.inPreferredConfig=Bitmap.Config.RGB_565;
        opt.inPurgeable =true;
        opt.inInputShareable=true;
        //获取资源图片
        InputStream is=context.getResources().openRawResource(resID);
        return BitmapFactory.decodeStream(is,null,opt);

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
            Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
//            getFragmentManager().popBackStack();
            MainActivity.backToFristFragment(getActivity());
        }else if (isINFilebrowser&&!returnBlankScreen){
            Log.i(TAG, "触摸无操作，屏幕亮直接返回主界面");
            returnFragment=true;
            new StartRecord().startRecord();
        }
    }

}
