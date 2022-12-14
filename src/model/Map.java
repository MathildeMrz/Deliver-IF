package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import observer.Observable;
import java.time.temporal.*;
import java.lang.Math;


public class Map extends Observable {
	private HashMap<Long, Intersection> nodes;
	private Intersection warehouse;
	private ArrayList<Courier> couriers;
	private boolean isLoaded;
	private float latitudeMin;
	private float latitudeMax;
	private float longitudeMin;
	private float longitudeMax;	
	private LocalDate mapDate;
	
	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	private String mapName;
	
	/**
	 * Create a Map
	 * */
	public Map() {
		this.nodes  = new HashMap<Long,Intersection>();
		this.couriers = new ArrayList<Courier>();//During load function
		this.warehouse = null;
		this.isLoaded = false;
		this.latitudeMin = Float.MAX_VALUE;
		this.latitudeMax = 0;
		this.longitudeMin = Float.MAX_VALUE;
		this.longitudeMax = 0;
		this.mapDate = LocalDate.now();
	}
	
	/**
	 * Reset all map attribute in order to load a new map
	 */
	public void resetMap() {
		this.nodes.clear();
		this.latitudeMin = Float.MAX_VALUE;
		this.latitudeMax = 0;
		this.longitudeMin = Float.MAX_VALUE;
		this.longitudeMax = 0;
		this.isLoaded = false;
	}

	public void resetNodes() {
		this.nodes.clear();
	}
	
	public void addWarehouse(Long intersectionID) {
		this.warehouse = this.nodes.get(intersectionID);
	}
	
	public void addNode(Intersection node) {
		nodes.put(node.getId(), node);
	}

	public HashMap<Long, Intersection> getNodes() {
		return nodes;
	}
	
	public ArrayList<Courier> getCouriers() {
		return couriers;
	}
	
	public LocalDate getMapDate() {
		return mapDate;
	}

	public void setMapDate(LocalDate mapDate) {
		this.mapDate = mapDate;
	}
	
	public void setCouriers(ArrayList<Courier> couriers) {
		this.couriers = couriers;
	}
	
	public void addCourier(Courier courier) {
		this.couriers.add(courier);
	}

	public Intersection getWarehouse() {
		return warehouse;
	}

	@Override
	public String toString() {
		return "Plan [nodes=" + nodes + ", warehouse=" + warehouse + "]";
	}
	
	public float getLatitudeMin() {
		return latitudeMin;
	}

	public void setLatitudeMin(float latitudeMin) {
		this.latitudeMin = latitudeMin;
	}

	public float getLatitudeMax() {
		return latitudeMax;
	}

	public void setLatitudeMax(float latitudeMax) {
		this.latitudeMax = latitudeMax;
	}

	public float getLongitudeMin() {
		return longitudeMin;
	}

	public void setLongitudeMin(float longitudeMin) {
		this.longitudeMin = longitudeMin;
	}

	public float getLongitudeMax() {
		return longitudeMax;
	}

	public void setLongitudeMax(float longitudeMax) {
		this.longitudeMax = longitudeMax;
	}

	public boolean getIsLoaded() {
		return isLoaded;
	}
	
	public void setIsLoaded(boolean loaded) {
		this.isLoaded = loaded;
	}

	public void setMapLoaded() {
		this.isLoaded = true;
	}
	
	/**
	 * return the closest intersection of the map from coordinates in parameters
	 * @param latitude : latitude of the point
	 * @param longitude	: longitude of the point
	 * @return the closest intersection
	 */
	public Intersection getClosestIntersection(float latitude, float longitude, long idIntersection)
	{
		float minimumDistance = Float.MAX_VALUE;
		Intersection closerIntersection = null;
		for(Intersection i : this.nodes.values())
	     {
			if(i.getId()!=idIntersection) {
				
			 float dist = (float) Math.sqrt((i.getLatitude() - latitude) * (i.getLatitude() - latitude) + (i.getLongitude() - longitude) * (i.getLongitude() - longitude));
	    	 
	    	 if(dist < minimumDistance)
	    	 {
	    		 minimumDistance = dist;
	    		 closerIntersection = i;
	    	 }
			}
	     }
		return closerIntersection;
	}
/**
 * Determine the best courier for a delivery based on the waiting time and number of deliveries the courriers have
 * @param inter : the intersection where the delivery would be
 * @param timeWindow : the hour of the start of the timeWindow for the delivery
 * @return the best courier for the delivery based on availability 
 */
	
	//TODO documentation
	public Courier getBestCourierAvalaibility(Intersection inter, int timeWindow) {
	   	 Courier bestCourier = this.couriers.get(0); //the best courier for the delivery found so far
	   	 int freeTime = 0; // the "free time" (= waiting time) the best courier has during the time window
	   	 for(Courier c : this.couriers) { // loop over the couriers
	   		 for(Delivery del : c.getTour().getDeliveries()) { // loop over their deliveries
	   			 if((del.getStartTime() == timeWindow + 1) && (del.getArrival().until(del.getDeliveryTime(), ChronoUnit.MINUTES) > freeTime)) {
	   				 //check if the courier has more free time during the timeWindow than the maximum determined so far
	   				 //update the bestCourier and their freetime 
	   				 bestCourier = c;
	   				 freeTime = (int) del.getArrival().until(del.getDeliveryTime(), ChronoUnit.MINUTES);
	   			 }
	   		 }
	   	 }
	   	 if(freeTime != 0) 
	   	 {return bestCourier;}
	   	 else {// if no courrier has any free time, we pick the one that has the lowest number of deliveries during their tour while having at least one
	   		 int nbMinDelivery = 100; // Number much bigger than the number of deliveries a courier can have during a tour
	   		 for(Courier c: this.couriers) { //loop over the couriers
	   			 int nbDelivery = c.getTour().getDeliveries().size();
	   			 if(nbDelivery > 0 && nbDelivery < nbMinDelivery) {
	   				 // check if the courier has less delivery than the best one so far
	   				 // if yes, update the best courier and the min number of deliveries
	   				 nbMinDelivery = nbDelivery;
	   				 bestCourier = c;
	   			 }
	   		 }
	   	 }
	   	 return bestCourier;
	    }
	    
	/**
	 * Determine the best courier for a delivery based on proximity, i.e the courier whose tour goes closest to the delivery point
	 * @param inter : the intersection where the delivery would be
	 * @param timeWindow : the hour of the start of the timeWindow for the delivery
	 * @return the best courier for the delivery based on proximity
	 */	
	    public Courier getBestCourierProximity(Intersection inter, int timeWindow) {
	   	 Courier bestCourier = couriers.get(0); // courier that goes closest to the intersection
	   	 Float distMin = Float.MAX_VALUE; // distance to the intersection
	   	 for(Courier c: this.couriers) { // loop over the couriers
	   		 for(Intersection i: c.getTour().getTourSteps()) { //loop over the steps of the courier's tour
	   			 float dist = (float) Math.hypot(Math.abs(inter.getLongitude() - i.getLongitude()), Math.abs(inter.getLatitude() - i.getLatitude())); // calculate the euclidian distance of the intersection with the delivery point
	   			 if(dist<distMin) { // if the tour pass by an intersection that is closer to the delivery point than the current best, we update the minimal distance and the best courier
	   				 distMin = dist;
	   				 bestCourier = c;
	   			 }
	   		 }
	   	 }
	   	 return bestCourier;
	    }
}
