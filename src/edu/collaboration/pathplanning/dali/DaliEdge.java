package edu.collaboration.pathplanning.dali;

public class DaliEdge {

	DaliNode src;
	DaliNode dest;
	double length;
	int id;
	double heat = 0;
	
	public DaliEdge(int id, DaliNode src, DaliNode dest) {
		this.id = id;
		this.src = src;
		this.dest = dest;
		length = src.distanceToOther(dest);
		src.edges.add(this);
	}
	
}
