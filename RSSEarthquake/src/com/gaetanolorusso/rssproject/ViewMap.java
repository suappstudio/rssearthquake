package com.gaetanolorusso.rssproject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;



//Activity for viewing MapFragment
public class ViewMap extends FragmentActivity {

	private GoogleMap mMap;
	double lat = 0;
	double lon = 0;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		lat = getIntent().getDoubleExtra("lat", 0);
		lon = getIntent().getDoubleExtra("lon", 0);
		final String link= getIntent().getStringExtra("link");
		Button btn = (Button) findViewById(R.id.button1);
		//btn.setText(link);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(link));
				startActivity(i);
			}
			
		});
		Log.i("LATLONG", lat + " " + lon);
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title("Marker"));
		CameraUpdate center = CameraUpdateFactory
				.newLatLng(new LatLng(lat, lon));
		CameraUpdate zoom=CameraUpdateFactory.zoomTo(3);
		mMap.moveCamera(center);
	    mMap.animateCamera(zoom);
	}

}
