package tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import model.Courier;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Tour;
import view.HomeView;
import javafx.application.Platform;
import javafx.scene.control.ListView;


public class TestSaveLoadCouriers {
	protected HomeView homeView;
	protected Map map;
	protected ArrayList<Courier> couriers;

	@Before
	public void setUp() throws Exception {
		homeView = new HomeView();
		map = new Map();
		couriers = new ArrayList<>();
		 byte[] array = new byte[7]; // length is bounded by 7
		    new Random().nextBytes(array);
		    String randomMapName = new String(array, Charset.forName("UTF-8"));
		map.setMapDate(LocalDate.now());
		map.setMapName(randomMapName);
		homeView.setMap(map);

		
	}
	
	@Test
	public void testSaveCouriers() {
				
				Platform.startup(() -> {});

		//TEST : CREATE THE COURIERS + TOURS + DELIVERIES 
				Courier courier1 = new Courier("Marilou");
				Courier courier2 = new Courier("Félicie");
				Courier courier3 = new Courier("Fatma");

				ArrayList<Delivery> deliveries1 = new ArrayList<>();
				ArrayList<Delivery> deliveries2 = new ArrayList<>();
				ArrayList<Delivery> deliveries3 = new ArrayList<>();
				
				deliveries1.add(new Delivery(8, new Intersection(),LocalTime.of(8, 45)));
				deliveries1.add(new Delivery(8, new Intersection(), LocalTime.of(9, 22)));
				deliveries1.add(new Delivery(8, new Intersection(), LocalTime.of(8, 15)));

				deliveries2.add(new Delivery(8, new Intersection(), LocalTime.of(8, 38)));
				deliveries2.add(new Delivery(8, new Intersection(), LocalTime.of(10, 18)));

				Tour tour1 = new Tour();
				Tour tour2 = new Tour();
				Tour tour3 = new Tour();
				
				tour1.setDeliveries(deliveries1);
				tour2.setDeliveries(deliveries2);
				tour3.setDeliveries(deliveries3);
				
				courier1.setTour(tour1);
				courier2.setTour(tour2);
				courier3.setTour(tour3);
				
				couriers.add(courier1);
				couriers.add(courier2);
				couriers.add(courier3);

				
				map.setCouriers(couriers);
				map.setMapName("largeMap");
				
				homeView.saveCouriers();
				homeView.setListViewCouriers(new ListView<Courier>());
				
				try {
					ArrayList<Courier> couriersFromSavedFile = homeView.loadCouriers();
					
					for(int i = 0; i< couriersFromSavedFile.size(); i++) {
						assertEquals(couriersFromSavedFile.get(i).getId(), couriers.get(i).getId());
						assertEquals(couriersFromSavedFile.get(i).getName(), couriers.get(i).getName());
						ArrayList<Delivery> deliveriesFromSaved = couriersFromSavedFile.get(i).getTour().getDeliveries();
						ArrayList<Delivery> initialDeliveries = couriers.get(i).getTour().getDeliveries();

						for(int j = 0; j<deliveriesFromSaved.size(); j++) {
							assertEquals(deliveriesFromSaved.get(j).getStartTime(), initialDeliveries.get(j).getStartTime());
							assertEquals(deliveriesFromSaved.get(j).getDestination(), initialDeliveries.get(j).getDestination());
							assertEquals(deliveriesFromSaved.get(j).getArrival(), initialDeliveries.get(j).getArrival());
						}
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}
	
}
