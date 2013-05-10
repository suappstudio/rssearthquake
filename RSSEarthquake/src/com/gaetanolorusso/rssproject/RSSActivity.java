package com.gaetanolorusso.rssproject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;


import com.gaetanolorusso.rssproject.RSSEarthquakeParser.Item;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RSSActivity extends FragmentActivity implements OnItemClickListener {

	

	 public static final String WIFI = "Wi-Fi";
	 public static final String ANY = "Any";
	  // Whether there is a Wi-Fi connection.
	    private static boolean wifiConnected = false;
	    // Whether there is a mobile connection.
	    private static boolean mobileConnected = false;
	    // Whether the display should be refreshed.
	    public static boolean refreshDisplay = true;

	    // The user's current network preference setting.
	    public static String sPref = null;

	    // The BroadcastReceiver that tracks network connectivity changes.
	    private NetworkReceiver receiver = new NetworkReceiver();
	
	
	String rssResult = "";
	boolean item = false;
	String rssUrl = "http://earthquake.usgs.gov/earthquakes/catalogs/eqs7day-M2.5.xml";
	ListView list;
	List<Item> itemList;
	ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rss_activity);
		list = (ListView) findViewById(R.id.listView1);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
		
	}
	
	  @Override
	    public void onStart() {
	        super.onStart();

	        // Gets the user's network preference settings
	        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

	        // Retrieves a string value for the preferences. The second parameter
	        // is the default value to use if a preference value is not found.
	        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

	        updateConnectedFlags();

	        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
	        // display. For example, if the user has set "Wi-Fi only" in prefs and the
	        // device loses its Wi-Fi connection midway through the user using the app,
	        // you don't want to refresh the display--this would force the display of
	        // an error page instead of stackoverflow.com content.
	        if (refreshDisplay) {
	            loadPage();
	        }
	    }
	  
	  private void loadPage() {
	        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
	                || ((sPref.equals(WIFI)) && (wifiConnected))) {
	            // AsyncTask subclass
	            new DownloadXmlTask().execute(rssUrl);
	        } else {
	            showErrorPage();
	            
	        }
	    }

	    // Displays an error if the app is unable to load content.
	    private void showErrorPage() {
	     Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
	    }
	  
	  
	  
	  
	  
	  @Override
	    public void onDestroy() {
	        super.onDestroy();
	        if (receiver != null) {
	            this.unregisterReceiver(receiver);
	        }
	    }

	    // Checks the network connection and sets the wifiConnected and mobileConnected
	    // variables accordingly.
	    private void updateConnectedFlags() {
	        ConnectivityManager connMgr =
	                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
	        if (activeInfo != null && activeInfo.isConnected()) {
	            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
	            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
	        } else {
	            wifiConnected = false;
	            mobileConnected = false;
	        }
	    }
	  
	  
	  
	//AsyncTask class
	public class DownloadXmlTask extends AsyncTask<String, Void, List<Item>> {

		
		@Override
		protected List<Item> doInBackground(String... params) {
			
			try {
				
				return loadXmlFromNetwork(params[0]);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			catch(XmlPullParserException e){
				e.printStackTrace();
			}
			return null;
		}

		
		
		
		@Override
		protected void onPostExecute(List<Item> result) {
			// TODO Auto-generated method stub
			
			progress.setVisibility(View.GONE);
			itemList = result;
			EarthquakeAdapter adapter = new EarthquakeAdapter(RSSActivity.this, R.layout.rss_row, itemList );
			list.setAdapter(adapter);
			list.setOnItemClickListener(RSSActivity.this);
		}



		// method to load xml
		public List<Item> loadXmlFromNetwork(String url)throws IOException, XmlPullParserException{
			
			InputStream stream = null;
			
			RSSEarthquakeParser rss = new RSSEarthquakeParser();
			List<Item> items = null;
				
			try {
				stream = downloadUrl(url);
				items = rss.parse(stream);
			}
			finally{
				if (stream != null){
					stream.close();
				}
				
			}						
			return  items ;
			}
		
		
		private InputStream downloadUrl(String urlString) throws IOException {
			
		    URL url = new URL(urlString);
		    
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setReadTimeout(10000 /* milliseconds */);
		    conn.setConnectTimeout(15000 /* milliseconds */);
		    conn.setRequestMethod("GET");
		    conn.setDoInput(true);
		    conn.connect();
		    return conn.getInputStream();
		}
				
		
	}
	
	
	// adapter for listview
	public class EarthquakeAdapter extends ArrayAdapter<Item>{

		private LayoutInflater layoutInflater;
		private int layout;
		private List<Item> itemList;
		View view;
		TextView tvDate;
		TextView tvTitle;
		
		public EarthquakeAdapter(Context context, int resource,
				 List<Item> objects) {
			super(context, resource,  objects);
			layoutInflater = LayoutInflater.from(context);
			layout = resource;
			itemList = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			view = convertView;
			if (view == null) {
				view = layoutInflater.inflate(layout, parent, false);
		}
			tvDate = (TextView)view.findViewById(R.id.textViewTitle);
			tvTitle = (TextView)view.findViewById(R.id.textView2);
			String title = itemList.get(position).getTitle();
			String mag = title.substring(2, 3);
			int magnitudo = Integer.parseInt(mag);
			setMagnitudoColor(magnitudo);
				
			
			String date = itemList.get(position).getPubDate();
			tvTitle.setText(title);
			tvDate.setText(date);
			
			return view;
		}

		//set backgroundcolor according to magnitudo
		private void setMagnitudoColor(int magnitudo) {
			// TODO Auto-generated method stub
			switch(magnitudo){
			case 1:
			case 2:
				view.setBackgroundColor(Color.WHITE);
				break;
			case 3:
				view.setBackgroundColor(Color.YELLOW);
				break;
			case 4:
				view.setBackgroundColor(Color.rgb(255, 102, 0));
				break;
			case 5:
			case 6:
			case 7:
				view.setBackgroundColor(Color.RED);
				break;
			default:
				view.setBackgroundColor(Color.BLACK);
				tvTitle.setTextColor(Color.WHITE);
				tvDate.setTextColor(Color.WHITE);
			}
		}
		
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		String latS = itemList.get(position).getGeolat();
		String lonS = itemList.get(position).getGeolong();
		Log.i("LATLONG", latS +" "+ lonS);
		Double lat = Double.valueOf(latS);
		Double lon = Double.valueOf(lonS);
		String link = itemList.get(position).getLink();
		String title = itemList.get(position).getTitle();
		//show a DialogFragment with a map Wiew	
		ViewMapFrag f = ViewMapFrag.newInstance(link, lat, lon, title);
		f.show(getSupportFragmentManager(), "map");
		
		//Or open an activity with a mapfragment
		/*Intent i = new Intent(RSSActivity.this, ViewMap.class);
		i.putExtra("lat", lat);
		i.putExtra("lon", lon);
		i.putExtra("link", link);
		startActivity(i);*/
	}
	
	
	// for detect internet connection
	public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Checks the user prefs and the network connection. Based on the result, decides
            // whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                refreshDisplay = true;
               // Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

                // If the setting is ANY network and there is a network connection
                // (which by process of elimination would be mobile), sets refreshDisplay to true.
            } else if (ANY.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.
            } else {
                refreshDisplay = false;
                Toast.makeText(context, "Problem loading data", Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	
	
	
	
	
}	


