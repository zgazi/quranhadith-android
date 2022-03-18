/*
 * Mirrored.java
 *
 * Part of the Mirrored app for Android
 *
 * Copyright (C) 2010 Holger Macht <holger@homac.de>
 *
 * This file is released under the GPLv3.
 *
 */

package com.greenledge.common;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.Context;
import android.util.Log;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainApplication extends Application {
    private static MainApplication instance;

	public static String APP_NAME;
	public static boolean LOG, ONLINE, PRIVATE;
	
    private CacheHelper cacheHelper;
    private DatabaseHandler dbh;
    
	static public String BASE_CATEGORY = "0";
	static public String FEED_URL = "";
	static public String BASE_DIR = "/Android/data/com.greenledge/";
	static public String FEED_DIR ="";
	public static String LANG = "", COUNTRY = "";
	public static String MEDIA_URL = "";
	private static String longitude="0.0", latitude="0.0", altitude="0.0";
	private static String gmt, dst, alert, alertOffset, method;
	private static String timeOffsets[];
	public static Map<String,List<Object>> dictionary = null;
	
    public static MainApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }
    
    public CacheHelper getCacheHelper() {
        return cacheHelper;
    }

	@Override
	public void onCreate() {
		//Set Temp/Cache File path
        cacheHelper = new CacheHelper(getApplicationContext().getCacheDir());
		APP_NAME = getPackageName().replace("com.greenledge.","");
		FEED_DIR = BASE_DIR + APP_NAME + "/";
		//change keyboard
		//InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
		//imm.showInputMethodPicker();
			
		ONLINE = online() || wifiConnected();

		loadPreferences();
		if (ONLINE){
			if (gmt == "") gmt = Integer.toString(TimeZone.getDefault().getRawOffset()/3600000); //milliseconds to hour
			if (dst == "") dst = Integer.toString(TimeZone.getDefault().getDSTSavings()/3600000); //milliseconds to hour
			LocationService loc = new LocationService(this);
			if (loc != null && loc.canGetLocation) {
				longitude = Double.toString(loc.getLongitude());
				latitude = Double.toString(loc.getLatitude());
			}
			//Log.i("MainApplication",longitude);
		}
		savePreferences();
		
		loadDictionary(LANG);
        instance = this;
	}

	@Override
	public void onTerminate() {
        instance = null;

	}

	public boolean online() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			//if (info == null || !info.isConnectedOrConnecting())
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

    public boolean wifiConnected() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = (NetworkInfo)cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean online = info.isConnected();
        return online;
    }

    public void setOfflineMode(boolean offline) {

		ONLINE = !offline;
	}

	public SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	}

	// Store system locale here, on class creation
	private static final Locale defaultLocale = Locale.getDefault();

	public String getCurrentLanguage() {
		return LANG;
	}

    public void setLanguage(Context ctx)
    {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
      LANG = prefs.getString("prefLanguage", Locale.getDefault().getLanguage());
      setLanguage(ctx, LANG);
      //setLanguage(lang);
    }

    public void setLanguage(String lang) {
		try {
			Resources res = getResources();
		    // Change locale settings in the app.
		    android.content.res.Configuration conf = res.getConfiguration();
		    conf.locale = (lang.length() ==0) ? defaultLocale : new Locale(lang);
		    LANG = (lang.length() == 0) ? defaultLocale.getLanguage() : lang;
		    if (Build.VERSION.SDK_INT >= 17) //Build.VERSION_CODES.JELLY_BEAN_MR1
		      {
		        conf.setLayoutDirection(conf.locale);
		      }
		    res.updateConfiguration(conf, res.getDisplayMetrics());
		} catch (Exception e) {
			Log.e("MainApplication", "error while setting locale " + lang, e);
		}
	}
    
    public static void setLanguage(Context ctx, String lang)
    {
      Configuration cfg = new Configuration();
      if (lang != null && lang.length() != 0)
        cfg.locale = new Locale(lang);
      else
        cfg.locale = Locale.getDefault();

      ctx.getResources().updateConfiguration(cfg, null);
      LANG = lang;
    }

	public String setFeedDir(String name){
		if (name == "" || name==null ) name = "/Android/data/com.greenledge/" + APP_NAME;
		File storageDir;
		String state=Environment.getExternalStorageState();
		// has writable external  storage
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			storageDir = new File(Environment.getExternalStorageDirectory(), name);
		} else {
			if (PRIVATE){
			ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
			storageDir = contextWrapper.getFilesDir().getParentFile();
			} else	storageDir = new File(Environment.getDataDirectory()+"/com.greenledge/" + APP_NAME);
		}
		
		if(!storageDir.exists()) storageDir.mkdirs();
		return storageDir.getAbsolutePath()+File.separator;
	}
	    
	public static String getLatitude() {
		if (latitude == "" || latitude == "0") latitude="0.0"; // sanitize for double
		return (String) latitude;
	}
	
	public static String getLongitude() {
		if (longitude == "" || longitude == "0") longitude="0.0"; // sanitize for double
		return (String) longitude;
	}
	
	public static String getGmt() {
		if (gmt == "" || gmt == "0") gmt="0.0"; // sanitize for float
		return (String) gmt;
	}
	
	public static String getDst() {
		if (dst == "" || dst == "0.0") dst="0"; // sanitize for int
		return (String) dst;
	}
	
	public static float getAngle(double destLatitude, double destLongitude)
	{		
      double rlng =  Utils.deg2rad(destLongitude - Double.parseDouble(longitude));
      
      return Utils.rad2deg(Math.atan2(Math.sin(rlng), Math.cos(Utils.deg2rad(Double.parseDouble(latitude)))* Math.tan( Utils.deg2rad(destLatitude))- Math.sin(Utils.deg2rad(Double.parseDouble(latitude)))* Math.cos(rlng)));
	}
	
    public static double getDistance(double destLatitude, double destLongitude) {

	    double earth_radius = 6371; //6367km or 6371e3 m
	    double dlon = Math.toRadians(Double.parseDouble(longitude)) - Math.toRadians(destLongitude);
	    double dlat = Math.toRadians(Double.parseDouble(latitude)) - Math.toRadians(destLatitude);
	    double $a = Math.pow(Math.sin(dlat / 2), 2) 
    		+ Math.cos(Math.toRadians(destLatitude)) * Math.cos(Math.toRadians(Double.parseDouble(latitude))) * Math.pow((Math.sin(dlon / 2)), 2);
	    double $c = 2 * Math.atan2(Math.sqrt($a), Math.sqrt(1 - $a));
	    return Math.round(earth_radius * $c);
    }

    public void loadPreferences(){
		LOG = getPreferences().getBoolean("prefEnableDebug", true);
		FEED_URL = getPreferences().getString("prefFeedUrl", FEED_URL);
		if (Environment.getExternalStorageState() != null) {
			BASE_DIR = Environment.getExternalStorageDirectory()+"/Android/data/com.greenledge/";
		} else {
			BASE_DIR = Environment.getDataDirectory()+"/com.greenledge/";
		}
		FEED_DIR = BASE_DIR + APP_NAME + File.separator;
		FEED_DIR = getPreferences().getString("prefFeedDir", FEED_DIR);
		LANG = getPreferences().getString("prefLanguage", Locale.getDefault().getLanguage()); //--TODO--
		COUNTRY = getPreferences().getString("prefLocation", Locale.getDefault().getCountry());
		setOfflineMode(getPreferences().getBoolean("prefStartWithOfflineMode", false));

		latitude = getPreferences().getString("prefLatitude", "0.0");
		longitude = getPreferences().getString("prefLongitude", "0.0");
		altitude = getPreferences().getString("prefAltitude", "0.0");
		gmt = getPreferences().getString("prefGMT","");
		dst = getPreferences().getString("prefDST","");
		alert = getPreferences().getString("prefAlert","0");
		alertOffset = getPreferences().getString("prefAlertOffset","0");
		method = getPreferences().getString("prefMethod","0");
    }
    
	public void savePreferences() {
	      SharedPreferences.Editor editor = getPreferences().edit();
	      editor.putString("prefFeedDir", FEED_DIR);
	      editor.putString("prefLocation", COUNTRY);
	      editor.putString("prefLanguage", LANG);
	      editor.putString("prefLatitude", latitude);
	      editor.putString("prefLongitude", longitude);
	      editor.putString("prefAltitude", altitude);
	      editor.putString("prefGMT", gmt);
	      editor.putString("prefDST", dst);
	      editor.putString("prefAlert", alert);
	      editor.putString("prefAlertOffset", alertOffset);
	      editor.putString("prefMethod", method);
	      editor.commit();
		}
	
	public boolean isSpkOn(int param)
	{
		return ((Integer.parseInt(alert) & param) > 0) ;
	}

	public void setAlert(int notification)
	{
		alert = Integer.toString(notification);
	}
	
	public void setTimeOffsets(String[] minutes)
	{
		timeOffsets = minutes;
	}
	
	public String[] getTimeOffsets()
	{
		return timeOffsets;
	}
	public void setAlertOffset(String alert_before) {
		alertOffset = alert_before;
	}
	
	public String getAlertOffset() {
		return alertOffset;
	}
	
	public void setMethod(int cal)	{
		method = Integer.toString(cal);
	}
	public String getMethod()	{
		return method;
	}
	
	public void loadDictionary(String lang) {
		if (dbh == null) dbh = new DatabaseHandler(this);
		dbh.open(FEED_DIR+APP_NAME+".db");
		String sql = "SELECT * FROM dictionary";
		if (lang == null || lang=="") sql+= " WHERE lang='en'"; else  sql+= " WHERE lang='"+lang+"'";
		Cursor c = dbh.getCursor(sql);
		if (c == null) { dbh.close(); return;}
		int columnCount = c.getColumnCount();
		dictionary = new HashMap<String, List<Object>>(columnCount);
		//Put the first value
		dictionary.put("app_name", new ArrayList<Object>());
		dictionary.get("app_name").add(APP_NAME);
	    while (c.moveToNext()) {
		    dictionary.put(c.getString(0), new ArrayList<Object>());
		    List row = dictionary.get(c.getString(0));
	        for (int i = 1; i <= columnCount; ++i) {
	            switch (c.getType(i)) {
	            case Cursor.FIELD_TYPE_NULL:
	                row.add(null);
	                break;
	            case Cursor.FIELD_TYPE_INTEGER:
	                row.add(c.getLong(i));
	                break;
	            case Cursor.FIELD_TYPE_FLOAT:
	                row.add(c.getDouble(i));
	                break;
	            case Cursor.FIELD_TYPE_STRING:
	            	row.add(c.getString(i));
	                break;
	            case Cursor.FIELD_TYPE_BLOB:
	            	row.add(c.getBlob(i));
	                break;
	            }
	        }
	    }
	}
	
	public static List getDictionary(String key) {
		if (dictionary!=null && dictionary.containsKey(key))
			 return dictionary.get(key);
		else return null;
	}
	
	public boolean setFeedUrl(String url, int timeout) {
		if (!url.startsWith("http")) url = "http://"+url; //fix protocol
	    url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
	    
	    Log.i("MainApplication", url);
	    
	    try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        if (200 <= responseCode && responseCode <= 399) 
	        FEED_URL=url;
	    } catch (MalformedURLException e) {
	        return false;
	    } catch (UnknownHostException e) {
	        return false;
	    }catch (IOException e) {
	        return false;
	    }catch (Exception e) {
	        return false;
	    }
		return true;
	}
}


