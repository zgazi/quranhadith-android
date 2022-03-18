package com.greenledge.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

import com.greenledge.quran.R;

public class NotificationReceiver extends BroadcastReceiver {
	private static Context context;
	private static Notification notification;

	public static void start(Context ctx, String msg, int defaults) {
		context = ctx;
		notification = new Notification(R.drawable.ic_launcher,msg,1000);
		notification.tickerText = msg;
		notification.defaults = defaults;
		stopNotification();
		startNotification();		
	}

	public static void stopNotification() {
		if(context != null) ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
	}
	
	private static void startNotification() {
		Intent i = new Intent();
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
		Notification.Builder builder = new Notification.Builder(context)
				.setContentIntent(pendingIntent)
	            .setSmallIcon(R.drawable.ic_launcher)
	            .setContentTitle(notification.tickerText);
		notification = builder.build();
		//notification.setLatestEventInfo(context, context.getString(R.string.app_name), notification.tickerText, PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT));
		notification.contentIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
		notification.deleteIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
		((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notification);

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		// Play athan
		if (intent == null)
			intent = new Intent(context, MediaService.class);
        
		String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	    	switch(Integer.parseInt(type)) {
	    		case 0:
	    			startNotification();
	    		default:
	    	        
	    	        context.startService(intent);
	    	        
	    			NotificationReceiver.stopNotification();
	    		break;
	    	}
	    }

	}
}

