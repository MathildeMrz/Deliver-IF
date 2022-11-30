package algorithm;

import java.util.List;
import model.Segment;
import model.Tour;
import observer.Observable;

import java.util.ArrayList;

import model.Intersection;
import model.Map;

public class RunTSP extends Observable {
	
	private static int nbVertices;
	private static List <Intersection> Intersection=new ArrayList<Intersection>();
	private static Map lePlan;
	private Tour tour;
	
	
	public RunTSP(int nbVertices, List<Intersection> intersection,Map lePlan, Tour tour) {
		this.nbVertices = nbVertices;
		Intersection = intersection;
		this.lePlan=lePlan;
		this.tour = tour;
	}



	public void start() {
		TemplateTSP tsp = new TSP1();
		/*for (int nbVertices = 8; nbVertices <= 16; nbVertices += 2){*/
			Graph g = new CompleteGraph(nbVertices,Intersection,lePlan);
			long startTime = System.currentTimeMillis();
			tsp.searchSolution(20000, g);
			System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
					+(System.currentTimeMillis() - startTime)+"ms : ");
			for (int i=0; i<nbVertices; i++)
				System.out.print(tsp.getSolution(i)+" ");
			System.out.println("0");
			
			/*imprimer l'itinéraire*/
			tour.addTourSteps(Intersection.get(tsp.getSolution(0)));
			for(int i=1; i<nbVertices; i++)
			{
				List <Segment> steps= g.getPath(tsp.getSolution(i-1),tsp.getSolution(i)).getPath();
				for(Segment s: steps)
				{
					tour.addTourSteps(s.getDestination());		
				}
			}
			//dernière destination vers le warehouse
			List <Segment> steps= g.getPath(tsp.getSolution(nbVertices-1),0).getPath();
			for(Segment s: steps)
			{
				tour.addTourSteps(s.getDestination());	
			}
			
		/*}*/
	}


}
