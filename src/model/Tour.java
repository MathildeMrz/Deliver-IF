package model;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Tour extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private ArrayList<Delivery> steps;
	private Date startDate;
	private Date endDate;
	private Path intersections;
	private Courier courier;
	private Map map;
	private float widthMap;
	private float heightMap;
	
	public Tour(ArrayList<Delivery> steps, Date startDate, Date endDate, Path intersections, Courier courier, Map map)
	{
		this.id = ID_FACTORY.getAndIncrement();
		steps = new ArrayList<Delivery>();
		this.startDate = startDate;
		this.endDate = endDate;
		this.intersections = intersections;
		this.courier = courier;
		this.map = map;
		this.widthMap = map.getLongitudeMax() - map.getLongitudeMin();
		this.heightMap = map.getLatitudeMax() - map.getLatitudeMin();	
	}
	
	public Tour(Map map)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.map = map;
		this.widthMap = map.getLongitudeMax() - map.getLongitudeMin();
		this.heightMap = map.getLatitudeMax() - map.getLatitudeMin();	
		steps = new ArrayList<Delivery>();
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setSteps(ArrayList<Delivery> steps) {
		this.steps = steps;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setIntersections(Path intersections) {
		this.intersections = intersections;
	}

	public void setCourier(Courier courier) {
		this.courier = courier;
	}

	public void setWidthMap(float widthMap) {
		this.widthMap = widthMap;
	}

	public void setHeightMap(float heightMap) {
		this.heightMap = heightMap;
	}

	public ArrayList<Delivery> getSteps() {
		return steps;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Path getIntersections() {
		return intersections;
	}

	public Courier getCourier() {
		return courier;
	}

	@Override
	public String toString() {
		return "Tour [id=" + id + ", steps=" + steps + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", intersections=" + intersections + ", courier=" + courier + "]";
	}
	
	public float getFactorLongitudeToX(float longitude) {
		return (longitude - map.getLongitudeMin()) / this.widthMap;
		
	}
	
	public float getFactorLatitudeToY(float latitude) {
		return (latitude - map.getLatitudeMin()) / this.heightMap; 
	}
	
	public float getFactorXToLongitude(float x) {
		return (x * this.widthMap) + map.getLongitudeMin(); 
	}
	
	public float getFactorYToLatitude(float y) {
		return (y * this.heightMap) + map.getLatitudeMin();  
	}

	
	public float getWidthMap() {
		return widthMap;
	}

	public float getHeightMap() {
		return heightMap;
	}

	public void addDelivery(float latitude, float longitude)
	{
		//Translate pixel coordinates to street coordinates
	     GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();	
	     
	     Intersection closer = null;
	     float minimumDistance = Float.MAX_VALUE;
	     //select the intersection closer
	     for(Intersection i : map.getNodes().values())
	     {
			 float dist = (float) Math.sqrt((i.getLatitude() - latitude) * (i.getLatitude() - latitude) + (i.getLongitude() - longitude) * (i.getLongitude() - longitude));
	    	 
	    	 if(dist < minimumDistance)
	    	 {
	    		 minimumDistance = dist;
	    		 closer = i;
	    	 }
	     }
	    Delivery delivery = new Delivery("test", 8, closer);
	    steps.add(delivery);
		notifyObservers();
	}	
	
}
