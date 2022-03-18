package com.greenledge.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadBitmap extends AsyncTask<ImageView, Void, Bitmap> {
	ImageView imageView = null;

	@Override
	protected Bitmap doInBackground(ImageView... imageViews) {
	    this.imageView = imageViews[0];
	    return download_Image((String)imageView.getTag());
	}

	@Override
	protected void onPostExecute(Bitmap result) {
	    imageView.setImageBitmap(result);
	}


	private Bitmap download_Image(String url) {
		 Bitmap bmp =null;
		 if (url != null) {
		 	try {
	            byte[] data = MainApplication.getInstance().getCacheHelper().loadCached(url, true);
	            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
	            if (null != bmp)
	                return bmp;
	        } catch (IOException e) {
	            if (MainApplication.LOG)
	                Log.e("DownloadBitmap", e.toString());
	        }
	        try{
	            URL ulrn = new URL(url);
	            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
	            InputStream is = con.getInputStream();
	            bmp = BitmapFactory.decodeStream(is);
	            if (null != bmp)
	                return bmp;

	        }catch(UnknownHostException e){}
	        catch(Exception e){}
		}
	    return bmp;
	}
}
