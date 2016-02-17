package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by bzmoop on 2015/12/19 0019.
 */
public class SideIndexGestureListener implements GestureDetector.OnGestureListener {
    public final static String TAG = "SideIndexGestureListener" ;
    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG,"onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG,"onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG,"onSingleTapUp");

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i(TAG,"onScroll");

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(TAG,"onLongPress");

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i(TAG,"onFling");

        return false;
    }
}
