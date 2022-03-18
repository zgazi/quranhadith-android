package com.greenledge.quran;

import java.util.Calendar;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenledge.common.MainApplication;
import com.greenledge.common.NotificationReceiver;
import com.greenledge.common.Utils;

/**
 * @author Selim Amroune
 * 
 */
public class PrayerFragment extends Fragment implements OnClickListener {
	/** Called when the activity is first created. */
	private Context context;
	private View view;
	private static int ParametreCode = 1;
    private static final double QIBLA_LATITUDE = 21.423333;
    private static final double QIBLA_LONGITUDE = 39.823333;
    private static PrayerFragment instance;
    private static ViewPager viewPager = null;
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//app = MainApplication.getInstance();
		super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu (true);
        setMenuVisibility (true);
        context = getActivity().getBaseContext();
        instance = this;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		if (view == null)
		{
            view = inflater.inflate(R.layout.prayerfragment, container, false);

            refresh();
            NotificationReceiver.start(getContext(), getString(R.string.app_name),
    				Notification.DEFAULT_LIGHTS);
        }
		else
		{
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }

		return view;
	}
	
	// reload time prayer and association with button/picture
	public void refresh() {
		setTimePrayer();
		showHolidays();
		setImageClick();
	}

	// fill prayer time and calendar
	private void setTimePrayer() {
		//Settings settings = Settings.getInstance(this.getContext());
		Calendar now = Calendar.getInstance();
		/*
		PrayerTime[] prayerTimes = PrayerTime.getPrayerTimes(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH),
				settings.getLatitude(), settings.getLongitude(), settings.getGmt2(), 0,
				settings.getMethod());
		*/
		PrayerTime[] prayerTimes = PrayerTime.getPrayerTimes(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH),
				Double.valueOf(MainApplication.getLatitude()), 
				Double.valueOf(MainApplication.getLongitude()), 
				Float.parseFloat(MainApplication.getGmt()), 
				Integer.parseInt(MainApplication.getDst()),	0);
		
		((TextView) view.findViewById(R.id.pt_fadjr)).setText(prayerTimes[0].getTime());
		((TextView) view.findViewById(R.id.pt_shurooq)).setText(prayerTimes[1].getTime());
		((TextView) view.findViewById(R.id.pt_zuhr)).setText(prayerTimes[2].getTime());
		((TextView) view.findViewById(R.id.pt_asr)).setText(prayerTimes[3].getTime());
		((TextView) view.findViewById(R.id.pt_maghrib)).setText(prayerTimes[4].getTime());
		((TextView) view.findViewById(R.id.pt_isha)).setText(prayerTimes[5].getTime());
		((TextView) view.findViewById(R.id.pt_location))
		.setText(Double.toString(MainApplication.getDistance(QIBLA_LATITUDE,QIBLA_LONGITUDE)));
		((TextView) view.findViewById(R.id.pt_qibla))
		.setText(Utils.dms2str(MainApplication.getAngle(QIBLA_LATITUDE,QIBLA_LONGITUDE)));
		//HadithList.Msg msg = HadithList.getInstance().getHadithTxt(-1);
		//((TextView) view.findViewById(R.id.wHadith)).setText(msg.txt);
		//((TextView) view.findViewById(R.id.wHadithSrc)).setText(msg.ref);
	}

	// associate button with action
	private void setImageClick() {
		((ImageView) view.findViewById(R.id.bQuran)).setOnClickListener(this);
		((CompassView) view.findViewById(R.id.bCompassView)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.bName)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.bHadith)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.bCalendar)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.bBook)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.pt_location)).setOnClickListener(this);
		((TableLayout) view.findViewById(R.id.pt_table)).setOnClickListener(this);
	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		if (Build.VERSION.SDK_INT >= 11) { //Build.VERSION_CODES.HONEYCOMB
			menu.add(0, 9, 0, R.string.settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else menu.add(0, 9, 0, R.string.settings).setShortcut('9', 's').setIcon(android.R.drawable.ic_menu_set_as);
		
        return;
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 9:
			try {
			PrayerSettingDialog psd = new PrayerSettingDialog(context);
			psd.setTargetFragment(this, 1);
			psd.show(getFragmentManager(), "dialog");
			} catch (Exception e) {Toast.makeText(context, R.string.alert, Toast.LENGTH_SHORT).show();}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// WakeLock.release();
		if (requestCode == ParametreCode) {
			refresh();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Context getContext() {
		if (context == null) context=getActivity().getBaseContext();
		return context;
	}

	public void setContext(Context ctx) {
		this.context = ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		NotificationReceiver.stopNotification();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (viewPager == null) viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
		switch (v.getId()) {
		case R.id.bQuran:
			//viewPager.setCurrentItem(1);
			startActivity(new Intent(context, QuranActivity.class));
			break;
		case R.id.bHadith:
			viewPager.setCurrentItem(2);
			break;
		case R.id.bName:
			//startActivity(new Intent(mContext, NameAllah.class));
			break;
		case R.id.alertLayout:
			//HadithList.Msg msg = HadithList.getInstance().getHadithTxt(-1);
			//((TextView)findViewById(R.id.wHadith)).setText(msg.txt);
			//((TextView)findViewById(R.id.wHadithSrc)).setText(msg.ref);
			break;
		case R.id.bCalendar:
			//startActivity(new Intent(mContext, DayView.class));
		case R.id.bBook:
			//startActivity(new Intent(mContext, BookView.class));
			viewPager.setCurrentItem(3);
			break;
		case R.id.bCompassView:
		case R.id.pt_location:
		case R.id.pt_table:
			try {
			PrayerSettingDialog psd = new PrayerSettingDialog(context);
			psd.setTargetFragment(this, 1);
			psd.show(getFragmentManager(), "dialog");
			} catch (Exception e) {Toast.makeText(context, R.string.alert, Toast.LENGTH_SHORT).show();}
			break;
		}
	}
	
	private void showHolidays() {
		String[] monthAR=null;
		if (MainApplication.getDictionary("hijri_months")!=null)
			 monthAR = (String[]) MainApplication.getDictionary("hijri_months").toArray();
		if (monthAR == null || monthAR.length <= 11) //12 months
			monthAR = getResources().getStringArray(R.array.hijri_months);
	    
		LinearLayout rl = (LinearLayout) view.findViewById(R.id.alertLayout);

		Calendar c = Calendar.getInstance();
		double julian1 = Utils.calendar2Julian(c);
		Date dd = Utils.julian2Hijir(julian1); 
		int yearH = dd.year;
		int days[]={1,10,12,26,1,26,1,1,10};
		int months[]={1,1,3,7,9,9,10,12,12};
		
		TextView day;
		day = new TextView(getActivity());
		day.setText(getString(R.string.today)+" : "+monthAR[dd.month - 1]+" "+dd.day+" "+dd.year); // month array starts from 0
		day.setPadding(10, 4, 0, 0);
		rl.addView(day);
		this.getActivity().setTitle(monthAR[dd.month - 1]+" "+dd.day+" "+dd.year);
		for (int i = 0; i < days.length; i++)
		{	day = new TextView(getActivity());
			day.setText(getEnglishDate(days[i],months[i],dd.year,c.get(Calendar.YEAR))+" "+monthAR[months[i]-1]+" "+days[i]+" "+dd.year);
		    //day.setTextColor(Color.WHITE);
			// Set the location of your textView.
		    day.setPadding(10, 4, 0, 0);
			rl.addView(day,i);
		}
	    // Show the listview
	    rl.setVisibility(View.VISIBLE);
	}
	
	private String getEnglishDate(final int day, final int month, final int yearH, final int thisYear)
	{
	Date dd= Utils.julian2calendar(Utils.hijir2Julian(day, month, yearH));
	//return Utils.calendar2str(dd);
	return (dd.year < thisYear) ? getEnglishDate(day, month, yearH+1, thisYear) : Utils.calendar2str(dd);
	}

}
