package view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import algorithm.RunTSP;
import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import observer.Observable;
import observer.Observer;

public class newRequestView extends Application implements Observer {

	private Map map;
	private Tour tour;
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private Stage stage;
	private boolean clicked;
	private int screenWidth; 
	private int screenHeight; 
	
	public newRequestView()
	{
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.tour.addObserver(this);
		createMap(this.map);
		this.clicked = false;
		this.stage.show();
	}

	public void createMap(Map map)
	{
		this.screenHeight = height/4;
		this.screenWidth = width/4;
		Pane mapPane = new Pane();
        mapPane.setMinWidth(screenWidth);
        mapPane.setMinHeight(screenHeight);  

        mapPane.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

        //Add warehouse
        float circleCenterX = tour.getFactorLongitudeToX(map.getWarehouse().getLongitude()) * this.screenWidth;
        float circleCenterY = tour.getFactorLatitudeToY(map.getWarehouse().getLatitude()) * this.screenHeight;
  
        Circle wareHouse = new Circle();
        wareHouse.setCenterX(circleCenterX);
        wareHouse.setCenterY(circleCenterY);
        wareHouse.setRadius(10.0f);
        mapPane.getChildren().add(wareHouse);
        
        //display the deliveries destinations
        System.out.println(tour.getSteps());
        if(tour.getSteps()!= null) {
	        for( Delivery d : tour.getSteps())
		    {     
	        	System.out.println("on entre for");
		    	Intersection dest = d.getDestination();
	        	float latDestination = dest.getLatitude();
	        	float longDestination = dest.getLongitude();
	        	
	        	float circleCenterDestinationX = tour.getFactorLongitudeToX(longDestination) * this.screenWidth;
	        	float circleCenterDestinationY = tour.getFactorLatitudeToY(latDestination) * this.screenHeight;
		        
		        Circle destination = new Circle();
		        destination.setFill(Color.BLUE);
		        destination.setCenterX(circleCenterDestinationX);
		        destination.setCenterY(circleCenterDestinationY);
		        destination.setRadius(5.0f);
		        mapPane.getChildren().add(destination);
			}
        }
		
        for (Intersection i : map.getNodes().values()) 
        { 
        	for (Segment s : i.getOutSections()) 
        	{        		
        		float x1 = tour.getFactorLongitudeToX(i.getLongitude()) * this.screenWidth;
        		float y1 = tour.getFactorLatitudeToY(i.getLatitude()) * this.screenHeight;
        		float x2 = tour.getFactorLongitudeToX(s.getDestination().getLongitude()) * this.screenWidth;
        		float y2 = tour.getFactorLatitudeToY(s.getDestination().getLatitude()) * this.screenHeight;
        		
        		//System.out.println(x1);
        		//System.out.println(y1);
        		
        		Line newLine = new Line(x1 , y1 , x2 , y2); 
        		mapPane.getChildren().add(newLine);	
            }
        } 
        
        display(mapPane);
	}

	public void display(Pane mapPane) {
		
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
		//vBoxcreateNewRequest.setFillWidth(true);
		//BackgroundFill bf = new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(1), null);
		//vBoxcreateNewRequest.setBackground(new Background(bf));
		DatePicker date = new DatePicker();
		date.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: rgb(49, 89, 47);");
		date.setValue(LocalDate.now());
		vBoxcreateNewRequest.getChildren().add(new Label("Date:"));
		vBoxcreateNewRequest.getChildren().add(date);
		vBoxcreateNewRequest.getChildren().add(new Label("Localisation:"));
		
		vBoxcreateNewRequest.getChildren().add(mapPane);
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
			
		mapPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(clicked == false)
				{
					float longitude = tour.getFactorXToLongitude((float)(event.getX()/screenWidth));
					float latitude = tour.getFactorYToLatitude((float)(event.getY()/screenHeight));
					controller.addDelivery(latitude, longitude);
				}				
				clicked = true;
			}
		});
		
		buttonTSP.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("TSP");
				Intersection ptDepart = map.getWarehouse();
				List<Intersection> sommets = new ArrayList<Intersection>();
				Long id1 = Long.parseLong("2292223595");
							
				Intersection intersection1 = map.getNodes().get(id1);
				sommets.add(ptDepart);
				sommets.add(intersection1);
				Long id2 = Long.parseLong("26317214");
				Intersection intersection2 = map.getNodes().get(id2);
				sommets.add(intersection2);
				
				System.out.println("Debut TSP");
				RunTSP testTSP = new RunTSP(3, sommets, map);
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
				        	   mv.setPlan(map);
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
		this.tour = (Tour) observed;
		createMap(this.map);	
	}

	public Map getPlan() {
		return map;
	}

	public void setPlan(Map map) {
		this.map = map;
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
	
	public void setTour(Tour tour) {
		this.tour = tour;
	}

}
