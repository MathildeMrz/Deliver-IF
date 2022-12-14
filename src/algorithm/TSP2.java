package algorithm;

import java.util.Collection;
import java.util.Iterator;

public class TSP2 extends TSP1 {
	
	@Override
	protected double bound(Integer currentVertex, Collection<Integer> unvisited, double cost[][],double currentCost,int [] timeLapsStart,int [] timeLapsEnd) {
		
		//Take the minimal costs between the unvisited nodes
		
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
	


