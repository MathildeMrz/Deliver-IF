package algorithm;

import java.util.List;
import model.Segment;
import java.util.ArrayList;

import model.Intersection;
import model.Map;

public class RunTSP {
	
	private static int nbVertices;
	private static List <Intersection> Intersection=new ArrayList<Intersection>();
	private static Map lePlan;
	
	
	public RunTSP(int nbVertices, List<Intersection> intersection,Map lePlan) {
		this.nbVertices = nbVertices;
		Intersection = intersection;
		this.lePlan=lePlan;
	}



	public static void start() {
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
			ArrayList <Intersection> intersection_steps=new ArrayList <Intersection>();
			intersection_steps.add(Intersection.get(tsp.getSolution(0)));
			for(int i=1; i<nbVertices; i++)
			{
				List <Segment> steps= g.getPath(tsp.getSolution(i-1),tsp.getSolution(i)).getPath();
				for(Segment s: steps)
				{
					System.out.print(s.getDestination().getId()+ " ");
					intersection_steps.add(s.getDestination());
					
				}
			}
			//dernière destination vers le warehouse
			List <Segment> steps= g.getPath(tsp.getSolution(nbVertices-1),0).getPath();
			for(Segment s: steps)
			{
				System.out.println(s.getDestination().getId()+ " ");
				intersection_steps.add(s.getDestination());
			}
			System.out.println();
			
		/*}*/
	}

}
