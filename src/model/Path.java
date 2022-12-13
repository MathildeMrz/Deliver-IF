package model;

import java.util.ArrayList;

public class Path {
	
	private ArrayList<Segment> path;
	private Intersection origin;
	private Intersection destination;
	
	public Path(ArrayList<Segment> path, Intersection origin, Intersection destination) {
		this.path = path;
		this.origin = origin;
		this.destination = destination;
	}

	public ArrayList<Segment> getPath() {
		return path;
	}

	public void setPath(ArrayList<Segment> path) {
		this.path = path;
	}

	public Intersection getOrigin() {
		return origin;
	}

	public void setOrigin(Intersection origin) {
		this.origin = origin;
	}

	public Intersection getDestination() {
		return destination;
	}

	public void setDestination(Intersection destination) {
		this.destination = destination;
	}

}
