package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
		this.deliveries = new ArrayList<Delivery>();
		this.tourSteps = new ArrayList<Intersection>();
		this.startDate = LocalDateTime.now();
		this.endDate = LocalDateTime.now();
		this.tourTimes = new LocalTime[this.deliveries.size()];
	}
	
	public void initArrivals()
	{
		this.tourTimes= new LocalTime [this.deliveries.size()+2];
		tourTimes[0]=this.startDate.toLocalTime();
	}
	
	
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
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");  
		String formatDateTime = this.tourTimes[orderOfArrival].format(format);   
		if(orderOfArrival == (this.tourTimes.length-1))
		{
			this.endDate = LocalDateTime.of(this.endDate.toLocalDate(),tourTimes[orderOfArrival]);
		
		}
		//Association of the arrival time and the delivery corresponding 
		int sizeSteps = this.deliveries.size();
		for (int i=0; i<sizeSteps; i++)
		{
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
			}
		}
	}
	
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
	
	/**
	 * add a delivery to the list of deliveries of a courier.
	 * @param intersection : intersection where the delivery takes place
	 * @param date : date of the delivery
	 * @param timeWindow : hour of the day when the courier should deliver (he must deliver between timeWindow and timeWindow +1)
	 * */
	public Delivery addDelivery(Intersection intersection, LocalDate date, int timeWindow)
	{
		//StartDate = timeWindow la plus tôt d'une Delivery
		if(this.startDate.getHour() > timeWindow)
		{
			this.startDate = LocalDateTime.of(date, LocalTime.of(timeWindow,0));
		}
	    LocalTime startDelivery = startDate.toLocalTime();
		Delivery delivery = new Delivery(timeWindow, intersection, startDelivery);
	    deliveries.add(delivery);
		notifyObservers(delivery);
		//sort deliveries by startTime (timeWindow)
		deliveries.sort(Comparator.comparing(Delivery::getStartTime));
		return delivery;
	}	

	/**
	 * add an intersections to the list of intersections that the courier should follow to make his deliveries
	 * @param tourSteps : next intersection where the courier should go
	 * */
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
