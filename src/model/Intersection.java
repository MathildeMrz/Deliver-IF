package model;

import java.util.ArrayList;

import observer.Observable;

public class Intersection extends Observable {
	private long id;
	private float latitude;
	private float longitude;
	private ArrayList<Segment> outSections = new ArrayList<Segment>();
	
	public Intersection(long id, float latitude, float longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}
	
	public ArrayList<Segment> getOutSections() {
		return outSections;
	}

	public void addOutSection(Segment section) {
		outSections.add(section);
	}

	@Override
	public String toString() {
		return "Intersection [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
}
