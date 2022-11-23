package model;

import java.util.ArrayList;

import observer.Observable;

public class Plan extends Observable {
	private ArrayList<Intersection> nodes;
	private Intersection warehouse;
	private float latitudeMin;
	private float latitudeMax;
	private float longitudeMin;
	private float longitudeMax;
	
	public Plan() {
		this.nodes  = new ArrayList<Intersection>();
		this.warehouse = null;
		latitudeMin = Float.MAX_VALUE;
		latitudeMax = 0;
		longitudeMin = Float.MAX_VALUE;
		longitudeMax = 0;
	}
	
	public void addWarehouse(float intersectionID) {
		for(Intersection node : this.nodes) {
			if(node.getId() == intersectionID) {
				this.warehouse = node;
				break;
			}
		}
	}
	
	public void addNode(Intersection node) {
		this.nodes.add(node);
	}

	public ArrayList<Intersection> getNodes() {
		return nodes;
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
	
	
}
