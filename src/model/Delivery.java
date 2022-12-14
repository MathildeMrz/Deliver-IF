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
	
	/**
	 * Create a Delivery with its destination and time of the beginning of its time window
	 * @param startTime : start time of delivery
	 * @param destination : destination of delivery
	 * @param arrival : arrival of delivery
	 * */
	public Delivery(int startTime, Intersection destination, LocalTime arrival)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.startTime = startTime;
		this.destination = destination;
		this.arrival = arrival;
		this.deliveryTime = arrival;
	}
	
	/**
	 * Create a Delivery with its destination and time of the beginning of its time window
	 * @param startTime : start time of delivery
	 * @param destination : destination of delivery
	 * @param arrival : arrival of delivery
	 * @param deliveryTime : time of delivery 
	 * */
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
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");  
		String deliveryWaiting = "";
		String formatTimeDeliveryTime = deliveryTime.format(format); 
		if(!this.arrival.equals(deliveryTime))
		{
			String formatTimeArrival = arrival.format(format); 
			deliveryWaiting = "Waiting time at the destination from "+formatTimeArrival+" to "+formatTimeDeliveryTime+"\n";
		}
		String deliveryTime = "Delivery's time : "+formatTimeDeliveryTime;
		String deliveryInformations = deliveryWaiting+""+deliveryTime;
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
