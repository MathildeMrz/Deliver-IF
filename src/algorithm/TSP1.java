package algorithm;

import java.util.Collection;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {
	@Override
	protected double bound(Integer currentVertex, Collection<Integer> unvisited, double cost[][],double currentCost,int [] timeLapsStart,int [] timeLapsEnd) {
		return 0;
	}

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g, double cost [][]) {
		return new SeqIter(unvisited, currentVertex, g);
	}

}
