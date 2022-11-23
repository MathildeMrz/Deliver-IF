package model;

import controller.Controller;
import view.mapView;
import view.newRequestView;

public class window {
	
	private newRequestView newRequestView;
	private mapView mapView;

	public window(Plan plan, Controller controller)
	{
		newRequestView = new newRequestView();
		mapView = new mapView();
	}
}
