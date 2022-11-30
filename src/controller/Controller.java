package controller;

import java.time.LocalDate;

import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Courier;
import model.Intersection;
import model.Tour;

public class Controller {
	private Tour tour;	

	
	public Controller(Stage stage, Tour tour, ListView<Courier> couriers)
	{
		this.tour= tour;
	}

	public void addDelivery(Intersection closerIntersection, LocalDate date, int timeWindow) {
		System.out.println("CCCCCCCCCCCCCCCCCCCC");
		System.out.println("closerIntersection "+closerIntersection+" date "+date+" timeWindow "+timeWindow);
		tour.addDelivery(closerIntersection, date, timeWindow);
		System.out.println("DDDDDDDDDDDDDD");
	}
}
