package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

/**
 * Created by bzmoop on 2015/12/22 0022.
 */
public class StartRecord {
    public static String mRecordStatus="";
    public static String mRecordmode="";

    public void startRecord(){
        Log.i("moop","StartRecord+开启录像");
        if (new MainActivity().M_VERSION > 2){
            new CameraStartRecord().execute();
        }else {
            new BackGetRecordStatus().execute();
        }
    }
    public void stopRecord(){
        new CameraStopRecord().execute();
    }

     class BackGetRecordStatus extends AsyncTask<URL, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute() ;
        }
        @Override
        protected String doInBackground(URL... params) {
            URL url = CameraCommand.commandRecordStatusUrl() ;
            if (url != null) {
                return CameraCommand.sendRequest(url) ;
            }
            return null ;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                String[] lines;
                String[] lines_temp = result.split("Camera.Preview.MJPEG.status.record=");
                if(null != lines_temp && 1 < lines_temp.length)
                {
                    lines = lines_temp[1].split(System.getProperty("line.separator")) ;
                    if(lines!=null)
                        mRecordStatus = lines[0];
                }
                lines_temp = result.split("Camera.Preview.MJPEG.status.mode=");
                if(null != lines_temp && 1 < lines_temp.length)
                {
                    lines = lines_temp[1].split(System.getProperty("line.separator")) ;
                    if(lines!=null)
                        mRecordmode = lines[0];
                }
            }
            else
            {
                mRecordmode ="";
                mRecordStatus = "";
            }

            if (mRecordmode.equals("Videomode"))
            {
                if(!mRecordStatus.equals("Recording"))
                {
                    new CameraVideoRecord().execute();
                }
            }

            super.onPostExecute(result) ;

        }
    }
    private class CameraVideoRecord extends AsyncTask<URL, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute() ;
        }
        @Override
        protected String doInBackground(URL... params) {
            URL url = CameraCommand.commandCameraRecordUrl() ;
            if (url != null) {
                return CameraCommand.sendRequest(url) ;
            }
            return null ;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result) ;

        }
    }

    private class CameraStartRecord extends AsyncTask<URL, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute() ;
        }
        @Override
        protected String doInBackground(URL... params) {
            URL url = CameraCommand.commandCameraStartRecordUrl() ;
            if (url != null) {
                return CameraCommand.sendRequest(url) ;
            }
            return null ;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result) ;

        }
    }
    private class CameraStopRecord extends AsyncTask<URL, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute() ;
        }
        @Override
        protected String doInBackground(URL... params) {
            URL url = CameraCommand.commandCameraStopRecordUrl() ;
            if (url != null) {
                return CameraCommand.sendRequest(url) ;
            }
            return null ;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result) ;

        }
    }
}
