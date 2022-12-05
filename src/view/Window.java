package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.desktop.ScreenSleepEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.util.impl.ServiceFactory;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import controller.ControllerAddDelivery;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Courier;
import model.Delivery;
import model.Map;
import model.Tour;
import xml.XMLdeserializer;

public class Window  extends Application  {
	private Map map;
	private ControllerAddDelivery controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private ListView<Delivery> deliveries;
	private mapView mv;
	private Tour tour;
	
	public static void main(String[] args) throws Exception {
		
		StorageService storageService = new StorageService() {
			@Override
            public Optional<File> getPrivateStorage() {
                // user home app config location (linux: /home/[yourname]/.gluonmaps)
                return Optional.of(new File(System.getProperty("user.home")));
            }

            @Override
            public Optional<File> getPublicStorage(String subdirectory) {
                // this should work on desktop systems because home path is public
                return getPrivateStorage();
            }

            @Override
            public boolean isExternalStorageWritable() {
                //noinspection ConstantConditions
                return getPrivateStorage().get().canWrite();
            }

            @Override
            public boolean isExternalStorageReadable() {
                //noinspection ConstantConditions
                return getPrivateStorage().get().canRead();
            }
		};
		 ServiceFactory<StorageService> storageServiceFactory = new ServiceFactory<StorageService>() {

	            @Override
	            public Class<StorageService> getServiceType() {
	                return StorageService.class;
	            }

	            @Override
	            public Optional<StorageService> getInstance() {
	                return Optional.of(storageService);
	            }

	        };
	        // register service
	        Services.registerServiceFactory(storageServiceFactory);       
	        
	        Application.launch(args);		
	}
	

	@Override
	public void start(Stage arg0) throws Exception {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.width = (int)Screen.getPrimary().getBounds().getWidth();
		this.height = (int)Screen.getPrimary().getBounds().getHeight();
		this.width = (int)Screen.getPrimary().getVisualBounds().getWidth();
		this.height = (int)Screen.getPrimary().getVisualBounds().getHeight();
		this.couriers = initCouriers();

		this.map = new Map();
		
		/* Définit la plate-forme pour éviter "javafx.platform is not defined" */
		  System.setProperty("javafx.platform", "desktop");

		  /*
		   * Définit l'user agent pour éviter l'exception
		   * "Server returned HTTP response code: 403"
		   */
		  System.setProperty("http.agent", "Gluon Mobile/1.0.3");

		  /* Création de la carte Gluon JavaFX */
		  MapView mapView = new MapView();

		  double latAverage = 45;
		  double longAverage = 2;
		  /* Création du point avec latitude et longitude */
		  MapPoint mapPoint = new MapPoint(latAverage, longAverage);
		  

		  /* Zoom de 14*/
		  mapView.setZoom(14);

		  /* Centre la carte sur le point */
		  mapView.setCenter(mapPoint);
		
		this.tour = new Tour(map);
		this.deliveries = initDeliveries();
		this.mv = new mapView();
		this.mv.setController(this.controller);
		this.mv.setCouriers(this.couriers);
		this.mv.setHeight(this.height);
		this.mv.setWidth(this.width);
		this.mv.setMap(this.map);
		System.out.println("this.map "+this.map);
		this.mv.setTour(this.tour);
		System.out.println("Deliveries of window "+deliveries);
		this.mv.setDeliveries(deliveries);
		this.mv.setMapView(mapView);
		this.mv.start(new Stage());	
		
	}
	
	public ListView<Delivery> initDeliveries()
	{
		//ArrayList<Tour> tours = plan.getTours();
		//for(Tour t : )
		this.deliveries = new ListView<Delivery>();
		for(Delivery d : tour.getDeliveries())
		{
			deliveries.getItems().add(d);
		}
		return deliveries;
		
	}

	public ListView<Courier> initCouriers() {

		File file = new File("./saveCouriers.txt");
		this.couriers = new ListView<Courier>();

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
						//String[] arrSplit_2 = st.split(";");
						//couriers.getItems().add(new Courier(arrSplit_2[0], Double.parseDouble(arrSplit_2[1])));
						couriers.getItems().add(new Courier(st));
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
		return couriers;
	}

	
}
