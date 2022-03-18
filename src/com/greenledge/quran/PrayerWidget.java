package com.greenledge.quran;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.greenledge.common.MainApplication;
import com.greenledge.common.NotificationReceiver;
import com.greenledge.common.Utils;
import com.greenledge.quran.R;

public class PrayerWidget extends AppWidgetProvider {
	private static final int[] text = new int[]{R.string.fadjr, R.string.shurooq, R.string.zuhr, R.string.asr, R.string.maghrib, R.string.isha};

	public static SharedPreferences settings;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		setLatestTimetable(context, appWidgetManager, appWidgetIds);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public static void setLatestTimetable(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PrayerWidget.class));
		setLatestTimetable(context, appWidgetManager, appWidgetIds);
	}
	private static void setLatestTimetable(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		context = context.getApplicationContext();
		settings = context.getSharedPreferences("settingsFile", Context.MODE_PRIVATE);
	
		MainApplication settings = MainApplication.getInstance();
		Calendar now = Calendar.getInstance();

		PrayerTime[] prayerTimes = PrayerTime.getPrayerTimes(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH),
				Double.parseDouble(settings.getLatitude()), 
				Double.parseDouble(settings.getLongitude()), 
				Float.parseFloat(settings.getGmt()), 0,
				Integer.parseInt(settings.getMethod()));

		now.add(Calendar.MINUTE, Integer.parseInt(settings.getAlertOffset()));
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		for (int k = 0; k < 5; k++) {
			if (now.equals(prayerTimes[k].getTime())) {
					NotificationReceiver
							.start(context,
									context.getText(R.string.notifPrayer)
											.toString()
											.replaceAll(
													"#",
													""
															.toString()),
									Notification.DEFAULT_ALL);
			}
		}

		//now = Calendar.getInstance();
		now.add(Calendar.MINUTE, -15);  // la selection de la prière reste 15
										// minutes après l'heure
		double julian1 = Utils.calendar2Julian(now);
		Date dd = Utils.julian2Hijir(julian1); 
		
		for(int i = 0; i < appWidgetIds.length; i++) {

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.prayerwidget);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ViewPagerFragmentActivity.class), 0);
		views.setOnClickPendingIntent(R.id.prayerwidget, pendingIntent);

		int idxSelected = R.id.wi_shurooq;
		if (now.before(prayerTimes[1].getCalendar())) {
			idxSelected = R.id.wi_fadjr;
		} else if (now.before(prayerTimes[2].getCalendar())) {
			idxSelected = R.id.wi_zuhr;
		} else if (now.before(prayerTimes[3].getCalendar())) {
			idxSelected = R.id.wi_asr;
		} else if (now.before(prayerTimes[4].getCalendar())) {
			idxSelected = R.id.wi_maghrib;
		} else if (now.after(prayerTimes[4].getCalendar())) {
			idxSelected = R.id.wi_isha;
		}

		views.setTextViewText(R.id.wi_fadjr, prayerTimes[0].getTime());

		views.setTextViewText(R.id.wi_shurooq, prayerTimes[1].getTime());

		views.setTextViewText(R.id.wi_zuhr, prayerTimes[2].getTime());

		views.setTextViewText(R.id.wi_asr, prayerTimes[3].getTime());

		views.setTextViewText(R.id.wi_maghrib, prayerTimes[4].getTime());

		views.setTextViewText(R.id.wi_isha, prayerTimes[5].getTime());

		views.setTextColor(idxSelected, 0xFFFF0000);
		
		views.setTextViewText(R.id.wi_text, "Subhanallah Alhamdulillah Lailaha Illalahu Allahu Akbar"); //QuranActivity.getAyahText(new Ayah()));

		views.setTextViewText(R.id.wi_source, ""+now.get(Calendar.DATE)+"/"+now.get(Calendar.MONTH)+"/"+now.get(Calendar.YEAR)+" ("+dd.day+"/"+dd.month+"/"+dd.year+") @ quranforworld.com");
		
		appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
	}
}
