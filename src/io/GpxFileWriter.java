package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import data.Repository;

import android.database.Cursor;

public class GpxFileWriter {

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

	private static final String TAG_GPX = "<gpx"
		+ " xmlns=\"http://www.topografix.com/GPX/1/1\""
		+ " version=\"1.1\""
		+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
		+ " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";

	private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static void writeGpxFile(String trackName, Cursor cursor, File target) throws IOException {
		
		try {
			FileOutputStream stream = new FileOutputStream(target, false);
	
			stream.write(XML_HEADER.getBytes());
			stream.write(TAG_GPX.getBytes());
	
			writeTrackPoints(trackName, stream, cursor);
	
			stream.write("</gpx>".getBytes());
			stream.close();
		}
		catch (Exception e) {
			
		}
	}

	public static void writeTrackPoints(String trackName, FileOutputStream stream, Cursor c) throws IOException {
		
		String trk = "\t" + "<trk>" + "\n" + "\t\t" + "<name>";
		stream.write(trk.getBytes());
		stream.write(trackName.getBytes());
		stream.write("</name>".getBytes());
		stream.write("\n".getBytes());

		String trkseg = "\t\t" + "<trkseg>" + "\n";
		stream.write(trkseg.getBytes());

		while (!c.isAfterLast() ) {
			StringBuffer out = new StringBuffer();
			out.append("\t\t\t" + "<trkpt lat=\"" 
					+ c.getDouble(c.getColumnIndex(Repository.KEY_LATITUDE)) + "\" "
					+ "lon=\"" + c.getDouble(c.getColumnIndex(Repository.KEY_LONGITUDE)) + "\">");
			out.append("<ele>" + c.getDouble(c.getColumnIndex(Repository.KEY_ALTITUDE)) + "</ele>");
			out.append("<time>" + POINT_DATE_FORMATTER.format(new Date(c.getLong(c.getColumnIndex(Repository.KEY_TIMESTAMP)))) + "</time>");

			out.append("</trkpt>" + "\n");
			stream.write(out.toString().getBytes());

			c.moveToNext();
		}

		String end = "\t\t" + "</trkseg>" + "\n" +"\t" + "</trk>" + "\n";
		stream.write(end.getBytes());
	}
}