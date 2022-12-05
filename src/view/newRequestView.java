package view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

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
	private ControllerAddDelivery controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private ListView<Delivery> deliveries;
	private Stage stage;
	private boolean clicked;
	private int screenWidth; 
	private int screenHeight;
	private int margin;
	private float requestedX;
	private float requestedY;
	private LocalDate requestedDate;
	private int requestedStartingTimeWindow;
	private Intersection closestIntersection;
	private final int noOfDaysToAdd = 2;
	private MapView mapView;
	private MapLayer mapPolygoneMarkerLayer;
	
	public newRequestView()
	{
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.setWidth(width);
		stage.setHeight(height);				
		this.clicked = false;
		display();
		this.closestIntersection = new Intersection();
		this.stage.show();
		System.out.println("NR : " + mapPolygoneMarkerLayer);
	}
	
	public void display() {
		this.screenHeight = height/2;
		this.screenWidth = width/2;
		this.margin = 50;
		
		HBox hbox = new HBox();
		hbox.setMinWidth(width / 2);
		hbox.setMinHeight(height / 3);
		hbox.setStyle("-fx-border-color: rgb(49, 89, 47);\r\n"
				+ "    -fx-border-radius: 5;\r\n");	
		
		/*hboxNavbar*/
		Button buttonChangePage = new Button("Map view");
		buttonChangePage.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
				
		/*vBoxCouriers*/
		VBox vBoxCouriers= new VBox();		
		vBoxCouriers.getChildren().add(new Label("Select a courier:"));
		vBoxCouriers.getChildren().add(couriers);
		
		
		DatePicker date = new DatePicker();
		date.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: rgb(49, 89, 47);");
		date.setValue(LocalDate.now());
		requestedDate = date.getValue();
		
		vBoxCouriers.getChildren().add(new Label("Time-window"));
		ComboBox<Integer> timeWindow = new ComboBox<Integer>();
		timeWindow.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		timeWindow.getItems().add(9);
		timeWindow.getItems().add(10);
		timeWindow.getItems().add(11);
		timeWindow.getSelectionModel().select(0);
		requestedStartingTimeWindow = 9;

		vBoxCouriers.getChildren().add(timeWindow);
		Button buttonValidate = new Button("Valider");
		buttonValidate.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
	
		
		vBoxCouriers.getChildren().add(buttonValidate);		
		vBoxCouriers.getChildren().add(buttonChangePage);
		
		/*vBoxcreateNewRequest*/		
		VBox vBoxcreateNewRequest = new VBox();
		vBoxcreateNewRequest.setMaxWidth(Double.MAX_VALUE);
		vBoxcreateNewRequest.setMaxHeight(Double.MAX_VALUE);
	
		vBoxcreateNewRequest.getChildren().add(new Label("Localisation (select the delivery's destination by clicking on the map):"));
		vBoxcreateNewRequest.getChildren().add(this.mapView);

		// hbox contains two elements
		hbox.getChildren().add(vBoxcreateNewRequest);
		hbox.getChildren().add(vBoxCouriers);

		Scene scene = new Scene(hbox, 200, 500);	
	
		vBoxcreateNewRequest.prefWidthProperty().bind(hbox.widthProperty().multiply(0.60));
		stage.setScene(scene);	
		this.stage.show();

		this.mapView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(clicked == false)
				{							
					requestedX = (float) event.getX();
					requestedY = (float) event.getY();		
					MapPoint mp = mapView.getMapPosition(requestedX, requestedY);
					float latitude = (float) mp.getLatitude();
					float longitude = (float) mp.getLongitude();
					closestIntersection = tour.getClosestIntersection(latitude, longitude);
				}				
				clicked = true;
			}
		});
		
		
		timeWindow.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			   System.out.println("requestedStartingTimeWindow :"+newValue);
			   requestedStartingTimeWindow = newValue;
			}); 
		
		// Listener for updating the checkout date w.r.t check in date
				date.valueProperty().addListener((ov, oldValue, newValue) -> {			
						requestedDate = newValue.plusDays(noOfDaysToAdd);
			            System.out.println("You clicked: " + requestedDate);
		        });
				
				timeWindow.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
					   System.out.println("requestedStartingTimeWindow :"+newValue);
					   requestedStartingTimeWindow = newValue;
					}); 
	
			
		buttonValidate.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(requestedX != 0.0f && requestedY != 0.0f)
				{
				    //LocalDate date = LocalDate.now();    
					//TODO : rajouter la date
					controller.addDelivery(closestIntersection, requestedDate ,requestedStartingTimeWindow);				
					try 
			 	   {		
			     	   mapView mv = new mapView();
			     	   mv.setController(controller);
			     	   mv.setCouriers(couriers);
			     	   mv.setHeight(height);
			     	   mv.setWidth(width);
			     	   mv.setMap(map);
			     	   mv.setTour(tour); 
			     	   mv.setDeliveries(deliveries);
			     	   mv.setMapView(mapView);
			     	   mv.setMapPolygoneMarkerLayer(mapPolygoneMarkerLayer);
			     	   mv.start(stage);	 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
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
				        	   mv.setDeliveries(deliveries);
				        	   mv.setHeight(height);
				        	   mv.setWidth(width);
				        	   mv.setMap(map);
				        	   mv.setTour(tour);
				        	   mv.start(stage);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				       }
				    });
			}
		});
	
			
		buttonValidate.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Validate");
				if(requestedX != 0.0f && requestedY != 0.0f)
				{
					controller.addDelivery(closestIntersection, requestedDate, requestedStartingTimeWindow);
					
					//vBoxcreateNewRequest.getChildren().add(new Label("The delivery has been registered"));
					//JOptionPane.showMessageDialog(null, "The delivery has been registered");
					System.out.println("Delivery added");
					//Change page
					try 
			 	   {		
					   mapView mv = new mapView();
			     	   mv.setController(controller);
			     	   mv.setCouriers(couriers);
			     	   mv.setHeight(height);
			     	   mv.setWidth(width);
			     	   mv.setMap(map);
			     	   mv.setTour(tour); 
			     	   mv.setDeliveries(deliveries);
			     	   mv.setMapView(mapView);
			     	   mv.setMapPolygoneMarkerLayer(mapPolygoneMarkerLayer);
			     	   mv.start(stage);	 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
		});		
		
		buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
//				int retour = JOptionPane.showConfirmDialog(this,
//			             "OK - Annuler", 
//			             "titre",
//			             JOptionPane.OK_CANCEL_OPTION);
				if (JOptionPane.showConfirmDialog(null, "Vos changements ne seront pas enregistrés", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
				{
					Platform.runLater(new Runnable() {
					       public void run() {             
					           try {		
					        	   mapView mv = new mapView();
					        	   mv.setController(controller);
					        	   mv.setCouriers(couriers);
					        	   mv.setDeliveries(deliveries);
					        	   mv.setHeight(height);
					        	   mv.setWidth(width);
					        	   mv.setMap(map);
					        	   mv.setTour(tour);
					        	   mv.start(stage);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					       }
					});
				}
			}
		});
	}
	
	@Override
	public void update(Observable observed, Object arg) {
		//createMap(this.map);	
	
	}

	public Map getPlan() {
		return map;
	}

	public void setPlan(Map map) {
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
	
	public void setDeliveries(ListView<Delivery> deliveries) {
		this.deliveries = deliveries;
	}
		
		public void setTour(Tour tour) {
			this.tour = tour;
		}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setMapPolygoneMarkerLayer(MapLayer layer) {
		this.mapPolygoneMarkerLayer = layer;
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
	public MapView getMapView() {
		return mapView;
	}
	
	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

}

