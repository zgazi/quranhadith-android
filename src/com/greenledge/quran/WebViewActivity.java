package com.greenledge.quran;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.greenledge.common.IOHelper;
import com.greenledge.common.MainApplication;

import android.os.AsyncTask;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.ProgressDialog;

import android.support.v4.content.FileProvider;
import android.content.res.AssetManager;

//import com.androidquery.AQuery;

import android.net.Uri;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class WebViewActivity extends Activity
{
	String link;
	String mediaType;
	String fileName;
	private boolean _online;
	private WebView _webview;
	private MainApplication app;
	private ProgressDialog pd;
	private View backButton, refreshButton, forwardButton;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		app =  MainApplication.getInstance();
		_online = app.online();

		setContentView(R.layout.activity_web);
		 
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		//final View contentView = findViewById(R.id.newsDescription);
		backButton = findViewById(R.id.back_button);
		refreshButton = findViewById(R.id.refresh_button);
		forwardButton = findViewById(R.id.forward_button);

		link = getIntent().getStringExtra("link");

        if (!link.startsWith("http")) link = "http://" + link;

    	if (fileName == null || "".equals(fileName)) fileName = link.substring(link.lastIndexOf("/")+1);
    	String ext = fileName.substring(fileName.lastIndexOf(".")+1);

    	if (mediaType == null || "".equals(mediaType) && ext != null) {
	    	if (ext.equalsIgnoreCase("html")) 		mediaType = "text/html";
	    	else if (ext.equalsIgnoreCase("pdf"))	mediaType = "application/pdf";
	    	else if (ext.equalsIgnoreCase("mp4"))	mediaType="video/mp4";
	    	else if (ext.equalsIgnoreCase("mp3")) 	mediaType="audio/mpeg3";
	    	else if (ext.equalsIgnoreCase("jpg")) 	mediaType="image/jpeg";
	    	else mediaType = "text/html";
    	}

	    //Button myButton = (Button) findViewById(R.id.refresh_button);
	    //if( mediaType.contains("application")) myButton.setText("Read");
	    //else if( mediaType.contains("image")) myButton.setText("View");
	    //else if( mediaType.contains("text")) myButton.setText("Refresh");
	    //else myButton.setText("Play");

		_webview = (WebView) findViewById(R.id.webview);
	
		_webview.setWebViewClient(new myWebClient());
		_webview.setNetworkAvailable(_online);
        _webview.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        _webview.getSettings().setAllowFileAccess( true );
        _webview.getSettings().setAppCacheEnabled( true );
        _webview.getSettings().setJavaScriptEnabled( true );
        _webview.getSettings().setSupportZoom(true);
        //_webview.getSettings().setPluginsEnabled(true);
        _webview.getSettings().setLoadWithOverviewMode(true);
        //_webview.getSettings().setUseWideViewPort(true);
        _webview.getSettings().setBuiltInZoomControls(true);
        _webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        _webview.setScrollbarFadingEnabled(true);
        _webview.getSettings().setLoadsImagesAutomatically(true);
        _webview.setClickable(true);
        _webview.setFocusableInTouchMode(true);

        //if ( _online ) // loading online
        //	_webview.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default
        //else
            _webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
 

            //if (MDebug.LOG) Log.e("WebViewActivity",link);

        _webview.loadUrl(link);
		
		backButton.setOnTouchListener(mDelayHideTouchListener);
		refreshButton.setOnTouchListener(mDelayHideTouchListener);
		forwardButton.setOnTouchListener(mDelayHideTouchListener);
		
	}

	 public class myWebClient extends WebViewClient
	 {
	 @Override
	 public void onPageStarted(WebView view, String url, Bitmap favicon) {
	 // TODO Auto-generated method stub
	 super.onPageStarted(view, url, favicon);
	 pd = new ProgressDialog(WebViewActivity.this);
     pd.show();
     if(_webview.canGoBack())
     {
         backButton.setEnabled(true);
     }
     else
     {
         backButton.setEnabled(false);
     }
     if(_webview.canGoForward())
     {
         forwardButton.setEnabled(true);
     }
     else
     {
         forwardButton.setEnabled(false);
     }
	 }
	 @Override
	 public boolean shouldOverrideUrlLoading(WebView view, String url) {
	 // TODO Auto-generated method stub
	 view.loadUrl(url);
	 return true;
	 }
	 @Override
	 public void onPageFinished(WebView view, String url) {
	 // TODO Auto-generated method stub
	 super.onPageFinished(view, url);
	 pd.dismiss();
	 }
	}

	 
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
		    switch(view.getId()){

		      case R.id.back_button: /** Start a new Activity MyCards.java */
		    	  if(_webview.canGoBack()) _webview.goBack();
		        break;

		      case R.id.refresh_button: /** AlerDialog when click on Exit */
		        _webview.loadUrl(link.trim());
		        break;

		      case R.id.forward_button: /** AlerDialog when click on Exit */
		    	  if(_webview.canGoForward()) _webview.goForward();
			     break;
		    }

			return true;
		}
	};




	@Override
	public Object onRetainNonConfigurationInstance() {
	    //if (MDebug.LOG)
	            Log.d("WebViewActivity", "onRetainNonConfigurationInstance");
	    return _webview;
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.setGroupVisible(R.id.group_online, _online);
        //menu.setGroupVisible(R.id.group_offline, !_online);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	        switch (item.getItemId()) {
	            case R.id.menu_share:
	                shareUrl(this, link);
	                return true;

	            case R.id.menu_play:

	                // get the content, just to be sure it has been downloaded, give false for internet state to
	                // make sure article is trimmed

	                if (link != null) 	save(link);

	                return true;

	            case R.id.menu_view:
	            	
	            	open(link);
	                return true;

	        }
	        return false;
	    }

	private static void shareUrl(Context context, String url) {
	        Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_TEXT, url.toString());

	        context.startActivity(Intent.createChooser(intent, context.getString(R.string.app_name)));
	}

	private void open(String link) {
	    	Intent intent;
        	if (fileName == null) fileName = link.substring(link.lastIndexOf("/")+1);
        	String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        	String mimetype = mediaType;
        	if (ext.equalsIgnoreCase("html")) mimetype = "text/html";
        	else if (ext.equalsIgnoreCase("pdf")) mimetype = "application/pdf";
        	else if (ext.equalsIgnoreCase("mp4")) mimetype = "video/mp4";
        	else if (ext.equalsIgnoreCase("mp3")) mimetype = "audio/mpeg3";
        	else if (ext.equalsIgnoreCase("jpg")) mimetype = "image/jpeg";
        	else ext = ".pdf";

        	//if (MDebug.LOG) 
        		Log.d("NewsActivity", "CacheDir:"+getApplicationContext().getCacheDir()+"/"+fileName);

        	if (!link.startsWith("http")) link = "http://" + link;
        	Uri uri = Uri.parse(link);
            intent = new Intent(Intent.ACTION_VIEW);

            // concatenate the internal cache folder with the document its path and filename
            //final File f = new File(getApplicationContext().getCacheDir(),app.getCacheHelper().md5(link));
            final File f = new File(getApplicationContext().getCacheDir(), fileName);
            //final File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/example.pdf");
            //final File f = getFile(link);
            //fileName = "file://" + getApplicationContext().getCacheDir().getAbsolutePath() + "/" + fileName;

            if (!f.exists()) {
	            if (_online) intent.setDataAndType(uri,mimetype);
	            else {
            		Log.e("NewsActivity", "copying from assets: " + f.toString());
                	AssetManager assets=getResources().getAssets();
                	try {

                		IOHelper.copy(assets.open("test."+ext), f);
                	}
                	catch (Exception e) {
                	Log.e("NewsActivityr", "Exception copying from assets", e);
                	}
	            }
            } else {
            	// let the FileProvider generate a Content URI for this private file
            	uri = FileProvider.getUriForFile(getApplicationContext(), "com.greenledge.mobile", f);
            	//Log.e("NewsActivity", "File Uri: " + uri.toString());
            	intent.setDataAndType(uri, mimetype);
            	//intent.setDataAndType(Uri.fromFile(f), mimetype);
            	intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            }
            startActivity(intent);
	 }


	public File getTempFile(Context context, String url) {
	    File file = null;
	    try {
	        fileName = Uri.parse(url).getLastPathSegment();
	        file = File.createTempFile(fileName, null, context.getCacheDir());
	    } catch (IOException e) {
	        // Error while creating file
	    }
	    return file;
	}

	public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        fileName=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(getApplicationContext().getCacheDir(), fileName);
        return f;
    }

	private void save(String fileURL){
		int BUFFER_SIZE = 4096;

		new AsyncTask<Void, Void, String>() {
			private static final int BUFFER_SIZE = 4096;

			@Override
			protected String doInBackground(Void... items) {
			    try {
			    	return downloadFile(link.trim(),getApplicationContext().getCacheDir().getAbsolutePath());
			    } catch (IOException ex) {
			        //ex.printStackTrace();
			    }
			    return "";
			}

			@Override
			protected void onPostExecute(String result) {
			    link = result;
			    return;
			}

		    public String downloadFile(String fileURL, String saveDir) throws IOException {
		        URL url = new URL(fileURL);
		        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		        int responseCode = httpConn.getResponseCode();
		        String fileName = "";
		        // always check HTTP response code first
		        if (responseCode == HttpURLConnection.HTTP_OK) {
		            String disposition = httpConn.getHeaderField("Content-Disposition");
		            String contentType = httpConn.getContentType();
		            int contentLength = httpConn.getContentLength();

		            if (disposition != null) {
		                // extracts file name from header field
		                int index = disposition.indexOf("filename=");
		                if (index > 0) {
		                    fileName = disposition.substring(index + 10,
		                            disposition.length() - 1);
		                }
		            } else {
		                // extracts file name from URL
		                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
		                        fileURL.length());
		            }

		            //System.out.println("Content-Type = " + contentType);
		            //System.out.println("Content-Disposition = " + disposition);
		            //System.out.println("Content-Length = " + contentLength);
		            //System.out.println("fileName = " + fileName);

		            // opens input stream from the HTTP connection
		            InputStream inputStream = httpConn.getInputStream();
		            String saveFilePath = saveDir + File.separator + fileName;

		            // opens an output stream to save into file
		            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

		            int bytesRead = -1;
		            byte[] buffer = new byte[BUFFER_SIZE];
		            while ((bytesRead = inputStream.read(buffer)) != -1) {
		                outputStream.write(buffer, 0, bytesRead);
		            }

		            outputStream.close();
		            inputStream.close();

		            //System.out.println(saveFilePath + " downloaded");
		        } else {
		            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		        }
		        httpConn.disconnect();
		        return fileName;
		    }

		}.execute();
	}
}
