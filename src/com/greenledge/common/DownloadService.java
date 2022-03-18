package com.greenledge.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class DownloadService extends Service {

	private int progress;
	public static boolean isRunning = false;
	public static int phase = 1;
	public static String URL = "";
	public static String URN = "";
	public static final String ACTION_DOWNLOAD_SINGLE = "com.greenledge.action.DOWNLOAD_SINGLE";
	public static final String ACTION_DOWNLOAD_FULL = "com.greenledge.action.DOWNLOAD_FULL";
	public static final String ACTION_STOP_DOWNLOAD = "com.greenledge.action.STOP_DOWNLOAD";

	private String fileName;
	private String base = Environment.getExternalStorageDirectory().getAbsolutePath()
			+File.separator + getApplicationContext().getPackageName()+File.separator;

	public class DownloadBinder extends Binder {
		public DownloadService getService(){
			return DownloadService.this;
		}
	}

	private final IBinder binder = new DownloadBinder();

	@Override
	public IBinder onBind(Intent intent){
		URL = intent.getStringExtra("URL");
		return binder;
	}

	@Override
	public void onCreate(){
		super.onCreate();
	}

	// only runs on pre-2.0 sdks
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		URL = intent.getStringExtra("URL");
		URN = intent.getStringExtra("URN");
		//mode = intent.getBooleanExtra("mode", false);
		handleStart(intent, startId);
	}

	// 2.0+ sdks
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
	    if (intent.getAction().equals("com.greenledge.common.DownloadService")) {
	        URL = intent.getStringExtra("URL");
	        URN = intent.getStringExtra("URN");
	        // do something with the value here
	        handleStart(intent, startId);
	    } else {
	        Log.d("DownloadService", "Received intent with action="+intent.getAction()+"; now what?");
	    }
	    return START_NOT_STICKY;
	}
	
	public void handleStart(Intent intent, int startId){
		DownloadService.isRunning = true;
		new DownloadThread(this).start();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		DownloadService.isRunning = false;
	}

	public void updateProgress(int progress){
		this.progress = progress;
	}

	public int getProgress(){
		return this.progress;
	}

	private class DownloadThread extends Thread {
		private DownloadService service;

		DownloadThread(DownloadService service){
			this.service = service;
		}

		@Override
		public void run(){
			String urlStr = URL;
			fileName = URL.substring(URL.lastIndexOf("/")+1);
			base = URN.substring(1, URN.lastIndexOf("/"));
			try {
				if (urlStr == null) return;
				Log.d("quran_srv", "file url: " + urlStr);

				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();

				conn.setDoOutput(true);
				conn.connect();

				int total = conn.getContentLength();
				Log.d("quran_srv", "total len: " + total);
				
				File file = new File(base, fileName);
				if (file.exists()){
					if (file.length() == total){
						if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) { unzipFile(); return;}
						else return;
					}else {
						Log.d("quran_srv", "deleting partial file found of len: " +	file.length());
						file.delete();						
					}
				}

				FileOutputStream f = new FileOutputStream(file);
				InputStream is = conn.getInputStream();

				int readlen = 0;
				int totalRead = 0;
				byte[] buf = new byte[1024];
				while ((readlen = is.read(buf)) > 0){
					f.write(buf, 0, readlen);
					totalRead += readlen;
					double percent = 100.0 * ((1.0 * totalRead)/(1.0 * total));
					service.updateProgress((int)percent);
				}
			}
			catch (IOException ioe){
				return;
			}
			if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) { unzipFile(); return;}
		}

		protected void unzipFile(){
			try {
				service.updateProgress(0);
				DownloadService.phase++;

				// success, unzip the file...
				FileInputStream is = new FileInputStream(base+fileName);
				ZipInputStream zis = new ZipInputStream(is);

				int ctr = 0;
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null){
					if (entry.isDirectory()){
						zis.closeEntry();
						continue;
					}

					double percentage = 100.0 * ((1.0 * ctr++) / 604.0);

					// ignore files that already exist
					File f = new File(base + entry.getName());
					if (!f.exists()){
						FileOutputStream ostream = new FileOutputStream(base + entry.getName());

						int size;
						byte[] buf = new byte[1024];
						while ((size = zis.read(buf)) > 0)
							ostream.write(buf, 0, size);
						ostream.close();
					}
					zis.closeEntry();
					service.updateProgress((int)percentage);
				}

				zis.close();
				is.close();

				File file = new File(base, fileName);
				file.delete();
			}
			catch (IOException ioe){
				Log.d("quran_srv", "io exception: " + ioe.toString());
			}

			service.stopSelf();
			DownloadService.isRunning = false;
		}
	}
}

