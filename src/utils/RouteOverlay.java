package utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.jjoe64.graphview.R.drawable;

public class RouteOverlay extends Overlay {

	private Context context;
	private List<GeoPoint> gpoints;

	public RouteOverlay(List<GeoPoint> gpoints, Context context) {
		this.gpoints = gpoints;
		this.context = context;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		
		List<Point> mpoints = new ArrayList<Point>();
		Path path = new Path();
	    Bitmap markerImage = BitmapFactory.decodeResource(context.getResources(), drawable.start_marker);
		
		// Convert to a point that can be drawn on the map.
		for (GeoPoint g : gpoints) {
			Point p = new Point();
			mapView.getProjection().toPixels(g, p);
			
			if (mpoints.isEmpty()) {
				path.moveTo(p.x, p.y);
				canvas.drawBitmap(markerImage, p.x - markerImage.getWidth() / 2, p.y - markerImage.getHeight() + 5, null);
			}
			
			path.lineTo(p.x, p.y);
			mpoints.add(p);
		}

		Paint paint = new Paint();
		paint.setARGB(255, 255, 68, 68);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		
		canvas.drawPath(path, paint);

		return false;
	}
}