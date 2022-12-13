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

	// Contient les intersection associees a leur antecedent
	private HashMap<Long, Intersection> pi;
	// Contient les id des intersections associees a leur cout
	private HashMap<Long, Double> cout;

	// Contient les intersections blanches, associees a leur cout
	private HashMap<Long, Double> intersectionsBlanches;
	// Contient les intersections grises, associees a leur cout
	private HashMap<Long, Double> intersectionsGrises;
	// Inverse la map intersectionsGrises
	private HashMap<Double, List<Long>> intersectionsGrisesInversees;

	// L'objet Plan de notre cas d'etude
	private Map lePlan;
	// Intersection d'ou l'on part
	private Intersection ptDepart;

	/**
	 * Initialise le plan et le graph à parcourir avant de lancer l'algorithme de plus courts chemins()
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
		
        //récupérer toutes les intersections du plan pour en faire 'un graphe'
		Collection<Intersection> c = lePlan.getNodes() .values();
		// On ajoute les intersections dans les differentes Hashmap
        // On initialise le cout à max comme on veut minimiser le cout
		for (Intersection intersection : c) {
			pi.put(intersection.getId(), null);
			cout.put(intersection.getId(), Double.MAX_VALUE);
			intersectionsBlanches.put(intersection.getId(), Double.MAX_VALUE);
		}

		// On met le point de départ dans les maps grises et on l'enleve de la map
		// intersectionsBlanches
		cout.put(pointDepart.getId(), 0.0);
		intersectionsBlanches.remove(pointDepart.getId());
		intersectionsGrises.put(pointDepart.getId(), 0.0);
		List<Long> aAjouter = new ArrayList<Long>();
		aAjouter.add(pointDepart.getId());
		intersectionsGrisesInversees.put(0.0, aAjouter);

	}

	/**
	 * Execute l'algorithme de Dijkstra
	 */
	public void run() {
		// On continue tant que l'on a des intersections grises
		while (intersectionsGrises.values().size() != 0) {
			double min = Collections.min(intersectionsGrises.values());
            /*on prend le noeud de cout minimal */
			Long idMin = intersectionsGrisesInversees.get(min).get(0); //on prend son id
			
			Intersection lIntersection = lePlan.getNodes().get(idMin);

			// On visite tous les successeurs du point courant
			for (Segment t : lIntersection.getOutSections()) {

				// On n'agit que sur les intersections blanches ou grises
				if (intersectionsBlanches.containsKey(t.getDestination().getId()) || intersectionsGrises.containsKey(t.getDestination().getId())) {

					// On relache l'arc en le point courant et le successeur que l'on est en train
					// de visiter
					relacher(lIntersection, t.getDestination());
					// On colorie le nouveau sommet visite en gris
					if (intersectionsBlanches.containsKey(t.getDestination().getId())) {
						// On l'ajoute dans la map "a l'endroit"
						intersectionsGrises.put(t.getDestination().getId(),cout.get(t.getDestination().getId()));

						// On l'ajoute à la map inverse, en verifiant si la valeur de cout existe
						if (intersectionsGrisesInversees.containsKey(cout.get(t.getDestination().getId()))) {
							intersectionsGrisesInversees.get(cout.get(t.getDestination().getId())).add(t.getDestination().getId());
						} else {
							List<Long> aAjouter = new ArrayList<Long>();
							aAjouter.add(t.getDestination().getId());
							intersectionsGrisesInversees.put(cout.get(t.getDestination().getId()), aAjouter);
						}

						// On enleve l'intersection des intersections blanches, vu qu'elle vient d'etre
						// coloriee en grise
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

			// Il faut maintenant ajouter le sommet aux intersections noires (ici, il suffit
			// qu'il ne soit dans aucune map contenant les intersections grises ou blanches
			intersectionsGrises.remove(idMin);

			if (intersectionsGrisesInversees.get(min).size() != 1) {
				intersectionsGrisesInversees.get(min).remove(idMin);
			} else {
				intersectionsGrisesInversees.remove(min);
			}
		}

	}

	/**
	 * Execute le relachement d'un arc : vérifier si son cout est bien minimal ou pas
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
		
		if((si.getId() == 1) ) {
			System.out.print("");
		}
		
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
	 * Obtenir le chemin depuis le point de départ (attribut de la classe) jusqu'à l'intersection d'id idDestination
	 *  @param idDestination : id de l'Intersection de destination
	 *  @return l'itinéraire entre l'Intersection de départ l'Intersection de destination
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
				// On verifie qu'il n'y a pas eu de probleme et que l'on a bien un nouveau
				// segment
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

