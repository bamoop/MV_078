package tw.com.a_i_t.IPCamViewer.FileBrowser;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.net.HttpURLConnection ;
import java.net.MalformedURLException ;
import java.net.URL ;
import java.util.ArrayList ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Locale ;

import tw.com.a_i_t.IPCamViewer.CameraCommand ;
import tw.com.a_i_t.IPCamViewer.MainActivity ;
import tw.com.a_i_t.IPCamViewer.R ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode ;
import tw.com.a_i_t.IPCamViewer.FileBrowser.Model.FileNode.Format;
import android.app.Activity ;
import android.app.AlertDialog ;
import android.app.Fragment ;
import android.app.Notification ;
import android.app.NotificationManager ;
import android.app.PendingIntent ;
import android.app.ProgressDialog ;
import android.content.Context ;
import android.content.DialogInterface ;
import android.content.Intent ;
import android.net.DhcpInfo ;
import android.net.Uri ;
import android.net.wifi.WifiManager ;
import android.net.wifi.WifiManager.WifiLock ;
import android.os.AsyncTask ;
import android.os.Bundle ;
import android.os.PowerManager ;
import android.util.Log ;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.View.OnClickListener ;
import android.view.ViewGroup ;
import android.webkit.MimeTypeMap ;
import android.widget.AdapterView ;
import android.widget.AdapterView.OnItemClickListener ;
import android.widget.Button ;
import android.widget.CheckedTextView ;
import android.widget.ListView ;
import android.widget.TextView ;
import android.widget.Toast;

public class DVFilesBrowserFragment extends Fragment {

}
