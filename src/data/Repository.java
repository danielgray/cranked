package data;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Repository implements IRepository {

	public static final String KEY_RIDEID = "rideId";
	public static final String KEY_TITLE = "title";
	public static final String KEY_STARTTIME = "startTime";
	public static final String KEY_ENDTIME = "endTime";
	public static final String KEY_ELAPSEDTIME = "elapsedTime";
	public static final String KEY_ELEVATIONGAIN = "elevationGain";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_AVERAGESPEED = "averageSpeed";

	public static final String KEY_TIMESTAMP = "timeStamp";
	public static final String KEY_POSITION = "position";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_ALTITUDE = "altitude";
	public static final String KEY_SPEED = "speed";

	private Context context;
	private SQLiteDatabase database;
	private Helper helper;

	public Repository(Context context) {
		this.context = context;
	}

	public void close() {
		database.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#completeRide(long,
	 * java.lang.Double, java.lang.Double, java.lang.String)
	 */
	@Override
	public boolean completeRide(long rideId, Double totalDistance,
			Double averageSpeed, Double totalElevation, String elapsedTime) {

		ContentValues cv = new ContentValues(4);

		cv.put(KEY_ENDTIME, new Date().getTime());
		cv.put(KEY_ELAPSEDTIME, elapsedTime);
		cv.put(KEY_DISTANCE, totalDistance);
		cv.put(KEY_ELEVATIONGAIN, totalElevation);
		cv.put(KEY_AVERAGESPEED, averageSpeed);

		return database.update(Helper.RIDE, cv, "_id=" + rideId, null) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#createRide()
	 */
	@Override
	public long createRide() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy HH:mm");
		ContentValues cv = new ContentValues(2);

		cv.put(KEY_TITLE, dateFormat.format(new Date()));
		cv.put(KEY_STARTTIME, new Date().getTime());

		return database.insert(Helper.RIDE, null, cv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#createWaypoint(long, int,
	 * double, double, double, float)
	 */
	@Override
	public long createWaypoint(long rideId, int position, double latitude,
			double longitude, double altitude, float speed) {

		ContentValues cv = new ContentValues(7);

		cv.put(Repository.KEY_RIDEID, rideId);
		cv.put(KEY_POSITION, position);
		cv.put(KEY_TIMESTAMP, new Date().getTime()); // Insert 'now' as the date
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);
		cv.put(KEY_ALTITUDE, altitude);
		cv.put(KEY_SPEED, speed);

		return database.insert(Helper.WAYPOINT, null, cv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#deleteRide(long)
	 */
	@Override
	public boolean deleteRide(long rideId) {
		return database.delete(Helper.RIDE, "_id=" + rideId, null) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#deleteWaypoints(long)
	 */
	@Override
	public boolean deleteWaypoints(long rideId) {
		return database.delete(Helper.WAYPOINT, "rideId=" + rideId, null) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#fetchRides()
	 */
	@Override
	public Cursor fetchRides() {
		return database.query(Helper.RIDE, new String[] { "_id", KEY_TITLE,
				KEY_DISTANCE, KEY_ELAPSEDTIME, KEY_AVERAGESPEED }, null, null,
				null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#fetchRideWaypoints(long)
	 */
	@Override
	public Cursor fetchRideWaypoints(long id) throws SQLException {
		Cursor cursor = database.query(true, Helper.WAYPOINT, new String[] {
				KEY_POSITION, KEY_LATITUDE, KEY_LONGITUDE, KEY_ALTITUDE,
				KEY_TIMESTAMP, KEY_SPEED }, "rideId=" + id, null, null, null,
				null, null);

		if (cursor != null)
			cursor.moveToFirst();

		return cursor;
	}

	public IRepository open() throws SQLException {
		helper = new Helper(context);
		database = helper.getWritableDatabase();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shevanel.cranked.database.IRepository#singelRide(long)
	 */
	@Override
	public Cursor fetchSingleRide(long id) throws SQLException {
		Cursor cursor = database.rawQuery("SELECT * FROM ride WHERE _id = "
				+ id + ";", null);

		if (cursor != null)
			cursor.moveToFirst();

		return cursor;
	}

}