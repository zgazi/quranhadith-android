package com.greenledge.quran;

/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.content.Context;
import android.content.Intent;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class TabListViewActivity extends TabActivity implements OnTabChangeListener {

	private static final String LIST1_TAB_TAG = "List1";
	private static final String LIST2_TAB_TAG = "List2";

	// The two views in our tabbed example
	private ListView listView1, lview;
	private ListView listView2;

	private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablistview);

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		// create some dummy strings to add to the list
		List<String> list1Strings = new ArrayList<String>();
		list1Strings.add("Item 1");
		list1Strings.add("Item 2");
		list1Strings.add("Item 3");
		list1Strings.add("Item 4");
		ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list1Strings);
		for(int c = 0; c < 4; c++){
			 lview = new ListView(this);
			 lview.setId(c+5000);    
			 FrameLayout fl = (FrameLayout) findViewById(android.R.id.tabcontent);
			 fl.addView(lview);
			 lview.setAdapter(aa);
				lview.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView parent, View view, int position, long id) {
						String item = (String) lview.getAdapter().getItem(position);
						if(item != null) {
							Toast.makeText(TabListViewActivity.this, item + " added to list 2", Toast.LENGTH_SHORT).show();
						}
					}
				});
			 tabHost.addTab(tabHost.newTabSpec("tab_test" + String.valueOf(c)).setIndicator("TAB "+ String.valueOf(c)).setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return lview;
					}
				}));

		}
		 
/*
		// setup list view 1
		//listView1 = (ListView) findViewById(R.id.list1);
		listView1 = new ListView(this);
		
		listView1.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, list1Strings));

		// setup list view 2
		//listView2 = (ListView) findViewById(R.id.list2);
		listView2 = new ListView(this);

		// create some dummy strings to add to the list (make it empty initially)
		List<String> list2Strings = new ArrayList<String>();
		listView2.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, list2Strings));

		// add an onclicklistener to add an item from the first list to the second list
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				String item = (String) listView1.getAdapter().getItem(position);
				if(item != null) {
					((ArrayAdapter) listView2.getAdapter()).add(item);
					Toast.makeText(TabListViewActivity.this, item + " added to list 2", Toast.LENGTH_SHORT).show();
				}
			}
		});

		tabHost.addView(listView1);
		tabHost.addView(listView2);
		
		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView1;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG).setIndicator(LIST2_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView2;
			}
		}));
*/
    }

	/**
	 * Implement logic here when a tab is selected
	 */
	public void onTabChanged(String tag) {
		if(tag.equals(LIST2_TAB_TAG)) {
			//do something
			Toast.makeText(TabListViewActivity.this, LIST2_TAB_TAG + " added to list 2", Toast.LENGTH_SHORT).show();
		}
		else if(tag.equals(LIST1_TAB_TAG)) {
			//do something
			Toast.makeText(TabListViewActivity.this, LIST1_TAB_TAG + " added to list 1", Toast.LENGTH_SHORT).show();

		}
	}
}
