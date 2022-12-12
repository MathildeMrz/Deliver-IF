package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Delivery extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private int startTime;
	private Intersection destination;
	private LocalTime arrival;
	private LocalTime deliveryTime; //Real time of the delivery
	
	public Delivery(int startTime, Intersection destination, LocalTime arrival)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.startTime = startTime;
		this.destination = destination;
		this.arrival = arrival;
		this.deliveryTime = arrival;
	}
	
	public Delivery(int startTime, Intersection destination, LocalTime arrival, LocalTime deliveryTime)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.startTime = startTime;
		this.destination = destination;
		this.arrival = arrival;
		this.deliveryTime = deliveryTime;
	}

	public LocalTime getArrival() {
		return arrival;
	}

	public void setArrival(LocalTime arrival) {
		this.arrival = arrival;
	}

	@Override
	public String toString() {
		//return this.courier+" : arrival : "+arrival+" time-window : " + startTime + " � " + (startTime+1) + ", destination=" + destination;
		//return " Arrivée au point de livraison : "+arrival+" time-window : " + startTime + " � " + (startTime+1) + ", Heure de livraison "+deliveryTime+" destination=" + destination;
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");  
		String deliveryWaiting = "";
		String formatTimeDeliveryTime = deliveryTime.format(format); 
		if(!this.arrival.equals(deliveryTime))
		{
			String formatTimeArrival = arrival.format(format); 
			deliveryWaiting = "Waiting time at the destination from "+formatTimeArrival+" to "+formatTimeDeliveryTime+"\n";
			System.out.println(deliveryWaiting);
		}
		String deliveryTime = "Delivery's time : "+formatTimeDeliveryTime;
		String deliveryInformations = deliveryWaiting+""+deliveryTime;
		System.out.println(deliveryInformations);
		return deliveryInformations;
	}

	public int getId() {
		return id;
	}

	public int getStartTime() {
		return startTime;
	}

	public Intersection getDestination() {
		return destination;
	}
	
	public LocalTime getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(LocalTime deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public static AtomicInteger getIdFactory() {
		return ID_FACTORY;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setDestination(Intersection destination) {
		this.destination = destination;
	}
	
	
	
}
