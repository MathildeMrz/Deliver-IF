package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;

public class Tour extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private int id;
	private ArrayList<Delivery> deliveries;
	private Date startDate;
	private Date endDate;
	private Path intersections;
	private Courier courier;
	
	public Tour(ArrayList<Delivery> deliveries, Date startDate, Date endDate, Path intersections, Courier courier)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.deliveries = deliveries;
		this.startDate = startDate;
		this.endDate = endDate;
		this.intersections = intersections;
		this.courier = courier;
	}

	public int getId() {
		return id;
	}

	public ArrayList<Delivery> getDeliveries() {
		return deliveries;
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
		return "Tour [id=" + id + ", deliveries=" + deliveries + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", intersections=" + intersections + ", courier=" + courier + "]";
	}

	
	
}
