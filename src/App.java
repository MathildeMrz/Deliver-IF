import javafx.scene.paint.Color;

import javafx.scene.shape.Line;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		
		ListView<Courier> listView = new ListView<Courier>();
		listView = initCouriers();
		
		//Resize the window
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		stage.setResizable(true);
		stage.setWidth(width/2);
		stage.setHeight(height/3);
		stage.centerOnScreen();
		stage.setFullScreen(true);
		
		//Display
		VBox couriers = new VBox(listView); 
		couriers.setMinWidth(width/8);
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
                
        HBox mapBox = new HBox();
        mapBox.setMinWidth(width/4);
        mapBox.setMinHeight(height/3);
        mapBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        
        for (int counter = 0; counter < lines.size(); counter++) { 		      
        	mapBox.getChildren().add(lines.get(counter));
        }        
        
        createNewRequest.getChildren().add(mapBox);                                    
        createNewRequest.getChildren().add(new Label("Time-window"));
        
        ComboBox<String> timeWindow = new ComboBox();
        timeWindow.getItems().add("9h-10h");
        timeWindow.getItems().add("10h-11h");
        timeWindow.getItems().add("11h-12h");
        createNewRequest.getChildren().add(timeWindow);
        
        createNewRequest.getChildren().add(new Label("Select a courier:"));
                
        HBox hbox = new HBox();  
        hbox.setMinWidth(width/2);
        hbox.setMinHeight(height/2);
  
        
        //hbox contains two elements
        hbox.getChildren().add(couriers);
        hbox.getChildren().add(createNewRequest);                    
               
        Scene scene = new Scene(hbox, 200, 500);
        stage.setScene(scene);        
        stage.show();
        
        ArrayList<Integer> hours = new ArrayList<Integer>();
        hours.add(9);
        hours.add(10);
        hours.add(11);
        hours.add(12);
        
        // Draw the main line from left to right
        int offset = 2;
        int axisLength = width - (2 * offset);
        Line horizontalAxis = new Line(offset, offset * 3, axisLength, offset * 3);
        horizontalAxis.setStroke(Color.DARKGREY);
        //horizontalAxis.setStrokeWidth(5);
        couriers.getChildren().add(horizontalAxis);
        
        HBox hTimeLineBox = new HBox();
        
        double distanceBetweenHours = axisLength / hours.size();
        System.out.println("distanceBetweenHours : "+distanceBetweenHours);

        for (int i = 0; i < hours.size(); i++) {
        	System.out.println(hours.get(i));
            int currentHour = hours.get(i);
            double hourLineX = (offset * 3) + (i * distanceBetweenHours);

            Line hourLine = new Line(hourLineX, offset * 2, hourLineX, offset * 4);
            hourLine.setStroke(Color.PINK);
            hourLine.setStrokeWidth(currentHour % 10 == 0 ? 4 : 2);
            hTimeLineBox.getChildren().add(hourLine);
            //couriers.getChildren().add(hTimeLineBox);

            // Add a label for every 10 year
            if (currentHour % 0.5 == 0) {
                Label hourLabel = new Label(String.valueOf(currentHour));
                hourLabel.setLayoutX(hourLineX - 20);
                hourLabel.setLayoutY(offset * 5);
                hourLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
                //hTimeLineBox.getChildren().add(hourLabel);
                //couriers.getChildren().add(hTimeLineBox);
            }
            //couriers.getChildren().add(hTimeLineBox);
        }
        
        //couriers.getChildren().add(hTimeLineBox);
        
        //mapView map = new mapView();
        //map.start(stage);
		
	}
	
	public ListView<Courier> initCouriers()
	{
		 // File path is passed as parameter
        File file = new File("./saveCouriers.txt");
        ListView<Courier> listView = new ListView<Courier>();

        if (file.exists()) 
	    {
	        // Creating an object of BufferedReader class
	        BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				// Declaring a string variable
		        String st;
		        // Condition holds true till
					try {
						
						while ((st = br.readLine()) != null)
						{
							//System.out.println("st = "+st);
							String[] arrSplit_2 = st.split(";");
							//System.out.println("arrSplit_2[0] = "+arrSplit_2[0]);
							//System.out.println("arrSplit_2[1] = "+arrSplit_2[1]);
							
							listView.getItems().add(new Courier(arrSplit_2[0], Double.parseDouble(arrSplit_2[1])));
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
        }
        else 
        {
        	  System.out.println(file.getPath() + " does not exist");
        }
        return listView;
	}
		

}
