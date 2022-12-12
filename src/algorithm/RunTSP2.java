package algorithm;

import java.util.List;
import model.Segment;
import model.Tour;
import observer.Observable;

import java.util.ArrayList;

import model.Intersection;
import model.Map;

public class RunTSP2 extends Observable {
	
	private static int nbVertices;
	private static List <Intersection> Intersection=new ArrayList<Intersection>();
	private static Map lePlan;
	private Tour tour;
	private Intersection warehouse;
	
	
	public RunTSP2(Intersection warehouse, int nbVertices, List<Intersection> intersection,Map lePlan, Tour tour) {
		this.nbVertices = nbVertices;
		Intersection = intersection;
		this.lePlan=lePlan;
		this.tour = tour;
		this.tour.clearTourSteps();
		this.warehouse=warehouse;
	}



	public void start() {
		//TemplateTSP2 tsp = new TSP2(); //après je vais copier le code dans TSP2 donc onsef
		TemplateTSP tsp = new TSP3();
		/*for (int nbVertices = 8; nbVertices <= 16; nbVertices += 2){*/
			Graph g = new CompleteGraph2(this.warehouse,nbVertices,tour,lePlan);
			long startTime = System.currentTimeMillis();
			tsp.searchSolution(20000, g);
			System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
					+(System.currentTimeMillis() - startTime)+"ms : ");
			for (int i=0; i<nbVertices; i++)
				System.out.print(tsp.getSolution(i)+" ");
			System.out.println("0");
			
			
			/*imprimer l'itinéraire*/
			tour.addDeliveryToOrderedDeliveries(Intersection.get(tsp.getSolution(0)));
			for(int i=1; i<nbVertices; i++)
			{
				List <Segment> steps= g.getPath(tsp.getSolution(i-1),tsp.getSolution(i)).getPath();
				for(Segment s: steps)
				{
					tour.addDeliveryToOrderedDeliveries(s.getDestination());		
				}
			}
			//dernière destination vers le warehouse
			List <Segment> steps= g.getPath(tsp.getSolution(nbVertices-1),0).getPath();
			for(Segment s: steps)
			{
				tour.addDeliveryToOrderedDeliveries(s.getDestination());	
			}
			
			//NEW: ADD TIME OF ARRIVALS TO DELIVERY POINTS
			tour.initArrivals();
			System.out.println("Tour : "+tour.toString());
			System.out.println("nbVertices : "+nbVertices);
			for(int i=1; i<nbVertices; i++)
			{
				/*calcul de la durée*/
				//tour.setArrival(this.Intersection.get(tsp.getSolution(i)),i,g.getCost(tsp.getSolution(i-1),tsp.getSolution(i))*0.004);
				tour.setArrival(this.Intersection.get(tsp.getSolution(i)),i,g.getCost(tsp.getSolution(i-1),tsp.getSolution(i))/60);
			}
			tour.setArrival(this.Intersection.get(tsp.getSolution(0)),nbVertices,g.getCost(tsp.getSolution(nbVertices-1),tsp.getSolution(0))/60);
			//END NEW
			
		/*}*/
	}


}

