package model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Delivery extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private int startTime;
	private Intersection destination;
	private LocalDateTime arrival;
	private Courier courier;
	
	public Delivery(String name, int startTime, Intersection destination, LocalDateTime arrival, Courier courier)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.startTime = startTime;
		this.destination = destination;
		this.arrival = arrival;
		this.courier = courier;
	}

	public LocalDateTime getArrival() {
		return arrival;
	}

	public void setArrival(LocalDateTime arrival) {
		this.arrival = arrival;
	}

	@Override
	public String toString() {
		return this.courier+" : arrival : "+arrival+" time-window : " + startTime + " à " + (startTime+1) + ", destination=" + destination;
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
	
}
