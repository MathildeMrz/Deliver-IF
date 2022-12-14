package model;

import java.io.InputStream;
import java.net.MalformedURLException;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Displays a pin on the map
 */
public class CustomPinLayer extends MapLayer{
	private final MapPoint mapPoint;
	private final ImageView mapPinImageView;
	private static final int PIN_WIDTH = 18, PIN_HEIGHT = 25;

	 /**
	  * @param mapPoint : point(latitude and longitude) where the pin must be displayed
	  * @see com.gluonhq.maps.MapPoint
	  * @param isRed : display a red pin if true (if the point was selected), display a black pin if false
	  * 
	  */
	
	 public CustomPinLayer(MapPoint mapPoint, boolean isRed) throws MalformedURLException {
		 
		 this.mapPoint = mapPoint;
		
		  /* add a pin to the MapLayer */
		  InputStream input = this.getClass().getResourceAsStream("/Resources/map-pin-black.png");
		  if(isRed) {
			  input = this.getClass().getResourceAsStream("/Resources/map-pin-red.png");
		  }
		  Image image = new Image(input, PIN_WIDTH, PIN_HEIGHT, false, false);
		  
		  /* Initialisation of the pin (image) */
		  this.mapPinImageView = new ImageView(image);
		  this.getChildren().add(this.mapPinImageView);
	 }
	 
	 public MapPoint getMapPoint() {
		 return this.mapPoint;
	 }

	 /**
	  *   The function is called everytime a refresh of the map is done. It moves the pin to the good position when
	  *   the user zooms for example
	 */
	 @Override
	 protected void layoutLayer() {
		 
	  /* Conversion from MapPoint to Point2D (latitude and longitude to screen pixels) */
	  Point2D point2d = this.getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());

	  /* Moves the pin according to the coordinates of the point */
	  mapPinImageView.setTranslateX(point2d.getX() - (PIN_WIDTH / 2));
	  mapPinImageView.setTranslateY(point2d.getY() - PIN_HEIGHT);
	 }
}
