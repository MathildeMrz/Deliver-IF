package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Tour extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private ArrayList<Delivery> deliveries;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private ArrayList<Intersection> tourSteps;
	private LocalTime [] tourTimes;
	
	public Tour()
	{
		this.id = ID_FACTORY.getAndIncrement();
		deliveries = new ArrayList<Delivery>();
		this.tourSteps = new ArrayList<Intersection>();
		this.startDate = LocalDateTime.now();
		this.endDate = LocalDateTime.now();
		this.tourTimes = new LocalTime[this.deliveries.size()];
	}
	
	//NEW : ADD TIME OF ARRIVALS TO DELIVERY POINTS
	public void initArrivals()
	{
		//this.tourTimes=new LocalDateTime [this.tourSteps.size()];
		this.tourTimes= new LocalTime [this.deliveries.size()+2];
		tourTimes[0]=this.startDate.toLocalTime();
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
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");  
		String formatDateTime = this.tourTimes[orderOfArrival].format(format);   
		System.out.println("After Formatting: " + formatDateTime );
		if(orderOfArrival == (this.tourTimes.length-1))
		{
			//this.endDate = this.tourTimes[orderOfArrival];
		
		}
		//Association of the arrival time and the delivery corresponding 
		int sizeSteps = this.deliveries.size();
		System.out.println("deliveryPt.getId() : "+deliveryPt.getId());
		for (int i=0; i<sizeSteps; i++)
		{
			System.out.println("this.steps.get(i).getDestination().getId() : "+this.deliveries.get(i).getDestination().getId());
			if((this.deliveries.get(i).getDestination().getId()) == deliveryPt.getId())
			{
				this.deliveries.get(i).setArrival(this.tourTimes[orderOfArrival]);
				int timeWindow = this.deliveries.get(i).getStartTime();
				if(timeWindow > (this.tourTimes[orderOfArrival].getHour()))
				{
					this.deliveries.get(i).setDeliveryTime(LocalTime.of(timeWindow, 0));
				}
				else
				{
					this.deliveries.get(i).setDeliveryTime(this.tourTimes[orderOfArrival]);
				}
				this.tourTimes[orderOfArrival] = this.deliveries.get(i).getDeliveryTime();
				System.out.println("New status of delivery : "+this.deliveries.get(i).toString());
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
		this.deliveries = steps;
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

	public ArrayList<Delivery> getDeliveries() {
		return deliveries;
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

	@Override
	public String toString() {
		return "Tour [id=" + id + ", steps=" + deliveries + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", intersections=" + tourSteps + "]";
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
	    LocalTime startDelivery = startDate.toLocalTime();
		Delivery delivery = new Delivery("test", timeWindow, closerIntersection, startDelivery);
	    deliveries.add(delivery);
		notifyObservers(delivery);
	}	

	
	public void addDeliveryToOrderedDeliveries(Intersection tourSteps)
	{
		this.tourSteps.add(tourSteps);
		notifyObservers();
	}
	
	public void clearDeliveries() {
		this.deliveries.clear();
	}
	
	public void clearTourSteps() {
		this.tourSteps.clear();
	}

	public LocalTime[] getTourTimes() {
		return tourTimes;
	}

	public void setTourTimes(LocalTime[] tourTimes) {
		this.tourTimes = tourTimes;
	}

	public static AtomicInteger getIdFactory() {
		return ID_FACTORY;
	}

	public void setDeliveries(ArrayList<Delivery> deliveries) {
		this.deliveries = deliveries;
	}
}
