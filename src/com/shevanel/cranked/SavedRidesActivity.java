package com.shevanel.cranked;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import data.Repository;

public class SavedRidesActivity extends ListActivity {

	Typeface roboto = null;

	private Repository repository;
	SimpleCursorAdapter rides;
	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		repository.close();
	}

	@Override
	protected void onListItemClick(ListView l, final View v, int position,
			final long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked

		final CharSequence[] items = { "View Details", "Write GPX" ,"Delete" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Options");

		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (items[item] == "View Details") {
					Intent intent = new Intent();
					intent.setClass(v.getContext(), StatsTabs.class);
					intent.putExtra("identifier", id);
					startActivity(intent);

				} else if (items[item] == "Delete") {
					repository.open();
					repository.deleteRide(id);
					repository.deleteWaypoints(id);
					repository.close();
					cursor.requery();
					populate();
				} else if (items[item] == "Write GPX") {
					
					repository.open();
					cursor = repository.fetchSingleRide(id);
					
					
					String FILENAME = cursor.getString(cursor.getColumnIndex(Repository.KEY_STARTTIME)) + ".gpx";
					final String DNAME = "/cranked";
					
					//Create a new directory on external storage 
					File rootPath = new File(Environment.getExternalStorageDirectory().toString(), DNAME);
					
					if(!rootPath.exists()) {
						rootPath.mkdirs();
					}
					
					// Create the reference
					File dataFile = new File(rootPath, FILENAME);
					
					cursor = repository.fetchRideWaypoints(id);
					
					repository.close();
					try {
						io.GpxFileWriter.writeGpxFile("test", cursor, dataFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					
				}
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onResume() {
		super.onResume();

		repository = new Repository(this);
		populate();
	}

	private void populate() {

		roboto = Typeface.createFromAsset(getApplicationContext().getAssets(),
				"fonts/Roboto-Medium.ttf");

		repository.open();

		cursor = repository.fetchRides();
		startManagingCursor(cursor);
		String[] from = new String[] { Repository.KEY_TITLE,
				Repository.KEY_DISTANCE };
		int[] to = new int[] { R.id.top_line, R.id.second_line };

		// Now create an array adapter and set it to display using our row
		rides = new CustomAdapter(this, R.layout.list_item, cursor, from, to);
		setListAdapter(rides);
		repository.close();
	}

	// Custom Adapter class
	private class CustomAdapter extends SimpleCursorAdapter {

		Cursor cur;
		LayoutInflater inflater;

		public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);

			this.cur = c;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = inflater.inflate(R.layout.list_item, null, false);
			cur.moveToPosition(position);

			TextView textTitle = (TextView) view.findViewById(R.id.top_line);
			TextView textDistance = (TextView) view.findViewById(R.id.second_line);

			textTitle.setText(cur.getString(1));
			textTitle.setTypeface(roboto);

			textDistance.setText("Distance: " + cur.getString(2) + " (mi)");
			textTitle.setTypeface(roboto);

			return view;
		}
	}
}