package algorithm;

import model.Intersection;
import model.Segment;
import model.Path;
import model.Map;

import java.util.List;
import java.util.ArrayList;

public class CompleteGraph implements Graph {
	int nbVertices;
	double [][] cost;
	Path [][] path;
	private static List <Intersection> Intersection;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 * @param nbVertices
	 */
	public CompleteGraph(int nbVertices, List <Intersection> intersection, Map lePlan){
		this.nbVertices = nbVertices;
		this.Intersection=intersection;

		cost = new double[nbVertices][nbVertices];
		path= new Path[nbVertices][nbVertices];
		for (int i=0; i<nbVertices; i++){
		    for (int j=0; j<nbVertices; j++){
		        if (i == j) cost[i][j] = -1;
		        else {
		        	Dijkstra djikstra= new Dijkstra(lePlan,this.Intersection.get(i)) ;
		        	System.out.println("Debut Dijkstra");
		        	djikstra.run();
		        	System.out.println("Fin Dijkstra");
		        	cost[i][j] = djikstra.getCoutIntersection(this.Intersection.get(j).getId());
		        	System.out.println("cost[i][j] : "+cost[i][j]);
		        	
		        	/*enregistrer l'itineraire*/
		        	path[i][j]=djikstra.getItinerary(this.Intersection.get(j).getId());
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
	public Path getPath(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return null;
		return path[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}

}
