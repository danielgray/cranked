package com.shevanel.cranked;

import service.LocationService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements LocationListener {

	private static final String TAG = "MainActivity";

	Typeface roboto = null;
	Chronometer chronometer;
	TextView title, distanceView, statusView;
	ImageView signal;

	private LocationManager locationManager;

	LocationService locationService;
	Intent serviceIntent;

	String rideId;
	static String status;

	/* Called when the activity is first created. */

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			locationService = ((LocationService.TrackerBinder) service)
					.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			locationService = null;
		}
	};

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String status = intent.getStringExtra("Status");

			double distance = intent.getDoubleExtra("Distance", 0);

			if (status != null && distance != -1) {
				statusView.setText(status);
				distanceView.setText(String.valueOf(distance));
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cranked);

		roboto = Typeface.createFromAsset(getApplicationContext().getAssets(),
				"fonts/Roboto-Medium.ttf");

		final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		signal = (ImageView) findViewById(R.id.signal);
		// speedView = (TextView) findViewById(R.id.speed);
		distanceView = (TextView) findViewById(R.id.distance);
		statusView = (TextView) findViewById(R.id.status);
		chronometer = (Chronometer) findViewById(R.id.chrono);

		chronometer.setText("00:00:00");
		chronometer.setTypeface(roboto);
		statusView.setTypeface(roboto);
		distanceView.setTypeface(roboto);

		toggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (toggleButton.isChecked()) {
					locationService.startTracking();
					chronometer.setBase(SystemClock.elapsedRealtime());
					chronometer.start();
				} else {
					chronometer.stop();
					locationService.stopTracking();
					chronometer.setText("00:00:00");
					distanceView.setText("0.00");
				}
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();

		// unbind from the service
		// unbindService(serviceConnection);

		// unregister the broadcast receiver
		unregisterReceiver(broadcastReceiver);

		// remove the location listener
		locationManager.removeUpdates(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		registerReceiver(broadcastReceiver, new IntentFilter(
				"com.android.Cranked.UPDATE"));

		serviceIntent = new Intent(this, LocationService.class);
		// Bind to the service
		getApplicationContext().bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
		// Starting the service makes it stick, regardless of bindings
		startService(serviceIntent);

		// if (locationService.isTracking == false) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.v(TAG, "GPS provider not enabled");
			return;
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 0, this);
		}
		// }
	}

	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

		/* This is called when the GPS status alters */
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			// Log.v(TAG, "Status Changed: Out of Service");

			statusView.setText("GPS signal out of service");

			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			// Log.v(TAG, "Status Changed: Temporarily Unavailable");

			// intent.putExtra("GPS signal temporarily unavailable");
			break;
		case LocationProvider.AVAILABLE:
			Log.v(TAG, "Status Changed: Available");

			statusView.setText("GPS OK");
			locationManager.removeUpdates(this);
			signal.setVisibility(View.VISIBLE);
			break;
		}
	}

}