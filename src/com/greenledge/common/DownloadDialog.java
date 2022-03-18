package com.greenledge.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.content.SharedPreferences;

public class DownloadDialog implements OnItemSelectedListener{
	protected Context mContext;
	private Spinner mSpinnerView;
	private File  surahFolder;
	private int mSurahId, mFolder;
	private SharedPreferences mPref;
	private boolean folderExist = false;
	private Toast toastMessage;
	
	public DownloadDialog(Context context, View containerView, int folder, int surahId)
	{
		this.mSpinnerView =  (Spinner) new Spinner(context);
		this.mSurahId = surahId;
		this.mFolder = folder;
		this.mContext = context;
		
		toastMessage = new Toast(mContext);

//		reciterFolder = new File(Environment.getExternalStorageDirectory() + "/iandroidquran/sound/" + mFolder);
		surahFolder = new File(Environment.getExternalStorageDirectory() + "/iandroidquran/sound/" + mFolder +"/" + mSurahId);

		checkExternalStorageState();
	}
	
	/**
	 * Checks if download service is running.
	 *
	 * @return true, if download service is running
	 */
	protected boolean isDownloadServiceRunning() {
	    ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.iandroid.quran.recitation.RecitationDownloadService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Creates necessary folders for the selected reciter and the selected surah.
	 */
	private void createFolders()
	{
		if(!surahFolder.isDirectory())
		{
			surahFolder.mkdirs();
		}
	}
	
	/**
	 * Check external storage state. Display the appropriate message for each
	 * state to notify the user if the storage is accessible or not
	 */
	private void checkExternalStorageState() {

		String state = Environment.getExternalStorageState();

		// Storage is available so display spinner with options to download
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			createFolders();
			createSpinner();
		}
		// Storage is removed
		else if (Environment.MEDIA_REMOVED.equals(state)) {
			//toastMessage.showToast(android.R.drawable.alert_dark_frame, android.R.string.dialog_alert_title);
		}
		// Storage is unmounted, notify user through toast message
		else if (Environment.MEDIA_UNMOUNTED.equals(state)) {
			//toastMessage.show(R.drawable.toast_warning,	R.string.recitationStorageInaccessible);
		} else {
			//toastMessage.show(R.drawable.toast_error, R.string.recitationStorageNotAvialble);
		}
	}
	
	/**
	 * Creates the spinner for download options. If files are already downloaded
	 * will set preference to the selected reciter
	 */
	private void createSpinner() {
		
			if(isDownloadServiceRunning() == false)
			{
				mSpinnerView.setAdapter(new MySpinnerAdapter());
				mSpinnerView.setOnItemSelectedListener(this);
				mSpinnerView.performClick();
			}else
			{ 
				//toastMessage.show(R.drawable.toast_error, R.string.recitationAnotherDownloadService);
			}
			
	}

	/**
	 * Checks if is folder for a certain recited exist. Mostly used externally. 
	 *
	 * @return true, if is folder exist
	 */
	public boolean isFolderExist()
	{
		return folderExist;
	}

	@Override
	public void onItemSelected(AdapterView<?> av, View view, int position,
			long id) {
		switch (position) {
		case 0:
			break;
		case 1:
			Intent fullQuran = new Intent(DownloadService.ACTION_DOWNLOAD_FULL);
			fullQuran.putExtra("recitationSurahFolder", 1);
			mContext.startService(fullQuran);
			break;
		case 2:
			Intent intent = new Intent(DownloadService.ACTION_DOWNLOAD_SINGLE);
			intent.putExtra("recitationSurahFolder", mSurahId);
			mContext.startService(intent);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	public class MySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

	    private List<String> items;
		private	LayoutInflater 	inflater;
		private View 			v;
		
	    public MySpinnerAdapter() {
	    	inflater = LayoutInflater.from(mContext);
	    	
	        items = new ArrayList<String>();
	        items.add(mContext.getString(android.R.string.dialog_alert_title)); // add first dummy item - selection of this will be ignored
	        items.add(mContext.getString(android.R.string.selectAll));
	        items.add("current item");
	        items.add(mContext.getString(android.R.string.no));
	    }

	    @Override
	    public int getCount() {
	        return items.size();
	    }

	    @Override
	    public Object getItem(int aPosition) {
	        return items.get(aPosition);
	    }

	    @Override
	    public long getItemId(int aPosition) {
	        return aPosition;
	    }

	    @Override
	    public View getView(int position, View view, ViewGroup parent) {
	    	v = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
	    	TextView text	= (TextView)v.findViewById(android.R.id.text1);
	     
	        if (position == 0) {
	        	 text.setText(items.get(position).toString());
	        	//text.setBackgroundResource(android.R.drawable..pref_header_grade);
	        } else {
	            text.setText(items.get(position).toString());
	        }
	        return text;
	    }
	}

}
