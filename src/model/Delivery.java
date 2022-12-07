package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Delivery extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private int startTime;
	private Intersection destination;
	private LocalDateTime arrival;
	private LocalTime deliveryTime; //Real time of the delivery
	
	public Delivery(String name, int startTime, Intersection destination, LocalDateTime arrival)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.startTime = startTime;
		this.destination = destination;
		this.arrival = arrival;
		this.deliveryTime = arrival;
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
		return " Arrivée au point de livraison : "+arrival+" time-window : " + startTime + " � " + (startTime+1) + ", Heure de livraison "+deliveryTime+" destination=" + destination;
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
	
}
