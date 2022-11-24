package controller;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Courier;
import model.Plan;
import view.mapView;
import view.newRequestView;

public class Controller {
	private Plan plan;	
	private ListView<Courier> couriers;
	private Stage stage;
	
	public Controller(Stage stage, Plan plan, ListView<Courier> couriers)
	{
		this.plan= plan;
		this.stage = stage;
		this.couriers = couriers;
	}

	public void newPositionToAdd(float y, float x) {
		plan.addPosition(y, x);
	}
}
