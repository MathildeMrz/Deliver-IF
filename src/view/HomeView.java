package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.SAXException;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import algorithm.RunTSP2;
import controller.ControllerAddDelivery;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Courier;
import model.CustomCircleMarkerLayer;
import model.CustomPinLayer;
import model.CustomPolygoneMarkerLayer;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Tour;
import observer.Observable;
import observer.Observer;
import xml.ExceptionXML;
import xml.XMLdeserializer;

public class HomeView extends Application implements Observer {

	private Map map;
	private ControllerAddDelivery controller;
	private int width;
	private int height;
	private ListView<Courier> listViewCouriers;
	private Stage stage;
	private MapView mapView;
	private ArrayList<MapLayer> mapPolygoneMarkerLayers;
	private HashMap<Integer, MapLayer> pinLayers;
	private Delivery lastSelectedDelivery;
	private MapLayer lastSelectedDeliveryLayer;
	private NewRequestView nr;
	private BackgroundFill background_fill;
	private Background background;
	private Button buttonLoadMap;
	private TreeView treeView;
	private TreeItem rootItem;
	private ArrayList<TreeItem> courierItems;
	private DatePicker datePicker;
	private HashMap<TreeItem, Delivery> treeItemToDelivery  ;
	
	@Override
	public void start(Stage stage) throws Exception {

		/* Init attributes */
		this.stage = stage;
		for (Courier c : this.map.getCouriers()) {
			c.getTour().addObserver(this);
		}
		this.controller = new ControllerAddDelivery(this.stage, this.map);

		/* Resize the window */
		stage.setWidth(width);
		stage.setHeight(height);
		this.pinLayers = new HashMap<Integer, MapLayer>();
		this.lastSelectedDelivery = null;
		this.lastSelectedDeliveryLayer = null;
		
		this.background_fill = new BackgroundFill(Color.rgb(216, 191, 170), CornerRadii.EMPTY, Insets.EMPTY);
		this.background = new Background(background_fill);
		this.buttonLoadMap = new Button("Sélectionner une carte");
		this.buttonLoadMap.setStyle(" -fx-border-radius: 6px;" +  " -fx-border-color: #000000" + " -fx-background-color: #AD785D; " + "-fx-background-radius: 6px" );
		this.datePicker = new DatePicker();	
		this.datePicker.setStyle("-fx-background-color: #8c4817; ");
		this.treeItemToDelivery = new HashMap<TreeItem, Delivery>();
		/*TreeView*/
		this.treeView = new TreeView();
		// Create the Root TreeItem
		this.rootItem = new TreeItem("Livraisons pour chaque livreur");
		// ArrayList of TreeItem Couriers
		this.courierItems = new ArrayList<TreeItem>();

		createMap(this.map);

		/*Mouse listeners*/		
			
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Changements non enregistrés");
				alert.setContentText("Voulez-vous sauvegarder vos changements?");
				alert.getButtonTypes().clear();
				alert.getButtonTypes().add(ButtonType.YES);
				alert.getButtonTypes().add(ButtonType.NO);
				Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
				noButton.setStyle("-fx-background-color: #BFD1E5; ");
				Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
				yesButton.setStyle("-fx-background-color: #BFD1E5; ");
				noButton.setDefaultButton(true);
				yesButton.setDefaultButton(false);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.YES) {
					System.out.println("YES!!!!!");
					saveCouriers();
					Platform.exit();
					System.exit(0);
				} else if (result.isPresent() && result.get() == ButtonType.NO) {
					System.out.println("NO!!!!!");
					Platform.exit();
					System.exit(0);
				} else {
					System.out.println("Come back to the page");
				}
			}
		});
		
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("add red pin");
				Delivery selectedDelivery = treeItemToDelivery.get(treeView.getSelectionModel().getSelectedItem());
				System.out.println(selectedDelivery);
				if (lastSelectedDelivery != null) {
					MapPoint position = ((CustomPinLayer) lastSelectedDeliveryLayer).getMapPoint();
					mapView.removeLayer(lastSelectedDeliveryLayer);
					try {
						MapLayer blackPin = new CustomPinLayer(position, false);
						pinLayers.put(lastSelectedDelivery.getId(), blackPin);
						mapView.addLayer(blackPin);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (selectedDelivery != null) {
					MapLayer layer = pinLayers.get(selectedDelivery.getId());
					MapPoint position = ((CustomPinLayer) layer).getMapPoint();
					mapView.removeLayer(layer);
					try {
						lastSelectedDeliveryLayer = new CustomPinLayer(position, true);
						mapView.addLayer(lastSelectedDeliveryLayer);
						lastSelectedDelivery = selectedDelivery;
						pinLayers.remove(selectedDelivery.getId());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		buttonLoadMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				try {
					map.setMapLoaded();
					map.resetMap();
					XMLdeserializer.load(map, stage);
					for (Courier c : map.getCouriers()) {
						c.getTour().clearTourSteps();
						c.getTour().clearDeliveries();
						c.getTour().getDeliveries().clear();
					}

					mapView = new MapView();
					double latAverage = (map.getLatitudeMin() + map.getLatitudeMax()) / 2;
					double longAverage = (map.getLongitudeMin() + map.getLongitudeMax()) / 2;
					MapPoint mapPoint = new MapPoint(latAverage, longAverage);
					mapView.setZoom(14);
					mapView.setCenter(mapPoint);

					createMap(map);
				} catch (ParserConfigurationException | SAXException | IOException | ExceptionXML e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
				
		datePicker.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Load another couriers file");
			}
			
		});
		

	}

	public void createMap(Map map) throws MalformedURLException, FileNotFoundException {

		if (this.map.getIsLoaded()) {
			// Add warehouse
			MapPoint mapPointWareHouse = new MapPoint(map.getWarehouse().getLatitude(),
					map.getWarehouse().getLongitude());
			MapLayer mapLayerWareHouse = new CustomCircleMarkerLayer(mapPointWareHouse, 7,
					javafx.scene.paint.Color.RED);
			this.mapView.addLayer(mapLayerWareHouse);

			// add deliveries
			for (Courier c : this.map.getCouriers()) {
				for (Delivery d : c.getTour().getDeliveries()) {
					float latDestination = d.getDestination().getLatitude();
					float longDestination = d.getDestination().getLongitude();
					MapPoint mapPointPin = new MapPoint(latDestination, longDestination);
					MapLayer pinLayer = new CustomPinLayer(mapPointPin, false);
					this.pinLayers.put(d.getId(), pinLayer);
					this.mapView.addLayer(pinLayer);
				}
			}

			//
			for (MapLayer layer : this.mapPolygoneMarkerLayers) {
				this.mapView.removeLayer(layer);
			}
			this.mapPolygoneMarkerLayers.clear();
			for (Courier c : this.map.getCouriers()) {
				Tour tour = c.getTour();
				if (tour.getDeliveries().size() != 0) {
					TSP(tour);
					ArrayList<MapPoint> points = new ArrayList<MapPoint>();

					for (int i = 0; i < tour.getTourSteps().size(); i++) {
						double x1 = tour.getTourSteps().get(i).getLongitude();
						double y1 = tour.getTourSteps().get(i).getLatitude();
						points.add(new MapPoint(y1, x1));
					}
					MapLayer layer = new CustomPolygoneMarkerLayer(points, this.mapView, c.getColor(), 5);
					mapPolygoneMarkerLayers.add(layer);
					this.mapView.addLayer(layer);
				}
			}

			// add mapBorders
			ArrayList<MapPoint> borderPoints = new ArrayList<MapPoint>();
			double longMin = this.map.getLongitudeMin();
			System.out.println(longMin);
			double longMax = this.map.getLongitudeMax();
			double latMin = this.map.getLatitudeMin();
			double latMax = this.map.getLatitudeMax();
			double coeff = (longMax - longMin) / 40;
			borderPoints.add(new MapPoint(latMax + coeff, longMin - coeff));
			borderPoints.add(new MapPoint(latMax + coeff, longMax + coeff));
			borderPoints.add(new MapPoint(latMin - coeff, longMax + coeff));
			borderPoints.add(new MapPoint(latMin - coeff, longMin - coeff));
			borderPoints.add(new MapPoint(latMax + coeff, longMin - coeff));
			CustomPolygoneMarkerLayer border = new CustomPolygoneMarkerLayer(borderPoints, this.mapView, Color.BLACK, 3);
			this.mapView.addLayer(border);

		}
		display();
	}

	public void display() throws FileNotFoundException {

		/* HBox */
		HBox hbox = new HBox();		
		hbox.setBackground(background);
		

		/* VBoxMap */
		VBox vBoxMap = new VBox();
		vBoxMap.setPadding(new Insets(20, 20, 20, 20));
		vBoxMap.setMaxHeight(this.height - 40);
		vBoxMap.setMaxWidth(this.width / 1.6);
		vBoxMap.prefWidthProperty().bind(hbox.widthProperty().multiply(0.55));

		/* vBoxiIntentedTours */
		VBox vBoxiIntentedTours = new VBox();
		vBoxiIntentedTours.setStyle("-fx-border-style: solid inside;" + "-fx-border-width: 2;" + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: #f3f6f4;" + "-fx-margin: 120 150 150 120;");
		vBoxiIntentedTours.setBackground(this.background);
		vBoxMap.setBackground(this.background);
		vBoxiIntentedTours.setMaxHeight(this.height - 40);
		vBoxiIntentedTours.setMaxWidth(this.width / 1.6);
		vBoxiIntentedTours.prefWidthProperty().bind(hbox.widthProperty().multiply(0.45));		
		
		// Parcours de chaque tournée
		if (this.map.getIsLoaded())
		{
			Label deliveriesOfTheDayLabel = new Label("Livreurs du jour : ");
			deliveriesOfTheDayLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

			vBoxiIntentedTours.getChildren().add(deliveriesOfTheDayLabel);
			vBoxMap.getChildren().add(this.mapView);
			Label chosenDayLabel = new Label("Date courante : "+this.map.getMapDate().toString());	
			vBoxMap.getChildren().add(chosenDayLabel);			
			// create a date picker
	        
			vBoxMap.getChildren().add(datePicker);				        
	
			for (Courier c : listViewCouriers.getItems())
			{
				// Nom du courier de la tournée
				TreeItem courierItem = new TreeItem(c.getName());
				// ArrayList of TreeItem TimeWindows
				ArrayList<TreeItem> timeWindows = new ArrayList<TreeItem>();
				// Liste des livraisons de la tournée +Tri de la liste
				ArrayList<Delivery> tourDeliveries = c.getTour().getDeliveries();
				Collections.sort(tourDeliveries, Comparator.comparing(a -> a.getDeliveryTime()));
				Collections.sort(tourDeliveries, Comparator.comparing(a -> a.getStartTime()));
				// TreeItem pour chaque TimeWindow
				TreeItem timeWindow8 = new TreeItem("8h à 9h");
				TreeItem timeWindow9 = new TreeItem("9h à 10h");
				TreeItem timeWindow10 = new TreeItem("10h à 11h");
				TreeItem timeWindow11 = new TreeItem("11h à 12h");
				// ArrayList de TreeItem pour les livraisons des timeWindow
				ArrayList<TreeItem> deliveries8 = new ArrayList<TreeItem>();
				ArrayList<TreeItem> deliveries9 = new ArrayList<TreeItem>();
				ArrayList<TreeItem> deliveries10 = new ArrayList<TreeItem>();
				ArrayList<TreeItem> deliveries11 = new ArrayList<TreeItem>();
				// Parcours de la liste de livraisons et ajout à chaque timeWindow correspondant
				tourDeliveries.forEach((d) -> {
					TreeItem deliveryItem = new TreeItem(d.toString());
					treeItemToDelivery.put(deliveryItem, d);
					courierItem.setExpanded(true);
					deliveryItem.setExpanded(true);
					switch (d.getStartTime()) {
					case 8:
						timeWindow8.setExpanded(true);
						deliveries8.add(deliveryItem);
						break;
					case 9:
						timeWindow9.setExpanded(true);
						deliveries9.add(deliveryItem);
						break;
					case 10:
						timeWindow10.setExpanded(true);
						deliveries10.add(deliveryItem);
						break;
					case 11:
						timeWindow11.setExpanded(true);
						deliveries11.add(deliveryItem);
						break;
					}
				});
				// Ajout de chaque liste de livraisons dans les arraylist de TreeItem
				timeWindow8.getChildren().addAll(deliveries8);
				timeWindow9.getChildren().addAll(deliveries9);
				timeWindow10.getChildren().addAll(deliveries10);
				timeWindow11.getChildren().addAll(deliveries11);
				// Ajout de chaque TimeWindow dans l'ArrayList de timeWindows
				timeWindows.add(timeWindow8);
				timeWindows.add(timeWindow9);
				timeWindows.add(timeWindow10);
				timeWindows.add(timeWindow11);	

				courierItem.getChildren().addAll(timeWindows);
				courierItems.add(courierItem);							
				
			}
			this.rootItem.setExpanded(true);
				
			vBoxiIntentedTours.getChildren().add(treeView);
			vBoxiIntentedTours.getChildren().add(buttonLoadMap);
			vBoxiIntentedTours.setSpacing(10);
			
			// Add children to the root
			rootItem.getChildren().addAll(courierItems);
			// Set the Root Node
			treeView.setRoot(rootItem);

			Scene scene = new Scene(hbox, 2000, 2000);

			this.stage.setScene(scene);

			hbox.getChildren().add(vBoxMap);
			hbox.getChildren().add(vBoxiIntentedTours);
		} 
		else 
		{
			InputStream inputLogo = this.getClass().getResourceAsStream("/Resources/logo_deliverif.png");
			Image imageLogo = new Image(inputLogo, 100, 150, false, false);
			ImageView imageViewLogo = new ImageView(imageLogo);
			vBoxMap.getChildren().add(imageViewLogo);
			Label loadMapLabel = new Label("Veuillez charger une carte");
			loadMapLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
			vBoxMap.setAlignment(Pos.CENTER);
			vBoxMap.getChildren().add(loadMapLabel);
			vBoxMap.getChildren().add(buttonLoadMap);
			Scene scene = new Scene(vBoxMap, 2000, 2000);
			this.stage.setScene(scene);
			// listViewCouriers = new ListView<Courier>();
		}

		// Modifications ajout tableau livraisons
		// TableView<Delivery> table = new TableView<Delivery>();
		// Create column UserName (Data type of String).
		// TableColumn<Delivery, Courier> courierCol //
		// = new TableColumn<Delivery, Courier>("Courier name");
		// TableColumn<Delivery, Intersection> locationCol //
		// = new TableColumn<Delivery, Intersection>("Location");
		// TableColumn<Delivery, String> timeWindowCol //
		// = new TableColumn<Delivery, String>("Time Window");
		// courierCol.setPrefWidth(200.0d);
		// locationCol.setPrefWidth(200.0d);
		// timeWindowCol.setPrefWidth(200.0d);
		// table.getColumns().addAll(courierCol, locationCol, timeWindowCol);

		// TEST : CREATE THE COURIERS + TOURS + DELIVERIES
		/*
		 * Courier courier1 = new Courier("Marilou"); Courier courier2 = new
		 * Courier("Félicie"); Courier courier3 = new Courier("Fatma");
		 * 
		 * ArrayList<Delivery> deliveries1 = new ArrayList<>(); ArrayList<Delivery>
		 * deliveries2 = new ArrayList<>(); ArrayList<Delivery> deliveries3 = new
		 * ArrayList<>();
		 * 
		 * Long id2=Long.parseLong("1850080438"); Intersection inter2=
		 * map.getNodes().get(id2); Long id3=Long.parseLong("25319182"); Intersection
		 * inter3= map.getNodes().get(id3); Long id4=Long.parseLong("1042749162");
		 * Intersection inter4= map.getNodes().get(id4); Long
		 * id5=Long.parseLong("21703596"); Intersection inter5= map.getNodes().get(id5);
		 * Long id6=Long.parseLong("26575616"); Intersection inter6=
		 * map.getNodes().get(id6);
		 * 
		 * deliveries1.add(new Delivery("Livraison", 8, inter2, LocalTime.of(8, 45)));
		 * deliveries1.add(new Delivery("Livraison", 9, inter3, LocalTime.of(9, 22)));
		 * deliveries1.add(new Delivery("Livraison", 8, inter4, LocalTime.of(8, 15)));
		 * deliveries2.add(new Delivery("Livraison", 8, inter5, LocalTime.of(8, 38)));
		 * deliveries2.add(new Delivery("Livraison", 10, inter5, LocalTime.of(10, 18)));
		 * 
		 * Tour tour1 = new Tour(); Tour tour2 = new Tour(); Tour tour3 = new Tour();
		 * tours.add(tour1); tours.add(tour2); tours.add(tour3);
		 */

		this.stage.show();

		// Ajout du bouton new request seulement si une map est chargée
		if (this.map.getIsLoaded()) {
			Button buttonChangePage = new Button("Nouvelle livraison");
			buttonChangePage.setStyle(" -fx-border-radius: 6px;" +  " -fx-border-color: #000000" + " -fx-background-color: #AD785D; " + "-fx-background-radius: 6px" );

			vBoxiIntentedTours.getChildren().add(buttonChangePage);
			buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {

					Platform.runLater(new Runnable() {
						public void run() {
							try {
								nr.setController(controller);
								nr.setListViewCouriers(listViewCouriers);
								nr.setHeight(height);
								nr.setWidth(width);
								nr.setMap(map);
								nr.setMapView(mapView);
								nr.setMapPolygoneMarkerLayers(mapPolygoneMarkerLayers);
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

		
	}

	/*public void TSP(Tour tour) {
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		for (Delivery d : tour.getDeliveries()) {
			sommets.add(d.getDestination());
		}
		System.out.println("Debut TSP");
		RunTSP testTSP = new RunTSP(sommets.size(), sommets, map, tour);
		testTSP.start();
		System.out.println("Fin TSP");
	}*/
	public void TSP(Tour tour) {
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		int debut =12;
		for (Delivery d : tour.getDeliveries()) {
			sommets.add(d.getDestination());
			System.out.print(d.getStartTime()+ " ");
			System.out.println();
			if(d.getStartTime()<debut) {
				debut=d.getStartTime();
			}
		}
		LocalDate tourStartDate=tour.getStartDate().toLocalDate();
		tour.setStartDate(tourStartDate.atTime(debut,0,0));
		System.out.println("Debut de la tournée à" + tour.getStartDate());
		System.out.println("Debut TSP");
		RunTSP2 testTSP = new RunTSP2(map.getWarehouse(),tour.getDeliveries().size()+1, sommets, map, tour);
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

	public ListView<Courier> getListViewCouriers() {
		return listViewCouriers;
	}

	public void setListViewCouriers(ListView<Courier> couriers) {
		this.listViewCouriers = couriers;
	}

	public Stage getStage() {
		return this.stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	public void setMapPolygoneMarkerLayers(ArrayList<MapLayer> layer) {
		this.mapPolygoneMarkerLayers = layer;
	}

	public void initMapPolygoneMarkerLayers() {
		this.mapPolygoneMarkerLayers = new ArrayList<MapLayer>();
	}

	@Override
	public void update(Observable observed, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof Delivery) {
//			deliveries.getItems().add((Delivery) arg);
		}
	}
	
	public NewRequestView getNr() {
		return nr;
	}

	public void setNr(NewRequestView nr) {
		this.nr = nr;
	}


	protected void saveCouriers() {
		System.out.println("Save couriers");
		LocalDate date = this.map.getMapDate();
		String path = "loadedDeliveries/" + date + ".json";

		JSONArray listeCouriersJson = new JSONArray();

		// For each courier
		for (Courier courier : this.map.getCouriers()) {
			System.out.println("I save one courier");

			JSONObject courierJson = new JSONObject();
			courierJson.put("id", courier.getId());
			courierJson.put("name", courier.getName());
			courierJson.put("speed", courier.getSpeed());
			JSONObject tourJson = new JSONObject();
			Tour tour = courier.getTour();

			tourJson.put("id", tour.getId());
			tourJson.put("startDate", tour.getStartDate());
			tourJson.put("endDate", tour.getEndDate());

			// Array tourSteps
			JSONArray tourStepsJson = new JSONArray();

			// For each intersection of the tour
			for (Intersection tourStep : tour.getTourSteps()) {
				JSONObject tourStepJson = new JSONObject();
				tourStepJson.put("id", tourStep.getId());
			}

			tourJson.put("tourSteps", tourStepsJson);

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

	public ListView<Courier> loadCouriers()
			throws org.json.simple.parser.ParseException, FileNotFoundException, IOException {
		ListView<Courier> couriersAL = new ListView<Courier>();
		String fileName = "loadedDeliveries/" + this.map.getMapDate() + ".json";

		File f = new File(fileName);
		if (f.exists() && !f.isDirectory()) {
			// JSON parser object to parse read file
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(fileName);
			Object obj = parser.parse(reader);
			System.out.println("obj : " + obj);

			JSONArray JsonCouriers = new JSONArray(obj.toString());

			// For each courier
			for (int i = 0; i < JsonCouriers.length(); i++) {
				System.out.println("New courier to load");

				JSONObject JsonCourier = JsonCouriers.getJSONObject(i);
				String courierName = JsonCourier.getString("name");
				int speed = JsonCourier.getInt("speed");

				// Creation of the courier
				Courier courier = new Courier(courierName);
				courier.setSpeed(speed);

				// Get the tour of the current courier
				JSONObject JsonTour = JsonCourier.getJSONObject("tour");

				JSONArray JsonIntersections = JsonTour.getJSONArray("tourSteps");
				ArrayList<Intersection> tourSteps = new ArrayList<Intersection>();

				System.out.println(courier.getName() + " a " + JsonIntersections.length() + " intersections");

				// For each intersection in the tour
				for (int j = 0; j < JsonIntersections.length(); j++) {
					System.out.println("Intersection");

					JSONObject JsonIntersection = JsonIntersections.getJSONObject(j);
					long intersectionId = JsonIntersection.getLong("id");

					if (this.map.getNodes().containsKey(intersectionId)) {
						System.out.println("Youpi");
						tourSteps.add(this.map.getNodes().get(intersectionId));
						break;
					}
				}

				String localDateStringEnd = JsonTour.getString("endDate");
				LocalDateTime localDateEnd = LocalDateTime.parse(localDateStringEnd);
				String localDateStringStart = JsonTour.getString("startDate");
				LocalDateTime localDateStart = LocalDateTime.parse(localDateStringStart);

				JSONArray JsonDeliveries = JsonTour.getJSONArray("deliveries");

				ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

				System.out.println(courier.getName() + " a " + JsonDeliveries.length() + " livraisons");

				// For each delivery in the Tour
				for (int m = 0; m < JsonDeliveries.length(); m++) {
					JSONObject JsonDelivery = JsonDeliveries.getJSONObject(m);

					int deliveryStartTime = JsonDelivery.getInt("startTime");
					String deliveryLocalTimeArrival = JsonDelivery.getString("arrival");
					LocalTime localTimeArrival = LocalTime.parse(deliveryLocalTimeArrival);
					String deliveryLocalTimeDelivery = JsonDelivery.getString("deliveryTime");
					LocalTime localTimeDelivery = LocalTime.parse(deliveryLocalTimeDelivery);

					// The intersection of the delivery
					JSONObject JsonDeliveryIntersection = JsonDelivery.getJSONObject("destination");

					long deliveryIntersectionId = JsonDeliveryIntersection.getLong("id");

					if (this.map.getNodes().containsKey(deliveryIntersectionId)) {
						System.out.println("Youpii");
						Delivery delivery = new Delivery(deliveryStartTime,
								this.map.getNodes().get(deliveryIntersectionId), localTimeArrival, localTimeDelivery);
						deliveries.add(delivery);
						Intersection destination = new Intersection();
						destination.setId(deliveryIntersectionId);
						this.map.addDestination(destination);
					}
				}
				Tour tour = new Tour();
				System.out.println(courier.getName() + " 2 a " + tour.getDeliveries().size() + " deliveries");
				System.out.println("Test1 : " + tour.getDeliveries().size());
				tour.setTourSteps(tourSteps);
				System.out.println("Test2 : " + tour.getDeliveries().size());
				System.out.println(courier.getName() + " 1 a " + tour.getDeliveries().size() + " deliveries");

				tour.setDeliveries(deliveries);
				tour.setEndDate(localDateEnd);
				tour.setStartDate(localDateStart);
				courier.setTour(tour);
				couriersAL.getItems().add(courier);
				// this.map.addCourier(courier);
			}
			reader.close();
		} else {
			System.out.println("Aucun fichier existant : " + fileName);
		}

		return couriersAL;
	}

}
