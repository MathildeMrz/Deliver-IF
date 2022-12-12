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
import javafx.scene.control.ListView;
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
	private ControllerAddDelivery controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private Stage stage;
	private boolean clicked;
	private boolean seeIntersection;
	private float requestedX;
	private float requestedY;
	private LocalDate requestedDate;
	private int requestedStartingTimeWindow;
	private Intersection closestIntersection;
	private Courier requestedCourier;
	private final int noOfDaysToAdd = 2;
	private MapView mapView;
	private MapLayer newDelivery;
	private ArrayList<CustomCircleMarkerLayer> mapLayerDelivery;
	private ArrayList<MapLayer> mapPolygoneMarkerLayers;
	private HomeView ourMapView;
	private ComboBox<Integer> timeWindow;
	private Label selectLocation;
	private Label labelSelectCourier;
	private Label labelSelectTimeWindow;
	private Button buttonValidate;
	private Button buttonChangePoint;
	private Button buttonChangePage;
	private BackgroundFill background_fill;
	private Background background;
	private Button buttonSeeIntersections;
	private DatePicker date;
	
	public HomeView getOurMapView() {
		return ourMapView;
	}

	public void setOurMapView(HomeView ourMapView) {
		this.ourMapView = ourMapView;
	}

	public NewRequestView() {
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.stage.setWidth(width);
		this.stage.setHeight(height);
		this.clicked = false;
		this.seeIntersection = false;
		this.closestIntersection = new Intersection();
		this.mapLayerDelivery = this.getMapLayerDelivery();
		this.timeWindow = new ComboBox<Integer>();
		this.timeWindow.getItems().add(8);
		this.timeWindow.getItems().add(9);
		this.timeWindow.getItems().add(10);
		this.timeWindow.getItems().add(11);
		this.timeWindow.setMouseTransparent(true);
		this.couriers.setMouseTransparent(true);
		
		/*Creation of the labels*/
		this.labelSelectCourier = new Label("Sélectionner un livreur");
		this.labelSelectCourier.setStyle("-fx-background-color:rgba(85, 255, 68,0.7);");
		this.labelSelectCourier.setVisible(false);
		this.selectLocation = new Label("Sélectionner la destination de la nouvelle livraison (en cliquant sur la carte)");
		this.selectLocation.setStyle("-fx-background-color:rgba(85, 255, 68,0.7);");
		this.labelSelectTimeWindow = new Label("Sélectionner une plage horaire");
		this.labelSelectTimeWindow.setStyle("-fx-background-color:rgba(85, 255, 68,0.7);");
		this.labelSelectTimeWindow.setVisible(false);		
		
		/*Creation of the buttons*/
		this.buttonValidate = new Button("Valider la livraison");
		this.buttonValidate.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
		this.buttonValidate.setMouseTransparent(true);
		this.buttonChangePoint = new Button("Changer le point de livraison");
		this.buttonChangePoint.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
		this.buttonChangePage = new Button("Map view");
		this.buttonChangePage.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
		this.buttonSeeIntersections = new Button("Voir les intersections");
		this.buttonSeeIntersections.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;" +" -fx-border-radius: 8px;" +  " -fx-border-color: #000000;"  + "-fx-background-radius: 8px;");
			
		/*Creation of the background*/
		this.background_fill = new BackgroundFill(Color.rgb(216, 191, 170), CornerRadii.EMPTY, Insets.EMPTY);
		this.background = new Background(background_fill);
		
		/* Creation of the datePicker */
		this.date = new DatePicker();
		this.date.setStyle("-fx-text-fill: #000000;\r\n" + "    -fx-border-radius: 3px;\r\n"
				+ "	   -fx-background-color: rgb(49, 89, 47);");
		this.date.setValue(LocalDate.now());
		
		display();	
		this.stage.show();
		
		/* mouse listeners */
		this.buttonSeeIntersections.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {	
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
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
						getMapView().removeLayer(customCircleMarkerLayer);
					}
					System.out.println("Come back to the page");
					buttonSeeIntersections.setText("Voir les intersections");
					seeIntersection = false;
				}
				
				getMapView().setZoom(getMapView().getZoom()-0.001);
				display();
			}
			
		});
		
		this.mapView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (clicked == false) 
				{
					requestedX = (float) event.getX();
					requestedY = (float) event.getY();
					MapPoint mp = mapView.getMapPosition(requestedX, requestedY);
					float latitude = (float) mp.getLatitude();
					float longitude = (float) mp.getLongitude();
					closestIntersection = map.getClosestIntersection(latitude, longitude);

					MapPoint mapPointPin = new MapPoint(closestIntersection.getLatitude(),
							closestIntersection.getLongitude());
					newDelivery = new CustomCircleMarkerLayer(mapPointPin, 6, javafx.scene.paint.Color.BLUE);
					mapView.addLayer(newDelivery);
					timeWindow.setStyle("-fx-text-fill: #000000;\r\n" + "    -fx-border-radius: 3px;\r\n");
					timeWindow.setMouseTransparent(false);
					selectLocation.setVisible(false);
					labelSelectTimeWindow.setVisible(true);
					getMapView().setZoom(getMapView().getZoom()+0.001);
					display();
				}
				clicked = true;
			}
		});

		this.timeWindow.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			System.out.println("old value :" + oldValue);
			System.out.println("requestedStartingTimeWindow :" + newValue);
			if(newValue!=null)
			{
				requestedStartingTimeWindow = newValue;
			}
			this.couriers.setMouseTransparent(false);
			labelSelectTimeWindow.setVisible(false);
			labelSelectCourier.setVisible(true);

		});

		// Listener for updating the checkout date w.r.t check in date
		this.date.valueProperty().addListener((ov, oldValue, newValue) -> {
			requestedDate = newValue.plusDays(noOfDaysToAdd);
			System.out.println("You clicked: " + requestedDate);
		});

		this.buttonChangePoint.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				clicked = false;
				mapView.removeLayer(newDelivery);
				//timeWindow.getSelectionModel().select(0);
				timeWindow.getSelectionModel().select(null);
				timeWindow.setMouseTransparent(true);
				timeWindow.setStyle(null);
				//requestedStartingTimeWindow = timeWindow.getItems().get(0);	
				//couriers.getSelectionModel().select(0);
				couriers.setMouseTransparent(true);
				labelSelectTimeWindow.setVisible(false);
				labelSelectCourier.setVisible(false);
				selectLocation.setVisible(true);
				buttonValidate.setMouseTransparent(true);
			}
		});

		this.buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning");
				alert.setHeaderText("Vos changements ne seront pas enregistrés");
				ButtonType buttonOk = new ButtonType("Continuer");
				ButtonType buttonCancel = new ButtonType("Annuler");
				alert.getButtonTypes().setAll(buttonOk, buttonCancel);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonOk) {
					mapView.removeLayer(newDelivery);
					for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
						getMapView().removeLayer(customCircleMarkerLayer);
					}
					Platform.runLater(new Runnable() {
						public void run() {
							try {
								ourMapView.setController(controller);
								ourMapView.setListViewCouriers(couriers);
								ourMapView.setHeight(height);
								ourMapView.setWidth(width);
								ourMapView.setMap(map);
								ourMapView.setMapView(mapView);
								ourMapView.setMapPolygoneMarkerLayers(mapPolygoneMarkerLayers);
								System.out.println("Pin Ajouté");
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

		/*If the user wants to leave the applicatoin*/
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Changements non enregistrés");
				alert.setContentText("Voulez-vous sauvegarder vos changements?");
				alert.getButtonTypes().clear();
				alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
				Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
				Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
				noButton.setDefaultButton(true);
				yesButton.setDefaultButton(false);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.YES)
				{
					System.out.println("YES!!!!!");
					saveCouriers();
					Platform.exit();
					System.exit(0);
				} 
				else if (result.isPresent() && result.get() == ButtonType.NO)
				{
					System.out.println("NO!!!!!");
					Platform.exit();
					System.exit(0);
				} 
				else if (result.isPresent() && result.get() == ButtonType.CANCEL)
				{
					System.out.println("Come back to the page");
				}
			}
		});
		
		this.couriers.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.out.println("requestedCourier :" + couriers.getSelectionModel().getSelectedItem());
				requestedCourier = couriers.getSelectionModel().getSelectedItem();
				buttonValidate.setMouseTransparent(false);
				System.out.println("Bouton valider appuyable");
				buttonSeeIntersections.setText("Voir les intersections");
				for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
						getMapView().removeLayer(customCircleMarkerLayer);
					}
					seeIntersection = false;
				}				
			});
		
		/*this.couriers.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.out.println("requestedCourier :" + couriers.getSelectionModel().getSelectedItem());
				requestedCourier = couriers.getSelectionModel().getSelectedItem();
			}
		});*/
		
		this.timeWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//TODO
				System.out.println("Mettre à jour les couriers disponibles");
			}
		});
	}

	public void display() {

		HBox hbox = new HBox();
		hbox.setBackground(background);

		/* vBoxCouriers */
		VBox vBoxCouriers = new VBox();
		//this.buttonValidate.setMouseTransparent(true);
		vBoxCouriers.getChildren().add(labelSelectTimeWindow);
		vBoxCouriers.getChildren().add(timeWindow);		
		vBoxCouriers.getChildren().add(labelSelectCourier);
		vBoxCouriers.getChildren().add(couriers);
		vBoxCouriers.getChildren().add(buttonChangePage);
		vBoxCouriers.getChildren().add(buttonValidate);
		vBoxCouriers.getChildren().add(buttonSeeIntersections);
		vBoxCouriers.getChildren().add(buttonChangePoint);
		vBoxCouriers.setSpacing(10);
		
		/* vBoxMap */
		VBox vBoxMap = new VBox();
		vBoxMap.setPadding(new Insets(20, 20, 20, 20));
		vBoxMap.setMaxHeight(this.height - 40);
		vBoxMap.setMaxWidth(this.width / 1.6);
		vBoxMap.prefWidthProperty().bind(hbox.widthProperty().multiply(0.60));		
		vBoxMap.getChildren().add(selectLocation);
		vBoxMap.getChildren().add(this.mapView);

		if (couriers.getItems().size() != 0) {
			requestedCourier = couriers.getItems().get(0);
		}
		
		requestedDate = date.getValue();

		// hbox contains two elements
		hbox.getChildren().add(vBoxMap);
		hbox.getChildren().add(vBoxCouriers);

		Scene scene = new Scene(hbox, 200, 500);

		stage.setScene(scene);
		
		this.buttonValidate.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Validate");
				mapView.removeLayer(newDelivery);
				for (CustomCircleMarkerLayer customCircleMarkerLayer : mapLayerDelivery) {
					getMapView().removeLayer(customCircleMarkerLayer);
				}
				if (requestedX != 0.0f && requestedY != 0.0f) {
					controller.addDelivery(closestIntersection, requestedDate, requestedStartingTimeWindow,
							requestedCourier);
					try {
						ourMapView.setController(controller);
						ourMapView.setListViewCouriers(couriers);
						ourMapView.setHeight(height);
						ourMapView.setWidth(width);
						ourMapView.setMap(map);
						ourMapView.setMapView(mapView);
						ourMapView.setMapPolygoneMarkerLayers(mapPolygoneMarkerLayers);
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

	protected void saveCouriers() {
		LocalDate date = this.map.getMapDate();
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
				System.out.println("HERE!!!!!!");
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
