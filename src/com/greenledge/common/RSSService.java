package com.greenledge.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.File;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class RSSService extends IntentService
{
	//public static final String NEWS_LINK = "http://news.yahoo.com/rss/entertainment";
	public static final String URL = "url";
	public static final String URN = "urn";
	public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";

	private FileInputStream fis;
	private static InputStream is;
	private static final int BUFFER_SIZE = 1024 * 4;
	
	public RSSService()
	{
		super("RSSService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.d("RSSService", RSSService.URL + " Feed Service started");
		List<RSSItem> items = null;
		try
		{
			RSSParser parser = new RSSParser();
			String url = intent.getStringExtra(RSSService.URL); //--TODO--
			String urn = intent.getStringExtra(RSSService.URN); //--TODO--
			
			//is = getInputStream(url);
			is = new BufferedInputStream(new URL(url).openStream(), 8192);
				
				/*
	            // opens an output stream to save into file
	            OutputStream os = new FileOutputStream(f);

	            int bytesRead = -1;
	            byte[] buffer = new byte[BUFFER_SIZE];
	            while ((bytesRead = is.read(buffer)) != -1) {
	                os.write(buffer, 0, bytesRead);
	            }

	            os.close();
				*/
				//IOHelper.copy(is,f);
			items = parser.parse(is);
			is.close();//--TODO--
		}
		//catch (XmlPullParserException exception){Log.w(exception.getMessage(), exception);}
		catch(UnknownHostException e){
		Log.w("RSSService", "UnknownHostException while retrieving the input stream", e);}
		catch (IOException exception){Log.w(exception.getMessage(), exception);}
		catch (Exception exception) {Log.w(exception.getMessage(), exception);}

		
		Bundle bundle = new Bundle();
		bundle.putSerializable(ITEMS, (Serializable) items);
		ResultReceiver resultReceiver = intent.getParcelableExtra(RECEIVER);
		resultReceiver.send(0, bundle);

	}

	// Get the input stream specified by the link
	public InputStream getInputStream(String link)
	{
		try
		{
			// Create a URL of the link
			URL url = new URL(link);
			// Open a connection and get the input
			return url.openConnection().getInputStream();
			
		} catch(UnknownHostException e){
			Log.w("RSSService", "UnknownHostException while retrieving the input stream", e);
            return null;
		}
		catch (IOException e)
		{
			Log.w("RSSService", "Exception while retrieving the input stream", e);
            return null;
		}
	}

}
