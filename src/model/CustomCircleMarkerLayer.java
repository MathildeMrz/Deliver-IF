package model;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/** Affiche un point sur la carte */
public class CustomCircleMarkerLayer extends MapLayer {
	 private final MapPoint mapPoint;
	 private final Circle circle;

	 /**
	  * @param mapPoint le point (latitude et longitude) où afficher le cercle
	  * @see com.gluonhq.maps.MapPoint
	  * @param circleSize la taille du cercle
	  * @param color la couleur
	  */
	 public CustomCircleMarkerLayer(MapPoint mapPoint, int circleSize, Color color) {
	  this.mapPoint = mapPoint;

	  /* Cercle rouge de taille 5 */
	  this.circle = new Circle(circleSize, color);

	  /* Ajoute le cercle au MapLayer */
	  this.getChildren().add(circle);
	 }

	 /* La fonction est appelée à chaque rafraichissement de la carte */
	 @Override
	 protected void layoutLayer() {
	  /* Conversion du MapPoint vers Point2D */
	  Point2D point2d = this.getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());

	  /* Déplace le cercle selon les coordonnées du point */
	  circle.setTranslateX(point2d.getX());
	  circle.setTranslateY(point2d.getY());
	 }
}