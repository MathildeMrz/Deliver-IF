package model;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import observer.Observable;

public class Courier extends Observable {
	private static final AtomicInteger ID_FACTORY = new AtomicInteger();
	private static final double SPEED_COURIER = 15.0;  
	private int id;
	private String name;
	private double speed;
	private Tour tour;
	private static final ArrayList<Color> ColorsList = new ArrayList<Color>(Arrays.asList(Color.HOTPINK, Color.PURPLE,  Color.SKYBLUE, Color.SALMON, Color.CADETBLUE, Color.AQUAMARINE, Color.CHOCOLATE, Color.GREENYELLOW, Color.LIGHTSEAGREEN,Color.MEDIUMSPRINGGREEN));
	private Color travelColor;
	
	public Courier(String name)
	{
		this.id = ID_FACTORY.getAndIncrement();
		this.name = name;
		this.speed = SPEED_COURIER;
		this.tour = new Tour();
		if(this.id<this.ColorsList.size()) {
			this.travelColor = ColorsList.get(this.id);
		} else {
			Random rand = new Random();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			this.travelColor = new Color(r, g, b, 1.0);
		}
		System.out.println("Id courier : "+this.id);
		System.out.println("name courier : "+this.name);
	}
	public Courier(String name, int id)
	{
		this.id = id;
		this.name = name;
		this.speed = SPEED_COURIER;
		this.tour = new Tour();
		if(this.id<this.ColorsList.size()) {
			this.travelColor = ColorsList.get(this.id);
		} else {
			Random rand = new Random();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			this.travelColor = new Color(r, g, b, 1.0);
		}
		System.out.println("Id courier : "+this.id);
		System.out.println("name courier : "+this.name);
	}
	
	@Override
    public String toString() {
        //return id + " " + name + " " +speed;
		return name;
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

	public Color getTravelColor() {
		return travelColor;
	}

	public void setTravelColor(Color travelColor) {
		this.travelColor = travelColor;
	}

	public static AtomicInteger getIdFactory() {
		return ID_FACTORY;
	}

	public static double getSpeedCourier() {
		return SPEED_COURIER;
	}

	public static ArrayList<Color> getColorslist() {
		return ColorsList;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	
	
}
