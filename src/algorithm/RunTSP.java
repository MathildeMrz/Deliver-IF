package algorithm;

import java.util.List;
import java.util.ArrayList;

import model.Intersection;
import model.Plan;

public class RunTSP {
	
	private static int nbVertices;
	private static List <Intersection> Intersection=new ArrayList<Intersection>();
	private static Plan lePlan;
	
	
	public RunTSP(int nbVertices, List<Intersection> intersection,Plan lePlan) {
		this.nbVertices = nbVertices;
		Intersection = intersection;
		this.lePlan=lePlan;
	}



	public static void main(String[] args) {
		TemplateTSP tsp = new TSP1();
		for (int nbVertices = 8; nbVertices <= 16; nbVertices += 2){
			System.out.println("Graphs with "+nbVertices+" vertices:");
			Graph g = new CompleteGraph(nbVertices,Intersection,lePlan);
			long startTime = System.currentTimeMillis();
			tsp.searchSolution(20000, g);
			System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
					+(System.currentTimeMillis() - startTime)+"ms : ");
			for (int i=0; i<nbVertices; i++)
				System.out.print(tsp.getSolution(i)+" ");
			System.out.println("0");
		}
	}

}
