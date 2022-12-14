package model;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Displays a circle on the map
 */
public class CustomCircleMarkerLayer extends MapLayer {
	 private final MapPoint mapPoint;
	 private final Circle circle;

	 /**
	  * @param mapPoint the point (latitude, longitude) where the circle must be displayed
	  * @see com.gluonhq.maps.MapPoint
	  * @param circleSize the size of the circle
	  * @param color the color
	  */
	 public CustomCircleMarkerLayer(MapPoint mapPoint, int circleSize, Color color) {
	  this.mapPoint = mapPoint;

	  /* Initialisation of the circle (color and size) */
	  this.circle = new Circle(circleSize, color);

	  /* Adds the circle to the MapLayer */
	  this.getChildren().add(circle);
	 }
	 
	 /**
	  *   The function is called everytime a refresh of the map is done. It moves the circle to the good position when
	  *   the user zooms for example
	 */
	 @Override
	 protected void layoutLayer() {
		 
	  /* Conversion from MapPoint to Point2D (latitude and longitude to screen pixels) */
	  Point2D point2d = this.getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());

	  /* Moves the circle according to the coordinates of the point */
	  circle.setTranslateX(point2d.getX());
	  circle.setTranslateY(point2d.getY());
	 }
}