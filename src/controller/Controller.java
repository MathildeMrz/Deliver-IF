package controller;

import java.time.LocalDate;
import java.util.Date;

import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Courier;
import model.Intersection;
import model.Map;
import model.Tour;

public class Controller {
	private Tour tour;	
	private ListView<Courier> couriers;
	private Stage stage;
	
	public Controller(Stage stage, Tour tour, ListView<Courier> couriers)
	{
		this.tour= tour;
		this.stage = stage;
		this.couriers = couriers;
	}

	public void addDelivery(Intersection closer, LocalDate date, int timeWindow) {
		tour.addDelivery(closer, date, timeWindow);
	}
}
