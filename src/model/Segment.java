package model;

import observer.Observable;

public class Segment extends Observable {
	private Intersection destination;
	private float length;
	private String name;
	
	/**
	 * Create a Segment that has a destination, a length and a name
	 * @param name : name of the Road/Segment
	 * @param destination : destination of the segment
	 * @param length : length of the segment in meters
	 * */
	public Segment(Intersection destination, float length, String name) {
		this.destination = destination;
		this.length = length;
		this.name = name;
	}

	public Intersection getDestination() {
		return destination;
	}

	public float getLength() {
		return length;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Segment [destination=" + destination + ", length=" + length + ", name=" + name + "]";
	}
		
}
