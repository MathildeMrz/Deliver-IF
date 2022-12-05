package algorithm;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class IteratorMin implements Iterator<Integer> {
	
	//private Integer[] candidates;
	private ArrayList<Integer> candidates= new ArrayList<Integer>();
	private int nbCandidates;
	private double [][] cost;
	private Integer currentNode;
	
	
	public IteratorMin(Collection<Integer> unvisited, double[][] cost, Integer currentNode,Graph g) {
		//this.candidates = candidates;
		this.cost = cost;
		this.currentNode = currentNode;
		
		/*Compléter le tableau des candidats*/
		//int i=0;
		
		//bien que je l'ai initialisé avant mais il veut pas...
		candidates= new ArrayList<Integer>();
		for(Integer c:unvisited)
		{
			if (g.isArc(currentNode, c)) {
			
		
				candidates.add(c);
				//System.out.println("j'ai add :"+c);
				//++i;
			}
		}
		nbCandidates=candidates.size();
		//System.out.println("taille de candidates :"+nbCandidates);
	}

	
	@Override
    public Integer next() {
		//Initialiser la valeur du candidat minimal
		//System.out.println("taille de candidates dans next :"+candidates.size());
        Integer min = candidates.get(0);
        //System.out.println("j'ai pris le premier :"+ min);
        int index = 0;

       /* if(nbCandidates >1)
        {*/
        	for(int i = 1; i < candidates.size(); i++) { //ne pas utiliser la taille fixe nbCandidates car à cause du remove la taille change
            	//if(candidates[i]!=-1) {
                    if(cost[currentNode][candidates.get(i)] < cost[currentNode][min]) {
                            min = candidates.get(i);
                            index = i;
                    }
            	//}
            }
            candidates.remove(index);
       // }
        return min;
    }

    @Override
    public void remove() {}


	@Override
	public boolean hasNext() {
		return candidates.size() > 0;
	}

}
