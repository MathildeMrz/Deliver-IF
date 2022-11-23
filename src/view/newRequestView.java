package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.Courier;
import model.Intersection;
import model.Plan;
import model.Segment;
import observer.Observable;
import observer.Observer;
import xml.XMLdeserializer;

public class newRequestView extends Application implements Observer  {	
    
	public static void main(String[] args) {
		Application.launch(args);
	}
		
	@Override
	public void start(Stage stage) throws Exception {
		Plan plan = new Plan();
		XMLdeserializer.load(plan);
		
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
        
        createNewRequest.getChildren().add(displayMap(width, height, plan, stage));
                
        //createNewRequest.getChildren().add(map);                                    
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
        
        /*ArrayList<Integer> hours = new ArrayList<Integer>();
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
        //map.start(stage);*/
		
	}
	
	public Pane displayMap(int width, int height, Plan plan, Stage stage)
	{
		Pane map = new Pane();
        map.setMinWidth(width/4);
        map.setMinHeight(height/4);     
   
        map.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
    
        float widthSegment = plan.getLongitudeMax() - plan.getLongitudeMin();
        float heightSegment = plan.getLatitudeMax() - plan.getLatitudeMin();
        
        //Add warehouse
        float latWareHouse = plan.getWarehouse().getLatitude();
        float longWareHouse = plan.getWarehouse().getLongitude();
        float circleCenterX = ((longWareHouse - plan.getLongitudeMin()) / widthSegment) * width/4;
        float circleCenterY = ((latWareHouse - plan.getLatitudeMin()) / heightSegment) * height/4;       
        Circle wareHouse = new Circle();
        wareHouse.setCenterX(circleCenterX);
        wareHouse.setCenterY(circleCenterY);
        wareHouse.setRadius(10.0f);
        map.getChildren().add(wareHouse);
        
        for (int counterIntersection = 0; counterIntersection < plan.getNodes().size(); counterIntersection++) { 
        	Intersection i = plan.getNodes().get(counterIntersection);
        	for (int counterSegment = 0; counterSegment < i.getOutSections().size(); counterSegment++) { 
        		Segment s = i.getOutSections().get(counterSegment);
        		
        		float x1 = ((i.getLongitude() - plan.getLongitudeMin()) / widthSegment) * width/4;
        		float y1 = ((i.getLatitude() - plan.getLatitudeMin()) / heightSegment) * height/4;
        		float x2 = ((s.getDestination().getLongitude() - plan.getLongitudeMin()) / widthSegment) * width/4;
        		float y2 = ((s.getDestination().getLatitude() - plan.getLatitudeMin()) / heightSegment) * height/4;
        		
        		Line newLine = new Line(x1 +20 , y1 +20 , x2 + 20, y2+20); 
        		map.getChildren().add(newLine);
        		
            }
        }  
        return map;
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

	@Override
	public void update(Observable observed, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
