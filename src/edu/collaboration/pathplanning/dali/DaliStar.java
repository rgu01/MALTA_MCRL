package edu.collaboration.pathplanning.dali;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.afarcloud.thrift.RegionType;

import edu.collaboration.pathplanning.NavigationArea;
import edu.collaboration.pathplanning.Node;
import edu.collaboration.pathplanning.Obstacle;
import edu.collaboration.pathplanning.Path;
import edu.collaboration.pathplanning.PathPlanningAlgorithm;
import edu.collaboration.tamaa.PlannerServiceHandler;

public class DaliStar extends Dali{

	public DaliStar(NavigationArea nArea) {
		super(nArea);
	}
	
	public DaliStar(NavigationArea nArea, List<DaliRegionConstraint> regionPreferences) {
		super(nArea, regionPreferences);		
	}

	@Override
	public Path calculate(Node start, Node destination, double vehicleSpeed, double startTime) {
		DaliNode target = findNearestNode(destination.lat, destination.lon);
		DaliNode source = findNearestNode(start.lat, start.lon);
		if (target == null) {
			System.out.println("Target node  not found");
			return null;
		}
		if (source == null) {
			System.out.println("Source node  not found");
			return null;
		}
		PriorityQueue<DaliNode> processing = new PriorityQueue<DaliNode>(new Comparator<DaliNode>() { 
			@Override
		    public int compare(DaliNode a, DaliNode b) {
				return a.currentEstimation < b.currentEstimation ? -1 : 
					             a.currentEstimation > b.currentEstimation ? 1 : 
					            	 (a.currentDistance < b.currentDistance ? -1 :
					            		 a.currentDistance == b.currentDistance ? 0 : 1);
			}
		});
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		Path p_result = new Path(start, destination);
		source.currentDistance = 0;
		processing.add(source);
		while(!processing.isEmpty() && !distances.containsKey(target)) {
			DaliNode current = processing.remove();
			double currentDistance = current.currentDistance;
			for (DaliEdge e : current.edges) {	
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (checkAnomalies && blockedByAnomalies(e.dest.id, currentDistance / vehicleSpeed + startTime)) continue;
				double priorityCoeff = 1;
				if (usePreferedAreas) 
					priorityCoeff = e.dest.isDesirable ? 1/e.dest.regionIntensity : e.dest.regionIntensity;
				double edist = currentDistance + priorityCoeff * e.length / (1-e.heat) ;
				if (current ==source) {
					edist = currentDistance + priorityCoeff * e.dest.distanceToPoint(start.lat, start.lon) / (1-e.heat);
				}
				if (e.dest == target) {
					edist = currentDistance + priorityCoeff * current.distanceToPoint(destination.lat, destination.lon) / (1-e.heat);
				}
				if (!processing.contains(e.dest) || e.dest.currentDistance > edist || 
						(current != source && e.dest.currentDistance == edist && sameDirection(e.dest, current))) {
					processing.remove(e.dest);
					e.dest.currentDistance = edist;
					e.dest.currentEstimation = e.dest.currentDistance + e.dest.distanceToOther(target);
					e.dest.previous = current;
					processing.add(e.dest);					
				}			
			}
			distances.put(current, currentDistance);
		}
		clearNodes();
		if (distances.containsKey(target)) {
			double totalLength = 0;
			List<Node> path = new ArrayList<Node>();
			DaliNode current = target;
			path.add(target);
			while (current.previous != source) {
				DaliEdge e = current.previous.findOutEdge(current);
				if (current ==target) {
					totalLength += current.previous.distanceToPoint(destination.lat, destination.lon) /(1-e.heat);
				} else {
					totalLength += e.length/(1-e.heat);
				} 
				current = current.previous;
				path.add(0, current);
			}
			DaliEdge e = current.previous.findOutEdge(current);
			totalLength +=  current.distanceToPoint(start.lat, start.lon)/(1-e.heat);
			path.add(0, source);
			p_result.segments = path;
			p_result.setLength(totalLength);
			System.out.println("Done");
			try {
				FileWriter fw = new FileWriter(PlannerServiceHandler.logFileDaliStar, true);
				String log = String.valueOf(distances.size())+ "\n";
				fw.write(log);
				fw.close();
			}catch (Exception ex) {}
			
			return p_result;
		}
		else
		{
			System.out.println("No path found");
			return null;
		}
	}	
}
