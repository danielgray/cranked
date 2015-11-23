package service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import utils.Conversions;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import data.Repository;

public class LocationService extends Service implements LocationListener {

	private static final String TAG = "LocationService";
	
	private LocationManager locationManager;
	private Repository repository;
	Intent intent = new Intent("com.android.Cranked.UPDATE");
	
	private Location lastLocation = null;
	
	private long rideId;
	private int position = 1;
	private long startTime;
	private long endTime;
	private double elapsedTime;
	public double elevationGain;
	public double mAverageSpeed;

	/* Service Setup Methods */
	
	boolean isTracking = false;

	public boolean isTracking() {
		return isTracking;
	}
	
	private ArrayList<Location> storedLocations;
	
	public ArrayList<Location> getLocations() {
		return storedLocations;
	}

	public int getLocationsCount() {
		return storedLocations.size();
	}

	/* Service Access Methods */

	private final IBinder binder = new TrackerBinder();

	public class TrackerBinder extends Binder {
		public LocationService getService() {
			return LocationService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}

	/* Standard Methods */
	
	@Override
	public void onCreate() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		storedLocations = new ArrayList<Location>();
		Log.i(TAG, "Location Service Running...");
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(this);
		Log.i(TAG, "Location Service Stopped.");
	}
	
	public void startTracking() {

		Toast.makeText(this, "Starting Tracking", Toast.LENGTH_SHORT).show();

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// Notify the user
			return;
		} else {
			repository = new Repository(this);
			startTime = SystemClock.elapsedRealtime();
			repository.open();
			rideId = repository.createRide();
			repository.close();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
			isTracking = true;
		}
	}

	public void stopTracking() {

		Toast.makeText(this, "Stopping Tracker", Toast.LENGTH_SHORT).show();

		locationManager.removeUpdates(this);

		endTime = SystemClock.elapsedRealtime();
		elapsedTime = endTime - startTime;

		// call calculation methods
		Double totalDistance = Conversions.sumTotalDistance(storedLocations);
		Double averageSpeed = Conversions.sumAverageSpeed(totalDistance,elapsedTime);
		Double elevationGain = Conversions.sumTotalElevation(storedLocations);

		DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		repository.open();
		repository.completeRide(rideId, totalDistance, averageSpeed, elevationGain, sdf.format(elapsedTime));
		repository.close();

		storedLocations.removeAll(storedLocations);
		isTracking = false;
	}
	
	
	
	

	/* LocationListener Methods */

	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "Adding Waypoint");

		if (location != null && location.hasAccuracy()) {

			try {
				long currentElapsed = SystemClock.elapsedRealtime() - startTime;

				// keep the elevation data in check
				if (lastLocation != null) {
					if (location.getAltitude() > lastLocation.getAltitude()) {
						if (location.getAltitude() - lastLocation.getAltitude() > 100) {
							// too much elevation gained
							location.setAltitude(lastLocation.getAltitude());
						}
					} else if (location.getAltitude() < lastLocation.getAltitude()) {
						if (lastLocation.getAltitude() - location.getAltitude() > 200) {
							// too much elevation lost
							location.setAltitude(lastLocation.getAltitude());
						}
					}
				}
				
				if (location.getSpeed() > 100) 
					location.setSpeed(lastLocation.getSpeed());
				
				storedLocations.add(location);
				
				repository.open();
				repository.createWaypoint(rideId, position, location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed());
				repository.close();
				
				// call calculation methods and update the main UI
				Double totalDistance = Conversions.sumTotalDistance(storedLocations);
				Double averageSpeed = Conversions.sumAverageSpeed(totalDistance, currentElapsed);

				intent.putExtra("Distance", totalDistance);
				intent.putExtra("Speed", averageSpeed);
				sendBroadcast(intent);

				lastLocation = location;
				position++;

			} catch (Exception ex) {
				Log.e(TAG, ex.toString());
			}
		}
	}

	@Override
	public void onProviderEnabled(String provider) {

	}
	
	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

		/* This is called when the GPS status alters */
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			Log.v(TAG, "Status Changed: Out of Service");
			intent.putExtra("Status", "GPS signal out of service");
			sendBroadcast(intent);
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v(TAG, "Status Changed: Temporarily Unavailable");
			break;
		case LocationProvider.AVAILABLE:
			Log.v(TAG, "Status Changed: Available");
			intent.putExtra("Status", "GPS signal OK");
			sendBroadcast(intent);
			break;
		}
	}

}