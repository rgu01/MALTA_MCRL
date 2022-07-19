package edu.collaboration.pathplanning.dali;

import java.util.HashSet;
import java.util.List;

import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;

public class DaliAnomaly {

	private HashSet<Integer> nodeIDs = new HashSet<Integer>();
	public final double startTime;
	public final double endTime;
	private final List<Node> vertices;
	
	public DaliAnomaly(Obstacle obs) {
		this.vertices = obs.vertices;
		this.startTime = obs.startTime;
		this.endTime = obs.endTime;
	}
	
	public void addNode(Node node) {
		nodeIDs.add(node.id);
	}
	
	public boolean isInside(Node node) {
		return Utils.isInsidePolygon(node.lat, node.lon, vertices);
	}
	
	public boolean isBlocking(int id, double currentTime) {
		return nodeIDs.contains(id) && startTime < currentTime && currentTime < endTime;
	}
}
