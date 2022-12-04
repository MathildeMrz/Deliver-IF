package model;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Tour extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private ArrayList<Delivery> steps;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private ArrayList<Intersection> tourSteps;
	private LocalDateTime [] tourTimes;
	private Courier courier;
	private Map map;
	private float widthMap;
	private float heightMap;
	
	public Tour(ArrayList<Delivery> steps, LocalDateTime startDate, LocalDateTime endDate, Courier courier, Map map)
	{
		this.id = ID_FACTORY.getAndIncrement();
		steps = new ArrayList<Delivery>();
		this.startDate = startDate;
		this.endDate = endDate;
		this.tourSteps = new ArrayList<Intersection>();
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
		this.tourSteps = new ArrayList<Intersection>();
	}
	
	//NEW : ADD TIME OF ARRIVALS TO DELIVERY POINTS
	public void initArrivals()
	{
		//this.tourTimes=new LocalDateTime [this.tourSteps.size()];
		this.tourTimes=new LocalDateTime [this.steps.size()+2];
		tourTimes[0]=this.startDate;
	}
	
	
	//public void setArrival(Intersection deliveryPt, double minutes)
	public void setArrival(Intersection deliveryPt, int orderOfArrival, double minutes)
	{
		//Computing of the arrival time 
		if(orderOfArrival == 1)
		{
			//minutes is a long and is rounded down so we always add 1
			this.tourTimes[orderOfArrival]=this.tourTimes[orderOfArrival-1].plusMinutes((long)(minutes+1.0));
		}
		else
		{
			//Time to perform the delivery : 5 minutes 
			this.tourTimes[orderOfArrival]=this.tourTimes[orderOfArrival-1].plusMinutes((long)(minutes+1.0+5.0));
		}
		//display
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");  
		String formatDateTime = this.tourTimes[orderOfArrival].format(format);   
		System.out.println("After Formatting: " + formatDateTime );
		if(orderOfArrival == (this.tourTimes.length-1))
		{
			this.endDate = this.tourTimes[orderOfArrival];
		}
		//Association of the arrival time and the delivery corresponding 
		int sizeSteps = this.steps.size();
		System.out.println("deliveryPt.getId() : "+deliveryPt.getId());
		for (int i=0; i<sizeSteps; i++)
		{
			System.out.println("this.steps.get(i).getDestination().getId() : "+this.steps.get(i).getDestination().getId());
			if((this.steps.get(i).getDestination().getId()) == deliveryPt.getId())
			{
				this.steps.get(i).setArrival(this.tourTimes[orderOfArrival]);
				System.out.println("New status of delivery : "+this.steps.get(i).toString());
			}
		}
		/*for(int i=1; i<this.tourTimes.length; i++)
		{
			if(this.tourSteps.get(i)==deliveryPt)
			{
				System.out.println("i = "+i+" this.tourTimes[i] = "+this.tourTimes[i]+" & i-1 = "+(i-1)+" this.tourTimes[i+1] = "+this.tourTimes[i+1]);
				this.tourTimes[i]=this.tourTimes[i-1].plusMinutes((long)(minutes));
				
				//display
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");  
				String formatDateTime = this.tourTimes[i].format(format);   
				System.out.println("After Formatting: " + formatDateTime );  
				
			}
		}*/
	}
	//END NEW
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setSteps(ArrayList<Delivery> steps) {
		this.steps = steps;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public void setTourSteps(ArrayList<Intersection> tourSteps) {
		this.tourSteps = tourSteps;
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

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public ArrayList<Intersection> getTourSteps() {
		return tourSteps;
	}

	public Courier getCourier() {
		return courier;
	}

	@Override
	public String toString() {
		return "Tour [id=" + id + ", steps=" + steps + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", intersections=" + tourSteps + ", courier=" + courier + "]";
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
	
	public Intersection getCloserIntersection(float latitude, float longitude)
	{
		float minimumDistance = Float.MAX_VALUE;
		Intersection closerIntersection = null;
		for(Intersection i : map.getNodes().values())
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
	
	public void addDelivery(Intersection closerIntersection, LocalDate date, int timeWindow)
	{
		//StartDate = timeWindow la plus tôt d'une Delivery
		if(this.startDate == null)
		{
			this.startDate = LocalDateTime.of(date, LocalTime.of(timeWindow,0));
		}
		else
		{
			if(this.startDate.getMinute() > timeWindow)
			{
				this.startDate = LocalDateTime.of(date, LocalTime.of(timeWindow,0));
			}
		}
		System.out.println("EEEEEEEEEEEEEEE");
	    Delivery delivery = new Delivery("test", timeWindow, closerIntersection,null);
	    System.out.println("DDDDDDDDDDDDDD");
	    steps.add(delivery);
	    System.out.println("Avant notify");
		notifyObservers(delivery);
		System.out.println("FFFFFFFFFFFFFF");
	}	
	
	public void addTourSteps(Intersection tourSteps)
	{
		this.tourSteps.add(tourSteps);
		notifyObservers();
	}
	
	public void clearTourSteps() {
		this.tourSteps.clear();
	}
	
}
