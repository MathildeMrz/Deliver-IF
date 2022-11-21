import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.time.LocalDate;
import java.util.ArrayList;

import Modele.Courier;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		Application.launch(args);
		

	}

	@Override
	public void start(Stage stage) throws Exception {
		
		//Resize the window
		stage.setResizable(true);
		stage.setWidth(1400);
		stage.setHeight(900);     
		
		//Initialisation of the couriers
		Courier c1 = new Courier("Dimitri",12.7);
		Courier c2 = new Courier("Dominique",12.7);		
		ListView<Courier> listView = new ListView<Courier>();
		listView.getItems().add(c1);
		listView.getItems().add(c2);
		
		//Display
		VBox couriers = new VBox(listView);        
		//VBox vbox2 = new VBox(listView2);        
        HBox hbox = new HBox();        
        
        //hbox.getChildren().add(vbox2);
        
        DatePicker date = new DatePicker();
        
        date.setValue(LocalDate.now());

        VBox createNewRequest = new VBox();     
        createNewRequest.getChildren().add(new Label("Date:"));
        createNewRequest.getChildren().add(date);
        createNewRequest.getChildren().add(new Label("Localisation:"));
                       
        createNewRequest.getChildren().add(new Label("Time-window"));
        
        ComboBox<String> timeWindow = new ComboBox();
        timeWindow.getItems().add("9h");
        timeWindow.getItems().add("10h");
        timeWindow.getItems().add("11h");
        createNewRequest.getChildren().add(timeWindow);
        
        createNewRequest.getChildren().add(new Label("Select a courier:"));
                
        //hbox containes two elements
        hbox.getChildren().add(couriers);
        hbox.getChildren().add(createNewRequest);
        Scene scene = new Scene(hbox, 300, 120);        
        stage.setScene(scene);
        
        stage.show();       
        
        
        //Construct a map
        String[] wkt = {
        	    "Point (-0.13666168754467312 50.81919869153657743)",
        	    "Point (-0.13622277073931291 50.82205165077141373)",
        	    "Point (-0.13545466632993253 50.82512406840893959)",
        	    "Point (-0.13457683271921211 50.82687973563037787)",
        	    "Point (-0.13413791591385191 50.82907431965718104)",
        	    "Point (-0.13951464677951447 50.8294035072611976)",
        	    "Point (-0.14346489802775639 50.83082998687861931)",
        	    "Point (-0.14697623247063807 50.83072025767727808)",
        	    "Point (-0.15004865010815954 50.83390240451614517)",
        	    "Point (-0.15740050659794308 50.8349996965295432)",
        	    "Point (-0.16486209228906662 50.83741373895902171)",
        	    "Point (-0.17276259478555042 50.83894994777778464)",
        	    "Point (-0.18549118214099652 50.8387304893751022)"
        	    };

        	    //build line
        	    /*WKTReader2 reader = new WKTReader2();
        	    GeometryFactory gf = new GeometryFactory();
        	    Coordinate[] points = new Coordinate[wkt.length];
        	    int i=0;
        	    for(String w:wkt) {
        	      Point p;
        	  try {
        	    p = (Point) reader.read(w);
        	    points[i++]=p.getCoordinate();
        	  } catch (ParseException e) {
        	    // TODO Auto-generated catch block
        	    e.printStackTrace();
        	  }

        	    }
        	    LineString line = gf.createLineString(points);
        	    SimpleFeatureBuilder builder = new SimpleFeatureBuilder(schema);
        	    builder.set("locations", line);
        	    SimpleFeature feature = builder.buildFeature("1");*/

        	    
        
    

		
	}
		

}
