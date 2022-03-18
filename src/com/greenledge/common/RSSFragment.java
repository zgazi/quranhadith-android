package com.greenledge.common;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import android.util.Log;
import android.webkit.MimeTypeMap;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ProgressBar;
import android.app.ProgressDialog;
import android.app.Dialog;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.greenledge.quran.R;
import com.greenledge.quran.MyPdfViewerActivity;

public class RSSFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener
{
	private ProgressBar progressBar;
	private ProgressDialog progressDialog;
	private ListView listView;
	private EditText editSearch;
	private RSSAdapter adapter;
	private FeedSaver feedSaver;
	private View view;
	private static String baseUrl, baseDir, feedUrl, feedFile, fileName, feedFileExt;
	private static String mediaUrl, mediaFile, mediaFileExt, mimeType;
	private static String fileToOpen;
	private static List<String> mediaExt = Arrays.asList("mp3","mp4","ogg","mpg","wmv");
	private static List<String> docExt = Arrays.asList("pdf","html","doc","rtf","txt");
	private List<RSSItem> items;
	private static RSSFragment instance;
	private int index;
	private DownloadManager downloadManager;
	private static MediaPlayer mediaPlayer;
	private static boolean isSearchMode, isMediaOn, isPlayMode, isReadMode;
	private long reference,size;
	private String[] dialogItems = {}, dialogValues = {}, returnValues; //dialog items
	private Bundle bundle;
	
	public RSSFragment(int page, String path){
		index = page;
		if (path == null || path =="") {
			feedUrl = MainApplication.FEED_URL;
			feedFile = MainApplication.FEED_DIR+MainApplication.BASE_CATEGORY+".xml";
		} else if (path != null && path.contains("http")){
			feedUrl = path;
			feedFile = MainApplication.FEED_DIR+path.substring(path.lastIndexOf("/")+1);
		}
		else {feedFile = path; feedUrl = MainApplication.FEED_URL;}
	}

	public RSSFragment(int page, String remoteUrl, String localFile){
		index = page;
		if ((localFile == null || localFile == "") && (remoteUrl == null || remoteUrl == "")){
				localFile = MainApplication.FEED_DIR+MainApplication.BASE_CATEGORY+".xml";
				remoteUrl = MainApplication.FEED_URL+MainApplication.BASE_CATEGORY+".xml";
		} else if (localFile == null || localFile == ""){
				localFile = MainApplication.FEED_DIR+remoteUrl.substring(remoteUrl.lastIndexOf("/")+1);
		} else if (remoteUrl == null || remoteUrl == ""){
			remoteUrl = MainApplication.FEED_URL+MainApplication.BASE_CATEGORY+".xml";
		}
		feedUrl= remoteUrl;
		feedFile = localFile;
	}
	
	public RSSFragment(){
		feedUrl = MainApplication.FEED_URL;
		feedFile = MainApplication.FEED_DIR+"bookmarks.xml"; //offline mode
	}

	public static RSSFragment getInstance(){
        if(instance == null){
            instance = new RSSFragment();
        }
        return instance;
    }

	// newInstance constructor for creating fragment with arguments
    public static RSSFragment newInstance(int page, String title) {
        instance = new RSSFragment();
        Bundle args = new Bundle();
        args.putInt("arg0", page);
        args.putString("arg1", title);
        instance.setArguments(args);
        return instance;
    }
	public void setFeedUrl(String url){
		feedUrl = url;
	}
	public void setFeedFile(String f){
		feedFile = f;
	}
	
	public int getIndex(){
		return index;
	}

/*
	BroadcastReceiver onComplete=new BroadcastReceiver() {
	    public void onReceive(Context ctxt, Intent intent) {
	        long receivedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
   
	            DownloadManager.Query query = new DownloadManager.Query();
	            query.setFilterById(receivedID);
	            Cursor cur = downloadManager.query(query);
	            int index = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
	            if(cur.moveToFirst()) {
	                if(cur.getInt(index) == DownloadManager.STATUS_SUCCESSFUL){
	                    // do something
	                }
	            }
	            cur.close();

	    }
	  };
	  
	BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
	    public void onReceive(Context ctxt, Intent intent) {
	      Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
	    }
	  };
*/	  
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//app = MainApplication.getInstance();
		super.onCreate(savedInstanceState);
		bundle = savedInstanceState;
        setRetainInstance(true);
        setHasOptionsMenu (true);
        setMenuVisibility (true);
		baseDir = MainApplication.FEED_DIR;
		baseUrl = MainApplication.FEED_URL;
/*
        downloadManager=(DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        getActivity().registerReceiver(onComplete,
                         new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getActivity().registerReceiver(onNotificationClick,
                         new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
*/
        instance = this;
	}


	@Override
	public void onDestroy() {
	    super.onDestroy();
		    
	    //getActivity().unregisterReceiver(onComplete);
	    //getActivity().unregisterReceiver(onNotificationClick);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		bundle = savedInstanceState;
		if (view == null)
		{
            view = inflater.inflate(R.layout.rssfragment, container, false);
            // Find the progressbar and listview
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            listView = (ListView) view.findViewById(R.id.listView);
    		// Locate the EditText in .xml
    	    editSearch = (EditText) view.findViewById(R.id.search);

            // set a click listener
            listView.setOnItemClickListener(this);

            refresh();

        }
		else
		{
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }

		return view;
	}

	public final void refresh() {
		Log.v("RSSFragment", "FEED_FILE "+feedFile);
		// Show the progressbar
        progressBar.setVisibility(View.VISIBLE);
        
        //build the menu
        if (items != null){
         dialogItems = new String[items.size()];
         dialogValues = new String[items.size()];
         for (int i=0; i < items.size(); i++) {
        	dialogItems[i] = items.get(i).getTitle();
        	dialogValues[i] = Integer.toString(i+1);
         }
        }
        
        File f = new File(feedFile);
        final long MAXFILEAGE = 2678400000L; // 1 month in milliseconds
        long lastModified = f.lastModified();
        
        if (!f.exists() || f.length() < 100 || lastModified+MAXFILEAGE<System.currentTimeMillis()  ){
			if (feedUrl != null && MainApplication.ONLINE){	
				startService(); // go online and get data
				downloadAsync(feedUrl,feedFile); //download if autodownload set
			} else {
				Toast.makeText(getActivity(), "Please Tutn On Wifi/Mobile Data", Toast.LENGTH_LONG).show();
				//read from database
			}

        }

        else if (f.exists()) {
        	items = FeedSaver.read(feedFile);
        	if (items != null) {
            	// Create a news adapter with the activity and the offline items list
                adapter = new RSSAdapter(getActivity(), items);
                adapter.notifyDataSetChanged();
                // Set the adapter to the list view
                listView.setAdapter(adapter);
                // Remove the progressbar
                //progressBar.setVisibility(View.GONE);
                
        	    // Capture Text in EditText
        	    editSearch.addTextChangedListener(new TextWatcher() {

        	    @Override
        	    public void afterTextChanged(Editable arg0) {
        	    // TODO Auto-generated method stub
        	    String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
        	    adapter.filter(text);
        	    }

        	    @Override
        	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        	    // TODO Auto-generated method stub
        	    }

        	    @Override
        	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        	    // TODO Auto-generated method stub
        	    }
        	    });

        	}
        	else Toast.makeText(getActivity(), "No items found", Toast.LENGTH_LONG).show();
    	    progressBar.setVisibility(View.GONE);
    	    // Show the listview
    	    listView.setVisibility(View.VISIBLE);
        }
        
	}
	// Start the rss service to retrieve the items
	private void startService()
	{
		// Create a new intent of the news service class
        Intent intent = new Intent(getActivity(), RSSService.class);

        // push the NewsService into the intent
        intent.putExtra(RSSService.URL, feedUrl);
        intent.putExtra(RSSService.URN, feedFile);
        intent.putExtra(RSSService.RECEIVER, resultReceiver);

        getActivity().startService(intent);
    }

	//Return data from dialogs
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Stuff to do, dependent on requestCode and resultCode
	    if(requestCode == 1) { // 1 is an arbitrary number, can be any int
	         // This is the return result of your DialogFragment
    		// from dialog
    		if (resultCode == 1) {
    			String[] returnValues = data.getStringArrayExtra("RESULTS");
                Toast.makeText(getActivity().getBaseContext(), returnValues[0],
                Toast.LENGTH_LONG).show();
    			//refresh();
    		}
	     }
	}
	
	//return data from services
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler())
    {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
        	if (resultCode == 1) //Selection
        		returnValues = resultData.getStringArray(MyDialogFragment.RESULTS);
        	if (resultCode == 0)
        		items = (List<RSSItem>) resultData.getSerializable(RSSService.ITEMS);

            // If the items are not null and contains something
            if (items != null && items.size() >= 1)
            {
            	Toast.makeText(getActivity().getBaseContext(), items.size() + " items found", Toast.LENGTH_LONG).show();
            	// Create a news adapter with the activity and the items list
                adapter = new RSSAdapter(getActivity(), items);
                //adapter.notifyDataSetChanged();
                // Set the adapter to the list view
                listView.setAdapter(adapter);
                // Remove the progressbar
                progressBar.setVisibility(View.GONE);
        	    // Show the listview
        	    listView.setVisibility(View.VISIBLE);

        	    // Capture Text in EditText
        	    editSearch.addTextChangedListener(new TextWatcher() {

        	    @Override
        	    public void afterTextChanged(Editable arg0) {
        	    // TODO Auto-generated method stub
        	    String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
        	    adapter.filter(text);
        	    }

        	    @Override
        	    public void beforeTextChanged(CharSequence arg0, int arg1,
        	    int arg2, int arg3) {
        	    // TODO Auto-generated method stub
        	    }

        	    @Override
        	    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
        	    int arg3) {
        	    // TODO Auto-generated method stub
        	    }
        	    });
            }
            else
            {
                Toast.makeText(getActivity().getBaseContext(), "Failed retreving items.",
                Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
    	// Creates a new news adapter from the parent
        RSSAdapter adapter = (RSSAdapter) parent.getAdapter();

        // creates a news item from the item clicked in the list, with the position
        RSSItem item = (RSSItem) adapter.getItem(position);
        mediaUrl = item.getMediaUrl();
        mimeType = item.getMediaType();
        if (mediaUrl != null){
        	mediaFile = baseDir+mediaUrl.substring(mediaUrl.lastIndexOf("/")+1);
        	mediaFileExt = mediaUrl.substring(mediaUrl.lastIndexOf(".")+1);
        }
        Log.v("RSSFragment","mediaFile "+mediaFileExt);
        feedUrl = item.getLink().trim();
        fileName = feedUrl.substring(feedUrl.lastIndexOf("/")+1);
        feedFileExt = feedUrl.substring(feedUrl.lastIndexOf(".")+1);
      	feedFile = MainApplication.getInstance().FEED_DIR+fileName;
      	   	
      	//reset menu options;
      	if (mediaExt.contains(mediaFileExt)) isPlayMode = true;
      	if (docExt.contains(mediaFileExt)) isReadMode = true;
      	getActivity().supportInvalidateOptionsMenu();
      	
        if (feedUrl.contains(".xml") || feedUrl.contains(".rss") || feedUrl.contains(".atom")
        		|| feedUrl.endsWith("/") || feedUrl.contains(".opds"))  {
        	refresh();
        }else if (feedUrl.contains(".php") || feedUrl.contains(".asp") || feedUrl.contains(".jsp")
        		|| feedUrl.contains("/?") || feedUrl.contains(".aspx")) {
	        // Create a new intent
	        Intent intent = new Intent();
	        // set the class to our activity class
	        intent.setClass(getActivity(), com.greenledge.quran.WebViewActivity.class);
	        intent.putExtra("image", item.getImageUrl());
	        intent.putExtra("title", item.getTitle());
	        intent.putExtra("link", item.getLink());
	        //intent.putExtra("description", Html.fromHtml(item.getDescription()).toString());
	        intent.putExtra("pubDate", item.getPubDate());
	        //intent.setAction(Intent.ACTION_VIEW);
	        //intent.setDataAndType(Uri.fromFile(file), type);
	        //intent.setDataAndType(Uri.fromFile(file), mimeType);
	        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        // start the activity with the intent
	        startActivity(intent);
        } else {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimeType = mime.getMimeTypeFromExtension(feedFileExt);
            Log.v("RSSFragment","MimeType : "+mimeType);
            showDownloadDialog(feedUrl,feedFile);
        }
    }

	/*
	 * Bookmark ayah on Long Click. Bookmark drawbale is displayed and mobile vibrates
	 * to notify user that action has happened
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		ImageView bookmarkImage = (ImageView) view.findViewById(R.id.itemImage);
		Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		Timestamp ts = new Timestamp(System.currentTimeMillis());

    	// Creates a new news adapter from the parent
        RSSAdapter adapter = (RSSAdapter) parent.getAdapter();

        // creates a news item from the item clicked in the list, with the position
        RSSItem item = (RSSItem) adapter.getItem(position);
        
		String link = item.getLink();

		// if ayah is bookmarked remove bookmark on long click, otherwise add bookmark with animation
		if (link == "" || link == null) {
			//do nothing
			Animation slideUp = new TranslateAnimation(0, 0, 0, -100);
			slideUp.setDuration(300);
			bookmarkImage.startAnimation(slideUp);
			bookmarkImage.setVisibility(View.GONE);
		} else {
			feedSaver = new FeedSaver();
			feedSaver.save(item);

			Animation slideDown = new TranslateAnimation(0, 0, -100, 0);
			slideDown.setDuration(300);
			bookmarkImage.startAnimation(slideDown);
			bookmarkImage.setVisibility(View.VISIBLE);
		}

		// Vibrate for 70 milliseconds
		long[] pattern = { 200, 70 };
		vibrator.vibrate(pattern, 1);

		return true;
	}
	
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//inflater.inflate(R.menu.news, menu);
		//menu.add(R.id.menu_categories);
		//editsearch.setTextColor(android.R.color.white);
		//editsearch.setFocusableInTouchMode(false);
        //menu.add(0, 0, 1, android.R.string.search_go).setActionView(editsearch).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//menu.add(0, 0, 1, android.R.string.search_go).setActionView(editSearch).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		if(isMediaOn){
			menu.findItem(R.id.menu_play).setIcon(android.R.drawable.ic_media_pause)
			.setTitle(R.string.off);
		} else 	menu.findItem(R.id.menu_play).setIcon(android.R.drawable.ic_media_play)
				.setTitle(R.string.on);
		menu.findItem(R.id.menu_play).setVisible(isPlayMode);
		menu.findItem(R.id.menu_view).setVisible(isReadMode);
		if (dialogItems.length <=0) menu.findItem(R.id.menu_list).setVisible(false);
		else menu.findItem(R.id.menu_list).setVisible(true);
		return;
	}
		
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {

			case R.id.menu_play:
				Log.v("RSSFragment","mediaFile "+mediaFile);
				if (isMediaOn) {
					setRecitationAudioOff();
				} else {
					setRecitationAudioOn();
					File f = new File(mediaFile);
					if (!f.exists() || f.length() < 1000)
						showDownloadDialog(mediaUrl,mediaFile); //download then play
					else play(mediaFile);
				}
			case R.id.menu_view:
				Log.v("RSSFragment","mediaFile "+mediaFile);

				File f = new File(mediaFile);
				if (!f.exists() || f.length() < 1000)
					showDownloadDialog(mediaUrl,mediaFile); //download then open
				else processFile(mediaFile,mimeType);
			case R.id.menu_list:
				
				try {
				AlertDialogFragment df = new AlertDialogFragment(getActivity().getBaseContext(),dialogItems,dialogValues);
				//df.bundle.putParcelable("receiver", resultReceiver);
				//df.setTargetFragment(this, 1);
				df.show(getFragmentManager(), "dialog");
				} catch (Exception e) {Toast.makeText(getActivity().getBaseContext(), R.string.alert, Toast.LENGTH_SHORT).show();}
				break;
	        default:
	            break;
	    }

	    return false;
	}

	private long downloadByManager(String from, String dest){
		downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
		boolean isDownloading = false;
		long id = 0;
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterByStatus(
		    DownloadManager.STATUS_PAUSED|
		    DownloadManager.STATUS_PENDING|
		    DownloadManager.STATUS_RUNNING|
		    DownloadManager.STATUS_SUCCESSFUL);
		android.database.Cursor cur = downloadManager.query(query);
		int col = cur.getColumnIndex(
		    DownloadManager.COLUMN_LOCAL_FILENAME);
		for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
		    isDownloading = isDownloading || (dest == cur.getString(col));
		}
		cur.close();
		if (!isDownloading) {
		    Uri source = Uri.parse(from);
		    Uri destination = Uri.fromFile(new File(dest));
		 
		    DownloadManager.Request request = new DownloadManager.Request(source);
		    request.setTitle("file title");
		    request.setDescription("file description");
		    request.setDestinationUri(destination);
     	   //Set the local destination for the downloaded file to a path within the application's external files directory
     	   request.setDestinationInExternalFilesDir(getActivity(),MainApplication.FEED_DIR,fileName);
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		    request.allowScanningByMediaScanner();
     	   //Restrict the types of networks over which this download may proceed.
     	   request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
     	   //Set whether this download may proceed over a roaming connection.
     	   request.setAllowedOverRoaming(false);

		    id = downloadManager.enqueue(request);
		}
		return id;
	}
	
	private void downloadAsync(String from, String dest){
	// instantiate it within the onCreate method
	progressDialog = new ProgressDialog(getActivity());
	progressDialog.setMessage("Loading please wait...");
	progressDialog.setIndeterminate(true);
	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	progressDialog.setCancelable(true);

	// execute this when the downloader must be fired
	final DownloadTask downloadTask = new DownloadTask(getActivity());
	downloadTask.execute(from,dest);

	progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	    @Override
	    public void onCancel(DialogInterface dialog) {
	        downloadTask.cancel(true);
	    }
	});
	
	}
	
	private void showDownloadDialog(final String sourceUrl, final String destFile) {
		AlertDialog.Builder textSelectorBuilder = new AlertDialog.Builder(getActivity());
		
		File f = new File(destFile);
		if (f.exists()) size=f.length()/1024;
		
		textSelectorBuilder.setTitle(sourceUrl);
		String[] items = null;
		if (size>1) items = new String[] {"Download Again","Open old File"};
		else items = new String[] {"Download & Open"};

		final int selectedIndex[] = {0};
		textSelectorBuilder.setSingleChoiceItems(items,
				0, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichIndex) {
						selectedIndex[0] = whichIndex;
					}
				});

		textSelectorBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						downloadAsync(sourceUrl,destFile);
					}
				});
		textSelectorBuilder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});
		
		
		AlertDialog dialog=textSelectorBuilder.create();
		
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
			return;
			}
		});
		
		dialog.show();
	}
	
	// usually, subclasses of AsyncTask are declared inside the activity class.
	// that way, you can easily modify the UI thread from here
	private class DownloadTask extends AsyncTask<String, Integer, String> {

	    private Context context;
	    //private PowerManager.WakeLock mWakeLock;

	    public DownloadTask(Context context) {
	        this.context = context;
	    }

	    @Override
	    protected String doInBackground(String... sUrl) {
	        InputStream input = null;
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        fileToOpen = sUrl[1];
	        Log.v("RSSFragment","file "+sUrl[0]+sUrl[1]);
	        try {
	            URL url = new URL(sUrl[0]);
	            //conection = url.openConnection();
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = new BufferedInputStream(url.openStream(), 8192);
	            //input = connection.getInputStream();
	            output = new FileOutputStream(sUrl[1]);

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                total += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }
	        } catch (Exception e) {
	            return e.toString();
	        } finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	            } catch (IOException ignored) {
	            }

	            if (connection != null)
	                connection.disconnect();
	        }
	        return null;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        // take CPU lock to prevent CPU from going off if the user 
	        // presses the power button during download
	        //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        //mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,  getClass().getName());
	        //mWakeLock.acquire();
	        progressDialog.show();
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        // if we get here, length is known, now set indeterminate to false
	        progressDialog.setIndeterminate(false);
	        progressDialog.setMax(100);
	        progressDialog.setProgress(progress[0]);
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        //mWakeLock.release();
	        progressDialog.dismiss();
	        if (result != null)
	            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
	        else {
	            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
	            processFile(fileToOpen,mimeType);
	        }
	    }

	}
	
	private void processFile(String file, String mimeType) {
		if (file.contains("mp3")) play(file);
		else if (mimeType != null && mimeType.contains("pdf")){
			
			Intent intent = new Intent(getActivity(), MyPdfViewerActivity.class);
			intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, file);
			startActivity(intent);
		}
	}
	
	private void play(String file){
		if(mediaPlayer!=null){
			try{
				if(mediaPlayer.isPlaying())
					mediaPlayer.stop();
			}catch(IllegalStateException se){}	
		} 
		//mediaPlayer.reset();
		File mp3file= new File(file);	
		
		try {
			mediaPlayer.setDataSource(mp3file.getPath());
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
				}
			});
			mediaPlayer.prepare();
		}
		catch (IOException e) {
			Toast.makeText(getActivity(),"Audio File Not Found in the folder: \""+file+"\" . See HELP for instructions.", Toast.LENGTH_LONG).show();
			//e.printStackTrace();
		}
		catch (IllegalStateException e) {
			Toast.makeText(getActivity(), "Illegal State", Toast.LENGTH_SHORT).show();
			//e.printStackTrace();
		}
	}
	
	private void setRecitationAudioOn(){
		isMediaOn=true;
		Toast.makeText(getActivity(), "Recitation On", Toast.LENGTH_SHORT).show();
		getActivity().supportInvalidateOptionsMenu();
		mediaPlayer = new MediaPlayer();
	}
	
	private void setRecitationAudioOff(){
		isMediaOn=false;
		if(mediaPlayer!=null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
			}
			mediaPlayer.release();
		}
		Toast.makeText(getActivity(), "Recitation Off", Toast.LENGTH_SHORT).show();
		getActivity().supportInvalidateOptionsMenu();
	}

}
