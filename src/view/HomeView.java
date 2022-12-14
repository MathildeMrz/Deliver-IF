package view;

import java.io.BufferedReader;
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
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> listViewCouriers;
	private Stage stage;
	private MapView mapView;
	private HashMap<TreeItem, Delivery> treeItemToDelivery;
	private HashMap<Integer, MapLayer> pinLayers;
	private ArrayList<MapLayer> mapPolygoneMarkerLayers;
	private ArrayList<MapLayer> lastToCurrentSelectedStepLayer;
	private Delivery lastSelectedDelivery;
	private Delivery lastAddedDelivery;
	private NewRequestView newRequestView;
	private BackgroundFill background_fill;
	private Background background;
	private Button buttonLoadMap;
	private Button buttonAddCourier;
	private Button buttonSaveMap;
	private Button dateValidateButton;
	private TreeView treeView;
	private TreeItem rootItem;
	private ArrayList<TreeItem> courierItems;
	private DatePicker datePicker;
	private boolean startPage;
	private Button buttonChangePage;
	private VBox vBoxMap;
	private VBox vBoxiIntentedTours;
	private VBox vBoxAddCourier;
	private HBox hBox;
	private Button buttonValidateAddCourier;
	private Button buttonDeleteDelivery;
	private TextField courierName;
	private VBox vBoxHome;
	private Scene scene;
	private HBox hboxAddCourier;
	private Label deleteDeliveryInstructions;

	@Override
	public void start(Stage stage) throws Exception {
		if (listViewCouriers == null) {
			this.listViewCouriers = initCouriers();
		}
		/* Init attributes */
		this.stage = stage;
		for (Courier c : this.map.getCouriers()) {
			c.getTour().addObserver(this);
		}

		/* Resize the window */
		stage.setWidth(width);
		stage.setHeight(height);
		this.lastSelectedDelivery = null;

		this.background_fill = new BackgroundFill(Color.rgb(216, 191, 170), CornerRadii.EMPTY, Insets.EMPTY);
		this.background = new Background(background_fill);
		this.buttonLoadMap = new Button("Ouvrir une carte");
		this.buttonAddCourier = new Button("Ajouter un livreur");
		this.buttonChangePage = new Button("Nouvelle livraison");
		this.buttonSaveMap = new Button("Enregistrer les tournées actives");
		this.buttonValidateAddCourier = new Button("Ajouter");
		this.buttonLoadMap.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");
		this.buttonChangePage.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");
		this.buttonAddCourier.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");
		this.buttonValidateAddCourier.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");
		this.buttonDeleteDelivery = new Button("Supprimer la livraison");
		this.buttonDeleteDelivery.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");
		this.buttonSaveMap.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");
		this.dateValidateButton = new Button("Changer date");
		this.dateValidateButton.setStyle("-fx-focus-color: transparent;" + " -fx-border-width: 1px;"
				+ " -fx-border-radius: 8px;" + " -fx-border-color: #000000;" + "-fx-background-radius: 8px;");

		this.courierName = new TextField();
		this.courierName.setPromptText("Nom du livreur");

		this.datePicker = new DatePicker();
		datePicker.setEditable(false);
		this.datePicker.setStyle("-fx-background-color: #8c4817; ");
		this.datePicker.setValue(map.getMapDate());
		this.treeItemToDelivery = new HashMap<TreeItem, Delivery>();
		/* TreeView */
		this.treeView = new TreeView();
		// Create the Root TreeItem
		this.rootItem = new TreeItem("Livraisons pour chaque livreur");
		// ArrayList of TreeItem Couriers
		this.courierItems = new ArrayList<TreeItem>();
		this.vBoxMap = new VBox();
		this.vBoxiIntentedTours = new VBox();
		this.vBoxAddCourier = new VBox();
		this.vBoxHome = new VBox();
		this.vBoxHome.setBackground(background);
		this.hBox = new HBox();
		this.hboxAddCourier = new HBox();
		this.hboxAddCourier.getChildren().add(this.buttonAddCourier);
		this.hboxAddCourier.getChildren().add(this.courierName);
		this.hboxAddCourier.getChildren().add(this.buttonValidateAddCourier);
		this.deleteDeliveryInstructions = new Label("Cliquer sur une livraison dans la vue textuelle avant de cliquer sur le bouton supprimer");
		this.buttonValidateAddCourier.setVisible(false);
		this.courierName.setVisible(false);

		this.scene = new Scene(this.hBox, 2000, 2000);
		Platform.setImplicitExit(false);
		createMap(this.map);

		/* Mouse listeners */
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if(startPage == false) {
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
							saveCouriers();
							Platform.exit();
							System.exit(0);
						}else if (result.get() == buttonNo) {
							Platform.exit();
							System.exit(0);
						} else if (result.get() == buttonCancel) {
							e.consume();
						}
					}
				}
			}
		});

		dateValidateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				LocalDate localDate = datePicker.getValue();
				if (localDate != null) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Changements non enregistrés");
					alert.setHeaderText("Voulez-vous sauvegarder vos changements?");
					ButtonType buttonOk = new ButtonType("Oui");
					ButtonType buttonNo = new ButtonType("Non");
					ButtonType buttonCancel = new ButtonType("Annuler");
					alert.getButtonTypes().setAll(buttonOk, buttonNo, buttonCancel);
					Optional<ButtonType> result = alert.showAndWait();
					if(result.isPresent() && result.get() == buttonCancel) {
						datePicker.setValue(map.getMapDate());
					}else {
						if (result.isPresent() && result.get() == buttonOk) {
							saveCouriers();
						}
					
						for (MapLayer layer : lastToCurrentSelectedStepLayer) {
							mapView.removeLayer(layer);
						}
						lastToCurrentSelectedStepLayer.clear();
						map.setMapDate(localDate);
						courierItems.clear();
						listViewCouriers.getItems().clear();
						map.getCouriers().clear();
						clearScreen();
	
						try {
							loadCouriers();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							createMap(map);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Date non renseignée");
					alert.setContentText("Veuillez renseigner la date!");
					alert.showAndWait();
				}
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

			for (MapLayer layer : this.pinLayers.values()) {
				mapView.removeLayer(layer);
			}
			lastSelectedDelivery = null;

			pinLayers.clear();

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

			for (MapLayer layer : this.mapPolygoneMarkerLayers) {
				this.mapView.removeLayer(layer);
			}

			this.mapPolygoneMarkerLayers.clear();

			// add tours
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

					MapLayer layer = new CustomPolygoneMarkerLayer(points, this.mapView, c.getColor(), 4);
					mapPolygoneMarkerLayers.add(layer);
					this.mapView.addLayer(layer);
				}
			}

			// add mapBorders
			ArrayList<MapPoint> borderPoints = new ArrayList<MapPoint>();
			double longMin = this.map.getLongitudeMin();
			double longMax = this.map.getLongitudeMax();
			double latMin = this.map.getLatitudeMin();
			double latMax = this.map.getLatitudeMax();
			double coeff = (longMax - longMin) / 40;
			borderPoints.add(new MapPoint(latMax + coeff, longMin - coeff));
			borderPoints.add(new MapPoint(latMax + coeff, longMax + coeff));
			borderPoints.add(new MapPoint(latMin - coeff, longMax + coeff));
			borderPoints.add(new MapPoint(latMin - coeff, longMin - coeff));
			borderPoints.add(new MapPoint(latMax + coeff, longMin - coeff));
			CustomPolygoneMarkerLayer border = new CustomPolygoneMarkerLayer(borderPoints, this.mapView, Color.BLACK,
					3);
			this.mapView.addLayer(border);

		}
		display();
	}

	public void display() throws FileNotFoundException {

		/* HBox */
		hBox.setBackground(background);

		/* VBoxMap */

		this.vBoxMap.setPadding(new Insets(20, 20, 20, 20));
		this.vBoxMap.setMaxHeight(this.height - 40);
		this.vBoxMap.setMaxWidth(this.width / 1.6);
		this.vBoxMap.prefWidthProperty().bind(hBox.widthProperty().multiply(0.55));

		/* vBoxiIntentedTours */
		this.vBoxiIntentedTours.setBackground(this.background);
		this.vBoxMap.setBackground(this.background);
		this.vBoxiIntentedTours.setMaxHeight(this.height - 40);
		this.vBoxiIntentedTours.setMaxWidth(this.width / 1.6);
		this.vBoxiIntentedTours.prefWidthProperty().bind(hBox.widthProperty().multiply(0.45));

		// Parcours de chaque tournée
		if (this.map.getIsLoaded()) {
			Label deliveriesOfTheDayLabel = new Label("Livreurs du jour : ");
			deliveriesOfTheDayLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

			this.vBoxiIntentedTours.getChildren().add(deliveriesOfTheDayLabel);
			this.vBoxMap.getChildren().add(this.mapView);
			Label chosenDayLabel = new Label("Date courante : " + this.map.getMapDate().toString());
			this.vBoxMap.getChildren().add(chosenDayLabel);
			// create a date picker

			vBoxMap.getChildren().add(datePicker);
			vBoxMap.getChildren().add(dateValidateButton);

			this.treeView = new TreeView();
			this.courierItems.clear();
			this.rootItem.getChildren().clear();
			scene.setRoot(this.hBox);

			for (Courier c : listViewCouriers.getItems()) {
				// Nom du courier de la tournée
				Label labelCourier = new Label(c.getName());
				Color colorCourier = c.getColor();
				labelCourier.setStyle("-fx-background-color:rgba(" + 255 * colorCourier.getRed() + ","
						+ 255 * colorCourier.getGreen() + "," + 255 * colorCourier.getBlue() + ", 0.7);");
				TreeItem courierItem = new TreeItem(labelCourier);
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
					String detailLivraison = d.toString();
					Label labelDetailLivraison = new Label(detailLivraison);
					if (detailLivraison.contains("Waiting time at the destination from")) {
						labelDetailLivraison.setStyle("-fx-text-fill:rgba(255, 0, 0, 1.00);");
					}
					if (d.getArrival().getHour() != d.getStartTime()) {
						labelDetailLivraison.setStyle("-fx-text-fill:rgba(255, 0, 0, 1.00);");
					}
					TreeItem deliveryItem = new TreeItem(labelDetailLivraison);
					treeItemToDelivery.put(deliveryItem, d);
					if (d == this.lastAddedDelivery) {
						courierItem.setExpanded(true);
						deliveryItem.setExpanded(true);
					}
					switch (d.getStartTime()) {
					case 8:
						if (d == this.lastAddedDelivery) {
							timeWindow8.setExpanded(true);
						}
						deliveries8.add(deliveryItem);
						break;
					case 9:
						if (d == this.lastAddedDelivery) {
							timeWindow9.setExpanded(true);
						}
						deliveries9.add(deliveryItem);
						break;
					case 10:
						if (d == this.lastAddedDelivery) {
							timeWindow10.setExpanded(true);
						}
						deliveries10.add(deliveryItem);
						break;
					case 11:
						if (d == this.lastAddedDelivery) {
							timeWindow11.setExpanded(true);
						}
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

			this.vBoxiIntentedTours.getChildren().add(treeView);
			this.vBoxiIntentedTours.getChildren().add(this.buttonChangePage);
			this.vBoxiIntentedTours.getChildren().add(hboxAddCourier);
			this.vBoxiIntentedTours.getChildren().add(deleteDeliveryInstructions);
			this.vBoxiIntentedTours.getChildren().add(buttonDeleteDelivery);
			this.vBoxiIntentedTours.getChildren().add(buttonLoadMap);
			this.vBoxiIntentedTours.getChildren().add(buttonSaveMap);
			this.vBoxiIntentedTours.setSpacing(10);

			// Add children to the root
			this.rootItem.getChildren().addAll(courierItems);

			// Set the Root Node
			this.treeView.setRoot(rootItem);

			this.vBoxiIntentedTours.setSpacing(10);

			this.stage.setScene(scene);

			hBox.getChildren().add(vBoxMap);
			hBox.getChildren().add(vBoxiIntentedTours);

			stage.setScene(scene);
			this.startPage = false;
		} else {
			// Retour page accueil
			InputStream inputLogo = this.getClass().getResourceAsStream("/Resources/logo_deliverif.png");
			Image imageLogo = new Image(inputLogo, 100, 150, false, false);
			ImageView imageViewLogo = new ImageView(imageLogo);
			this.vBoxHome.getChildren().add(imageViewLogo);
			Label loadMapLabel = new Label("Veuillez charger une carte");
			loadMapLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
			this.vBoxHome.setAlignment(Pos.CENTER);
			this.vBoxHome.getChildren().add(loadMapLabel);
			this.vBoxHome.getChildren().add(buttonLoadMap);
			scene.setRoot(this.vBoxHome);
			stage.setScene(scene);
			this.startPage = true;
		}

		this.stage.show();

		this.buttonDeleteDelivery.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Delivery selectedDelivery = treeItemToDelivery.get(treeView.getSelectionModel().getSelectedItem());

				if (selectedDelivery != null) {
					for (MapLayer layer : lastToCurrentSelectedStepLayer) {
						mapView.removeLayer(layer);
					}
					lastToCurrentSelectedStepLayer.clear();

					MapLayer pinLayerToRemove = pinLayers.get(selectedDelivery.getId());
					mapView.removeLayer(pinLayerToRemove);
					pinLayers.remove(selectedDelivery.getId());

					controller.deleteDelivery(selectedDelivery);
					treeItemToDelivery.remove(selectedDelivery);

					try {
						clearScreen();
						createMap(map);
						mapView.setZoom(mapView.getZoom() - 0.001);
					} catch (FileNotFoundException | MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		this.buttonAddCourier.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (buttonValidateAddCourier.isVisible() == true) {
					buttonValidateAddCourier.setVisible(false);
					courierName.setVisible(false);

				} else {
					buttonValidateAddCourier.setVisible(true);
					courierName.setVisible(true);
					courierName.setText("");
				}
			}
		});

		this.buttonValidateAddCourier.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				if (!(courierName.getText() == null) && !(courierName.getText().trim().isEmpty())) {
					controller.addCourierWithName(courierName.getText(), listViewCouriers);
					File file = new File("saveCouriers.txt");
					FileWriter fr;
					try {
						fr = new FileWriter(file, true);
						fr.write("\n"+courierName.getText());
						fr.close();
					}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // parameter 'true' is for append mode
					
	
			
					buttonValidateAddCourier.setVisible(false);
					courierName.setVisible(false);
					courierName.setText("");
					
				}
				try {
					clearScreen();
					display();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		this.buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for (MapLayer layer : lastToCurrentSelectedStepLayer) {
					mapView.removeLayer(layer);
				}

				Platform.runLater(new Runnable() {
					public void run() {
						try {
							newRequestView.setController(controller);
							newRequestView.setListViewCouriers(listViewCouriers);
							newRequestView.setHeight(height);
							newRequestView.setWidth(width);
							newRequestView.setMap(map);
							newRequestView.setMapView(mapView);
							newRequestView.setMapPolygoneMarkerLayers(mapPolygoneMarkerLayers);
							newRequestView.setTreeview(treeView);
							newRequestView.start(stage);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});

		buttonLoadMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boolean loadNewMap = false;
				if (startPage == false) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Changements non enregistrés");
					alert.setHeaderText("Voulez-vous enregistrer vos modifications avant de charger une nouvelle carte ?");
					ButtonType buttonOk = new ButtonType("Oui");
					ButtonType buttonNo = new ButtonType("Non");
					ButtonType buttonCancel = new ButtonType("Annuler");
					alert.getButtonTypes().setAll(buttonOk, buttonNo, buttonCancel);
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonOk) {
						loadNewMap = true;
						saveCouriers();
					}else if(result.get() == buttonNo) {
						loadNewMap = true;
					}
				}
				if(startPage || loadNewMap) {
					map.resetMap();
					try {
						XMLdeserializer.load(map, stage);
					} catch (ParserConfigurationException | SAXException | IOException | ExceptionXML e1) {
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setContentText("Veuillez charger un fichier XML correspondant à une carte.");
						alert.showAndWait();
						e1.printStackTrace();
					}
					clearScreen();
	
					if (map.getIsLoaded()) {
						// map.setMapLoaded();
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
					}
					try {
						loadCouriers();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						createMap(map);
					} catch (MalformedURLException | FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				for (MapLayer layer : lastToCurrentSelectedStepLayer) {
					mapView.removeLayer(layer);
				}
				mapView.setZoom(mapView.getZoom() - 0.001);
				Delivery selectedDelivery = treeItemToDelivery.get(treeView.getSelectionModel().getSelectedItem());
				if (lastSelectedDelivery != null) {
					MapLayer lastSelectedDeliveryLayer = pinLayers.get(lastSelectedDelivery.getId());
					MapPoint position = ((CustomPinLayer) lastSelectedDeliveryLayer).getMapPoint();
					mapView.removeLayer(lastSelectedDeliveryLayer);
					pinLayers.remove(lastSelectedDelivery.getId());
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
					lastToCurrentSelectedStepLayer.clear();
					boolean deliveryFound = false;
					ArrayList<MapPoint> points = new ArrayList<MapPoint>();
					for (Courier c : map.getCouriers()) {
						Tour tour = c.getTour();
						for (int i = 0; i < tour.getDeliveries().size(); i++) {
							if (tour.getDeliveries().get(i) == selectedDelivery) {
								Intersection beginIntersection;
								if (i - 1 < 0) {
									beginIntersection = map.getWarehouse();
									points.add(new MapPoint(beginIntersection.getLatitude(),
											beginIntersection.getLongitude()));
								} else {
									beginIntersection = tour.getDeliveries().get(i - 1).getDestination();
								}

								for (int j = 0; j < tour.getTourSteps().size(); j++) {
									if (deliveryFound == true
											&& tour.getTourSteps().get(j) != selectedDelivery.getDestination()) {
										double x2 = tour.getTourSteps().get(j).getLongitude();
										double y2 = tour.getTourSteps().get(j).getLatitude();
										points.add(new MapPoint(y2, x2));
									}
									if (tour.getTourSteps().get(j) == selectedDelivery.getDestination()) {
										double x2 = tour.getTourSteps().get(j).getLongitude();
										double y2 = tour.getTourSteps().get(j).getLatitude();
										points.add(new MapPoint(y2, x2));
										break;
									}
									if (deliveryFound == false && tour.getTourSteps().get(j) == beginIntersection) {
										deliveryFound = true;
										double x2 = tour.getTourSteps().get(j).getLongitude();
										double y2 = tour.getTourSteps().get(j).getLatitude();
										points.add(new MapPoint(y2, x2));
									}
								}

								MapLayer layer = new CustomPolygoneMarkerLayer(points, mapView, Color.RED, 5);
								lastToCurrentSelectedStepLayer.add(layer);
								mapView.addLayer(layer);
								break;
							}
						}
					}
					// Pin
					lastSelectedDelivery = selectedDelivery;
					MapLayer layer = pinLayers.get(selectedDelivery.getId());
					MapPoint position = ((CustomPinLayer) layer).getMapPoint();
					mapView.removeLayer(layer);
					pinLayers.remove(selectedDelivery.getId());
					try {
						MapLayer newLayer = new CustomPinLayer(position, true);
						mapView.addLayer(newLayer);
						pinLayers.put(selectedDelivery.getId(), newLayer);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mapView.setZoom(mapView.getZoom() - 0.001);
				}
			}
		});

		buttonSaveMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				saveCouriers();
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Itinéraires enregistrés");
				alert.setContentText("Vos itinéraires ont été enregistrés avec succès!");
				alert.showAndWait();
			}
		});
	}

	public void clearScreen()
	{
		vBoxMap.getChildren().clear();
		vBoxiIntentedTours.getChildren().clear();
		vBoxAddCourier.getChildren().clear();
		vBoxHome.getChildren().clear();
		hBox.getChildren().clear();
	}

	public void TSP(Tour tour) {
		this.controller.TSP(tour);
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

	public void setLastAddedDelivery(Delivery delivery) {
		this.lastAddedDelivery = delivery;
	}

	public void initMapPolygoneMarkerLayers() {
		this.mapPolygoneMarkerLayers = new ArrayList<MapLayer>();
	}

	public void initLastToCurrentSelectedStepLayer() {
		this.lastToCurrentSelectedStepLayer = new ArrayList<MapLayer>();
	}

	@Override
	public void update(Observable observed, Object arg) {
		// TODO Auto-generated method stub
	}

	public NewRequestView getNr() {
		return newRequestView;
	}

	public void setNr(NewRequestView nr) {
		this.newRequestView = nr;
	}

	public void setPinLayersHashMap() {
		this.pinLayers = new HashMap<Integer, MapLayer>();
	}

	protected void saveCouriers() {
		LocalDate date = this.map.getMapDate();
		String path = "loadedDeliveries/" + this.map.getMapName() + "-" + date + ".json";

		JSONArray listeCouriersJson = new JSONArray();

		// For each courier
		for (Courier courier : this.map.getCouriers()) {

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
		ListView<Courier> listViewItemsToIterateOver = new ListView<>();
		for (int i = 0; i < this.listViewCouriers.getItems().size(); i++) {
			listViewItemsToIterateOver.getItems().add(i, this.listViewCouriers.getItems().get(i));
		}
		ArrayList<Courier> couriers = new ArrayList<>();
		String fileName = "loadedDeliveries/" + this.map.getMapName() + "-" + this.map.getMapDate() + ".json";

		File f = new File(fileName);
		if (f.exists() && !f.isDirectory()) {
			// JSON parser object to parse read file
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(fileName);
			Object obj = parser.parse(reader);

			JSONArray JsonCouriers = new JSONArray(obj.toString());

			// For each courier
			for (int i = 0; i < JsonCouriers.length(); i++) {
				JSONObject JsonCourier = JsonCouriers.getJSONObject(i);
				String courierName = JsonCourier.getString("name");
				int speed = JsonCourier.getInt("speed");
				int idCourier = JsonCourier.getInt("id");

				// Creation of the courier
				Courier courier = new Courier(courierName, idCourier);
				courier.setSpeed(speed);

				// Get the tour of the current courier
				JSONObject JsonTour = JsonCourier.getJSONObject("tour");

				JSONArray JsonIntersections = JsonTour.getJSONArray("tourSteps");
				ArrayList<Intersection> tourSteps = new ArrayList<Intersection>();

				// For each intersection in the tour
				for (int j = 0; j < JsonIntersections.length(); j++) {

					JSONObject JsonIntersection = JsonIntersections.getJSONObject(j);
					long intersectionId = JsonIntersection.getLong("id");

					if (this.map.getNodes().containsKey(intersectionId)) {
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
						Delivery delivery = new Delivery(deliveryStartTime,
								this.map.getNodes().get(deliveryIntersectionId), localTimeArrival, localTimeDelivery);
						deliveries.add(delivery);
						Intersection destination = new Intersection();
						destination.setId(deliveryIntersectionId);
						this.map.addDestination(destination);
					}
				}
				Tour tour = new Tour();
				tour.setTourSteps(tourSteps);
				tour.setDeliveries(deliveries);
				tour.setEndDate(localDateEnd);
				tour.setStartDate(localDateStart);
				courier.setTour(tour);
				couriers.add(courier);

				for (Courier item : listViewItemsToIterateOver.getItems()) {

					if (item.getId() == courier.getId()) {

						int id = listViewItemsToIterateOver.getItems().stream()
								.filter(courierItem -> item.getId() == courierItem.getId()).findFirst().orElse(null)
								.getId();
						this.listViewCouriers.getItems().set(id, courier);
					} else {
						this.listViewCouriers.getItems().add(item);
					}
				}
				couriersAL.getItems().add(courier);
			}
			this.map.setCouriers(couriers);
			setListViewCouriers(couriersAL);
			reader.close();
		} else {
			ArrayList<Courier> couriersInit = new ArrayList<>();
			couriersAL = initCouriers();
			for (Courier item : couriersAL.getItems()) {
				couriersInit.add(item);
			}
			this.map.setCouriers(couriersInit);
		}
		couriersAL = listViewItemsToIterateOver;

		return couriersAL;
	}

	public ListView<Courier> initCouriers() {

		File file = new File("./saveCouriers.txt");
		this.listViewCouriers = new ListView<Courier>();

		if (file.exists()) {
			// Creating an object of BufferedReader class
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				// Declaring a string variable
				String st;
				// Condition holds true till
				try {

					while ((st = br.readLine()) != null) {
						Courier courier = new Courier(st);
						listViewCouriers.getItems().add(courier);
						this.map.addCourier(courier);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println(file.getPath() + " does not exist");
		}
		return listViewCouriers;
	}

}
