package model;

import java.util.ArrayList;

public class Plan {
	private ArrayList<Intersection> nodes;
	private Intersection warehouse;
	
	public Plan() {
		this.nodes  = new ArrayList<Intersection>();
		this.warehouse = null;
	}
	
	public void addWarehouse(Long intersectionID) {
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
	
	
}
