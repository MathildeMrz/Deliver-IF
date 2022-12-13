package controller;

import java.time.LocalDate;

import javafx.scene.control.ListView;
import model.Courier;
import model.Delivery;
import model.Intersection;
import model.Map;

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

}
