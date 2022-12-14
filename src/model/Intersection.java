package model;

import java.util.ArrayList;
import observer.Observable;

public class Intersection extends Observable {
	private long id;
	private float latitude;
	private float longitude;
	private ArrayList<Segment> outSections = new ArrayList<Segment>();
	
	public Intersection()
	{	
	}
	
	/**
	 * Create an Intersection, with an id, a latitude and a longitude
	 * @param id : id of the intersection
	 * @param latitude : latitude of the intersection
	 * @param longitude : longitude of the intersection
	 * */
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
	
	public void setId(long id) {
		this.id = id;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public void setOutSections(ArrayList<Segment> outSections) {
		this.outSections = outSections;
	}
	@Override
	public String toString() {
		return "Intersection [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
}
