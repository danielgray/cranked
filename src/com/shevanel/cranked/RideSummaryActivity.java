package com.shevanel.cranked;

import data.Repository;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class RideSummaryActivity extends Activity {

	private Repository repository = null;
	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);

		repository = new Repository(this);
		Bundle extras = getIntent().getExtras();
		long id = extras.getLong("identifier");

		repository.open();
		cursor = repository.fetchSingleRide(id);

		startManagingCursor(cursor);

		TextView time = (TextView) findViewById(R.id.time);
		TextView speed = (TextView) findViewById(R.id.speed);
		TextView distance = (TextView) findViewById(R.id.distance);
		TextView elevation = (TextView) findViewById(R.id.elevation);

		distance.setText(cursor.getString(cursor.getColumnIndex(Repository.KEY_DISTANCE)) + " mi");
		time.setText(cursor.getString(cursor.getColumnIndex(Repository.KEY_ELAPSEDTIME)));
		speed.setText(cursor.getString(cursor.getColumnIndex(Repository.KEY_AVERAGESPEED)) + " mph");
		elevation.setText(cursor.getString(cursor.getColumnIndex(Repository.KEY_ELEVATIONGAIN)) + " ft");

		repository.close();

	}

}