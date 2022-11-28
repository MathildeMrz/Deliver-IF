package controller;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Courier;
import model.Map;
import view.mapView;
import view.newRequestView;

public class Controller {
	private Map plan;	
	private ListView<Courier> couriers;
	private Stage stage;
	
	public Controller(Stage stage, Map plan, ListView<Courier> couriers)
	{
		this.plan= plan;
		this.stage = stage;
		this.couriers = couriers;
	}

	public void newDeliveryToAdd(float y, float x) {
		//
	}
}
