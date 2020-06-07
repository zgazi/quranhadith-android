package com.greenledge.quran;

import android.app.ActivityGroup;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.content.Context;
import android.content.Intent;
import java.lang.reflect.Type;

public class TabFlipperActivity extends ActivityGroup implements OnTabChangeListener, OnClickListener {

    Button doSomething;
    TabHost tabHost;
    ViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablayout);

        flipper = new ViewFlipper(getApplicationContext());
        flipper.setOnClickListener(this);

        //tabHost = getTabHost();
        //tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Quran").setContent(R.id.tab1));
        //tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Hadith").setContent(R.id.tab2));
        //tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Books").setContent(R.id.tab3));
        //tabHost.setCurrentTab(0);
        
        createTabs();

    }
/*
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabHost = getTabHost(); //new TabHost(this,null);
        TabWidget tabWidget = new TabWidget(this);
        tabWidget.setId(android.R.id.tabs);
        tabHost.addView(tabWidget);

        FrameLayout tabcontentFrame = new FrameLayout(this);
        tabcontentFrame.setId(android.R.id.tabcontent);

        tabHost.addView(tabcontentFrame);


        tabHost.setup();

        LinearLayout lay = new LinearLayout(this);
        lay.setId(1);
        tabHost.addView(lay);
        setContentView(tabHost);
        
        //TabSpec spec = tabHost.newTabSpec(" ");
        //spec.setContent(android.R.id.content);
        //tabHost.addTab(spec);
        createTabs();
    }
*/
    
    // Manages the Tab changes, synchronizing it with Pages
    public void onTabChanged(String tag) {
        int pos = this.tabHost.getCurrentTab();
        flipper.setDisplayedChild(pos);
    }
    
    @Override
    public void onClick(View v) {

        // show a toast in second tab
        if (v == doSomething) {
            Toast.makeText(getApplicationContext(), "doing something", Toast.LENGTH_SHORT).show();
        }

        // toggle TextView in first tab
        if (v == flipper) {
            flipper.showNext();
            tabHost.setCurrentTabByTag(v.getTag().toString());
            tabHost.getChildAt(flipper.indexOfChild(flipper.getCurrentView()));
        }
    }

    private void createTabs()
    {
            tabHost = (TabHost)findViewById(android.R.id.tabhost);
    		//tabHost = getTabHost(); //when tabactivity
            tabHost.setup();
            //tabHost.setOnTabChangedListener(this);
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String arg0) {
                    //updateUi();
                }
            });

            // TAB 1
            //tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Quran",getResources().getDrawable(R.drawable.icon1)).setContent(R.id.tab1));
            // TAB 2
            //tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Hadith",getResources().getDrawable(R.drawable.icon2)).setContent(R.id.tab2));
            //tabHost.setBackgroundColor(Color.WHITE);
            //createTab(R.id.tab1, "tab1","Quran",R.drawable.icon1);
            //createTab(R.id.tab2, "tab2","Hadith",R.drawable.icon2);
            createTab(R.layout.tablistview, "tab1","Quran");
            createTab(R.layout.tablistview, "tab2","Hadith");
            tabHost.setCurrentTabByTag("tab1"); // Should always set content to second
    }

    private void createTab(int res, final String tag, Intent intent) {
        View view = LayoutInflater.from(tabHost.getContext()).inflate(res, null);
        view.setTag(tag);
        TabSpec ts = tabHost.newTabSpec(tag);
        ts.setIndicator(view);
        ts.setContent(intent);
        tabHost.addTab(ts);
}

    private void createTab(final int res, String tag, String label) {
        TabSpec ts = tabHost.newTabSpec(tag).setIndicator(label);
        //ts.setContent(res);
        ts.setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return (View) findViewById(res);
			}
		});
        tabHost.addTab(ts);
}

    
    private void createTab(Class<?> contentType, String tag, String label, int drawableId )
    {
        Intent intent = new Intent().setClass(this, contentType);
        //intent.AddFlags(ActivityFlags.NewTask);
        tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(label, getResources().getDrawable(drawableId)).setContent(intent));
    }
    
    private void createTab(String contentName, String tag, String label, int drawableId )
    {
    	Intent intent = new Intent().setClassName(this, contentName);
        tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(label, getResources().getDrawable(drawableId)).setContent(intent));
    }
    
    private void createTab(int resId, String tag, String label, int drawableId )
    {
        tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(label, getResources().getDrawable(drawableId)).setContent(resId));
    }
}