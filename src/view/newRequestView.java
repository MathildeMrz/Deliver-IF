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
		stage.setFullScreen(true);
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
    	System.out.println("DEBUT2------------------------------------------------------------");
        float latWareHouse = plan.getWarehouse().getLatitude();
        float longWareHouse = plan.getWarehouse().getLongitude();
        System.out.println("(plan.getLongitudeMin()) : "+(plan.getLongitudeMin()));
    	System.out.println("(plan.getLatitudeMin()) : "+(plan.getLatitudeMin()));
    	System.out.println("longDestination : "+longWareHouse);
    	System.out.println("latDestination : "+latWareHouse);
    	System.out.println("(longDestination - plan.getLongitudeMin()) : "+(longWareHouse - plan.getLongitudeMin()));
    	System.out.println("(latDestination - plan.getLatitudeMin()) : "+(latWareHouse - plan.getLatitudeMin()));
    	System.out.println("(longDestination - plan.getLongitudeMin()) / widthSegment) : "+((longWareHouse - plan.getLongitudeMin()) / widthSegment));
    	System.out.println("(latDestination - plan.latDestination()) / heightSegment) : "+((latWareHouse - plan.getLatitudeMin()) / heightSegment));
    	System.out.println("(longDestination - plan.getLongitudeMin())/ widthSegment)* width/4 : "+((longWareHouse - plan.getLongitudeMin()) / widthSegment)* width/4);
    	System.out.println("(latDestination - plan.latDestination()) / heightSegment)* height/4 : "+((latWareHouse - plan.getLatitudeMin()) / heightSegment)* height/4);
        
        
        float circleCenterX = ((longWareHouse - plan.getLongitudeMin()) / widthSegment) * width/4;
        float circleCenterY = ((latWareHouse - plan.getLatitudeMin()) / heightSegment) * height/4;       
        System.out.println("latWareHouse Y: "+latWareHouse);
    	System.out.println("longWareHouse X: "+longWareHouse);
    	System.out.println("circleCenterX Y: "+circleCenterX);
    	System.out.println("circleCenterY X: "+circleCenterY);
        Circle wareHouse = new Circle();
        wareHouse.setCenterX(circleCenterX);
        wareHouse.setCenterY(circleCenterY);
        wareHouse.setRadius(10.0f);
        map.getChildren().add(wareHouse);
        
        //display the new destinations
        for (int counterDestinations = 0; counterDestinations < plan.getDestinations().size(); counterDestinations++) 
		{
        	System.out.println("DEBUT------------------------------------------------------------");
        	float latDestination = plan.getDestinations().get(counterDestinations).getLatitude();
        	float longDestination = plan.getDestinations().get(counterDestinations).getLongitude();
        	System.out.println("(plan.getLongitudeMin()) : "+(plan.getLongitudeMin()));
        	System.out.println("(plan.getLatitudeMin()) : "+(plan.getLatitudeMin()));
        	System.out.println("longDestination : "+longDestination);
        	System.out.println("latDestination : "+latDestination);
        	System.out.println("(longDestination - plan.getLongitudeMin()) : "+(longDestination - plan.getLongitudeMin()));
        	System.out.println("(latDestination - plan.getLatitudeMin()) : "+(latDestination - plan.getLatitudeMin()));
        	System.out.println("(longDestination - plan.getLongitudeMin()) / widthSegment) : "+((longDestination - plan.getLongitudeMin()) / widthSegment));
        	System.out.println("(latDestination - plan.latDestination()) / heightSegment) : "+((latDestination - plan.getLatitudeMin()) / heightSegment));
        	System.out.println("(longDestination - plan.getLongitudeMin()) / widthSegment)* width/4 : "+((longDestination - plan.getLongitudeMin()) / widthSegment)* width/4);
        	System.out.println("(latDestination - plan.latDestination()) / heightSegment)* height/4 : "+((latDestination - plan.getLatitudeMin()) / heightSegment)* height/4);
        	
        	float circleCenterDestinationX = ((longDestination - plan.getLongitudeMin()) / widthSegment) * width/4;
        	float circleCenterDestinationY = ((latDestination - plan.getLatitudeMin()) / heightSegment) * height/4;
	        
        	System.out.println("circleCenterX : "+circleCenterDestinationX);
        	System.out.println("circleCenterY : "+circleCenterDestinationY);
	        Circle destination = new Circle();
	        destination.setFill(Color.YELLOW);
	        destination.setCenterX(circleCenterDestinationX);
	        destination.setCenterY(circleCenterDestinationY);
	        destination.setRadius(10.0f);
	        map.getChildren().add(destination);
		}
		
        for (int counterIntersection = 0; counterIntersection < plan.getNodes().size(); counterIntersection++) 
        { 
        	Intersection i = plan.getNodes().get(counterIntersection);
        	for (int counterSegment = 0; counterSegment < i.getOutSections().size(); counterSegment++) 
        	{ 
        		Segment s = i.getOutSections().get(counterSegment);
        		
        		float x1 = ((i.getLongitude() - plan.getLongitudeMin()) / widthSegment) * width/4;
        		float y1 = ((i.getLatitude() - plan.getLatitudeMin()) / heightSegment) * height/4;
        		float x2 = ((s.getDestination().getLongitude() - plan.getLongitudeMin()) / widthSegment) * width/4;
        		float y2 = ((s.getDestination().getLatitude() - plan.getLatitudeMin()) / heightSegment) * height/4;
        		
        		Line newLine = new Line(x1 +20 , y1 +20 , x2 + 20, y2+20); 
        		map.getChildren().add(newLine);
        		
            }
        } 
        
        display(map);
	}

	public void display(Pane map) {
		ListView<Courier> listView = new ListView<Courier>();
		for (int counter = 0; counter < couriers.getItems().size(); counter++) {
			listView.getItems().add(couriers.getItems().get(counter));
		}

		VBox vobxCouriers = new VBox(listView);
		vobxCouriers.setMinWidth(width / 8);
		DatePicker date = new DatePicker();
		date.setValue(LocalDate.now());

		VBox vBoxcreateNewRequest = new VBox();
		vBoxcreateNewRequest.getChildren().add(new Label("Date:"));
		vBoxcreateNewRequest.getChildren().add(date);
		vBoxcreateNewRequest.getChildren().add(new Label("Localisation:"));

		vBoxcreateNewRequest.getChildren().add(map);

		// createNewRequest.getChildren().add(map);
		vBoxcreateNewRequest.getChildren().add(new Label("Time-window"));

		ComboBox<String> timeWindow = new ComboBox();
		timeWindow.getItems().add("9h-10h");
		timeWindow.getItems().add("10h-11h");
		timeWindow.getItems().add("11h-12h");
		vBoxcreateNewRequest.getChildren().add(timeWindow);

		vBoxcreateNewRequest.getChildren().add(new Label("Select a courier:"));

		HBox hbox = new HBox();
		hbox.setMinWidth(width / 2);
		hbox.setMinHeight(height / 2);

		// hbox contains two elements
		hbox.getChildren().add(vobxCouriers);
		hbox.getChildren().add(vBoxcreateNewRequest);
		Scene scene = new Scene(hbox, 200, 500);
		stage.setScene(scene);
		stage.show();

		map.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("onclick X : " + event.getSceneX());
				System.out.println("onClick Y : " + event.getSceneY());
				controller.newPositionToAdd((float)event.getSceneY(), (float)event.getSceneX());
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
