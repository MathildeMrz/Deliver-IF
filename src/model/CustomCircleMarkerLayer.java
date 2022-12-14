package model;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Display a point oon the map */
public class CustomCircleMarkerLayer extends MapLayer {
	 private final MapPoint mapPoint;
	 private final Circle circle;

	 /**
	  * @param mapPoint : point where we need to display the circle
	  * @see com.gluonhq.maps.MapPoint
	  * @param circleSize : size of the circle
	  * @param color :circle's color
	  */
	 public CustomCircleMarkerLayer(MapPoint mapPoint, int circleSize, Color color) {
	  this.mapPoint = mapPoint;
	  this.circle = new Circle(circleSize, color);

	  /* add the circle to the MapLayer */
	  this.getChildren().add(circle);
	 }

	 /* function is called whenever the map is refreshed */
	 @Override
	 protected void layoutLayer() {
	  /* convert point on point 2D */
	  Point2D point2d = this.getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());

	  /* move the circle with the 2d coordinates */
	  circle.setTranslateX(point2d.getX());
	  circle.setTranslateY(point2d.getY());
	 }
}