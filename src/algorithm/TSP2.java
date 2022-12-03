package algorithm;

import java.util.ArrayList;
import java.util.Collection;

public class TSP2 extends TSP1 {
	

	@Override
	protected double bound(Integer currentVertex, Collection<Integer> unvisited, double cost[][]) {
		
		//Récupérer le cout minimal sur les sommets non visités
		
		double minCurrentPoint = Double.MAX_VALUE;
		for(Integer i : unvisited) {
			if(cost[currentVertex][i] <minCurrentPoint) {
				minCurrentPoint = cost[currentVertex][i];
			}
		}
		double bound = minCurrentPoint;
		
		return bound;
	}
}
	


