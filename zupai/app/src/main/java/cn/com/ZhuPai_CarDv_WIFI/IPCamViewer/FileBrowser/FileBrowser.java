package cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser ;

import java.io.BufferedReader ;
import java.io.ByteArrayInputStream ;
import java.io.IOException;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.Reader ;
import java.io.StringWriter ;
import java.io.Writer ;
import java.net.HttpURLConnection ;
import java.net.MalformedURLException ;
import java.net.URI ;
import java.net.URISyntaxException ;
import java.net.URL ;
import java.util.ArrayList ;
import java.util.List ;

import javax.xml.parsers.DocumentBuilderFactory ;
import javax.xml.parsers.ParserConfigurationException ;

import org.w3c.dom.Document ;
import org.xml.sax.SAXException ;

import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileBrowserModel ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileBrowserModel.ModelException ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileNode ;
import cn.com.ZhuPai_CarDv_WIFI.IPCamViewer.FileBrowser.Model.FileNode.Format ;

import android.util.Log ;

public class FileBrowser {

	public enum Action {

		dir ;
	}

	public static final int COUNT_MAX = 16 ;
	
	private final URL mUrl ;
	private final int mCount ;
	public boolean mIsError;
	private boolean mCompleted ;
	private List<FileNode> mFileList = new ArrayList<FileNode>() ;

	public FileBrowser(URL url, int count) {

		mUrl = url ;
		mCount = count < 1 ? 1 : (count > COUNT_MAX ? COUNT_MAX : count) ;
		mCompleted = false ;
		mIsError = false;
	}
	
	public boolean isCompleted() {
		
		return mCompleted ;
	}
	
	public List<FileNode> getFileList() {
		
		List<FileNode> fileList = mFileList ;
		mFileList = new ArrayList<FileNode>() ;
		
		return fileList ;
	}
	
	private static String buildQuery(String directory, Format aFormat, int aCount) {
		
		String action = "action=" + Action.dir ;
		String property = "property=" + directory ;
		String format = "format=" + aFormat.name() ;
		String count = "count=" + aCount ;
		
		return action + "&" + property + "&" + format + "&" + count ;
	}
	
	public static String buildFirstQuery(String directory, Format aFormat, int aCount,int getDataCount) {
		String from;
		if (getDataCount>0){
			from = "from="+String.valueOf(getDataCount);
		}else
			   from = "from=0" ;
		
		return buildQuery(directory, aFormat, aCount) + "&" + from ;
	}

	private Document sendRequest(URL url) {

		HttpURLConnection urlConnection ;
		try {
			urlConnection = (HttpURLConnection) url.openConnection() ;

			urlConnection.setUseCaches(false) ;
			urlConnection.setDoInput(true) ;

			urlConnection.setConnectTimeout(10000) ;
			urlConnection.setReadTimeout(10000) ;

			urlConnection.connect() ;

			int responseCode = urlConnection.getResponseCode() ;

			Log.i("FileBrowser", "responseCode = " + responseCode) ;

			if (responseCode != HttpURLConnection.HTTP_OK) {

				return null ;
			}

			InputStream inputStream = urlConnection.getInputStream() ;
			
			Writer writer = new StringWriter();
			 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            }
            catch(IOException eio1)
            {
            	Log.d("filebrower",eio1.toString());
            }
            finally {
            	inputStream.close();
            }
            String string = writer.toString() ;
            string = string.substring(0, string.lastIndexOf(">") +1);
            Log.d("filebrower",string);
            InputStream updatedStream = new ByteArrayInputStream(string.getBytes("UTF-8"));
            
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance() ;
			//factory.setNamespaceAware(true) ;

			try {
				Document document = factory.newDocumentBuilder().parse(updatedStream) ;
				//FileBrowserModel.printDocument(document) ;
				return document ;

			} catch (SAXException e) {
				e.printStackTrace() ;
			} catch (ParserConfigurationException e) {
				e.printStackTrace() ;
			} finally {

				urlConnection.disconnect() ;
			}
			
		} catch (Exception e) {
			e.printStackTrace() ;
		}

		return null ;
	}

	public void retrieveFileList(String directory, Format format, boolean fromHead,int getDataCount) {

		if (mCompleted && !fromHead) {
			return ;
		}
		mCompleted = false ;
		mFileList.clear() ;
		String query;
		if (getDataCount>=0){
			query = buildFirstQuery(directory, format, mCount,getDataCount)  ;
		}else {
			 query = fromHead ? buildFirstQuery(directory, format, mCount,getDataCount) : buildQuery(directory, format, mCount) ;
		}
		URL url = null ;
		try {
			URI uri = new URI(mUrl.getProtocol(), mUrl.getUserInfo(), mUrl.getHost(), mUrl.getPort(),
					mUrl.getPath(), query, mUrl.getRef()) ;
			url = uri.toURL() ;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (url == null) {
			return ;
		}
		Log.i("FileBrowser", url.toString()) ;
		Document document = sendRequest(url) ;
		if (document == null) {
			mIsError = true;
			return ;
		}
		
		try {
			int amount = FileBrowserModel.parseDirectoryModel(document, directory, mFileList) ;
			
			if (amount != mCount) {
				mCompleted = true ;
			}
			
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	public void retrieveAllFileList(String directory, Format format) {

		String firstQuery = buildFirstQuery(directory, format, mCount,0) ;
		String query = buildQuery(directory, format, mCount) ;
		
		mFileList.clear() ;

		URL firstUrl = null ;
		URL url = null ;
		
		try {
			URI uri = new URI(mUrl.getProtocol(), mUrl.getUserInfo(), mUrl.getHost(), mUrl.getPort(),
					mUrl.getPath(), firstQuery, mUrl.getRef()) ;
			firstUrl = uri.toURL() ;
			
			uri = new URI(mUrl.getProtocol(), mUrl.getUserInfo(), mUrl.getHost(), mUrl.getPort(),
					mUrl.getPath(), query, mUrl.getRef()) ;
			url = uri.toURL() ;
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (firstUrl == null || url == null) {
			
			return ;
		}

		Log.i("FileBrowser", firstUrl.toString()) ;
		Log.i("FileBrowser", url.toString()) ;

		Document document = sendRequest(firstUrl) ;
		if(document == null)
		{
			mIsError = true;
		}
		try {
			while (document != null) {

				int amount = FileBrowserModel.parseDirectoryModel(document, directory, mFileList) ;
				
				if (amount != mCount) {
					mCompleted = true ;
					break ;
				}

				document = sendRequest(url) ;
				if(document == null)
				{
					mIsError = true;
				}
			} ;
			
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
}
