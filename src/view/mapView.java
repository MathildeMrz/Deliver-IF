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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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

public class mapView extends Application implements Observer {

	private Map map;
	private ControllerAddDelivery controller;
	private int width;
	private int height;
	private ListView<Courier> listViewCouriers;
	private Stage stage;
	private MapView mapView;
	private MapLayer mapPolygoneMarkerLayer;

	@Override
	public void start(Stage stage) throws Exception {

		/* Init attributes */
		this.stage = stage;
		for(Courier c : this.map.getCouriers())
		{
			c.getTour().addObserver(this);
		}
		this.controller = new ControllerAddDelivery(this.stage, this.map);

		/* Resize the window */
		stage.setWidth(width);
		stage.setHeight(height);

		createMap(this.map);
	}

	public void createMap(Map map) throws MalformedURLException {
		System.out.println("dans createMap : " + map);

		if (this.map.getIsLoaded()) {
			// Add warehouse
			MapPoint mapPointWareHouse = new MapPoint(map.getWarehouse().getLatitude(), map.getWarehouse().getLongitude());
			MapLayer mapLayerWareHouse = new CustomCircleMarkerLayer(mapPointWareHouse, 7, javafx.scene.paint.Color.RED);
			this.mapView.addLayer(mapLayerWareHouse);

			// add deliveries
			for (Courier c : this.map.getCouriers()) {
				for (Delivery d : c.getTour().getDeliveries()) {
					float latDestination = d.getDestination().getLatitude();
					float longDestination = d.getDestination().getLongitude();
					MapPoint mapPointPin = new MapPoint(latDestination, longDestination);
					MapLayer mapLayerPin = new CustomPinLayer(mapPointPin);
					this.mapView.addLayer(mapLayerPin);
				}
			}

			for (Courier c : this.map.getCouriers()) {
				Tour tour = c.getTour();
				if (tour.getDeliveries().size() != 0) {
					TSP(tour);
					// TODO Voir avec Gloria si warehouse pas déjà ajoutée
					//tour.getTourSteps().add(this.map.getWarehouse());
					ArrayList<MapPoint> points = new ArrayList<MapPoint>();
	
					for (int i = 0; i < tour.getTourSteps().size(); i++) {
						double x1 = tour.getTourSteps().get(i).getLongitude();
						double y1 = tour.getTourSteps().get(i).getLatitude();
						points.add(new MapPoint(y1, x1));
					}
					this.mapView.removeLayer(mapPolygoneMarkerLayer);
					mapPolygoneMarkerLayer = new CustomPolygoneMarkerLayer(points, this.mapView, Color.BLUE, 5);
					this.mapView.addLayer(mapPolygoneMarkerLayer);
				}
			}
			
			//add mapBorders
			ArrayList<MapPoint> borderPoints = new ArrayList<MapPoint>();
			double longMin = this.map.getLongitudeMin();
			System.out.println(longMin);
			double longMax = this.map.getLongitudeMax();
			double latMin = this.map.getLatitudeMin();
			double latMax = this.map.getLatitudeMax();
			double coeff = (longMax - longMin)/40;
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

	public void display() {

		HBox hbox = new HBox();

		VBox vBoxMap = new VBox();
		vBoxMap.setPadding(new Insets(20, 20, 20, 20));
		vBoxMap.setMaxHeight(this.height - 40);
		vBoxMap.setMaxWidth(this.width / 1.6);
		vBoxMap.prefWidthProperty().bind(hbox.widthProperty().multiply(0.55));

		if (this.map.getIsLoaded()) {
			// display the map
			vBoxMap.getChildren().add(this.mapView);
		} else {
			vBoxMap.getChildren().add(new Label("Veuillez charger une carte"));
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

		VBox vBoxiIntentedTours = new VBox();
		vBoxiIntentedTours.setStyle("-fx-border-style: solid inside;" + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
				+ "-fx-border-radius: 5;" + "-fx-border-color: #f3f6f4;" + "-fx-margin: 120 150 150 120;");

		vBoxiIntentedTours.getChildren().add(new Label("Deliveries of the day:"));
		
		//TREEVIEW OF THE DELIVERIES FOR EACH COURIER 
		// Create the TreeView
		TreeView treeView = new TreeView();
		// Create the Root TreeItem
		TreeItem rootItem = new TreeItem("Deliveries");
		
		ArrayList<TreeItem> courierItems = new ArrayList<TreeItem>();
		
		for(Courier c : this.map.getCouriers()) {
			TreeItem courierItem = new TreeItem(c.getName());
			ArrayList<TreeItem> deliveryItems = new ArrayList<TreeItem>();
			ArrayList<Delivery> tourDeliveries = c.getTour().getDeliveries();
			tourDeliveries.forEach((d)->{
				TreeItem deliveryItem = new TreeItem(d.toString());
				deliveryItems.add(deliveryItem);
			});
			courierItem.getChildren().addAll(deliveryItems);
			courierItems.add(courierItem);
		}
		// Add children to the root
		rootItem.getChildren().addAll(courierItems);
		// Set the Root Node
		treeView.setRoot(rootItem);
		
		vBoxiIntentedTours.getChildren().add(treeView);

		// -> Test ajout colonne

		// hbox contains two elements
		hbox.getChildren().add(vBoxMap);
		hbox.getChildren().add(vBoxiIntentedTours);
		Scene scene = new Scene(hbox, 200, 500);
		this.stage.setScene(scene);
		this.stage.show();

		// Ajout du bouton new request seulement si une map est chargée
		if (this.map.getIsLoaded()) {
			Button buttonChangePage = new Button("New request");
			vBoxiIntentedTours.getChildren().add(buttonChangePage);
			buttonChangePage.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {

					Platform.runLater(new Runnable() {
						public void run() {
							try {
								newRequestView nr = new newRequestView();
								nr.setController(controller);
								nr.setListViewCouriers(listViewCouriers);
								nr.setHeight(height);
								nr.setWidth(width);
								nr.setMap(map);
								nr.setMapView(mapView);
								nr.setMapPolygoneMarkerLayer(mapPolygoneMarkerLayer);
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
		vBoxiIntentedTours.getChildren().add(buttonLoadMap);

		buttonLoadMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				try {
					map.resetMap();
					XMLdeserializer.load(map, stage);
					map.setMapLoaded();
					for(Courier c : map.getCouriers()) {
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
	}

	public void TSP(Tour tour) {
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		for (Delivery d : tour.getDeliveries()) {
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

	public void setMapPolygoneMarkerLayer(MapLayer layer) {
		this.mapPolygoneMarkerLayer = layer;
	}

	@Override
	public void update(Observable observed, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof Delivery) {
//			deliveries.getItems().add((Delivery) arg);
		}
	}

}
