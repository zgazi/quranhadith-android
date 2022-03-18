package com.greenledge.common;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener{

	 private final Context mContext;

	    // flag for GPS status
	    boolean isGPSEnabled = false;

	    // flag for network status
	    boolean isNetworkEnabled = false;
	    boolean isPassiveEnabled = false;
	    boolean canGetLocation = false;

	    Location location=null; // location
	    double latitude, longitude, altitude;
	    float speed;
	    long time;

	    // The minimum distance to change Updates in meters
	    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	    // The minimum time between updates in milliseconds
	    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	    // Declaring a Location Manager
	    protected LocationManager locationManager;

	    public LocationService (Context context) {
	        this.mContext = context;
	        location = getLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();
            }
	    }
	    
		public static Location getCurrentLocation(Context context) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setCostAllowed(true);

			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			Location currentLocation = null;
			try {
				currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
				if(currentLocation == null) {
					criteria.setAccuracy(Criteria.ACCURACY_COARSE);
					currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
				}
			} catch(Exception ex) {
				// GPS and wireless networks are disabled
			}
			return currentLocation;
		}

	    public Location getLocation() {
	        try {
	            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

	            //if (locationManager != null){
                //Criteria criteria = new Criteria();
                //location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
	            //}
	            // getting GPS status
	            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	            // getting network status
	            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	            isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

	            if (!isGPSEnabled && !isNetworkEnabled && !isPassiveEnabled) {
	                // no network provider is enabled
	            	Toast.makeText(mContext, "No Location Provider Found", Toast.LENGTH_SHORT).show();
	            } else {
	                this.canGetLocation = true;
	                // First get location from Network Provider
	                if (isNetworkEnabled && location==null)
	                {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.NETWORK_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    if (locationManager != null) {
	                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    }
	                    Toast.makeText(mContext, "Network Provider Found", Toast.LENGTH_SHORT).show();
	                }
	                // if GPS Enabled get lat/long using GPS Services
	                if (isGPSEnabled)
	                {
	                    if (location == null)
	                    {
	                        locationManager.requestLocationUpdates(
	                                LocationManager.GPS_PROVIDER,
	                                MIN_TIME_BW_UPDATES,
	                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                        if (locationManager != null)
	                        {
	                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        }
	                    }
	                    Toast.makeText(mContext, "GPS Provider Found", Toast.LENGTH_SHORT).show();
	                }
	                else
	                	Toast.makeText(mContext, "GPS FAILED", Toast.LENGTH_SHORT).show();
	            }

	        } catch (Exception e) {
                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
	        }

	        return location;
	    }

	   public double getLatitude()
	    {
	    	return latitude;
	    }
	   public double getLongitude()
	    {
	    	return longitude;
	    }
	   
	   public double getAltitude()
	    {
	    	return altitude;
	    }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

    @Override
    public void onProviderEnabled(String provider) {
            Toast.makeText(this.mContext, "Enabled "+ provider + " provider.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
            Toast.makeText(this.mContext, "Enabled "+ provider + " provider.", Toast.LENGTH_SHORT).show();
    }

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
