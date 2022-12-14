package algorithm;

import model.Intersection;
import model.Map;
import model.Segment;
import model.Path;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Dijkstra {

	// Contains the intersections associated to their antecedent 
	private HashMap<Long, Intersection> pi;
	// Contains the id of intersections associated to their costs
	private HashMap<Long, Double> cout;

	// Contains the blanc intersections, associated with their costs
	private HashMap<Long, Double> intersectionsBlanches;
	// Contains the grey intersections, associated with their costs
	private HashMap<Long, Double> intersectionsGrises;
	// Reverse the map intersectionsGrises
	private HashMap<Double, List<Long>> intersectionsGrisesInversees;

	// Object Map
	private Map lePlan;
	// Starting point
	private Intersection ptDepart;

	/**
	 * Initialize the map and the graph (starting from the attribute ptDepart) before running the algorithm 
	 * 
	 * @param lePlan
	 * @param pointDepart
	 */
	public Dijkstra(Map lePlan, Intersection pointDepart) {
		this.lePlan = lePlan;
		this.ptDepart = pointDepart;
		
		pi = new HashMap<Long,Intersection>();
		cout = new HashMap<Long,Double>();
		intersectionsBlanches = new HashMap<Long, Double>();
		intersectionsGrises = new HashMap<Long, Double>();
		intersectionsGrisesInversees = new HashMap<Double, List<Long>>();
		
		//create a graph with all the intersections of the plan
		Collection<Intersection> c = lePlan.getNodes() .values();

		//initialising the costs to MAX
		for (Intersection intersection : c) {
			pi.put(intersection.getId(), null);
			cout.put(intersection.getId(), Double.MAX_VALUE);
			intersectionsBlanches.put(intersection.getId(), Double.MAX_VALUE);
		}

		//We put the starting point in the gray map and we remove it from the blanc map
		cout.put(pointDepart.getId(), 0.0);
		intersectionsBlanches.remove(pointDepart.getId());
		intersectionsGrises.put(pointDepart.getId(), 0.0);
		List<Long> aAjouter = new ArrayList<Long>();
		aAjouter.add(pointDepart.getId());
		intersectionsGrisesInversees.put(0.0, aAjouter);
	}

	/**
	 * Execute Djikstra's algorithm
	 */
	public void run() {
		// As long as we still have grey intersections, we go on
		while (intersectionsGrises.values().size() != 0) {
			double min = Collections.min(intersectionsGrises.values());
            /*we take the node of minimal cost */
			Long idMin = intersectionsGrisesInversees.get(min).get(0); //on prend son id
			
			Intersection lIntersection = lePlan.getNodes().get(idMin);

			// we visit the successors of the current node
			for (Segment t : lIntersection.getOutSections()) {

				// We only modify grey and blanc nodes
				if (intersectionsBlanches.containsKey(t.getDestination().getId()) || intersectionsGrises.containsKey(t.getDestination().getId())) {

					
					relacher(lIntersection, t.getDestination());
			
					if (intersectionsBlanches.containsKey(t.getDestination().getId())) {
						
						intersectionsGrises.put(t.getDestination().getId(),cout.get(t.getDestination().getId()));

						
						if (intersectionsGrisesInversees.containsKey(cout.get(t.getDestination().getId()))) {
							intersectionsGrisesInversees.get(cout.get(t.getDestination().getId())).add(t.getDestination().getId());
						} else {
							List<Long> aAjouter = new ArrayList<Long>();
							aAjouter.add(t.getDestination().getId());
							intersectionsGrisesInversees.put(cout.get(t.getDestination().getId()), aAjouter);
						}

						
						intersectionsBlanches.remove(t.getDestination().getId());

					}
					else
					{
						Double nouvelleValeur = cout.get(t.getDestination().getId());
						Double ancienneValeur = intersectionsGrises.get(t.getDestination().getId());
						
						if(ancienneValeur != nouvelleValeur)
						{
							intersectionsGrises.put(t.getDestination().getId(), cout.get(t.getDestination().getId()));
							
							if (intersectionsGrisesInversees.get(ancienneValeur).size() != 1) {
								intersectionsGrisesInversees.get(ancienneValeur).remove(t.getDestination().getId());
							} else {
								intersectionsGrisesInversees.remove(ancienneValeur);
							}
							
							if (intersectionsGrisesInversees.containsKey(cout.get(t.getDestination().getId()))) {
								intersectionsGrisesInversees.get(cout.get(t.getDestination().getId())).add(t.getDestination().getId());
							} else {
								List<Long> aAjouter = new ArrayList<Long>();
								aAjouter.add(t.getDestination().getId());
								intersectionsGrisesInversees.put(cout.get(t.getDestination().getId()), aAjouter);
							}
						}
					}
				}

			}

	
			intersectionsGrises.remove(idMin);

			if (intersectionsGrisesInversees.get(min).size() != 1) {
				intersectionsGrisesInversees.get(min).remove(idMin);
			} else {
				intersectionsGrisesInversees.remove(min);
			}
		}

	}

	/**
	 * Verify if the cost assigned to an arc is actually minimized, otherwise change it
	 */
	public void relacher(Intersection si, Intersection sj) {

		List<Segment> lesSegments = si.getOutSections();
		Segment leSegment = null;
		
		for (Segment t : lesSegments) {
			if (t.getDestination().getId() == sj.getId()) {
				if(leSegment == null) {
					leSegment = t;					
				}
				else if(t.getLength() < leSegment.getLength()) {
					leSegment = t;
				}

			}
		}
		
		Double valeurATester = cout.get(si.getId()) + leSegment.getLength();
			
		if(cout.get(sj.getId()) == Double.MAX_VALUE) {
			cout.put(sj.getId(), valeurATester);
			pi.put(sj.getId(), si);
		}
		else if (cout.get(sj.getId()) > valeurATester) {
			cout.put(sj.getId(), valeurATester);
			pi.put(sj.getId(), si);
		}
	}

	/**
	 * Get the shortest path from the start point to the destination of which we precise the id 
	 *  @param idDestination : id of the Intersection corresponding to the destination
	 *  @return the path between the start point and the destination
	 */
	public Path getItinerary(Long idDestination) {

		if (lePlan.getNodes().containsKey(idDestination)) {
			Intersection intersectionDestination = lePlan.getNodes().get(idDestination);
			Intersection intersectionCurrent = lePlan.getNodes().get(idDestination);
			List<Segment> cheminInverse = new ArrayList<Segment>();

			while (pi.get(intersectionCurrent.getId()) != null) {
				Intersection intersectionPrecedente = intersectionCurrent;
				intersectionCurrent = pi.get(intersectionCurrent.getId());

				int tailleChemin = cheminInverse.size();

				List<Segment> listeSegment = intersectionCurrent.getOutSections();
				if(listeSegment != null) {
					for (Segment t : listeSegment) {
						if (t.getDestination().getId() == intersectionPrecedente.getId()) {
							cheminInverse.add(t);
							break;
						}
					}
				}
				
				if (tailleChemin == cheminInverse.size()) {
					return null;
				}
			}
			
			if(cheminInverse.size() == 0) {
				return null;
			}
			ArrayList<Segment> chemin = new ArrayList<Segment>();
			for (int i = cheminInverse.size() - 1; i != -1; i--) {
				chemin.add(cheminInverse.get(i));
			}
			Path calculatedPath= new Path (chemin, ptDepart, intersectionDestination);
			return calculatedPath;
			
		}
		else{
			return null;	
		}
	}

	public HashMap<Long, Intersection> getPi() {
		return pi;
	}

	public Intersection getPrecedent(Long id) {
		return pi.get(id);
	}

	public void setPi(HashMap<Long, Intersection> pi) {
		this.pi = pi;
	}

	public HashMap<Long, Double> getCout() {
		return cout;
	}

	public Double getCoutIntersection(Long id) {
		return cout.get(id);
	}

	public void setCout(HashMap<Long, Double> cout) {
		this.cout = cout;
	}

	public HashMap<Long, Double> getIntersectionsBlanches() {
		return intersectionsBlanches;
	}

	public void setIntersectionsBlanches(HashMap<Long, Double> intersectionsBlanches) {
		this.intersectionsBlanches = intersectionsBlanches;
	}

	public HashMap<Long, Double> getIntersectionsGrises() {
		return intersectionsGrises;
	}

	public void setIntersectionsGrises(HashMap<Long, Double> intersectionsGrises) {
		this.intersectionsGrises = intersectionsGrises;
	}

	public HashMap<Double, List<Long>> getIntersectionsGrisesInversees() {
		return intersectionsGrisesInversees;
	}

	public void setIntersectionsGrisesInversees(HashMap<Double, List<Long>> intersectionsGrisesInversees) {
		this.intersectionsGrisesInversees = intersectionsGrisesInversees;
	}

	public Map getLePlan() {
		return lePlan;
	}

	public void setLePlan(Map lePlan) {
		this.lePlan = lePlan;
	}

	public Intersection getPtDepart() {
		return ptDepart;
	}

	public void setPtDepart(Intersection ptDepart) {
		this.ptDepart = ptDepart;
	}
}

