package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import observer.Observable;
import java.time.*;
import java.time.temporal.*;
import java.lang.Math;


public class Map extends Observable {
	private HashMap<Long, Intersection> nodes;
	private ArrayList<Intersection> destinations;
	private Intersection warehouse;
	private ArrayList<Courier> couriers;
	private boolean isLoaded;
	private float latitudeMin;
	private float latitudeMax;
	private float longitudeMin;
	private float longitudeMax;	
	private LocalDate mapDate;
	private int mapSize;
	private String mapName;

	
	public LocalDate getMapDate() {
		return mapDate;
	}

	public void setMapDate(LocalDate mapDate) {
		this.mapDate = mapDate;
	}
	
	public Map() {
		this.nodes  = new HashMap<Long,Intersection>();
		this.destinations  = new ArrayList<Intersection>();//During load function
		this.couriers = new ArrayList<Courier>();//During load function
		this.warehouse = null;
		this.isLoaded = false;
		latitudeMin = Float.MAX_VALUE;
		latitudeMax = 0;
		longitudeMin = Float.MAX_VALUE;
		longitudeMax = 0;
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
	
	public ArrayList<Intersection> getDestinations() {
		return destinations;
	}
	
	public ArrayList<Courier> getCouriers() {
		return couriers;
	}
	
	public void setCouriers(ArrayList<Courier> couriers) {
		this.couriers = couriers;
	}
	
	public void addCourier(Courier courier) {
		this.couriers.add(courier);
	}
	
	public void addDestination(Intersection intersection) {
		this.destinations.add(intersection);
	}

	public Intersection getWarehouse() {
		return warehouse;
	}

	@Override
	public String toString() {
		return "Plan [nodes=" + nodes + ", warehouse=" + warehouse + "]";
	}
	
	public void resetMap() {
		this.nodes.clear();
		this.destinations.clear();
		this.latitudeMin = Float.MAX_VALUE;
		this.latitudeMax = 0;
		this.longitudeMin = Float.MAX_VALUE;
		this.longitudeMax = 0;
		this.isLoaded = false;
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

	public void setMapLoaded() {
		this.isLoaded = true;
	}
	
	public Intersection getClosestIntersection(float latitude, float longitude)
	{
		float minimumDistance = Float.MAX_VALUE;
		Intersection closerIntersection = null;
		for(Intersection i : this.nodes.values())
	     {
			 float dist = (float) Math.sqrt((i.getLatitude() - latitude) * (i.getLatitude() - latitude) + (i.getLongitude() - longitude) * (i.getLongitude() - longitude));
	    	 
	    	 if(dist < minimumDistance)
	    	 {
	    		 minimumDistance = dist;
	    		 closerIntersection = i;
	    		 System.out.println("closerIntersection changes");
	    	 }
	     }
		return closerIntersection;
	}
	
	public Courier getBestCourierAvalaibility(Intersection inter, int timeWindow) {
	   	 Courier bestCourier = this.couriers.get(0);
	   	 int tempsLibre = 0;
	   	 for(Courier c : this.couriers) {
	   		 for(Delivery del : c.getTour().getDeliveries()) {
	   			 if((del.getStartTime() == timeWindow + 1) && (del.getArrival().until(del.getDeliveryTime(), ChronoUnit.MINUTES) > tempsLibre)) {
	   				 bestCourier = c;
	   				 tempsLibre = (int) del.getArrival().until(del.getDeliveryTime(), ChronoUnit.MINUTES);
	   			 }
	   		 }
	   	 }
	   	 if(tempsLibre != 0)
	   	 {return bestCourier;}
	   	 else {
	   		 int nbMinDelivery = 1;
	   		 for(Courier c: this.couriers) {
	   			 int nbDelivery = c.getTour().getDeliveries().size();
	   			 if(nbDelivery > 0 && nbDelivery < nbMinDelivery) {
	   				 nbMinDelivery = nbDelivery;
	   				 bestCourier = c;
	   			 }
	   		 }
	   	 }
	   	 return bestCourier;
	    }
	    
	    public Courier getBestCourierProximity(Intersection inter, int timeWindow) {
	   	 Courier bestCourier = couriers.get(0);
	   	 Float distMin = Float.MAX_VALUE;
	   	 for(Courier c: this.couriers) {
	   		 for(Intersection i: c.getTour().getTourSteps()) {
	   			 float dist = (float) Math.hypot(Math.abs(inter.getLongitude() - i.getLongitude()), Math.abs(inter.getLatitude() - i.getLatitude()));
	   			 if(dist<distMin) {
	   				 distMin = dist;
	   				 bestCourier = c;
	   			 }
	   		 }
	   	 }
	   	 return bestCourier;
	    }

	
}
