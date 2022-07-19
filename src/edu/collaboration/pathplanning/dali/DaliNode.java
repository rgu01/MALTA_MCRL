package edu.collaboration.pathplanning.dali;

import java.util.ArrayList;
import java.util.List;

import edu.collaboration.pathplanning.Node;

public class DaliNode extends Node {
	
	List<DaliEdge> edges= new ArrayList<DaliEdge>();
	
	boolean isDesirable = true;
	double regionIntensity = 1;
	double currentDistance = Double.POSITIVE_INFINITY;
	double currentEstimation = Double.POSITIVE_INFINITY;
	
	DaliNode previous = null; //for backtracking

	public DaliNode(int id, double x, double y) {
		super(x, y);
		this.id = id;
	}
	
	public DaliEdge createEdge(DaliNode dest, int edgeID) {
		DaliEdge newEdge = new DaliEdge(edgeID, this, dest);
		this.edges.add(newEdge);
		return newEdge;
	}
	
	public double distanceToOther(Node other) {
		return distanceToPoint(other.lat, other.lon);
	}
	
	public double distanceToPoint(double x, double y) {
		return Math.sqrt((this.lat - x)*(this.lat - x) + (this.lon - y)*(this.lon - y));
	}
	
	public double distanceManhToOther(Node other) {
		return Double.max(Math.abs(this.lat - other.lat), Math.abs(this.lon - other.lon));
	}
	
	public DaliEdge findOutEdge(Node dest) {
		return this.edges.stream().filter(x -> x.dest == dest).findFirst().orElse(null);
	}
}
