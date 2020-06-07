package com.greenledge.quran;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.greenledge.common.DatabaseHandler;
import com.greenledge.quran.R;

public class MainTabActivity extends TabActivity implements OnTabChangeListener, OnItemClickListener {
	protected TabHost 			tabHost;     // The activity TabHost
	protected String 			titleLang;
	protected SharedPreferences preferences;
	protected DatabaseHandler	db;
	protected Context 			context;
	protected Cursor 			surahCursor;
	protected Cursor 			bookmarkCursor;
	protected ListAdapter 		adapter;
	protected ListView 			surahList;
	protected ListView 			bookmarklist;
	protected Intent			intent;
	protected String 			titleColumn;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.home_acitivity);

	    tabHost 	= getTabHost();

	    // Add views
	    //viewSurahList(); 
	    //viewBookmarkList();
	    
	    // add tabs to tab widget
	    addCustomTab("surah", "Surahs", R.id.surahList);
	    addCustomTab("bookmarks", "Bookmarks", R.id.bookmarkList); 
	    
	    // set current view and change 
	    tabHost.setCurrentTab(0);
	    View currentTab = tabHost.getCurrentTabView();
	    setTabView(currentTab, "PRESSED", "LEFT");
		 
	    // add itemclicklistener
		tabHost.setOnTabChangedListener(this);
		surahList.setOnItemClickListener(this);
		bookmarklist.setOnItemClickListener(this);
		
	    db 	= new DatabaseHandler("quran.sqlite");
	//	copyDBToSDcard();
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	

	private void addCustomTab(String tag, String text, int id)
	{
		 TabHost.TabSpec spec = tabHost.newTabSpec(tag).setIndicator(text).setContent(id);
		 tabHost.addTab(spec);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			Intent intent = new Intent(this, Preferences.class);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.slide_right_show, R.anim.slide_right_hide);
			break;
		case R.id.search: 
			Intent searchIntent = new Intent(this, QuranActivity.class);
			startActivity(searchIntent);
			overridePendingTransition(R.anim.slide_right_show, R.anim.slide_right_hide);
			break;
/*	case R.id.about:
			Intent aboutUsIntent = new Intent(this, WebViewActivity.class);
			startActivity(aboutUsIntent);
			overridePendingTransition(R.anim.slide_left_show, R.anim.slide_left_hide);
			break;*/
		case R.id.menu_share:
			final Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quran app for Android");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "Alsalam Alykom\n" +
					"Download this nice Quran app for your phone. It has 11 translations and many nice features.\n" +
					"Here is the link: https://market.android.com/details?id=com.iandroid.quran");
			startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
			break; 
		case R.id.menu_about:
			Intent marketIntent = new Intent(Intent.ACTION_VIEW);
			marketIntent.setData(Uri.parse("market://details?id=com.iandroid.quran.donate")); 
			startActivity(marketIntent);
			break;
		}
		return true; 
	}

	/* (non-Javadoc)
	 * Display right bg for the selected and unselected tabs
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	@Override
	public void onTabChanged(String tag) {
		View currentTab = tabHost.getCurrentTabView();
		String currentTabTag = tabHost.getCurrentTabTag();
		int pos = tabHost.getCurrentTab();
            tabHost.setCurrentTabByTag(tag.toString());
            tabHost.getChildAt(pos);
		if(currentTabTag.equals(tag))
		{
			setTabView(currentTab, "PRESSED", "LEFT");
			currentTab.setBackgroundResource(R.drawable.tab_widget_bg_pressed_left); 

			View view = tabHost.getTabWidget().getChildAt(pos);
			setTabView(view, "DEFAULT", "RIGHT");
			
		}else
		{
			setTabView(currentTab, "PRESSED", "RIGHT");
	
			View view = tabHost.getTabWidget().getChildAt(pos);
			setTabView(view, "DEFAULT", "LEFT");
		}
	}
	
	/**
	 * Sets the tab view and display the right bg for the tab view based on params.
	 *
	 * @param view the view to apply the bg
	 * @param state the state of the view (DEFAULT, PRESSED)
	 * @param side the side of the view (LEFT, RIGHT)
	 */
	private void setTabView(View view, String state, String side)
	{
		int id = R.drawable.tab_widget_bg_default_left;
		int color = 0x99ffffff;
		
		if(state == "DEFAULT")
		{
			id = R.drawable.tab_widget_bg_default_left;
			color = 0x99000000;
			
			if(side == "RIGHT")
			{
				id = R.drawable.tab_widget_bg_default_right;
			}
		}else if(state == "PRESSED")
		{
			id = R.drawable.tab_widget_bg_pressed_left;
			if(side == "RIGHT")
			{				
				id = R.drawable.tab_widget_bg_pressed_right;
			}
		}

		view.setBackgroundResource(id); 
		view.setBackgroundColor(color);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		switch(adapter.getId()){
		  case R.id.surahList:
			  Intent intent = new Intent(this, QuranActivity.class);
			  surahCursor = (Cursor) surahList.getAdapter().getItem(position);
			  intent.putExtra("surahID", surahCursor.getInt(surahCursor.getColumnIndexOrThrow("surahID")));
			  intent.putExtra("activity", "home");
			  startActivityForResult(intent, 3);
			break;

		  case R.id.bookmarkList:
			  Intent bookmarkIntent = new Intent(this, QuranActivity.class);
			  bookmarkCursor = (Cursor) bookmarklist.getAdapter().getItem(position);
			  bookmarkIntent.putExtra("surahID", bookmarkCursor.getInt(bookmarkCursor.getColumnIndexOrThrow("surahID")));
			  bookmarkIntent.putExtra("bookmark", bookmarkCursor.getInt(bookmarkCursor.getColumnIndexOrThrow("ayahID")));
			  bookmarkIntent.putExtra("activity", "home");
			  startActivityForResult(bookmarkIntent, 2);
		       break;
	  }

		overridePendingTransition(R.anim.slide_left_show, R.anim.slide_left_hide);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            	// preferences results
            	if (requestCode == 1) {
            		viewSurahList();
            		viewBookmarkList();
            	// Bookmark results
            	}else if(requestCode == 2) {
            		viewBookmarkList();
            	}else if(requestCode == 3) {
            		viewBookmarkList();
            	}
           }
	
	private void viewSurahList() {
		surahList = (ListView) findViewById(R.id.surahList);
		
		try
			{
				surahCursor = db.getCursor("SELECT * FROM sura");
			} catch (SQLiteException e)
			{
				Toast toast = Toast.makeText(this, getText(R.string.alert), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show(); 
			}
		View contentView = (View) findViewById(android.R.id.content);
		//adapter = new SurahListAdapter(this, surahCursor, pref, contentView);
		surahList.setAdapter(adapter);
		
	}
	
	private void viewBookmarkList()
	{
		bookmarklist= (ListView) findViewById(R.id.bookmarkList);
		bookmarkCursor = db.getCursor(
				"SELECT qSurah._id, qSurah.surahNameArabic, qSurahContent._id," +
				"qSurahContent.surahID,qSurahContent.ayahID, qSurahContent.surahContentArabic " +
				"FROM qSurahContent " +
				"JOIN qSurah " +
				"ON (qSurah.surahID = qSurahContent.surahID) " +
				"WHERE qSurahContent.bookmark = 1");
		if(bookmarkCursor.getCount() > 0)
			{
//				adapter = new BookmarkList(this, bookmarkCursor);
				bookmarklist.setAdapter(adapter);
			}else
			{
				bookmarklist.setContentDescription(getText(R.string.none));
			} 
	}
}