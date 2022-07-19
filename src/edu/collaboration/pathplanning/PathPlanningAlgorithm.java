package edu.collaboration.pathplanning;

/**
 * 
 * @author rgu01
 * The result of path should be a sequence of nodes starting from the start, and ending at the destination
 */
public interface PathPlanningAlgorithm {
	public Path calculate(Node start, Node destination, double vehicleSpeed);
}
