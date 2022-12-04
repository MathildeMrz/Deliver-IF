package model;

import com.gluonhq.maps.MapLayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

public class CustomPolygoneMarkerLayer extends MapLayer{
	private final Polyline line;
    private double[] points;
   
	 public CustomPolygoneMarkerLayer(double[] points) {		 
		 this.points = new double[points.length];
		 this.points = points;
		 this.line = new Polyline();				 
	
		 line.setFill(Color.TRANSPARENT);
		 line.setStroke(Color.BLUE);
		 line.setStrokeWidth(5);
		 this.getChildren().add(line);
	 }

	 /* La fonction est appelée à chaque rafraichissement de la carte */
	 @Override
	 protected void layoutLayer() 
	 {		 
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
