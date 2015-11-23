package com.shevanel.cranked;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import data.Repository;

public class RideElevationActivity extends Activity {

	private Repository repository = null;
	Cursor cursor = null;
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

	String[] dateValues;
	GraphViewData[] graphArray;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphs);

		repository = new Repository(this.getBaseContext());
		Bundle extras = getIntent().getExtras();
		long id = extras.getLong("identifier");
		getWaypoints(id);

		GraphViewSeries elevationSeries = new GraphViewSeries("Elevation", Color.rgb(11, 107, 189), graphArray);
		GraphView graphView = new LineGraphView(this, "Elevation (ft) v Time");
		((LineGraphView) graphView).setDrawBackground(true);
		
		// custom static labels
		graphView.setHorizontalLabels(new String[] { dateValues[0],
				dateValues[dateValues.length / 2],
				dateValues[dateValues.length - 1] });
		// graphView.setVerticalLabels(new String[]
		// {Double.toString(maxAltitude), Double.toString(minAltitude) });
		graphView.addSeries(elevationSeries); // data

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph2);
		layout.addView(graphView);
	}

	private void getWaypoints(long id) {
		try {
			long startTime = 0;
			repository.open();
			cursor = repository.fetchRideWaypoints(id);
			graphArray = new GraphViewData[cursor.getCount()];
			dateValues = new String[cursor.getCount()];
			do {
				if (cursor.getString(0) != null && cursor.getString(2) != null) {

					int position = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Repository.KEY_POSITION)));
					long time = Long.parseLong(cursor.getString(cursor.getColumnIndex(Repository.KEY_TIMESTAMP)));

					if (startTime == 0)
						startTime = time;
					
					Date resultDate = new Date(time - startTime - 3600000);
					dateValues[position - 1] = formatter.format(resultDate);
					
					double altitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Repository.KEY_ALTITUDE)));
					graphArray[position - 1] = new GraphViewData(position - 1, altitude);
				}

			} while (cursor.moveToNext());
			
			cursor.close();
		} catch (Exception e) {
			Log.e("Error in getWaypoints() could be data acess problem", e.toString());
		}
	}
}