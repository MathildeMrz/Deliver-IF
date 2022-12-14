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

	public Delivery addDelivery(Intersection closerIntersection, LocalDate date, int timeWindow, Courier courier) {
		return courier.getTour().addDelivery(closerIntersection, date, timeWindow);
	}
	
	public void addCourierWithName(String name, ListView<Courier> listView) {
		Courier courier = new Courier(name);
		this.map.addCourier(courier);
		listView.getItems().add(courier);

	}
	
	public Intersection getClosestIntersection(float latitude, float longitude, long idIntersection) {
		 return map.getClosestIntersection(latitude, longitude, idIntersection);
	}
	
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
