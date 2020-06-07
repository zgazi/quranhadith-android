package com.greenledge.quran;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.util.Log;

import com.greenledge.common.MainApplication;
import com.greenledge.common.TabsPagerAdapter;
import com.greenledge.common.RSSFragment;
import com.greenledge.azkar.android.AzkarActivity;

public class ViewPagerFragmentActivity extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

	private FragmentTransaction ft;
	private Fragment currentFragment;
	private List<Fragment> fragments;
	private TabsPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private String[] tabs, urls;
    private TabHost mTabHost;
    private String baseUrl="", feedUrl="", baseDir="", feedDir="";
    private MainApplication app;
    private static boolean _internetReady;
    public static final String MEDIA_URL = "";
    static final int REQ_PICK_CATEGORY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = MainApplication.getInstance();
 		_internetReady = app.online();
        String[] urls = {"quranforworld.com","www.quranforworld.com","tafhimulquran.com","www.tafhimulquran.com"};
        baseUrl = "http://quranforworld.com/"; //initialize
		for(int i = 0; i< urls.length; i++)	{
			if (app.setFeedUrl(urls[i], 60)) { 
				baseUrl = urls[i];
				break;
			}
		}
		baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl+"/";
        baseDir = app.BASE_DIR;
 		baseDir = baseDir.endsWith(File.separator) ? baseDir : baseDir+File.separator;
 		feedDir = baseDir + app.APP_NAME + "/";
 		feedUrl = baseUrl + app.APP_NAME + "/";
 		
        super.onCreate(savedInstanceState);
        
        setTitle(MainApplication.APP_NAME);
        setContentView(R.layout.activity_main);

	    if (Build.VERSION.SDK_INT >= 11) {
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(findViewById(R.id.search));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM  | ActionBar.DISPLAY_SHOW_HOME);
	    }
        // Tab Initialization
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
		String[] tabs = {"Prayer","Quran","Hadith","Books"}; //--TODO--Localize
		String[] tabUrls = new String[tabs.length];
		String[] tabFiles = new String[tabs.length];
		int i=0;
		String lang = MainApplication.LANG;
		Log.i("MainActivity","currentlanguage "+lang);
		for(i = 0; i< tabs.length; i++)
		{
			tabUrls[i] = (lang == "") ? feedUrl+tabs[i]+".xml" : feedUrl+tabs[i]+"-"+lang+".xml";
			tabFiles[i] = (lang == "") ? feedDir+tabs[i]+".xml" : feedDir+tabs[i]+"-"+lang+".xml";
	        // TODO Put here your Tabs
	        addTab(this, this.mTabHost, this.mTabHost.newTabSpec(Integer.toString(i)).setIndicator(tabs[i]));
		}
		
		mTabHost.setOnTabChangedListener(this);
		
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager(),tabUrls,tabFiles);
	    mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(this);
        
        mViewPager.setCurrentItem(0);
        mTabHost.setCurrentTab(0);
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

	/**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case 0: // we set this to 0
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
            return progressDialog;
        default:
            return null;
        }
    }
    
    // Method to add a TabHost
    private void addTab(TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
    }

    // Method to add a TabHost
    private static void addTab(ViewPagerFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(activity));
        tabHost.addTab(tabSpec);
    }
    
    // Manages the Tab changes, synchronizing it with Pages
    @Override
    public void onTabChanged(String tag) {
        int pos = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // Manages the Page changes, synchronizing it with Tabs
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int pos = mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);
    }

    @Override
        public void onPageSelected(int arg0) {
    }


    @Override
	public void onBackPressed() {
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prefConfirmOnExit", true))
			tryExitApp();
		else
			finish();
	}

	private void tryExitApp() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.exit)
				.setMessage(R.string.confirm)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}

						}).setNegativeButton("No", null).show();
	}
	
	public void refresh() {

		if (Build.VERSION.SDK_INT >= 11) { //Build.VERSION_CODES.HONEYCOMB
	            supportInvalidateOptionsMenu();
	    }
	    //setFeed();

		fragments = getSupportFragmentManager().getFragments();
		
		boolean pending = getSupportFragmentManager().executePendingTransactions();
		    //Log.d(TAG, "Any pending : " + Boolean.toString(pending));
		    //Log.d(TAG, "Size : " + Integer.toString(fragments.size()));
		for (Fragment myFragment : fragments){
		    //Log.d(TAG, "Visible Fragment ID: " + myFragment.getId() +" Tag: "+ myFragment.getTag());
			if (((RSSFragment)myFragment).getIndex() == 0 && myFragment instanceof RSSFragment) {
			    myFragment = RSSFragment.getInstance();
			    //((RSSFragment)myFragment).setFeedUrl(feedPath+"quran.xml");
			    ((RSSFragment)myFragment).refresh();
			    mAdapter.notifyDataSetChanged();
			    break;
			} 
			else Log.i("MainActivity", "Visible Fragment Class : " + myFragment.getClass());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.setGroupVisible(R.id.group_online, _internetReady && !_offline_mode);
        //menu.setGroupVisible(R.id.group_offline, !_internetReady || _offline_mode);
        return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		currentFragment = mAdapter.getItem(mViewPager.getCurrentItem());
		boolean pending = getSupportFragmentManager().executePendingTransactions();
		switch (item.getItemId()) {

			case R.id.menu_preferences:

				intent = new Intent(this, Preferences.class);
				startActivityForResult(intent, REQ_PICK_CATEGORY);
				/*
			    ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                if (!pending) {
                	ft.hide(currentFragment);
                	getFragmentManager().beginTransaction().replace(android.R.id.tabcontent, new SettingsFragment()).addToBackStack("settings").commit();
                }
                ft.commit();
                */
				return true;
			case R.id.menu_duahs:

				intent = new Intent(this, AzkarActivity.class);
				startActivityForResult(intent, REQ_PICK_CATEGORY);
				return true;
			
			case R.id.menu_search:

				intent = new Intent(this, QuranActivity.class);
				startActivityForResult(intent, REQ_PICK_CATEGORY);
				return true;
/*
			case R.id.menu_clear:

                app.getCacheHelper().cleanDir();
                refresh();
				return true;

			case R.id.menu_offline:

				_offline_mode = true;

				app.setOfflineMode(true);

				refresh();

				return true;

			case R.id.menu_online:

				_internetReady = app.online();

				if (_internetReady == false){
					Helper.showDialog(this, getString(R.string.please_check_internet));
				}
				else {
					app.setOfflineMode(false);
					_offline_mode = false;
					createUI();
					refresh();
				}

				return true;

			case R.id.menu_refresh:

				refresh();
				return true;

			case R.id.menu_add:

				//Helper.showDialog(this, getString(R.string.about_dialog_message));
				showDetail("Publish Free","http://m.gotoversity.com/index.php?page=item&action=item_add&locale="+app.getCurrentLanguage(),
						"",
						"" );
				return true;
*/
			case R.id.menu_share:
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = getString(R.string.app_name) + "\n"
						+ "http://play.google.com/store/apps/details?id="
						+ getApplicationContext().getPackageName();
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						getString(R.string.app_name));
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				startActivity(Intent.createChooser(sharingIntent,getString(R.string.app_name)));
				return true;
				
			case R.id.menu_about:
				
				//Helper.showDialog(this, getString(R.string.about_dialog_message));
		        intent = new Intent(this, WebViewActivity.class);

		        intent.putExtra("title", R.string.about_us);
		        intent.putExtra("link", feedUrl+"credits.html");
		        //intent.putExtra("description", Html.fromHtml(description));
		        intent.putExtra("description", R.string.app_name);
		        intent.putExtra("pubDate","" );
		        intent.putExtra("image", "http://greenledge.com/images/greenledge.gif");

		        // start the activity with the intent
		        startActivity(intent);
				return true;

			case R.id.menu_close:
				finish();
				System.exit(0);

		}

	  return super.onOptionsItemSelected(item);
	}

}