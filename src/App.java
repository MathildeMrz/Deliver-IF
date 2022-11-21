import javafx.scene.paint.Color;

import javafx.scene.shape.Line;

import java.time.LocalDate;
import java.util.ArrayList;

import model.Courier;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
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

		//TODO : ajouter un background color
		//TODO : rendre la listeView non cliquable au début

		listView.getItems().add(c1);
		listView.getItems().add(c2);
		
		//Display
		VBox couriers = new VBox(listView);        
		//VBox vbox2 = new VBox(listView2);        
        HBox hbox = new HBox();        
                
        DatePicker date = new DatePicker();
        
        date.setValue(LocalDate.now());

        VBox createNewRequest = new VBox();     
        createNewRequest.getChildren().add(new Label("Date:"));
        createNewRequest.getChildren().add(date);
        createNewRequest.getChildren().add(new Label("Localisation:"));
        
        ArrayList<Line> lines = new ArrayList<Line>();
        Line line1 = new Line(20, 37, 40, 56);
        Line line2 = new Line(78, 55, 34, 90);
        Line line3 = new Line(90, 72, 49, 93);
        Line line4 = new Line(12, 80, 12, 16);  

        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);
        
        HBox hbox2 = new HBox();
        hbox2.setMinWidth(500);
        hbox2.setMinHeight(500);
        hbox2.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

        
        for (int counter = 0; counter < lines.size(); counter++) { 		      
        	hbox2.getChildren().add(lines.get(counter));
        }        
        
        createNewRequest.getChildren().add(hbox2);
                                    
        createNewRequest.getChildren().add(new Label("Time-window"));
        
        ComboBox<String> timeWindow = new ComboBox();
        timeWindow.getItems().add("9h-10h");
        timeWindow.getItems().add("10h-11h");
        timeWindow.getItems().add("11h-12h");
        createNewRequest.getChildren().add(timeWindow);
        
        createNewRequest.getChildren().add(new Label("Select a courier:"));
                
        //hbox contains two elements
        hbox.getChildren().add(couriers);
        hbox.getChildren().add(createNewRequest);                    
               
        Scene scene = new Scene(hbox, 300, 120);
        stage.setScene(scene);        
        stage.show();
        
        //mapView map = new mapView();
        //map.start(stage);
		
	}
		

}
