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
import java.util.Date;
import java.util.List;

import algorithm.RunTSP;
import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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

public class newRequestView extends Application implements Observer {

	private Plan plan;
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private Stage stage;
	private boolean clicked;
	private float requestedX;
	private float requestedY;
	private Date requestedDate;
	private int requestedStartingTimeWindow;
	private Tour requestedTour;
	private Delivery requestedDelivery;
	
	public newRequestView()
	{
		
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.plan.addObserver(this);
		createMap(this.plan);
		this.clicked = false;
		this.stage.show();
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
	        destination.setFill(Color.BLUE);
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
		
		HBox hbox = new HBox();
		hbox.setMinWidth(width / 2);
		hbox.setMinHeight(height / 3);
		hbox.setStyle("-fx-border-color: rgb(49, 89, 47);\r\n"
				+ "    -fx-border-radius: 5;\r\n");	
		
		/*hboxNavbar*/
		HBox hboxNavbar = new HBox();
		Button buttonChangePage = new Button("Map view");
		buttonChangePage.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
		hboxNavbar.getChildren().add(buttonChangePage);
		Button buttonTSP = new Button("TSP");
		buttonTSP.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
		hboxNavbar.getChildren().add(buttonTSP);
		
		/*vBoxCouriers*/
		VBox vBoxCouriers= new VBox();
		
		vBoxCouriers.getChildren().add(new Label("Select a courier:"));
		vBoxCouriers.getChildren().add(couriers);
		
		/*vBoxcreateNewRequest*/		
		VBox vBoxcreateNewRequest = new VBox();
		vBoxcreateNewRequest.setMaxWidth(Double.MAX_VALUE);
		vBoxcreateNewRequest.setMaxHeight(Double.MAX_VALUE);
		
		DatePicker date = new DatePicker();
		date.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: rgb(49, 89, 47);");
		date.setValue(LocalDate.now());
		vBoxcreateNewRequest.getChildren().add(new Label("Date:"));
		vBoxcreateNewRequest.getChildren().add(date);
		vBoxcreateNewRequest.getChildren().add(new Label("Localisation:"));
		
		vBoxcreateNewRequest.getChildren().add(map);
		vBoxcreateNewRequest.getChildren().add(new Label("Time-window"));
		ComboBox<String> timeWindow = new ComboBox<String>();
		timeWindow.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		timeWindow.getItems().add("9h-10h");
		timeWindow.getItems().add("10h-11h");
		timeWindow.getItems().add("11h-12h");
		vBoxcreateNewRequest.getChildren().add(timeWindow);
		Button buttonValidate = new Button("Valider");
		buttonValidate.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		vBoxcreateNewRequest.getChildren().add(buttonValidate);		
				
		// hbox contains two elements
		hbox.getChildren().add(vBoxCouriers);
		hbox.getChildren().add(vBoxcreateNewRequest);
		
		VBox vbox = new VBox();
		vbox.getChildren().add(hboxNavbar);
		vbox.getChildren().add(hbox);

		Scene scene = new Scene(vbox, Double.MAX_VALUE, Double.MAX_VALUE);
		stage.setScene(scene);		
			
		map.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(clicked == false)
				{
					requestedX = (float)event.getX();
					requestedY = (float)event.getY();
				}				
				clicked = true;
			}
		});
		
		buttonValidate.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Validate");
				//TODO : Vérifier que attributs non vides
				//requestedDelivery = new Delivery();
				controller.newPositionToAdd(requestedY, requestedX);
				//controller.newDeliveryToAdd(requestedDelivery);
			}
		});
		
		buttonTSP.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("TSP");
				Intersection ptDepart = plan.getWarehouse();
				List<Intersection> sommets = new ArrayList<Intersection>();
				Long id1 = Long.parseLong("2292223595");
							
				Intersection intersection1 = plan.getNodes().get(id1);
				sommets.add(ptDepart);
				sommets.add(intersection1);
				Long id2 = Long.parseLong("26317214");
				Intersection intersection2 = plan.getNodes().get(id2);
				sommets.add(intersection2);
				
				System.out.println("Debut TSP");
				RunTSP testTSP = new RunTSP(3, sommets, plan);
				testTSP.start();
				System.out.println("Fin TSP");

			}
		});

		
		buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				Platform.runLater(new Runnable() {
				       public void run() {             
				           try {		
				        	   mapView mv = new mapView();
				        	   mv.setController(controller);
				        	   mv.setCouriers(couriers);
				        	   mv.setHeight(height);
				        	   mv.setWidth(width);
				        	   mv.setPlan(plan);
				        	   mv.start(stage);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				       }
				    });
			}
		});
	}
	
	@Override
	public void update(Observable observed, Object arg) {
		createMap(this.plan);
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public ListView<Courier> getCouriers() {
		return couriers;
	}

	public void setCouriers(ListView<Courier> couriers) {
		this.couriers = couriers;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	

}
