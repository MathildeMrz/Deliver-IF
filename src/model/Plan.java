package model;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;

import observer.Observable;

public class Plan extends Observable {
	private HashMap<Long, Intersection> nodes;
	private ArrayList<Intersection> destinations;
	private Intersection warehouse;
	private float latitudeMin;
	private float latitudeMax;
	private float longitudeMin;
	private float longitudeMax;
	
	public Plan() {
		this.nodes  = new HashMap<Long,Intersection>();
		this.destinations  = new ArrayList<Intersection>();
		this.warehouse = null;
		latitudeMin = Float.MAX_VALUE;
		latitudeMax = 0;
		longitudeMin = Float.MAX_VALUE;
		longitudeMax = 0;
	}
	
	public void addWarehouse(Long intersectionID) {
		this.warehouse = this.nodes.get(intersectionID);
	}
	
	public void addNode(Intersection node) {
		//this.nodes.add(node);
		nodes.put(node.getId(), node);
	}

	public HashMap<Long, Intersection> getNodes() {
		return nodes;
	}
	
	public ArrayList<Intersection> getDestinations() {
		return destinations;
	}

	public Intersection getWarehouse() {
		return warehouse;
	}

	@Override
	public String toString() {
		return "Plan [nodes=" + nodes + ", warehouse=" + warehouse + "]";
	}

	public float getLatitudeMin() {
		return latitudeMin;
	}

	public void setLatitudeMin(float latitudeMin) {
		this.latitudeMin = latitudeMin;
	}

	public float getLatitudeMax() {
		return latitudeMax;
	}

	public void setLatitudeMax(float latitudeMax) {
		this.latitudeMax = latitudeMax;
	}

	public float getLongitudeMin() {
		return longitudeMin;
	}

	public void setLongitudeMin(float longitudeMin) {
		this.longitudeMin = longitudeMin;
	}

	public float getLongitudeMax() {
		return longitudeMax;
	}

	public void setLongitudeMax(float longitudeMax) {
		this.longitudeMax = longitudeMax;
	}
	
	
	
	public void addPosition(float y, float x)
	{
		//Translate pixel coordinates to street coordinates
		 float widthSegment = this.getLongitudeMax() - this.getLongitudeMin();
	     float heightSegment = this.getLatitudeMax() - this.getLatitudeMin();
	     GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	     float width = gd.getDisplayMode().getWidth();
	     float height = gd.getDisplayMode().getHeight();
			
	     float xStreet = (x * widthSegment)/width + this.getLongitudeMin();
	     float yStreet = (x * heightSegment)/height + this.getLatitudeMin();

		//Id automatique ??
		Intersection newPosition = new Intersection(1212, yStreet, xStreet);
		destinations.add(newPosition);
		System.out.println("Let's notify the observers");
		System.out.println("IMPORTAAAANT X 	: "+xStreet);
		System.out.println("IMPORTAAAANT Y 	: "+yStreet);
		notifyObservers();
	}	
}
