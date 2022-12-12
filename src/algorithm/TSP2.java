package algorithm;

import java.util.Collection;
import java.util.Iterator;

public class TSP2 extends TSP1 {
	

	@Override
	protected double bound(Integer currentVertex, Collection<Integer> unvisited, double cost[][],double currentCost,int [] timeLapsStart,int [] timeLapsEnd) {
		
		//Récupérer le cout minimal parmis les couts du sommet courant vers les sommets non visités
		
		//System.out.println("JE SUIS TSP2 TQT");
		double minCurrentPoint = Double.MAX_VALUE;
		for(Integer i : unvisited) {
			if(cost[currentVertex][i] <minCurrentPoint) {
				minCurrentPoint = cost[currentVertex][i];
			}
		}
		double bound = minCurrentPoint;
		
		return bound;
	}
	
	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g, double cost [][]) {
		return new IteratorMin(unvisited, cost, currentVertex, g);
	}
}
	


