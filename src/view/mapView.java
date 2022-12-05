package view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import algorithm.RunTSP;
import controller.ControllerAddDelivery;
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
import model.CustomCircleMarkerLayer;
import model.CustomPinLayer;
import model.CustomPolygoneMarkerLayer;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import observer.Observable;
import observer.Observer;
import xml.ExceptionXML;
import xml.XMLdeserializer;

public class mapView extends Application implements Observer{

	private Map map;
	private ControllerAddDelivery controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private ListView<Delivery> deliveries;
	private Stage stage;
	private Tour tour;
	private int screenWidth; 
	private int screenHeight;
	private int margin;
	private MapView mapView;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		/*Init attributes*/
		this.stage = stage;	
		this.tour.addObserver(this);
		this.controller = new ControllerAddDelivery(this.stage, this.tour, this.couriers);

		/*Resize the window*/
		stage.setWidth(width/1.3);
		stage.setHeight(height/1.4);
		
		System.out.println("this.map 2 : "+this.map);
		createMap(this.map);
		
		/*XMLdeserializer.load(map, stage);
		map.setMapLoaded();
		tour.calculateWidthHeightMap();
		tour.clearOrderedDeliveries();
		tour.clearUnorderedDeliveries();
		deliveries.getItems().clear();
		map.setRatio();
		createMap(map);*/
		
		//TSP run every time 
		/*List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		System.out.println("tour.getSteps() -> "+tour.getUnorderedDeliveries());
		if(!tour.getUnorderedDeliveries().isEmpty())
		{
			for(Delivery d : tour.getUnorderedDeliveries()) {
				System.out.println("Delivery d destination: "+d.getDestination());
				sommets.add(d.getDestination());
			}
			System.out.println("Debut TSP");
			RunTSP testTSP = new RunTSP(sommets.size(), sommets, map, tour);
			testTSP.start();
			System.out.println("Fin TSP");
		}*/
	}
	
	public ListView<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(ListView<Delivery> deliveries) {
		this.deliveries = deliveries;
	}
	public void createMap(Map map) throws MalformedURLException
	{
		System.out.println("dans createMap : "+map);
		this.screenWidth = (int)(width/(2));
		this.screenHeight = (int)(width/(2 *map.getRatioLongOverLat()));
		
		this.margin = 45;
	
		//if(this.map.getIsLoaded()) {
			//Add warehouse       
	        MapPoint mapPointWareHouse = new MapPoint(map.getWarehouse().getLatitude(), map.getWarehouse().getLongitude());     
	        MapLayer mapLayerWareHouse = new CustomCircleMarkerLayer(mapPointWareHouse, 10, javafx.scene.paint.Color.RED);
	        this.mapView.addLayer(mapLayerWareHouse);
        
	        //add intersections
	        for (Intersection i : map.getNodes().values()) 
	        {    
				MapPoint mapDelivery = new MapPoint(i.getLatitude(), i.getLongitude());     
		        MapLayer mapLayerDelivery = new CustomCircleMarkerLayer(mapDelivery, 2, javafx.scene.paint.Color.BLUEVIOLET);
		        this.mapView.addLayer(mapLayerDelivery);
	        }
	        
	        //add deliveries
	        for(Delivery d : tour.getUnorderedDeliveries())
		    {     
	        	float latDestination = d.getDestination().getLatitude();
	        	float longDestination = d.getDestination().getLongitude();   
	        	MapPoint mapPointPin = new MapPoint(latDestination, longDestination);
	        	MapLayer mapLayerPin = new CustomPinLayer(mapPointPin);
	            this.mapView.addLayer(mapLayerPin);
			}
	        
	        if(this.deliveries.getItems().size() != 0)
			{
				TSP();
				//TODO Voir avec Gloria si warehouse pas déjà ajoutée
				tour.getOrderedDeliveries().add(this.map.getWarehouse());
				double[] points = new double[(tour.getOrderedDeliveries().size())*2];
				
				int cptDble = 0;
				for (int i =0; i<tour.getOrderedDeliveries().size(); i++) { 
	        		
					double x1 = tour.getOrderedDeliveries().get(i).getLongitude();
	        		double y1 = tour.getOrderedDeliveries().get(i).getLatitude();
	        	            		
	        		double mapWidth = this.mapView.getWidth();
	        		double mapHeight = this.mapView.getHeight();
	        		
	         		MapPoint min_lat_long = this.mapView.getMapPosition(0, mapHeight-1);
	        		double min_long = min_lat_long.getLongitude();
	        		double min_lat = min_lat_long.getLatitude();
	        
	        		MapPoint max_lat_long = this.mapView.getMapPosition(mapWidth-1, 0);
	        		double max_long = max_lat_long.getLongitude();
	        		double max_lat = max_lat_long.getLatitude();
	        		
	        		double pixel_per_long = (mapWidth / (max_long - min_long));
	        		double pixel_per_lat = (mapHeight / (max_lat - min_lat));
	        		
	        		double x = (pixel_per_long * (x1- min_long));
	        		double y = (pixel_per_lat * (max_lat-y1));
	        			
	                points[cptDble] = x;
	                points[(cptDble+1)] = y;
	                
	                cptDble+=2;

		        } 
		        MapLayer mapPolygoneMarkerLayer = new CustomPolygoneMarkerLayer(points);
		        this.mapView.addLayer(mapPolygoneMarkerLayer);
			}		
	   
		//}
		  display();
	}

	public void display() {
		
		HBox hbox = new HBox();
		hbox.setMinWidth(width / 2);
		hbox.setMinHeight(height / 3);
		
		VBox vBoxMap = new VBox();
		vBoxMap.prefWidthProperty().bind(hbox.widthProperty().multiply(0.60));

		//display the map
		vBoxMap.getChildren().add(this.mapView);
		
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
		
		VBox vBoxiIntentedTours = new VBox();
		vBoxiIntentedTours.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: #f3f6f4;" + "-fx-margin: 120 150 150 120;");

		
		vBoxiIntentedTours.getChildren().add(new Label("Deliveries of the day:"));
	    vBoxiIntentedTours.getChildren().add(deliveries);
		
		Button buttonChangePage = new Button("New request");
		Button button = new Button("TSP");
		vBoxiIntentedTours.getChildren().add(buttonChangePage);
		vBoxiIntentedTours.getChildren().add(button);

	    // -> Test ajout colonne
	    
		// hbox contains two elements
		hbox.getChildren().add(vBoxMap);
		hbox.getChildren().add(vBoxiIntentedTours);
		Scene scene = new Scene(hbox, 200, 500);
		this.stage.setScene(scene);
		this.stage.show();
		
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
				        	   nr.setMapView(mapView);			        	   
				        	   nr.start(stage);
				        	   
				        	   nr.start(stage);	   
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
				       }
				    });
				}
			});
		
		//Ajout du bouton new request seulement si une map est chargée
		/*if(this.map.getIsLoaded()) {			
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
				        	   nr.setMapView(mapView);			        	   
				        	   nr.start(stage);
				        	   
				        	   nr.start(stage);	   
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
				       }
				    });
				}
			});
		}
		
		Button buttonLoadMap = new Button("Load a Map");
		vBoxMap.getChildren().add(buttonLoadMap);
		
		buttonLoadMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				try {
					map.resetMap();
					XMLdeserializer.load(map, stage);
					map.setMapLoaded();
					tour.calculateWidthHeightMap();
					tour.clearOrderedDeliveries();
					tour.clearUnorderedDeliveries();
					deliveries.getItems().clear();
					map.setRatio();
					createMap(map);
				} catch (ParserConfigurationException | SAXException | IOException | ExceptionXML e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});*/
	}

	public void TSP()
	{
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		for(Delivery d : tour.getUnorderedDeliveries()) {
			sommets.add(d.getDestination());
		}
		System.out.println("Debut TSP");
		RunTSP testTSP = new RunTSP(sommets.size(), sommets, map, tour);
		testTSP.start();
		System.out.println("Fin TSP");
	}
	
	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public ControllerAddDelivery getController() {
		return controller;
	}

	public void setController(ControllerAddDelivery controller) {
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
	
	

	public MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	@Override
	public void update(Observable observed, Object arg) {
		// TODO Auto-generated method stub
		if(arg instanceof Delivery)
		{
			deliveries.getItems().add((Delivery) arg);
		}
	}

}
