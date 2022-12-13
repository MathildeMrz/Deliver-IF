package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import algorithm.RunTSP2;
import javafx.stage.Stage;
import model.Courier;
import model.Delivery;
import model.Intersection;
import model.Map;
import model.Tour;

public class ControllerAddDelivery {
	private Map map;	

	public ControllerAddDelivery(Stage stage, Map map)
	{
		this.map= map;
	}

	public Delivery addDelivery(Intersection closerIntersection, LocalDate date, int timeWindow, Courier courier) {
		return courier.getTour().addDelivery(closerIntersection, date, timeWindow);
	}
	
	public void TSP(Tour tour) {
		List<Intersection> sommets = new ArrayList<Intersection>();
		sommets.add(map.getWarehouse());
		int debut =12;
		for (Delivery d : tour.getDeliveries()) {
			sommets.add(d.getDestination());
			/*System.out.print(d.getDestination().getId()+ " ");
			ArrayList <Segment> t= (d.getDestination().getOutSections());
			System.out.println("les outsections de ce point :");
			if(t.size()!=0) {
				for(Segment s:t) {
					System.out.print(s.getDestination().getId()+ " ");
				}
			}*/
			System.out.println();
			if(d.getStartTime()<debut) {
				debut=d.getStartTime();
			}
		}
		LocalDate tourStartDate=tour.getStartDate().toLocalDate();
		tour.setStartDate(tourStartDate.atTime(debut,0,0));
		System.out.println("Debut de la tournée à" + tour.getStartDate());
		System.out.println("Debut TSP");
		RunTSP2 testTSP = new RunTSP2(map.getWarehouse(),tour.getDeliveries().size()+1, sommets, map, tour);
		testTSP.start();
		System.out.println("Fin TSP");
	}
}
