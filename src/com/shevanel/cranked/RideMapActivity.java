package com.shevanel.cranked;

import java.util.ArrayList;
import java.util.List;

import utils.RouteOverlay;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import data.Repository;

public class RideMapActivity extends MapActivity {

	private MapView mapView = null;
	private Repository repository = null;
	Cursor cursor;
	List<GeoPoint> mPoints = new ArrayList<GeoPoint>();

	private int maxLatitude = -2147483648;
	private int maxLongitude = -2147483648;
	private int minLatitude = 2147483647;
	private int minLongitude = 2147483647;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.route);
		mapView.setBuiltInZoomControls(true);

		repository = new Repository(this);
		
		new Thread(new Runnable() {
			public void run() {
				Bundle extras = getIntent().getExtras();
				final long id = extras.getLong("identifier");
				repository.open();
				cursor = repository.fetchRideWaypoints(id);
				startManagingCursor(cursor);
				getWaypoints(id);
				repository.close();

				if (!mPoints.isEmpty()) {
					RouteOverlay routeOverlay = new RouteOverlay(mPoints, RideMapActivity.this);
					mapView.getOverlays().add(routeOverlay);
				}
			}
		}).start();
	}

	private void getWaypoints(long id) {
		try {
			do {
				if (cursor.getString(1) != null && cursor.getString(2) != null) {

					double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Repository.KEY_LATITUDE))) * 1e6;

					double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Repository.KEY_LONGITUDE))) * 1e6;

					maxLatitude = (int) Math.max(latitude, maxLatitude);
					minLatitude = (int) Math.min(latitude, minLatitude);
					maxLongitude = (int) Math.max(longitude, maxLongitude);
					minLongitude = (int) Math.min(longitude, minLongitude);

					GeoPoint point = new GeoPoint((int) latitude, (int) longitude);
					mPoints.add(point);
				}
			} while (cursor.moveToNext());
		} catch (Exception e) {
			Log.e("Error when getting waypoints in map viewer class",
					e.toString());
		} finally {
			mapView.getController().animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
			mapView.getController().zoomToSpan(
					Math.abs(maxLatitude - minLatitude),
					Math.abs(maxLongitude - minLongitude));
			cursor.close();
		}
	}
}