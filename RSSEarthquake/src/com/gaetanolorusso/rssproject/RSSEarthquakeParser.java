package com.gaetanolorusso.rssproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class RSSEarthquakeParser {

	private static final String ns = null;
	
	
	
	public List<Item> parse(InputStream in) throws XmlPullParserException,
			IOException {
		
		try {
			//Log.i("PARSE", "starting parse");
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			
			parser.nextTag();
			
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List readFeed(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		List entries = new ArrayList();
		//Log.i("PARSE", "starting readFeed");
		parser.require(XmlPullParser.START_TAG, ns, "rss");
		
		while (parser.next() != XmlPullParser.END_DOCUMENT ) {
			
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			//Log.i("PARSE", name);
			// Starts by looking for the entry tag
			if (name.equals("item")) {
				//Log.i("PARSE", "Item");
				entries.add(readItem(parser));
				//i++;
			} else {
				//Log.i("PARSE", "skip");
				//skip(parser);
				parser.next();
			}
			/*if (i > 6){
				Log.i("PARSE", "STOP");
				break;
			}*/
		}
		
		return entries;
	}

	private Item readItem(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		//Log.i("PARSE", "starting readItem");
		parser.require(XmlPullParser.START_TAG, ns, "item");
		
		String pubDate = null;
		String title = null;
		String link = null;
		String geolat = null;
		String geolong = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				title = readTitle(parser);
			} else if (name.equals("pubDate")) {
				pubDate = readPubDate(parser);
			} else if (name.equals("link")) {
				link = readLink(parser);
			} else if (name.equals("geo:lat")) {
				geolat = readGeoLat(parser);

			} else if (name.equals("geo:long")) {
				geolong = readGeoLong(parser);
			} else {
				skip(parser);
			}
		}

		return new Item(pubDate, title, link, geolat, geolong);

	}
	
	private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException{
		
		parser.require(XmlPullParser.START_TAG, ns, "title");
	    String pubDate = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "title");
	    return pubDate;
			
	}
	
	private String readGeoLat(XmlPullParser parser) throws IOException, XmlPullParserException{
		
		parser.require(XmlPullParser.START_TAG, ns, "geo:lat");
	    String pubDate = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "geo:lat");
	    return pubDate;
			
	}
	
	private String readGeoLong(XmlPullParser parser) throws IOException, XmlPullParserException{
		
		parser.require(XmlPullParser.START_TAG, ns, "geo:long");
	    String pubDate = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "geo:long");
	    return pubDate;
			
	}
	
	
	
	
	
	
	private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException{
		
		parser.require(XmlPullParser.START_TAG, ns, "pubDate");
	    String pubDate = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "pubDate");
	    return pubDate;
			
	}
	
	private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException{
		
		parser.require(XmlPullParser.START_TAG, ns, "link");
	    String pubDate = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "link");
	    return pubDate;
			
	}
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }

	public static class Item {

		private String pubDate = null;
		private String title = null;
		private String link = null;
		private String geolat = null;
		private String geolong = null;

		public Item(String pubDate, String title, String link, String geolat,
				String geolong) {
			this.pubDate = pubDate;
			this.title = title;
			this.link = link;
			this.geolat = geolat;
			this.geolong = geolong;
		}

		public String getPubDate() {
			return pubDate;
		}

		public void setPubDate(String pubDate) {
			this.pubDate = pubDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getGeolat() {
			return geolat;
		}

		public void setGeolat(String geolat) {
			this.geolat = geolat;
		}

		public String getGeolong() {
			return geolong;
		}

		public void setGeolong(String geolong) {
			this.geolong = geolong;
		}
		
		

	}

}
