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

	private Map plan;
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private ListView<Delivery> deliveries;
	private Stage stage;
	private Tour tour;
	private VBox vBoxiIntentedTours;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		/*Init attributes*/
		this.stage = stage;	
		System.out.println("1");
		//this.plan.addObserver(this);
		this.tour.addObserver(this);
		this.controller = new Controller(this.stage, this.tour, this.couriers);
		System.out.println("2");

		/*Resize the window*/
		stage.setResizable(true);
		System.out.println("3");
		stage.setWidth(width / 2);
		System.out.println("4");
		stage.setHeight(height / 3);
		System.out.println("5");
		stage.centerOnScreen();
		System.out.println("6");
		//stage.setFullScreen(true);
		/*Display stage*/
		createMap(this.plan);	
	}
	
	public ListView<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(ListView<Delivery> deliveries) {
		this.deliveries = deliveries;
	}
	
	public void createMap(Map plan)
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
		
		vBoxiIntentedTours = new VBox();
		vBoxiIntentedTours.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: #f3f6f4;");
		
		VBox vBoxMap = new VBox();
		vBoxMap.getChildren().add(map);

		Button button = new Button("TSP");
		vBoxMap.getChildren().add(button);
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
		
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
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
				    	   try 
				    	   {		
				        	   newRequestView nr = new newRequestView();
				        	   nr.setController(controller);
				        	   nr.setCouriers(couriers);
				        	   nr.setHeight(height);
				        	   nr.setWidth(width);
				        	   nr.setPlan(plan);
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

	public Map getPlan() {
		return plan;
	}

	public void setPlan(Map plan) {
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
		
	}

}
