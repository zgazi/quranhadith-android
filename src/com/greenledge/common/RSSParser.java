package com.greenledge.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class RSSParser
{
	private final String ns = null;
	private String feedImageUrl = "http://greenledge.com/greenledge/images/greenledge.gif";

	// Parse the inputstream as a list of News Items
	public List<RSSItem> parse(InputStream inputStream) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			xmlPullParser.setInput(inputStream, null);
			xmlPullParser.nextTag();
            return readRSS(xmlPullParser);
	    } catch (Exception e) {
	        //e.printStackTrace();
	    }
		return null;
	}


         public List<RSSItem> readRSS(XmlPullParser parser) throws XmlPullParserException, IOException {

        	    String tagName;

        	    String title = null;
        	    String link = null;
        	    String description = null;
        	    String category = null;
        	    String pubDate = null;
        	    String guid = null;
        	    String imageUrl = null;
        	    String mediaUrl = null;
        	    String mediaType = null;
        	    String source = null;
        	    RSSItem item = null;
                List<RSSItem> itemList = null;
        	 try {

                 int count = 0;
                 int eventType = parser.getEventType();
                 boolean done = false;
                 item = new RSSItem();
                 itemList = new ArrayList<RSSItem>();
                 while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                     tagName = parser.getName();

                     switch (eventType) {
                         case XmlPullParser.START_DOCUMENT:
                             break;
                         case XmlPullParser.START_TAG:
                             if (tagName.equals("item")) {
                                 item = new RSSItem();
                             }
                             if (tagName.equals("title")) {
                                 title = parser.nextText().toString();
                             }
                             if (tagName.equals("link")) {
                                 link = parser.nextText().toString();
                             }
                             if (tagName.equals("description")) {
                                 description = parser.nextText().toString();
                             }
                             if (tagName.equals("category")) {
                                 category = parser.nextText().toString();
                             }
                             if (tagName.equals("pubDate")) {
                                 pubDate = parser.nextText().toString();
                             }
                             if (tagName.equals("guid")) {
                                 guid = parser.nextText().toString();
                             }
                             if (tagName.equals("url")) {
                                 imageUrl = parser.nextText().toString();
                             }
                             if (tagName.equals("source")) {
                                 source = parser.nextText().toString();
                             }
                             if (tagName.equals("enclosure")) {
                            	 mediaUrl = parser.getAttributeValue(null, "url");
                            	 mediaType = parser.getAttributeValue(null, "type");
                             }
                             if ("media:content".equals(tagName)) {
                            	 mediaUrl = parser.getAttributeValue(null, "url");
                             }
                             if ("media:thumbnail".equals(tagName)) {
                            	 imageUrl = parser.getAttributeValue(null, "url");
                             }
                             break;
                         case XmlPullParser.END_TAG:
                             if (tagName.equals("channel")) {
                                 done = true;
                             } else if (tagName.equals("item")) {
                            	//get imageurl from media url
                             	if (mediaUrl != null && imageUrl == null){
                             		String imageExt[] = {".jpg",".png",".gif",".jpeg",".bmp"};
                                 	String mediaExt = mediaUrl.substring(mediaUrl.lastIndexOf("."));
                            		if (Arrays.asList(imageExt).contains(mediaExt))
                            			imageUrl = mediaUrl;
                             	}
                             	if (imageUrl == null) imageUrl = feedImageUrl;
                                 item = new RSSItem(title, description, link, pubDate, imageUrl);
                                 item.setMediaUrl(mediaUrl);
                                 item.setMediaType(mediaType);
                                 itemList.add(item);
                                 imageUrl = null;
                                 mediaUrl = null;
                                 source = null;
                             }
                             break;
                     }
                     eventType = parser.next();
                 }
             } catch (Exception e) {
                 //e.printStackTrace();
             }
        	 Log.d("RSSParser", "Found " + itemList.size() + " items");
             return itemList;
         }

}
