package deliverif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.util.impl.ServiceFactory;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import controller.Controller;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Courier;
import model.Map;
import view.HomeView;
import view.NewRequestView;

/**
 * creates the application DELIVER'IF
 * */
public class DeliverIf extends Application {
	private Map map;
	private Controller controller;
	private int widthScreen; 
	private int heightScreen; 
	private ListView<Courier> listViewCouriers;
	private HomeView homeView; 
	private NewRequestView newRequestView;

	public static void main(String[] args) throws Exception {
		// Settings for the map
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
				// noinspection ConstantConditions
				return getPrivateStorage().get().canWrite();
			}

			@Override
			public boolean isExternalStorageReadable() {
				// noinspection ConstantConditions
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
		
		//get width and height from our screen
		this.widthScreen = (int) Screen.getPrimary().getVisualBounds().getWidth();
		this.heightScreen = (int) Screen.getPrimary().getVisualBounds().getHeight();
		
		this.map = new Map();
		this.map.setMapDate(LocalDate.now());
		this.controller = new Controller(this.map);
		this.listViewCouriers = initCouriers();

		//Defines the platform to avoid "javafx.platform is not defined
		System.setProperty("javafx.platform", "desktop");

		//Set the user agent to avoid the "Server returned HTTP response code: 403" exception
		System.setProperty("http.agent", "Gluon Mobile/1.0.3");

		/* Creates the Gluon JavaFX map */
		MapView mapView = new MapView();

		double latAverage = 45;
		double longAverage = 2;
		/* Creates a point with a latitude and longitude */
		MapPoint mapPoint = new MapPoint(latAverage, longAverage);

		/* Sets a zoom to see the map well */
		mapView.setZoom(14);

		/* Centers the map on the point */
		mapView.setCenter(mapPoint);

		//Initiates the homeView
		this.homeView = new HomeView();
		this.homeView.initMapPolygoneMarkerLayers();
		this.homeView.initLastToCurrentSelectedStepLayer();
		this.homeView.setListViewCouriers(listViewCouriers);
		this.homeView.setController(this.controller);
		this.homeView.setHeight(this.heightScreen);
		this.homeView.setWidth(this.widthScreen);
		this.homeView.setMap(this.map);
		this.homeView.setMapView(mapView);
		this.newRequestView = new NewRequestView();
		this.newRequestView.setOurMapView(homeView);
		this.homeView.setNr(newRequestView);
		this.homeView.setPinLayersHashMap();
		this.homeView.start(new Stage());
	}

	
	/**
	 * Initiates a list of couriers by reading a txt file.
	 * */
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
						controller.addCourierWithName(st, listViewCouriers);
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
