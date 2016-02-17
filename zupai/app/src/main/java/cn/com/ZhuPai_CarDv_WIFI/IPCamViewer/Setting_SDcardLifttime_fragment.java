package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URL;

/**
 * Created by bzmoop on 2015/11/25 0025.
 */
public class Setting_SDcardLifttime_fragment extends Fragment {
    String sdcardlifetime="";
    TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_sdcardlifetime_fragment, container, false) ;
        textView= (TextView) view.findViewById(R.id.tv_setting_sdlifetime);
        new GetSDcardLifeTimeRecorStatus().execute();
        return view;
    }
    private class GetSDcardLifeTimeRecorStatus extends AsyncTask<URL, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute() ;
        }
        @Override
        protected String doInBackground(URL... params) {
            URL url=CameraCommand.commandSDcardLifeTime();
            if (url!=null){
                return CameraCommand.sendRequest(url);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result!=null){
                String[] lines;
                String[] lines_temp =result.split("Camera.Preview.MJPEG.status.dwDegreOfWear=");
                if (null!=lines_temp && 1<lines_temp.length)
                {
                    lines = lines_temp[1].split(System.getProperty("line.separator")) ;
                    if (lines!=null&&!lines[0].equals(0)){
                        sdcardlifetime=lines[0];
                        try {
                            float sd= Integer.parseInt(sdcardlifetime);
                            textView.setText(String.valueOf(sd/1000));
                        }catch (Exception e){
                            Log.i("moop", String.valueOf(e));
                        }
                        Log.i("moop", "shijian=" + sdcardlifetime);
                    }else {
                        textView.setText("没检测到SD卡寿命使用情况，请更换SD卡后重新尝试。");
                    }
                }
            }
            super.onPostExecute(result) ;
        }
}
}
