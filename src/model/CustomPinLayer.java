package model;

import java.io.InputStream;
import java.net.MalformedURLException;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** Affiche une épingle sur la carte */
public class CustomPinLayer extends MapLayer{
	private final MapPoint mapPoint;
	private final ImageView mapPinImageView;
	private static final int PIN_WIDTH = 18, PIN_HEIGHT = 25;

	 /**
	  * @param mapPoint le point (latitude et longitude) où afficher l'épingle
	  * @see com.gluonhq.maps.MapPoint
	  */
	 public CustomPinLayer(MapPoint mapPoint, boolean isRed) throws MalformedURLException {
		  this.mapPoint = mapPoint;
		
		  /* Ajoute l'épingle au MapLayer */
		  InputStream input = this.getClass().getResourceAsStream("/Resources/map-pin-black.png");
		  if(isRed) {
			  input = this.getClass().getResourceAsStream("/Resources/map-pin-red.png");
		  }
		  Image image = new Image(input, PIN_WIDTH, PIN_HEIGHT, false, false);
		  this.mapPinImageView = new ImageView(image);
		  this.getChildren().add(this.mapPinImageView);
	 }
	 
	 public MapPoint getMapPoint() {
		 return this.mapPoint;
	 }

	 /* La fonction est appelée à chaque rafraichissement de la carte */
	 @Override
	 protected void layoutLayer() {
	  /* Conversion du MapPoint vers Point2D */
	  Point2D point2d = this.getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());

	  /* Déplace l'épingle selon les coordonnées du point */
	  mapPinImageView.setTranslateX(point2d.getX() - (PIN_WIDTH / 2));
	  mapPinImageView.setTranslateY(point2d.getY() - PIN_HEIGHT);
	 }
}
