package com.greenledge.common;

import java.util.ArrayList;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.greenledge.quran.PrayerFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter  {

	private int fCount = 1;
	private String[] feedUrls, feedFiles;
	FragmentManager fManager;
	ArrayList<Fragment> pooledFragments;
	Fragment currentFragment;

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		fManager = fm;
		fCount = 1;
	}
	
	public TabsPagerAdapter(FragmentManager fm, int n) {
		super(fm);
		fManager = fm;
		fCount = n;
	}

	public TabsPagerAdapter(FragmentManager fm, String[] urls) {
		super(fm);
		fManager = fm;
		fCount = urls.length;
		feedUrls = urls;
		if (fm.getFragments() != null) {
	        fm.getFragments().clear();
	    }
	}
	
	public TabsPagerAdapter(FragmentManager fm, String[] urls, String[] files) {
		super(fm);
		fManager = fm;
		fCount = urls.length;
		feedUrls = urls;
		feedFiles = files;
		if (fm.getFragments() != null) {
	        fm.getFragments().clear();
	    }
	}

	@Override
	public Fragment getItem(int index)
	{
		Log.v("TabsPagerAdapter",feedUrls[index]);
		if (index >= 1) return new RSSFragment(index,feedUrls[index], feedFiles[index]);
		if (index==0) return new PrayerFragment();
		return null;
	}

	@Override
	public int getItemPosition(Object item) {
        currentFragment=(Fragment)item;
        pooledFragments=new ArrayList<Fragment>(fManager.getFragments());
        if(pooledFragments.contains(currentFragment))
            return POSITION_NONE;
        else return POSITION_UNCHANGED;
    }

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
	if (currentFragment != object) {
	currentFragment = (Fragment) object;
	}
	super.setPrimaryItem(container, position, object);
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		if (fCount<1) return 1;
		else return fCount;
	}

}
