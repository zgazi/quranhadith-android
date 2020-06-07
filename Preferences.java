package com.greenledge.quran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.provider.SearchRecentSuggestions;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.BaseAdapter;

import com.greenledge.common.MainApplication;

public class Preferences extends PreferenceActivity 
implements OnPreferenceClickListener, OnPreferenceChangeListener, 
SharedPreferences.OnSharedPreferenceChangeListener
{
	
	private boolean currentStatus = true;

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	
	    super.onCreate(savedInstanceState);
	    
    	//Boolean currentStatus = pref.getVowelsColor();
    	
    	// Display preference screen
	    addPreferencesFromResource(R.xml.preferences);
    
	    // Message will warn user about performance slowness 
	    refresh();
	}
	
	@Override
    public void onContentChanged() {
        // put your code here
        super.onContentChanged();
    }
	
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
    	SharedPreferences.Editor editor = sharedPref.edit();
        
        Preference pref = (Preference) findPreference(key);
        String value = sharedPref.getString(key, "");
        pref.setSummary(value);
        editor.putString(key, value); //where ever or whatever new value is 
        editor.commit();
        ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        this.onContentChanged();
        getListView().invalidate();
    }
    
	@Override  
    public void onBackPressed() {  
    	finish();
    	//overridePendingTransition(R.anim.slide_left_show, R.anim.slide_left_hide);
    }
   
	@Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        pref.setSummary(newValue.toString());
        ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        this.onContentChanged();
        getListView().invalidate();
        return true;
    }
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();
		
		if(key.equals("clearSearchHistory")) {
			String title = getString(R.string.search);
			String confirmMessage = getString(R.string.confirm);
			String OK = getString(android.R.string.ok);
			final String historyCleared = getString(R.string.clear);
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(confirmMessage);
			alertDialog.setButton((OK), new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
				   SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getApplicationContext(),getApplicationContext().getPackageName(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
					suggestions.clearHistory();
					Toast toast = Toast.makeText(getApplicationContext(), historyCleared, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
			   }
			});
			alertDialog.show();
		}else if(key.equals("prefVowelizationColor"))
		{
			String title = getString(R.string.share);
			String confirmMessage = getString(R.string.confirm);
			String OK = getString(android.R.string.ok);
			
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(confirmMessage);
			alertDialog.setButton((OK), new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			   }
			});
			alertDialog.show();
		}
		return false;
	}
	
	private void refresh(){
		Preference prefLocation = (Preference) findPreference("prefLocation");
		prefLocation.setSummary(MainApplication.COUNTRY);
		
		Preference prefLat = (Preference) findPreference("prefLatitude");
		prefLat.setSummary(MainApplication.getLatitude());
		
		Preference prefLon = (Preference) findPreference("prefLongitude");
		prefLon.setSummary(MainApplication.getLongitude());
		
		Preference prefGMT = (Preference) findPreference("prefGMT");
		prefGMT.setSummary(MainApplication.getGmt());
		
		// Show message confirming user action to clear search history
		Preference prefDST = (Preference) findPreference("prefDST");
		prefDST.setSummary(MainApplication.getDst());
		prefDST.setOnPreferenceClickListener(this);
		
		// Fix listview background bug
	    getListView().setCacheColorHint(0);
	}
}
