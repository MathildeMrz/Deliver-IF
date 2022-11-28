package controller;

import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Courier;
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

	public void addDelivery(float latitude, float longitude) {
		tour.addDelivery(latitude, longitude);
	}
}
