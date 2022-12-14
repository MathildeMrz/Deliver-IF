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
	
	/**
	 * Create a Courier, with a speed of 15 km/h, a specific color and a tour
	 * @param name : name of the Courier
	 * */
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
	}
	
	/**
	 * Create a Courier, with a speed of 15 km/h, a specific color and a tour
	 * @param name : name of the Courier
	 * @param id : id of the Courier
	 * */
	public Courier(String name, int id)
	{
		this.id = id;
		this.name = name;
		this.speed = SPEED_COURIER;
		this.tour = new Tour();
		//add a color to the courier from our list of colors 
		if(this.id<this.ColorsList.size()) {
			this.travelColor = ColorsList.get(this.id);
		} else {
			//if all or colors have already been added to a courier, we give a random color to the courier
			Random rand = new Random();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			this.travelColor = new Color(r, g, b, 1.0);
		}
	}
	
	@Override
    public String toString() {
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
