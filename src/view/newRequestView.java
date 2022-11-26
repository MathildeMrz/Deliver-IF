package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import controller.Controller;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

public class newRequestView extends Application implements Observer {

	private Plan plan;
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private Stage stage;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;

		// init
		this.plan = new Plan();
		XMLdeserializer.load(this.plan);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.width = gd.getDisplayMode().getWidth();
		this.height = gd.getDisplayMode().getHeight();
		this.couriers = initCouriers();

		this.controller = new Controller(stage, plan, couriers);

		// Resize the window
		stage.setResizable(true);
		stage.setWidth(width / 2);
		stage.setHeight(height / 3);
		stage.centerOnScreen();
		//stage.setFullScreen(true);
		createMap(plan);

		this.plan.addObserver(this);

	}

	public void createMap(Plan plan)
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
        
        //display the deliveries destinations
        for(Intersection d : plan.getDestinations())
	    {     
        	float latDestination = d.getLatitude();
        	float longDestination = d.getLongitude();
        	
        	float circleCenterDestinationX = ((longDestination - plan.getLongitudeMin()) / widthSegment) * width/4;
        	float circleCenterDestinationY = ((latDestination - plan.getLatitudeMin()) / heightSegment) * height/4;
	        
	        Circle destination = new Circle();
	        destination.setFill(Color.YELLOW);
	        destination.setCenterX(circleCenterDestinationX);
	        destination.setCenterY(circleCenterDestinationY);
	        destination.setRadius(5.0f);
	        map.getChildren().add(destination);
		}
		
        for (int counterIntersection = 0; counterIntersection < plan.getNodes().size(); counterIntersection++) 
        { 
        	Intersection i = plan.getNodes().get(plan.getNodes().keySet().toArray()[counterIntersection]);
        	for (int counterSegment = 0; counterSegment < i.getOutSections().size(); counterSegment++) 
        	{ 
        		Segment s = i.getOutSections().get(counterSegment);
        		
        		float x1 = ((i.getLongitude() - plan.getLongitudeMin()) / widthSegment) * width/4;
        		float y1 = ((i.getLatitude() - plan.getLatitudeMin()) / heightSegment) * height/4;
        		float x2 = ((s.getDestination().getLongitude() - plan.getLongitudeMin()) / widthSegment) * width/4;
        		float y2 = ((s.getDestination().getLatitude() - plan.getLatitudeMin()) / heightSegment) * height/4;
        		
        		Line newLine = new Line(x1 , y1 , x2 , y2); 
        		map.getChildren().add(newLine);	
            }
        } 
        
        display(map);
	}

	public void display(Pane map) {
		
		VBox vobxCouriers = new VBox(couriers);
		vobxCouriers.setMinWidth(width / 8);
		DatePicker date = new DatePicker();
		date.setValue(LocalDate.now());

		VBox vBoxcreateNewRequest = new VBox();
		vBoxcreateNewRequest.getChildren().add(new Label("Date:"));
		vBoxcreateNewRequest.getChildren().add(date);
		vBoxcreateNewRequest.getChildren().add(new Label("Localisation:"));

		vBoxcreateNewRequest.getChildren().add(map);

		vBoxcreateNewRequest.getChildren().add(new Label("Time-window"));

		ComboBox<String> timeWindow = new ComboBox();
		timeWindow.getItems().add("9h-10h");
		timeWindow.getItems().add("10h-11h");
		timeWindow.getItems().add("11h-12h");
		vBoxcreateNewRequest.getChildren().add(timeWindow);

		vBoxcreateNewRequest.getChildren().add(new Label("Select a courier:"));

		HBox hbox = new HBox();
		hbox.setMinWidth(width / 2);
		hbox.setMinHeight(height / 3);

		// hbox contains two elements
		hbox.getChildren().add(vobxCouriers);
		hbox.getChildren().add(vBoxcreateNewRequest);
		Scene scene = new Scene(hbox, 200, 500);
		stage.setScene(scene);
		stage.show();
		
	
		map.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				controller.newPositionToAdd((float)event.getY(), (float)event.getX());
			}
		});
	}

	public static ListView<Courier> initCouriers() {

		File file = new File("./saveCouriers.txt");
		ListView<Courier> couriers = new ListView<Courier>();

		if (file.exists()) {
			// Creating an object of BufferedReader class
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				// Declaring a string variable
				String st;
				// Condition holds true till
				try {

					while ((st = br.readLine()) != null) {
						String[] arrSplit_2 = st.split(";");
						couriers.getItems().add(new Courier(arrSplit_2[0], Double.parseDouble(arrSplit_2[1])));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println(file.getPath() + " does not exist");
		}
		return couriers;
	}

	@Override
	public void update(Observable observed, Object arg) {
		createMap(this.plan);
	}

}
