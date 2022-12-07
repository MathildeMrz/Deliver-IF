package model;

import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Courier extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private static final double SPEED_COURIER = 15.0;  
	private int id;
	private String name;
	private double speed;
	private Tour tour;
	
	public Courier(String name)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.name = name;
		this.speed = SPEED_COURIER;
		this.tour = new Tour();
	}
	
	@Override
    public String toString() {
        return id + " " + name + " " +speed;
    }

	public String getName() {
		return name;
	}

	public double getSpeed() {
		return speed;
	}

	public int getId() {
		return id;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}
	
}
