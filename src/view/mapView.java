package view;

import java.util.ArrayList;
import java.util.List;

import algorithm.RunTSP;
import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class mapView extends Application implements Observer{

	private Map map;
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private ListView<Delivery> deliveries;
	private Stage stage;
	private Tour tour;
	private int screenWidth; 
	private int screenHeight;
	private int margin;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		/*Init attributes*/
		this.stage = stage;	
		//this.map.addObserver(this);
		this.tour.addObserver(this);
		this.controller = new Controller(this.stage, this.tour, this.couriers);
		System.out.println("2");

		/*Resize the window*/
		stage.setWidth(width);
		stage.setHeight(height);
		/*Display stage*/
		createMap(this.map);
		
		//TSP run every time 
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		System.out.println("tour.getSteps() -> "+tour.getSteps());
		if(!tour.getSteps().isEmpty())
		{
			for(Delivery d : tour.getSteps()) {
				System.out.println("Delivery d destination: "+d.getDestination());
				sommets.add(d.getDestination());
			}
			System.out.println("Debut TSP");
			RunTSP testTSP = new RunTSP(sommets.size(), sommets, map, tour);
			testTSP.start();
			System.out.println("Fin TSP");
		}
	}
	
	public ListView<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(ListView<Delivery> deliveries) {
		this.deliveries = deliveries;
	}
	public void createMap(Map map)
	{
		this.screenWidth = (int)(width/(2));
		this.screenHeight = (int)(width/(2 *map.getRatioLongOverLat()));
		
		this.margin = 45;
		
		Pane mapPane = new Pane();
		mapPane.setMinWidth(screenHeight);
		mapPane.setMinHeight(screenHeight);
		

		mapPane.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;" + "-fx-border-insets: 30px;");
    

        //Add warehouse
		float circleCenterX = tour.getFactorLongitudeToX(map.getWarehouse().getLongitude()) * this.screenWidth + margin;
        float circleCenterY = tour.getFactorLatitudeToY(map.getWarehouse().getLatitude()) * this.screenHeight + margin;
        
        Circle wareHouse = new Circle();
        wareHouse.setCenterX(circleCenterX);
        wareHouse.setCenterY(circleCenterY);
        wareHouse.setRadius(10.0f);
        mapPane.getChildren().add(wareHouse);
        
        //display the deliveries destinations
        for(Delivery d : tour.getSteps())
	    {     
        	float latDestination = d.getDestination().getLatitude();
        	float longDestination = d.getDestination().getLongitude();
        	
        	float circleCenterDestinationX = tour.getFactorLongitudeToX(longDestination) * this.screenWidth;
        	float circleCenterDestinationY = tour.getFactorLatitudeToY(latDestination) * this.screenHeight;
	        
	        Circle destination = new Circle();
	        destination.setFill(Color.BLUE);
	        destination.setCenterX(circleCenterDestinationX + margin);
	        destination.setCenterY(circleCenterDestinationY + margin);
	        destination.setRadius(5.0f);
	        mapPane.getChildren().add(destination);
		}
		
        // display the map
        for (Intersection i : map.getNodes().values()) 
        { 
        	for (Segment s : i.getOutSections()) 
        	{      
        		float x1 = tour.getFactorLongitudeToX(i.getLongitude()) * this.screenWidth;
        		float y1 = tour.getFactorLatitudeToY(i.getLatitude()) * this.screenHeight;
        		float x2 = tour.getFactorLongitudeToX(s.getDestination().getLongitude()) * this.screenWidth;
        		float y2 = tour.getFactorLatitudeToY(s.getDestination().getLatitude()) * this.screenHeight;
        	
        		Line newLine = new Line(x1 + margin, y1 + margin, x2 + margin, y2 + margin); 
        		mapPane.getChildren().add(newLine);	
            }
        } 
      
        
     // display the tour
        if(tour.getSteps()!= null) {
	        for (int i =0; i<tour.getTourSteps().size() -1; i++) { 
	        	
        		float x1 = tour.getFactorLongitudeToX(tour.getTourSteps().get(i).getLongitude()) * this.screenWidth;
        		float y1 = tour.getFactorLatitudeToY(tour.getTourSteps().get(i).getLatitude()) * this.screenHeight;
        		float x2 = tour.getFactorLongitudeToX(tour.getTourSteps().get(i+1).getLongitude()) * this.screenWidth;
        		float y2 = tour.getFactorLatitudeToY(tour.getTourSteps().get(i+1).getLatitude()) * this.screenHeight;
        	
        		Line newLine = new Line(x1 + margin, y1 + margin, x2 + margin, y2 + margin); 
        		newLine.setStroke(Color.RED);
        		newLine.setStrokeWidth(4);
        		mapPane.getChildren().add(newLine);	
	        } 
        }
        
        display(mapPane);
	}

	public void display(Pane mapPane) {
		
		VBox vBoxiIntentedTours = new VBox();
		vBoxiIntentedTours.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: #f3f6f4;" + "-fx-margin: 120 150 150 120;");
		
		VBox vBoxMap = new VBox();
		vBoxMap.getChildren().add(mapPane);

		/*Button button = new Button("TSP");
		vBoxMap.getChildren().add(button);*/
		Button buttonChangePage = new Button("New request");
		vBoxMap.getChildren().add(buttonChangePage);
		
		//Modifications ajout tableau livraisons
	    //TableView<Delivery> table = new TableView<Delivery>();
	    //Create column UserName (Data type of String).
	    //TableColumn<Delivery, Courier> courierCol //
	        //= new TableColumn<Delivery, Courier>("Courier name");
	    //TableColumn<Delivery, Intersection> locationCol //
        	//= new TableColumn<Delivery, Intersection>("Location");
	    //TableColumn<Delivery, String> timeWindowCol //
	    	//= new TableColumn<Delivery, String>("Time Window");
	    //courierCol.setPrefWidth(200.0d);
	    //locationCol.setPrefWidth(200.0d);
	    //timeWindowCol.setPrefWidth(200.0d);
	    //table.getColumns().addAll(courierCol, locationCol, timeWindowCol);
		System.out.println(deliveries);
		vBoxiIntentedTours.getChildren().add(new Label("Deliveries of the day:"));
	    vBoxiIntentedTours.getChildren().add(deliveries);
	    // -> Test ajout colonne
	    
	    //Fin modifications
		
		HBox hbox = new HBox();
		hbox.setMinWidth(width / 2);
		hbox.setMinHeight(height / 3);

		// hbox contains two elements
		hbox.getChildren().add(vBoxMap);
		hbox.getChildren().add(vBoxiIntentedTours);
		Scene scene = new Scene(hbox, 200, 500);
		
		/*button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				List<Intersection> sommets = new ArrayList<Intersection>();
				sommets.add(map.getWarehouse());
				for(Delivery d : tour.getSteps()) {
					sommets.add(d.getDestination());
				}
				System.out.println("Debut TSP");
				RunTSP testTSP = new RunTSP(sommets.size(), sommets, map, tour);
				testTSP.start();
				System.out.println("Fin TSP");
			}
		});*/
		
		buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				Platform.runLater(new Runnable() {
				       public void run() { 
				    	   try 
				    	   {		
				        	   newRequestView nr = new newRequestView();
				        	   nr.setController(controller);
				        	   nr.setCouriers(couriers);
				        	   nr.setHeight(height);
				        	   nr.setWidth(width);
				        	   nr.setPlan(map);
				        	   nr.setTour(tour);
				        	   nr.setDeliveries(deliveries);
				        	   
				        	   nr.start(stage);	   
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
				       }
				    });
			}
		});
		
		this.stage.setScene(scene);
		this.stage.show();
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
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
		return this.stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setTour(Tour tour) {
		this.tour = tour;
	}

	@Override
	public void update(Observable observed, Object arg) {
		// TODO Auto-generated method stub
		if(arg instanceof Delivery)
		{
			System.out.println("object :"+arg);
			deliveries.getItems().add((Delivery) arg);
		}
		this.createMap(map);	
	}

}
