package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {

	private static final String DB_NAME = "cranked";
	private static final int DB_VERSION = 1;

	public static final String RIDE = "ride";
	public static final String WAYPOINT = "waypoint";

	private static final String CREATE_RIDE = "CREATE TABLE ride (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startTime INTEGER, endTime INTEGER, elapsedTime INTEGER, elevationGain REAL, distance REAL, averageSpeed REAL);";

	private static final String CREATE_WAYPOINT = "CREATE TABLE waypoint (_id INTEGER PRIMARY KEY AUTOINCREMENT, rideId INTEGER, timeStamp INTEGER NOT NULL, position INTEGER NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL, altitude REAL, speed REAL);";

	public Helper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the database tables
		db.execSQL(CREATE_RIDE);
		db.execSQL(CREATE_WAYPOINT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		onCreate(db);
	}

}