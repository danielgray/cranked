package data;

import android.database.Cursor;
import android.database.SQLException;

public interface IRepository {

	/**
	 * Ride database method
	 */
	public abstract long createRide();

	/**
	 * Waypoint database method
	 */
	public abstract long createWaypoint(long rideId, int position,
			double latitude, double longitude, double altitude, float speed);

	/**
	 * Complete a ride with calculated variables
	 * 
	 * @param rideId
	 * @param totalDistance
	 * @param averageSpeed
	 * @param totalElevation
	 * @param elapsedTime
	 * @return
	 */
	public abstract boolean completeRide(long rideId, Double totalDistance,
			Double averageSpeed, Double totalElevation, String elapsedTime);

	/**
	 * Delete specified ride
	 * 
	 * @param rideId
	 * @return
	 */
	public abstract boolean deleteRide(long rideId);

	/**
	 * Delete specified waypoints
	 * 
	 * @param rideId
	 * @return
	 */
	public abstract boolean deleteWaypoints(long rideId);

	/**
	 * Return a Cursor over the list of all rides in the database
	 * 
	 * @return Cursor over all rides for ListView
	 */
	public abstract Cursor fetchRides();

	/**
	 * Return a Cursor over all the waypoints positioned at the defined ride
	 * 
	 * @return Cursor positioned at the defined ride
	 */
	public abstract Cursor fetchRideWaypoints(long id) throws SQLException;

	/**
	 * Return a Cursor over the ride positioned at the defined ride
	 * 
	 * @return Cursor positioned at the identified ride
	 */
	public abstract Cursor fetchSingleRide(long id) throws SQLException;

}