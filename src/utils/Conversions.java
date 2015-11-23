package utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.location.Location;

public class Conversions {

	public static Double sumAverageSpeed(double totalDistance,
			double elapsedTime) {

		double decimalTime = elapsedTime / 3600000;

		if (decimalTime == 0.0)
			return 0.0;

		DecimalFormat format = new DecimalFormat("##.#");
		return Double.valueOf(format.format(totalDistance / decimalTime));

	}

	public static Double sumTotalDistance(ArrayList<Location> storedLocations) {

		Location lastLocation = null;
		float totalDistance = 0;

		for (Location location : storedLocations) {
			if (lastLocation == null) {
				lastLocation = location;
				continue;
			}
			totalDistance = totalDistance + lastLocation.distanceTo(location);
			lastLocation = location;
		}

		DecimalFormat format = new DecimalFormat("#.##");
		return Double.valueOf(format.format(totalDistance * 0.00062137119));

	}

	public static Double sumTotalElevation(ArrayList<Location> storedLocations) {

		Location lastLocation = null;
		double totalElevation = 0;

		for (Location location : storedLocations) {
			if (lastLocation == null) {
				lastLocation = location;
				continue;
			}

			if (location.getAltitude() > lastLocation.getAltitude()) {
				totalElevation += location.getAltitude() - lastLocation.getAltitude();
			}
		}

		return totalElevation;

	}
}
