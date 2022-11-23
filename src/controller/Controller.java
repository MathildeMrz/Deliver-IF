package controller;

import java.util.ArrayList;

import model.Courier;
import model.Plan;

public class Controller {
	private Plan plan;
	private Window window;
	private ArrayList<Courier> listOfCouriers;
	
	public Controller(Plan plan) {
		this.plan = plan;
		listOfCouriers = new ArrayList<Courier>();
		window = new Window(plan, this);
	}
	
}
