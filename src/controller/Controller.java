package controller;

import java.util.ArrayList;

import model.Courier;
import model.Plan;
import view.mapView;
import view.newRequestView;

public class Controller {
	private Plan plan;
	private newRequestView newRequestView;
	private mapView mapView;
	private ArrayList<Courier> listOfCouriers;
	
	public Controller(Plan plan) {
		this.plan = plan;
		listOfCouriers = new ArrayList<Courier>();
		newRequestView = new newRequestView();
		mapView = new mapView();
	}
}
