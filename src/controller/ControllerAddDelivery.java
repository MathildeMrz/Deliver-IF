package controller;

import java.time.LocalDate;

import javafx.stage.Stage;
import model.Courier;
import model.Delivery;
import model.Intersection;
import model.Map;

public class ControllerAddDelivery {
	private Map map;	

	public ControllerAddDelivery(Stage stage, Map map)
	{
		this.map= map;
	}

	public Delivery addDelivery(Intersection closerIntersection, LocalDate date, int timeWindow, Courier courier) {
		return courier.getTour().addDelivery(closerIntersection, date, timeWindow);
	}
}
