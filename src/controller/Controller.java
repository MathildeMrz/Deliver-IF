package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import algorithm.RunTSP2;
import javafx.scene.control.ListView;
import model.Courier;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Tour;

public class Controller {
	private Map map;	

	public Controller(Map map)
	{
		this.map= map;
	}
	
	/**
	 * add a delivery to the list of deliveries of a courier.
	 * @param intersection : intersection where the delivery takes place 
	 * @param date : date of the delivery
	 * @param timeWindow : hour of the day when the courier should deliver (he must deliver between timeWindow and timeWindow +1)
	 * @param courier : courier to whom we want to add a delivery
	 * */
	public Delivery addDelivery(Intersection intersection, LocalDate date, int timeWindow, Courier courier) {
		return courier.getTour().addDelivery(intersection, date, timeWindow);
	}
	
	/**
	 * add a courier with a name
	 * @param name : name of the courier
	 * @param listView : listView of the map where we need to display the added courier
	 * */
	public void addCourierWithName(String name, ListView<Courier> listView) {
		Courier courier = new Courier(name);
		this.map.addCourier(courier);
		listView.getItems().add(courier);

	}
	
	/**
	 * get the closest intersection from the request coordinates
	 * @param latitude : latitude of the requested point on the map
	 * @param longitude : longitude of the requested point on the map
	 * @param listView : listView of the map where we need to display the added courier
	 * */
	public Intersection getClosestIntersection(float latitude, float longitude, long idIntersection) {
		 return map.getClosestIntersection(latitude, longitude, idIntersection);
	}
	
	/**
	 * calculates the shortest tour composed of one or multiple delivery points
	 * @param tour : the tour that will be used for the calculations 
	 * */
	public void TSP(Tour tour) {
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		int debut =12;
		for (Delivery d : tour.getDeliveries()) {
			sommets.add(d.getDestination());
			if(d.getStartTime()<debut) {
				debut=d.getStartTime();
			}
		}
		LocalDate tourStartDate=tour.getStartDate().toLocalDate();
		tour.setStartDate(tourStartDate.atTime(debut,0,0));
		RunTSP2 testTSP = new RunTSP2(map.getWarehouse(),tour.getDeliveries().size()+1, sommets, map, tour);
		testTSP.start();
	}
	
	/**
	 * add a delivery from the list of deliveries of a courier.
	 * @param selectedDelivery : delivery to remove
	 * */
	public void deleteDelivery(Delivery selectedDelivery)
	{
		for(Courier c : map.getCouriers()) 
		{
			if(c.getTour().getDeliveries().contains(selectedDelivery))
			{
				c.getTour().getDeliveries().remove(selectedDelivery);
			}
		}
	}
}
