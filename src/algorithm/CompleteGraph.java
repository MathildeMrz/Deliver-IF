package algorithm;

import model.Intersection;
import model.Plan;

import java.util.List;
import java.util.ArrayList;

public class CompleteGraph implements Graph {
	private static final int MAX_COST = 40;
	private static final int MIN_COST = 10;
	int nbVertices;
	double [][] cost;
	private static List <Intersection> Intersection;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 * @param nbVertices
	 */
	public CompleteGraph(int nbVertices, List <Intersection> intersection, Plan lePlan){
		this.nbVertices = nbVertices;
		this.Intersection=intersection;
		//int iseed = 1;
		cost = new double[nbVertices][nbVertices];
		for (int i=0; i<nbVertices; i++){
		    for (int j=0; j<nbVertices; j++){
		        if (i == j) cost[i][j] = -1;
		        else {
		            /*int it = 16807 * (iseed % 127773) - 2836 * (iseed / 127773);
		            if (it > 0)	iseed = it;
		            else iseed = 2147483647 + it;
		            cost[i][j] = MIN_COST + iseed % (MAX_COST-MIN_COST+1);*/
		        	Dijkstra djikstra= new Dijkstra(lePlan,Intersection.get(i)) ;
		        	
		        	System.out.println("Debut de djikstra");
		        	djikstra.run();
		        	System.out.println("Fin de djikstra");
		        	cost[i][j] = djikstra.getCoutIntersection(Intersection.get(j).getId());
		        }
		    }
		}
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public double getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1.0;
		return cost[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}

}
