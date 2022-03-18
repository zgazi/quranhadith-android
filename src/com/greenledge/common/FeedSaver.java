/*
 * FeedSaver.java
 *
 * Part of the Mirrored app for Android
 *
 * Copyright (C) 2010 Holger Macht <holger@homac.de>
 *
 * This file is released under the GPLv3.
 *
 */

package com.greenledge.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.text.TextUtils;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import android.util.Log;
import android.os.Environment;


public class FeedSaver {

    public static final DateFormat RSS822_DATE = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

	static public final String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.greenledge/";

	private static List<RSSItem> articles;
	private static RSSItem article;
	private static File file;
	static private String TAG = "FeedSaver";

	static private String FILE_NAME = "articles.xml";

	public FeedSaver() {
		FILE_NAME = "articles.xml";
	}

	public FeedSaver(File f) {
		file = f;
	}
	
	public FeedSaver(String fname) {
		FILE_NAME = fname;
	}
	
	public boolean save(RSSItem item){
		return true;
	}
	
	public boolean save(String fileName) {
		String dirName;
		if (fileName == null || fileName.length() == 0) { 
			dirName = FILE_DIR;
			fileName = FILE_DIR+FILE_NAME;
		}
		else dirName = fileName.substring(0, fileName.lastIndexOf("/"));
		
		FileOutputStream fos = null;

		File directory = new File(dirName);

		Log.d(TAG, "Saving");

		if (!directory.exists()) directory.mkdirs();

		File f = new File(fileName);

		if (!storageReady()) {
			Log.d(TAG, "SD card not ready");
			return false;
		}

		//we are writing whole rss/atom file
		try {
				f.createNewFile();
			} catch (IOException e) {
				throw new IllegalStateException(e.toString() + f.toString());
		}

		try {
			fos = new FileOutputStream(f);

			fos.write(_startXML().getBytes());
			if (articles != null)
				for (RSSItem article : articles) {
					fos.write(_articleXML(article).getBytes());
				}
			fos.write(_finishXML().getBytes());

		} catch (IOException e) {
				Log.e(TAG, e.toString());
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
						Log.e(TAG, e.toString());
				}
			}
		}

		return true;
	}

	public static List<RSSItem> read(String fileName) {

		String dirName;
		if (fileName == null || fileName.length() == 0) { 
			dirName = FILE_DIR;
			fileName = FILE_DIR+FILE_NAME;
		}
		else dirName = fileName.substring(0, fileName.lastIndexOf("/"));
		
		File directory = new File(dirName);

		if (!directory.exists()) directory.mkdirs();
		
		File f = new File(fileName);

			Log.d(TAG, "Reading saved articles " + fileName);

		if (!f.exists())
			return null;
		else {
			try{
				articles = new RSSParser().parse(new FileInputStream(f));
			}catch (IOException e) {
					Log.d(TAG, "error parsing articles " + e.toString());
			}catch (Exception e) {
					Log.d(TAG, "error parsing articles " + e.toString());
				}
		}

		return articles;
	}

	private String _startXML() {
		String o;

		o = "";
		o += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		o += "<rss version=\"2.0\">\n";
		o += " <channel>\n";
		o += "  <title>Downloads</title>\n";
		o += "  <link>http://feed.gotoversity.com/</link>\n";
		o += "  <image><url>http://gotoversity.com/img/logo-6.jpg</url></image>\n";

		return o;
	}

	private String _startAtomXML() {
		String o;

		o = "";
		o += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		o += "<feed xmlns=\"http://www.w3.org/2005/Atom\">\n";
		o += "<title>Downloads List</title>\n";
		o += "<link href=\"http://feed.gotoversity.com/\"/>\n";
		return o;
	}

	private String _articleXML(RSSItem article) {
		String o = "";
		char quote ='"';

		o += "\n";
		o += " <item>\n";
		o += "  <link>" + TextUtils.htmlEncode(article.getLink()) + "</link>\n";
		o += "  <title>" + TextUtils.htmlEncode(article.getTitle()) + "</title>\n";
		o += "  <description>" + TextUtils.htmlEncode(article.getDescription()) + "</description>\n";

		if (article.getCategory() == null)
			article.setCategory("");

		o += "  <category>" + TextUtils.htmlEncode(article.getCategory()) + "</category>\n";
        if (article.getPubDate() != null) {
            o += "  <pubDate>" + article.getPubDate() + "</pubDate>\n";
        }
        if (article.getSource() != null) {
            o += "  <source>" + article.getSource() + "</source>\n";
        }
        o += "  <image><url>" + TextUtils.htmlEncode(article.getImageUrl()) + "</url></image>\n";
        if (article.getMediaUrl() != null) {
        	o += "  <enclosure url="+ quote + TextUtils.htmlEncode(article.getMediaUrl()) + quote + " type=" + quote + article.getMediaType() + quote + "></enclosure>\n";
        }
		o += " </item>\n";

		return o;
	}

	private String _articleAtomXML(RSSItem article) {
		String o = "";

		o += "\n";
		o += " <entry>\n";
		o += "  <title>" + article.getTitle() + "</title>\n";
		o += "  <id>" + article.getGuid() + "</id>\n";
		o += "  <link>" + article.getLink().toString() + "</link>\n";
		o += "  <summary><![CDATA[" + article.getDescription() + "]]></summary>\n";

		if (article.getCategory() == null)
			article.setCategory("0");

		o += "  <category>" + article.getCategory() + "</category>\n";
        if (article.getPubDate() != null) {
            o += "  <updated>" + article.getPubDate() + "</updated>\n";
        }

		o += "  <content><![CDATA[" + article.getContent() + "]]></content>\n";
		o += " </entry>\n";

		return o;
	}

	private String _finishXML() {
		String end = "";
		end += " </channel>\n";
		end += "</rss>\n";
		return end;
	}

	private String _finishAtomXML() {
		String end = "";
		end += "</feed>\n";
		return end;
	}
	public static boolean storageReady() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;

		return false;
	}
}
