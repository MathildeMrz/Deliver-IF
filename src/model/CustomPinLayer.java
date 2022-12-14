package model;

import java.io.InputStream;
import java.net.MalformedURLException;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** add a pin on the map */
public class CustomPinLayer extends MapLayer{
	private final MapPoint mapPoint;
	private final ImageView mapPinImageView;
	private static final int PIN_WIDTH = 18, PIN_HEIGHT = 25;

	 /**
	  * @param mapPoint : point(latitude and longitude) where the pin must be displayed
	  * @see com.gluonhq.maps.MapPoint
	  * @param isRed : display a red pin if true, display a black pin if false
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
		  this.mapPinImageView = new ImageView(image);
		  this.getChildren().add(this.mapPinImageView);
	 }
	 
	 public MapPoint getMapPoint() {
		 return this.mapPoint;
	 }

	 /* function is called whenever the map is refreshed */
	 @Override
	 protected void layoutLayer() {
	 /* convert point on point 2D */
	  Point2D point2d = this.getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());

	  /* move the pin with the 2d coordinates */
	  mapPinImageView.setTranslateX(point2d.getX() - (PIN_WIDTH / 2));
	  mapPinImageView.setTranslateY(point2d.getY() - PIN_HEIGHT);
	 }
}
