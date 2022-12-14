package algorithm;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class IteratorMin implements Iterator<Integer> {
	
	private ArrayList<Integer> candidates= new ArrayList<Integer>();
	private double [][] cost;
	private Integer currentNode;
	
	/**
	 * Create an iterator that iterates to the candidate in a way to minimize the cost
	 * @param unvisited : unvisited nodes by the TSP
	 * @param cost : table of costs
	 * @param currentNode : current node in the TSP
	 * @param g : the graph used by the TSP
	 * */
	public IteratorMin(Collection<Integer> unvisited, double[][] cost, Integer currentNode,Graph g) {
		this.cost = cost;
		this.currentNode = currentNode;
		
		candidates= new ArrayList<Integer>();
		for(Integer c:unvisited)
		{
			if (g.isArc(currentNode, c)) {
				candidates.add(c);
			}
		}
	}

	
	@Override
    public Integer next() {
		//Initialiser la valeur du candidat minimal
        Integer min = candidates.get(0);
        int index = 0;

        	for(int i = 1; i < candidates.size(); i++) { 
                if(cost[currentNode][candidates.get(i)] < cost[currentNode][min]) {
                        min = candidates.get(i);
                        index = i;
                }
            }
            candidates.remove(index);
        return min;
    }

    @Override
    public void remove() {}


	@Override
	public boolean hasNext() {
		return candidates.size() > 0;
	}
}
