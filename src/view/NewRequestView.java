package view;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import controller.Controller;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Courier;
import model.CustomCircleMarkerLayer;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Segment;
import model.Tour;
import observer.Observable;
import observer.Observer;

public class NewRequestView extends Application implements Observer {

	private Map map;
	private Controller  controller;
	private int screenWidth;
	private int screenHeight;
	private ListView<Courier> couriers;
	private Stage stage;
	private boolean clickedOnMap; // true when user chose a point on map
	private boolean seeIntersection; // true when all intersections are displayed
	private float requestedX; // coordinates X of the chosen point for new delivery
	private float requestedY; // coordinates Y of the chosen point for new delivery
	private int requestedStartingTimeWindow; // time window of the requested delivery
	private Intersection closestIntersection;
	private Courier requestedCourier; // for the requested delivery
	private final int noOfDaysToAdd = 2;
	private MapView mapView;
	private MapLayer newDelivery;
	private ArrayList<CustomCircleMarkerLayer> mapLayerDelivery; // lists of circle layers for each intersection
	private ArrayList<MapLayer> mapPolygoneMarkerLayers; // lists of each tour layer
	private HomeView ourMapView;
	private ComboBox<Integer> timeWindow;
	private Label selectLocation;
	private Label labelSelectCourier;
	private Label labelSelectTimeWindow;
	private Label labelCourierSuggestion;
	private Button buttonValidate;
	private Button buttonChangePoint;
	private Button cancelRequest;
	private BackgroundFill background_fill;
	private Background background;
	private Button buttonSeeIntersections;
	private DatePicker date;
	private TreeView treeview;
	private Courier bestCourierAvailable;
	private Courier bestCourierProx;
	private VBox vBoxCouriers;
	
	public HomeView getOurMapView() {
		return ourMapView;
	}

	public void setOurMapView(HomeView ourMapView) {
		this.ourMapView = ourMapView;
	}

	public NewRequestView() {
	}

	/**
	 * Initialize Map attributes
	 * @param stage : stage of our screen
	 */
	@Override
	public void start(Stage stage) throws Exception {
		
		// Resize the window 
		this.stage = stage;
		this.stage.setWidth(screenWidth);
		this.stage.setHeight(screenHeight);
		
		// Creation of the background
		this.background_fill = new BackgroundFill(Color.rgb(216, 191, 170), CornerRadii.EMPTY, Insets.EMPTY);
		this.background = new Background(background_fill);
		
		// Init attributes
		this.clickedOnMap = false;
		this.seeIntersection = false;
		this.closestIntersection = new Intersection();
		this.mapLayerDelivery = this.getMapLayerDelivery();
		this.timeWindow = new ComboBox<Integer>();
		this.timeWindow.getItems().add(8);
		this.timeWindow.getItems().add(9);
		this.timeWindow.getItems().add(10);
		this.timeWindow.getItems().add(11);
		this.timeWindow.setMouseTransparent(true);
		this.timeWindow.getSelectionModel().select(0); //valeur par défaut affichée
		this.requestedStartingTimeWindow = timeWindow.getItems().get(0);	//valeur par défaut pour la timeWindow de la livraison
		this.couriers.setMouseTransparent(true);
		this.couriers.getSelectionModel().select(0); //valeur par défaut affichée 
		this.requestedCourier = couriers.getSelectionModel().getSelectedItem(); //valeur par défaut pour le livreur de la livraison
		
		// Creation of the labels
		this.labelCourierSuggestion = new Label("La liste des livreurs n'est pas triée");
		this.labelCourierSuggestion.setVisible(false);
		this.labelSelectCourier = new Label("2.b Sélectionner un livreur");
		this.labelSelectCourier.setPadding(new Insets(2));
		this.labelSelectCourier.setStyle("-fx-font-size: 15;" + "-fx-background-color:rgba(255, 255, 86, 1.00);");
		this.labelSelectCourier.setVisible(false);
		this.selectLocation = new Label("1. Sélectionner la destination de la nouvelle livraison (en cliquant sur la carte)");
		this.selectLocation.setPadding(new Insets(2));
		this.selectLocation.setStyle("-fx-font-size: 15;" + "-fx-background-color:rgba(255, 255, 86, 1.00);");
		this.labelSelectTimeWindow = new Label("2.a Sélectionner une plage \n horaire");
		this.labelSelectTimeWindow.setPadding(new Insets(2));
		this.labelSelectTimeWindow.setStyle("-fx-font-size: 15;" + "-fx-background-color:rgba(255, 255, 86, 1.00);");
		this.labelSelectTimeWindow.setVisible(false);		
		
		// Creation of the buttons
		this.buttonValidate = new Button("Valider la livraison");
		this.buttonValidate.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
		this.buttonValidate.setMouseTransparent(true);
		this.buttonChangePoint = new Button("Changer le point de livraison");
		this.buttonChangePoint.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
		this.cancelRequest = new Button("Annuler requête livraison");
		this.cancelRequest.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
		this.buttonSeeIntersections = new Button("Voir les intersections");
		this.buttonSeeIntersections.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
			
		// Creation of the datePicker 
		this.date = new DatePicker();
		this.date.setStyle("-fx-text-fill: #000000;\r\n" + "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: rgb(49, 89, 47);");
		
		
		// Display the map
		display();	
		this.stage.show();
		
		/* mouse listeners */
		
		// Display or hide the circle in each intersection of the map
		this.buttonSeeIntersections.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// Display the intersection and change label to hide intersections
				if (seeIntersection == false) 
				{
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
						getMapView().addLayer(customCircleMarkerLayer);
					}
					seeIntersection = true;
					buttonSeeIntersections.setText("Cacher les intersections");
				} 
				else 
				{
					// Hide the intersection and change label to display intersections
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
						getMapView().removeLayer(customCircleMarkerLayer);
					}
					buttonSeeIntersections.setText("Voir les intersections");
					seeIntersection = false;
				}
				getMapView().setZoom(getMapView().getZoom()-0.001);
				display();
			}
		});
		
		//Alert with a pop-up when we close the window to save new changes
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Changements non enregistrés");
				alert.setHeaderText("Voulez-vous sauvegarder vos changements?");
				ButtonType buttonOk = new ButtonType("Oui");
				ButtonType buttonNo = new ButtonType("Non");
				ButtonType buttonCancel = new ButtonType("Annuler");
				alert.getButtonTypes().setAll(buttonOk, buttonNo, buttonCancel);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() != null) {
					if (result.get() == buttonOk) {
						// We save new changes
						saveCouriers();
						Platform.exit();
						System.exit(0);
					}else if (result.get() == buttonNo) {
						// We exit without saving new changes
						Platform.exit();
						System.exit(0);
					} else if (result.get() == buttonCancel) {
						// We go back to the page 
						e.consume();
					}
				}
			}
		});
		
		// Get the coordinates of the new delivery and display blue circle
		this.mapView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// The user can only choose one point (click one time only) 
				if (clickedOnMap == false) 
				{
					// Get the requested coordinates for a new delivery
					requestedX = (float) event.getX();
					requestedY = (float) event.getY();
					MapPoint mp = mapView.getMapPosition(requestedX, requestedY);
					
					// Get real coordinates latitude and longitude of the requested point
					float latitude = (float) mp.getLatitude();
					float longitude = (float) mp.getLongitude();
					
					// Get the closestIntersection from the requested point
					closestIntersection = map.getClosestIntersection(latitude, longitude, -1);
					if(closestIntersection.getOutSections().size() == 0)
					{
						closestIntersection = map.getClosestIntersection(closestIntersection.getLatitude(), closestIntersection.getLongitude(),closestIntersection.getId());
					}
					
					// Display a blue circle in the closestIntersection
					MapPoint mapPointPin = new MapPoint(closestIntersection.getLatitude(), closestIntersection.getLongitude());
					newDelivery = new CustomCircleMarkerLayer(mapPointPin, 6, javafx.scene.paint.Color.BLUE);
					mapView.addLayer(newDelivery);
					
					// Display the following steps to add the requested delivery
					timeWindow.setStyle("-fx-text-fill: #000000;\r\n" + "    -fx-border-radius: 3px;\r\n");
					timeWindow.setMouseTransparent(false);
					selectLocation.setVisible(false);
					labelSelectTimeWindow.setVisible(true);
					getMapView().setZoom(getMapView().getZoom()+0.001);
					buttonValidate.setMouseTransparent(false);
					labelSelectCourier.setVisible(true);
					couriers.setMouseTransparent(false);
					requestedCourier = map.getBestCourierAvalaibility(closestIntersection, requestedStartingTimeWindow);
					couriers.getSelectionModel().select(requestedCourier);
					labelCourierSuggestion.setText("Le livreur qui a la meilleure disponibilité\n est sélectionné par défaut");
					labelCourierSuggestion.setVisible(true);
					display();
				}
				// Prevent from selecting a new delivery
				clickedOnMap = true;
			}
		});

		// Change the place of the requested delivery
		this.buttonChangePoint.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// Allow to select a new delivery
				clickedOnMap = false;
				
				// Remove the blue circle in the previous delivery place
				mapView.removeLayer(newDelivery);
				
				// Display the steps to follow in order to add a delivery
				timeWindow.setMouseTransparent(true);
				timeWindow.setStyle(null);
				couriers.setMouseTransparent(true);
				labelSelectTimeWindow.setVisible(false);
				labelSelectCourier.setVisible(false);
				selectLocation.setVisible(true);
				labelCourierSuggestion.setVisible(false);
				buttonValidate.setMouseTransparent(true);
			}
		});

		// Cancel the delivery requested
		this.cancelRequest.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// Alert ask the user confirmation
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning");
				alert.setHeaderText("Vos changements ne seront pas enregistrés");
				ButtonType buttonOk = new ButtonType("Continuer");
				ButtonType buttonCancel = new ButtonType("Annuler");
				alert.getButtonTypes().setAll(buttonOk, buttonCancel);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonOk) {
					// The blue circle of the requested delivery is removed and we go back to homePage
					mapView.removeLayer(newDelivery);
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
						getMapView().removeLayer(customCircleMarkerLayer);
					}
					Platform.runLater(new Runnable() {
						public void run() {
							try {
								ourMapView.setController(controller);
								ourMapView.setListViewCouriers(couriers);
								ourMapView.setHeight(screenHeight);
								ourMapView.setWidth(screenWidth);
								ourMapView.setMap(map);
								ourMapView.setMapView(mapView);
								ourMapView.setMapPolygoneMarkerLayers(mapPolygoneMarkerLayers);
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
		
		this.timeWindow.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if(newValue!=null)
			{
				requestedStartingTimeWindow = newValue;
				bestCourierAvailable = map.getBestCourierAvalaibility(closestIntersection, requestedStartingTimeWindow);
				bestCourierProx = map.getBestCourierProximity(closestIntersection, requestedStartingTimeWindow);
				requestedCourier = bestCourierAvailable;
				vBoxCouriers.getChildren().remove(couriers);
				vBoxCouriers.getChildren().remove(cancelRequest);
				vBoxCouriers.getChildren().remove(buttonValidate);
				vBoxCouriers.getChildren().remove(buttonSeeIntersections);
				vBoxCouriers.getChildren().remove(buttonChangePoint);
				ListView<Courier> couriersTmp = couriers;
				ListView<Courier> newCouriers = new ListView<Courier>();
				if(bestCourierProx != bestCourierAvailable)
				{
					newCouriers.getItems().add(bestCourierAvailable);
					newCouriers.getItems().add(bestCourierProx);	
					labelCourierSuggestion.setText("Le 1er livreur dans la liste a la meilleure disponibilité\nLe 2ème livreur dans la liste a la meilleure proximité");
				}
				else
				{
					newCouriers.getItems().add(bestCourierAvailable);
					labelCourierSuggestion.setText("Le 1er livreur dans la liste a la meilleure\n disponibilité et la meilleure proximité");
				}
				for (Courier c : couriersTmp.getItems())
				{
					if(c!=bestCourierProx && c!=bestCourierAvailable)
					{
						newCouriers.getItems().add(c);
					}
				}
				couriers = newCouriers;
				couriers.getSelectionModel().select(0);
				vBoxCouriers.getChildren().add(couriers);
				vBoxCouriers.getChildren().add(cancelRequest);
				vBoxCouriers.getChildren().add(buttonValidate);
				vBoxCouriers.getChildren().add(buttonSeeIntersections);
				vBoxCouriers.getChildren().add(buttonChangePoint);
			}
			couriers.setMouseTransparent(false);
			labelSelectTimeWindow.setVisible(false);
			labelSelectCourier.setVisible(true);
			try {
				buttonCourier();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		buttonCourier();
	}
	
	public void buttonCourier() throws Exception {
	
	this.couriers.setOnMouseClicked(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			requestedCourier = couriers.getSelectionModel().getSelectedItem();
			//Expand du treeView 
			treeview.getRoot().getChildren().forEach(t ->{
				if(((TreeItem)t).getValue().toString().contains(requestedCourier.getName()))
				{
					((TreeItem)t).setExpanded(true);
					((TreeItem)t).getChildren().forEach(ti ->{
						((TreeItem)ti).setExpanded(true);
					});
				}
				else
				{
					((TreeItem)t).setExpanded(false);
				}
			});
			
			buttonSeeIntersections.setText("Voir les intersections");
			for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
					getMapView().removeLayer(customCircleMarkerLayer);
				}
				seeIntersection = false;
			}				
		});
	}

	/**
	 * Displays home page
	 */
	public void display() {

		// Hbox
		HBox hbox = new HBox();
		hbox.setBackground(background);
		
		// vBox treeView
        VBox vBoxTreeView = new VBox();
        treeview.getRoot().getChildren().forEach(t ->{
        	((TreeItem)t).setExpanded(false);
        });
        vBoxTreeView.getChildren().add(treeview);
        vBoxTreeView.setPadding(new Insets(90, 20, 20, 20));
        vBoxTreeView.prefWidthProperty().bind(hbox.widthProperty().multiply(0.30));

		/* vBoxCouriers */
		vBoxCouriers = new VBox();
		vBoxCouriers.setPadding(new Insets(5, 0, 0, 0));
		vBoxCouriers.getChildren().add(labelSelectTimeWindow);
		vBoxCouriers.getChildren().add(timeWindow);		
		vBoxCouriers.getChildren().add(labelSelectCourier);
		vBoxCouriers.getChildren().add(labelCourierSuggestion);
		vBoxCouriers.getChildren().add(couriers);
		vBoxCouriers.getChildren().add(cancelRequest);
		vBoxCouriers.getChildren().add(buttonValidate);
		vBoxCouriers.getChildren().add(buttonSeeIntersections);
		vBoxCouriers.getChildren().add(buttonChangePoint);
		vBoxCouriers.setSpacing(10);
		
		/* vBoxMap */
		VBox vBoxMap = new VBox();
		vBoxMap.setPadding(new Insets(20, 20, 20, 20));
		vBoxMap.setMaxHeight(this.screenHeight - 40);
		vBoxMap.setMaxWidth(this.screenWidth / 1.6);
		vBoxMap.prefWidthProperty().bind(hbox.widthProperty().multiply(0.60));		
		vBoxMap.getChildren().add(selectLocation);
		vBoxMap.getChildren().add(new Label(""));
		vBoxMap.getChildren().add(this.mapView);
		
		// Hbox contains previous vBox
		hbox.getChildren().add(vBoxMap);
		hbox.getChildren().add(vBoxCouriers);
		hbox.getChildren().add(vBoxTreeView);

		Scene scene = new Scene(hbox, 200, 500);
		stage.setScene(scene);
		
		// Validate the requested delivery and add the delivery to the requested courier tour
		this.buttonValidate.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				// Remove all added layers (requested delivery circle and all intersections circles)
				mapView.removeLayer(newDelivery);
				for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
					getMapView().removeLayer(customCircleMarkerLayer);
				}
				
				// If the user as chosen an intersection on the map
				if (requestedX != 0.0f && requestedY != 0.0f && map.getCouriers().size() != 0) {
					
					// The requested delivery is added to the requested courier tour
					Delivery delivery = controller.addDelivery(closestIntersection, map.getMapDate(), requestedStartingTimeWindow,
							requestedCourier);
					
					// We go back on homePage
					try {
						ourMapView.setController(controller);
						ourMapView.setListViewCouriers(couriers);
						ourMapView.setHeight(screenHeight);
						ourMapView.setWidth(screenWidth);
						ourMapView.setMap(map);
						ourMapView.setMapView(mapView);
						ourMapView.setMapPolygoneMarkerLayers(mapPolygoneMarkerLayers);
						ourMapView.setLastAddedDelivery(delivery);
						ourMapView.start(stage);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	

	@Override
	public void update(Observable observed, Object arg) {
	}

	public Map getMap() {
		return map;
	}

	public ArrayList<CustomCircleMarkerLayer> getMapLayerDelivery() {
		ArrayList<CustomCircleMarkerLayer> mapLayerDelivery = new ArrayList<CustomCircleMarkerLayer>();
		for (Intersection i : map.getNodes().values()) {
			MapPoint mapDelivery = new MapPoint(i.getLatitude(), i.getLongitude());
			mapLayerDelivery.add(new CustomCircleMarkerLayer(mapDelivery, 2, javafx.scene.paint.Color.BLUEVIOLET));
		}
		return mapLayerDelivery;
	}
	
    public void setTreeview(TreeView treeview) {
        this.treeview = treeview;
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
		return screenWidth;
	}

	public void setWidth(int width) {
		this.screenWidth = width;
	}

	public int getHeight() {
		return screenHeight;
	}

	public void setHeight(int height) {
		this.screenHeight = height;
	}

	public ListView<Courier> getListViewCouriers() {
		return couriers;
	}

	public void setListViewCouriers(ListView<Courier> couriers) {
		this.couriers = couriers;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setMapPolygoneMarkerLayers(ArrayList<MapLayer> layer) {
		this.mapPolygoneMarkerLayers = layer;
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

	/**
	 * Saves couriers and its attributes in a json file
	 */
	protected void saveCouriers() {
		LocalDate date = this.map.getMapDate();
		
		//File name
		String path = "loadedDeliveries/" + date + ".json";
		JSONArray listeCouriersJson = new JSONArray();

		// For each courier
		for (Courier courier : this.map.getCouriers()) {
			JSONObject courierJson = new JSONObject();
			courierJson.put("id", courier.getId());
			courierJson.put("name", courier.getName());
			courierJson.put("speed", courier.getSpeed());
			JSONObject tourJson = new JSONObject();
			Tour tournee = courier.getTour();
			tourJson.put("id", tournee.getId());
			tourJson.put("startDate", tournee.getStartDate());
			tourJson.put("endDate", tournee.getEndDate());

			// Array tourSteps
			JSONArray tourStepsJson = new JSONArray();

			// For each intersection of the tour
			for (Intersection tourStep : tournee.getTourSteps()) {
				JSONObject tourStepJson = new JSONObject();
				tourStepJson.put("id", tourStep.getId());
				JSONArray tourSegmentsJson = new JSONArray();

				// For each segment of the intersection
				for (Segment tourStepSegment : tourStep.getOutSections()) {
					JSONObject tourStepSegmentJson = new JSONObject();
					tourStepSegmentJson.put("length", tourStepSegment.getLength());
					tourStepSegmentJson.put("name", tourStepSegment.getName());
					tourSegmentsJson.put(tourStepSegmentJson);
					tourStepsJson.put(tourStepJson);
				}
				Tour tour = courier.getTour();
				tourJson.put("tourSteps", tourStepsJson);
				tourJson.put("outSections", tourSegmentsJson);

				JSONArray tourTimesJson = new JSONArray();

				// For each tour time
				for (LocalTime tourTime : tour.getTourTimes()) {
					JSONObject tourTimeJson = new JSONObject();
					tourTimeJson.put("time", tourTime.toString());
					tourTimesJson.put(tourTimeJson);
				}
				tourJson.put("tourTimes", tourTimesJson);
				JSONArray deliveriesJson = new JSONArray();

				// For each delivery
				for (Delivery delivery : tour.getDeliveries()) {
					JSONObject deliveryJson = new JSONObject();
					deliveryJson.put("id", delivery.getId());
					deliveryJson.put("startTime", delivery.getStartTime());
					deliveryJson.put("arrival", delivery.getArrival());
					deliveryJson.put("deliveryTime", delivery.getDeliveryTime());

					JSONObject intersectionJson = new JSONObject();
					Intersection destinationIntersection = delivery.getDestination();
					intersectionJson.put("id", destinationIntersection.getId());

					JSONArray outsectionsJson = new JSONArray();
					// For each segment of the intersection
					for (Segment segment : destinationIntersection.getOutSections()) {
						JSONObject segmentJson = new JSONObject();
						JSONObject segmentIntersectionJson = new JSONObject();
						Intersection segmentIntersection = segment.getDestination();
						segmentIntersectionJson.put("id", segmentIntersection.getId());
						segmentJson.put("destination", segmentIntersectionJson);
						outsectionsJson.put(segmentJson);
					}
					intersectionJson.put("outsections", outsectionsJson);
					deliveryJson.put("destination", intersectionJson);
					deliveriesJson.put(deliveryJson);
				}
				tourJson.put("deliveries", deliveriesJson);
				courierJson.put("tour", tourJson);
				listeCouriersJson.put(courierJson);
			}
			File pathAsFile = new File("loadedDeliveries");
			if (!Files.isDirectory(Paths.get("loadedDeliveries"))) {
				pathAsFile.mkdir();
			}
			try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
				out.write(listeCouriersJson.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
