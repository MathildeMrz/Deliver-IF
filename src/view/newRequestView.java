package view;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import controller.ControllerAddDelivery;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Courier;
import model.CustomCircleMarkerLayer;
import model.CustomPinLayer;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import observer.Observable;
import observer.Observer;
import javafx.scene.control.ButtonBar;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 


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
	private boolean seeIntersection;
	private int margin;
	private float requestedX;
	private float requestedY;
	private LocalDate requestedDate;
	private int requestedStartingTimeWindow;
	private Intersection closestIntersection;
	private Courier requestedCourier;
	private final int noOfDaysToAdd = 2;
	private MapView mapView;
	private mapView ourMapView;
	public mapView getOurMapView() {
		return ourMapView;
	}

	public void setOurMapView(mapView ourMapView) {
		this.ourMapView = ourMapView;
	}

	private MapLayer newDelivery;
	ArrayList<CustomCircleMarkerLayer> mapLayerDelivery;
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
		this.seeIntersection = false;
		display();
		this.closestIntersection = new Intersection();
		mapLayerDelivery = this.getMapLayerDelivery();
		this.stage.show();
		System.out.println("NR : " + mapPolygoneMarkerLayer);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent e) {
		    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		    	alert.setTitle("Changements non enregistrés");
		    	alert.setContentText("Voulez-vous sauvegarder vos changements?");
		    	
		    	ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		    	ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		    	//ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		    	alert.getButtonTypes().setAll(okButton, noButton/*, cancelButton*/);
		    	alert.showAndWait().ifPresent(type -> {
		    	        if (type == ButtonType.YES) {
		    	        	System.out.println("OK!!!!!!!");
		    	        	saveCouriers();
		    	        	Platform.exit();

		    			    System.exit(0);
		    			 
		    	        	
		    	        } else if (type == ButtonType.NO) {
		    	        	System.out.println("NO!!!!!!!");
		    	        	Platform.exit();

		    			    System.exit(0);

		    	        } else {
		    	        	System.out.println("CANCEL!!!!!!!");
		    	        	saveCouriers();

		    	        }
		    	});
				
		    	
		    }
		  });
	}
	
	protected void saveCouriers() {
		LocalDate date = this.map.getMapDate();
		String path = "loadedDeliveries/"+date+".json";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String listeCouriersJson = "";
		try {
			listeCouriersJson = ow.writeValueAsString(this.map.getCouriers());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(listeCouriersJson);
		
		
		/*
		JSONArray listeCouriersJson = new JSONArray();
		
        for(Courier courier : this.map.getCouriers()) {
            JSONObject courierJson = new JSONObject();
            courierJson.put("id", courier.getId());
            courierJson.put("name", courier.getName());
            courierJson.put("speed", courier.getSpeed());
            JSONObject tourJson = new JSONObject();
            Tour tournée = courier.getTour();
            tourJson.put("id", tournée.getId());
            tourJson.put("startDate", tournée.getStartDate());
            tourJson.put("endDate", tournée.getEndDate());
            //Array tourSteps
            JSONArray tourStepsJson = new JSONArray();
            for(Intersection tourStep : tournée.getTourSteps()) {
              JSONObject tourStepJson = new JSONObject();
              tourStepJson.put("id", tourStep.getId());
              tourStepJson.put("latitude", tourStep.getLatitude());
              tourStepJson.put("longitude", tourStep.getLongitude());
              tourStepsJson.add(tourStepJson);
            }
            tourJson.put("tourSteps", tourStepsJson);
            //Array tourTimes
            JSONArray tourTimesJson = new JSONArray();
            for(LocalTime tourTime : tour.getTourTimes()) {
            	JSONObject tourTimeJson = new JSONObject();
            	tourTimeJson.put("time", tourTime.toString());
                tourTimesJson.add(tourTimeJson);
            }
            tourJson.put("tourTimes", tourTimesJson);
            //Array deliveries
    		JSONArray deliveriesJson = new JSONArray();
    		
    		for(Delivery delivery : tour.getDeliveries()) {
               JSONObject deliveryJson = new JSONObject();
               deliveryJson.put("id", delivery.getId());
               deliveryJson.put("startTime", delivery.getStartTime());
               deliveryJson.put("arrival", delivery.getArrival());
               deliveryJson.put("deliveryTime", delivery.getDeliveryTime());
               
               JSONObject intersectionJson = new JSONObject();
               Intersection intersection = delivery.getIntersection();
               intersectionJson.put("id", intersection.getId());
               intersectionJson.put("latitude", intersection.getLatitude());
               intersectionJson.put("longitude", intersection.getLongitude());
               
               //array outsections
       		   JSONArray outsectionsJson = new JSONArray();
       		   for(Segment segment : intersection.getOutSections()) {
       			   JSONObject segmentJson = new JSONObject();
       			   JSONObject segmentIntersectionJson = new JSONObject();
       			   Intersection segmentIntersection = segment.getDestination();
       			   segmentIntersectionJson.put("id", segmentIntersection.getId());
       			   segmentIntersectionJson.put("latitude", segmentIntersection.getLatitude());
       			   segmentIntersectionJson.put("longitude", segmentIntersection.getLongitude());
       			   segmentJson.put("length", segment.getLength());
       			   segmentJson.put("name", segment.getName());
       			   segmentJson.put("destinations", segmentIntersectionJson);
       			   outsectionsJson.add(segmentJson);

       		   }
       		   intersectionJson.put("outsections", outsectionsJson);
       		   deliveryJson.put("destination", intersectionJson);
       		   deliveriesJson.add(deliveryJson);
    		}
    		tourJson.put("deliveries", deliveriesJson);
    		courierJson.put("tour", tourJson);
    		listeCouriersJson.add(courierJson);
        }*/
		File pathAsFile = new File("loadedDeliveries");
		Path path2 = Paths.get("loadedDeliveries");
		if (!Files.isDirectory(path2)) {
			System.out.println("HERE!!!!!!");
			System.out.println(pathAsFile.getAbsolutePath());
			pathAsFile.mkdir();
		}
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(listeCouriersJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
      
       
		
	}

	public void display() {
		this.margin = 50;
		
		HBox hbox = new HBox();
		
		/*button return mapView*/
		Button buttonChangePage = new Button("Map view");
		buttonChangePage.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
				
		/*vBoxCouriers*/
		VBox vBoxCouriers= new VBox();		
		vBoxCouriers.getChildren().add(new Label("Select a courier:"));		
		vBoxCouriers.getChildren().add(couriers);
		couriers.getSelectionModel().select(0);
		requestedCourier = couriers.getItems().get(0);		
		couriers.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			   System.out.println("requestedCourier :"+requestedCourier);
			   requestedCourier = newValue;
			});
		
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
		timeWindow.getItems().add(8);
		timeWindow.getItems().add(9);
		timeWindow.getItems().add(10);
		timeWindow.getItems().add(11);
		timeWindow.getSelectionModel().select(0);
		requestedStartingTimeWindow = timeWindow.getItems().get(0);

		vBoxCouriers.getChildren().add(timeWindow);
		Button buttonValidate = new Button("Valider la livraison");
		buttonValidate.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
		Button buttonSeeIntersections;
			if(seeIntersection == false) {
				buttonSeeIntersections = new Button("Voir les intersections");
			} else {
				buttonSeeIntersections = new Button("Cacher les intersections");
			}
		buttonValidate.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
		Button buttonChangePoint = new Button("Changer le point de livraison");
		buttonChangePoint.setStyle("-fx-text-fill: #000000;\r\n"
				+ "    -fx-border-color: #e6bf4b;\r\n"
				+ "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: #ffffff; ");
		
		vBoxCouriers.getChildren().add(buttonValidate);		
		vBoxCouriers.getChildren().add(buttonChangePage);
		vBoxCouriers.getChildren().add(buttonSeeIntersections);
		vBoxCouriers.getChildren().add(buttonChangePoint);
		
		/*vBoxMap*/		
		VBox vBoxMap = new VBox();
		vBoxMap.setPadding(new Insets(20, 20, 20, 20));
		vBoxMap.setMaxHeight(this.height - 40);
		vBoxMap.setMaxWidth(this.width / 1.6);
		vBoxMap.prefWidthProperty().bind(hbox.widthProperty().multiply(0.60));
	
		vBoxMap.getChildren().add(new Label("Localisation (select the delivery's destination by clicking on the map):"));
		vBoxMap.getChildren().add(this.mapView);
		
		// hbox contains two elements
		hbox.getChildren().add(vBoxMap);
		hbox.getChildren().add(vBoxCouriers);

		Scene scene = new Scene(hbox, 200, 500);	
	
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
					
					MapPoint mapPointPin = new MapPoint(closestIntersection.getLatitude(), closestIntersection.getLongitude());
					newDelivery = new CustomCircleMarkerLayer(mapPointPin, 6, javafx.scene.paint.Color.BLUE);
					mapView.addLayer(newDelivery);
					display();
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
	
		
		buttonChangePoint.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				clicked = false;
				mapView.removeLayer(newDelivery);
			}
		});
		buttonValidate.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Validate");
				mapView.removeLayer(newDelivery);
				for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) 
		        {   
					getMapView().removeLayer(customCircleMarkerLayer);
		        }
				if(requestedX != 0.0f && requestedY != 0.0f)
				{
					controller.addDelivery(closestIntersection, requestedDate ,requestedStartingTimeWindow, requestedCourier);				
					try 
			 	   {		
			     	   ourMapView.setController(controller);
			     	   ourMapView.setCouriers(couriers);
			     	   ourMapView.setHeight(height);
			     	   ourMapView.setWidth(width);
			     	   ourMapView.setMap(map);
			     	   ourMapView.setTour(tour); 
			     	   ourMapView.setDeliveries(deliveries);
			     	   ourMapView.setMapView(mapView);
			     	   ourMapView.setMapPolygoneMarkerLayer(mapPolygoneMarkerLayer);
			     	   ourMapView.start(stage);	 
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
				if (JOptionPane.showConfirmDialog(null, "Vos changements ne seront pas enregistrés", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
				{
					mapView.removeLayer(newDelivery);
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) 
			        {   
						getMapView().removeLayer(customCircleMarkerLayer);
			        }
					Platform.runLater(new Runnable() {
					       public void run() {             
					           try {		
					        	   ourMapView.setController(controller);
					        	   ourMapView.setCouriers(couriers);
					        	   ourMapView.setDeliveries(deliveries);
					        	   ourMapView.setMapView(mapView);
					        	   ourMapView.setHeight(height);
					        	   ourMapView.setWidth(width);
					        	   ourMapView.setMap(map);
					        	   ourMapView.setTour(tour);
					        	   ourMapView.setMapView(mapView);
					        	   ourMapView.setMapPolygoneMarkerLayer(mapPolygoneMarkerLayer);
					        	   ourMapView.start(stage);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					       }
					});
				}
			}
		});
		
		buttonSeeIntersections.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (seeIntersection == false) {
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) 
			        {   
						getMapView().addLayer(customCircleMarkerLayer);
			        }
					seeIntersection = true;
				} else {
					seeIntersection =false;
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) 
			        {   
						getMapView().removeLayer(customCircleMarkerLayer);
			        }
				}
				display();
			}
		});
	}
	
	@Override
	public void update(Observable observed, Object arg) {
	}

	public Map getPlan() {
		return map;
	}
	
	public ArrayList<CustomCircleMarkerLayer> getMapLayerDelivery(){
		ArrayList<CustomCircleMarkerLayer> mapLayerDelivery = new ArrayList<CustomCircleMarkerLayer>();
		for (Intersection i : map.getNodes().values()) 
        {    
			MapPoint mapDelivery = new MapPoint(i.getLatitude(), i.getLongitude());
			mapLayerDelivery.add(new CustomCircleMarkerLayer(mapDelivery, 2, javafx.scene.paint.Color.BLUEVIOLET));
        }
		return mapLayerDelivery;
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

