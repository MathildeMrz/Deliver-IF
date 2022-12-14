package model;

import java.util.ArrayList;

public class Path {
	
	private ArrayList<Segment> path;
	private Intersection origin;
	private Intersection destination;
	
	/**
	 * Creates a Path, with contains the list of segments corresponding to the shortest path between the origin and the destination
	 * @param path : the shortest path
	 * @param origin : the origin of the path
	 * @param destination : the destination of the path
	 * */
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
