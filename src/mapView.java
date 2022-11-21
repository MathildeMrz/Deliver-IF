import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class mapView extends Application{

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		//Draw map		
		ArrayList<Line> lines = new ArrayList<Line>();
        Line line1 = new Line(200, 370, 400, 560);
        Line line2 = new Line(780, 550, 340, 900);
        Line line3 = new Line(900, 720, 490, 930);
        Line line4 = new Line(12, 820, 125, 168);  

        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);
        
        HBox hbox = new HBox();
        
        for (int counter = 0; counter < lines.size(); counter++) { 		      
        	hbox.getChildren().add(lines.get(counter));
        }  
                
        Scene scene = new Scene(hbox, 300, 120);   
        stage.setScene(scene);
        
        stage.show();
		
	}

}
