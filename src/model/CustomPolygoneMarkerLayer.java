package model;

import java.util.ArrayList;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

/**
 * Displays a round on the map
 */
public class CustomPolygoneMarkerLayer extends MapLayer{
	private final Polyline line;
	private ArrayList<MapPoint> mapPoints;
    private double[] points;
    private MapView mapView;
      
    /**
     * @param mapPoints all points concerning by the round
     * @param mapView
     * @param lineColor color
     * @param lineWidth width
     */
	 public CustomPolygoneMarkerLayer(ArrayList<MapPoint> mapPoints, MapView mapView, Color lineColor, int lineWidth) {	
		 this.mapView = mapView;
		 this.mapPoints = mapPoints;
		 this.points = new double[mapPoints.size()*2];
		 this.line = new Polyline();				 
	
		 line.setFill(Color.TRANSPARENT);
		 line.setStroke(lineColor);
		 line.setStrokeWidth(lineWidth);
		 this.getChildren().add(line);
	 }

	 /**
	  *   The function is called everytime a refresh of the map is done
	 */
	 @Override
	 protected void layoutLayer() 
	 {
		 int cpt = 0;
		 
		 /* Manual conversion from latitude and longitude to screen pixels*/
		 for(MapPoint p : this.mapPoints) 
		 {
			 double mapWidth = this.mapView.getWidth();
	 		 double mapHeight = this.mapView.getHeight();
	 		
	  		 MapPoint min_lat_long = this.mapView.getMapPosition(0, mapHeight-1);
	 		 double min_long = min_lat_long.getLongitude();
	 		 double min_lat = min_lat_long.getLatitude();
	 
	 		 MapPoint max_lat_long = this.mapView.getMapPosition(mapWidth-1, 0);
	 		 double max_long = max_lat_long.getLongitude();
	 		 double max_lat = max_lat_long.getLatitude();
	 		
	 		 double pixel_per_long = (mapWidth / (max_long - min_long));
	 		 double pixel_per_lat = (mapHeight / (max_lat - min_lat));
	 		
	 		 double x = (pixel_per_long * (p.getLongitude()- min_long));
	 		 double y = (pixel_per_lat * (max_lat-p.getLatitude()));
			 points[cpt] = x;
			 points[cpt+1] = y;
			 cpt += 2;
		 }
		 this.getChildren().remove(this.line);
		 this.getChildren().clear();
	     this.line.getPoints().clear();
	     
		 for(int i = 0; i < this.points.length; i++)
		 {
			 this.line.getPoints().add(this.points[i]);   
		 }
		 this.getChildren().add(this.line);
	 }	 
}
