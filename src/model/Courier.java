package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import observer.Observable;
import javafx.scene.paint.Color;

public class Courier extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private static final double SPEED_COURIER = 15.0;  
	private int id;
	private String name;
	private double speed;
	private Tour tour;
	private static final ArrayList<Color> ColorsList = new ArrayList<Color>(Arrays.asList(Color.HOTPINK, Color.PURPLE, Color.SALMON, Color.SEASHELL, Color.VIOLET, Color.SKYBLUE, Color.PEACHPUFF, Color.ORCHID, Color.STEELBLUE, Color.ROYALBLUE));
	private Color travelColor;
	
	public Courier(String name)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.name = name;
		this.speed = SPEED_COURIER;
		this.tour = new Tour();
		this.travelColor = ColorsList.get(id);
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
	
	public Color getColor() {
		return travelColor;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}
	
}
