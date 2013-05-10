package com.gaetanolorusso.rssproject;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ViewMapFrag extends DialogFragment implements  OnInfoWindowClickListener{

	String link;
	double lat;
	double lon;
	String title;
	View mRoot;
	GoogleMap mMap;
	
	private MapView mMapView;
	
	public static ViewMapFrag newInstance(String link, double lat, double lon, String title){
		
		ViewMapFrag f = new ViewMapFrag();
		Bundle data = new Bundle();
		data.putString("link", link);
		data.putDouble("lat", lat);
		data.putDouble("lon", lon);
		data.putString("title", title);
		f.setArguments(data);
		return f;
		
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		link = getArguments().getString("link");
		lat = getArguments().getDouble("lat");
		lon =getArguments().getDouble("lon");
		title= getArguments().getString("title");
		this.setStyle(STYLE_NO_TITLE, 0);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mRoot = inflater.inflate(R.layout.frag_map, null);
		//Button btn = (Button)mRoot. findViewById(R.id.button1);
		mMapView = (MapView) mRoot.findViewById(R.id.map_view);
		mMapView.onCreate(savedInstanceState);
		mMapView.onResume();//needed to get the map to display immediately
		
		try {
     		MapsInitializer.initialize(getActivity());
 		} catch (GooglePlayServicesNotAvailableException e) {
     		e.printStackTrace();
 		}
		setUpMapIfNeeded();
		return mRoot;
	}
	
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			
			mMap = mMapView.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		
		Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(title).snippet("Click for Details").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_quake)));
		marker.showInfoWindow();
		//mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		CameraUpdate center = CameraUpdateFactory
				.newLatLng(new LatLng(lat, lon));
		CameraUpdate zoom=CameraUpdateFactory.zoomTo(4);
		mMap.moveCamera(center);
	    mMap.animateCamera(zoom);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}



	@Override
	public void onInfoWindowClick(Marker marker) {
		// open details in a browser
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(link));
		startActivity(i);
				
	}


	
	
}
