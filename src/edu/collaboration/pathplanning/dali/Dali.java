package edu.collaboration.pathplanning.dali;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
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

public class Dali implements PathPlanningAlgorithm {

	HashMap<Integer, DaliNode> nodes = new HashMap<Integer, DaliNode>();
	HashMap<CoordinatesTuple, DaliNode> loc2node = new HashMap<CoordinatesTuple, DaliNode>();
	HashMap<Integer, DaliEdge> edges = new HashMap<Integer, DaliEdge>();
	
	List<DaliAnomaly> anomalies = new ArrayList<DaliAnomaly>();
	List<Obstacle> permanentObstacles;
	HashMap<Integer, Double> heatNodes = new HashMap<Integer, Double>();
	
	//public double vehicleSpeed = 1;
	//double startTime =0;
	boolean checkAnomalies = false;
	boolean usePreferedAreas = true; 
	
	public Dali(NavigationArea nArea) {
		generateGraph(nArea);
		updateHeatMap();
	}
	
	public Dali(NavigationArea nArea, List<DaliRegionConstraint> regionPreferences) {
		generateGraph(nArea, regionPreferences);
		updateHeatMap();
	}
	
//	public Dali(NavigationArea nArea, List<DaliConstraint> constraints) {
//		generateGraph(nArea);
//		for (DaliConstraint c : constraints) {
//			DaliNode center = findNearestNode(c.lat, c.lon);
//			if (center != null) 
//				c.addConstaint(center);
//		}
//	}
	
///////////////////////////////////////////////////////////
// Creation of a graph
///////////////////////////////////////////////////////////
	int nid;
	int eid;
	List<DaliNode> processing = new ArrayList<DaliNode>();
	
	boolean isInNavigationArea(NavigationArea nArea, CoordinatesTuple coordinates) {
		if (!Utils.isInsidePolygon(coordinates.lat, coordinates.lon, nArea.boundry)) {
			return false;
		}
		for (Obstacle obs : permanentObstacles) {
			if (Utils.isInsidePolygon(coordinates.lat, coordinates.lon, obs.vertices)) {
				return false;
			}
		}
		return true;
	}
	
	DaliNode addNode(CoordinatesTuple coordinates, DaliNode prev) {
		DaliNode newNode = findNode(coordinates);
		if (newNode == null) {
			newNode = new DaliNode(nid, coordinates.lat, coordinates.lon);
			nid++;
			processing.add(newNode);
			nodes.put(nid, newNode);
			loc2node.put(coordinates, newNode);
			addToAnomalies(newNode);
		}
		DaliEdge e = prev.createEdge(newNode, eid);
		edges.put(eid, e);
		eid++;
		return newNode;
	}
	
	DaliNode findNode(double x, double y) {
		return loc2node.getOrDefault(new CoordinatesTuple(x, y), null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}

	DaliNode findNode(CoordinatesTuple coordinates) {
		return loc2node.getOrDefault(coordinates, null);
		//return nodes.values().stream().filter(n -> n.lat == x && n.lon == y).findAny().orElse(null);
	}

	DaliNode findNearestNode(double x, double y) {
		DaliNode tl = this.nodes.get(0);
		double xPos = tl.lat - NavigationArea.threshold *(Math.round((tl.lat -x)/ NavigationArea.threshold));
		double yPos = tl.lon + NavigationArea.threshold *(Math.round((y- tl.lon)/ NavigationArea.threshold));
		return findNode(xPos, yPos);
		//return nodes.values().stream().filter(n -> Math.abs(n.lat-x)< NavigationArea.threshold / 2 
		//										&& Math.abs(n.lon-y)< NavigationArea.threshold / 2).findAny().orElse(null);
	}
	
	void generateGraph(NavigationArea nArea) {
		generateGraph(nArea, null);
	}
		
	void generateGraph(NavigationArea nArea, List<DaliRegionConstraint> regionPreferences) {
		//processAnomalies(nArea);
		anomalies = nArea.obstacles.stream().filter(obs -> (obs.endTime >0)).
														map(obs -> new DaliAnomaly(obs)).collect(Collectors.toList());
		permanentObstacles = nArea.obstacles.stream().filter(obs -> (obs.endTime <=0)).collect(Collectors.toList());
		DaliNode topLeft = new DaliNode(0, nArea.boundry.get(0).lat - NavigationArea.threshold / 2 , 
											nArea.boundry.get(0).lon + NavigationArea.threshold / 2 );
		addToAnomalies(topLeft);
		processing.add(topLeft);
		nodes.put(0, topLeft);
		loc2node.put(new CoordinatesTuple(nArea.boundry.get(0).lat, nArea.boundry.get(0).lon), topLeft);
		nid = 1;
		eid = 0;
		while (!processing.isEmpty()) {
			DaliNode currentDaliNode = processing.remove(0);
			for (CoordinatesTuple neighbour : Utils.neighbourLocations(currentDaliNode.lat, currentDaliNode.lon)) {
				if (isInNavigationArea(nArea, neighbour)) {
					DaliNode newNode = addNode(neighbour, currentDaliNode);
					processPriorityRegion(regionPreferences, newNode);
				}				
			}
		}
	}

	private void processPriorityRegion(List<DaliRegionConstraint> regionPreferences, DaliNode newNode) {
		if (regionPreferences != null) {
			for(DaliRegionConstraint rc : regionPreferences) {
				if (Utils.isInsidePolygon(newNode.lat, newNode.lon, rc.corners)) {
					if (rc.regionType == RegionType.HEAT_REGION) {
						if (heatNodes.containsKey(newNode.id)) {
							heatNodes.put(newNode.id, Double.max(heatNodes.get(newNode.id), rc.priority));
						}
						else {
							heatNodes.put(newNode.id, rc.priority);
						}
					}
					else { 
						//double localIntencity = 1 + (rc.priority -1)/ newNode.distanceToPoint(rc.center.lat, rc.center.lon);
						double localIntencity = rc.priority;
						if (newNode.regionIntensity < localIntencity) {
							newNode.regionIntensity = localIntencity;
							newNode.isDesirable = rc.regionType == RegionType.PREFERRED;
						}
					}
				}
			}
		}
	}
	
	private void addToAnomalies(Node node) {
		for (DaliAnomaly anomaly : anomalies) {
			if (anomaly.isInside(node)) {
				anomaly.addNode(node);
			}
		}
	}
	
	protected boolean blockedByAnomalies(int node_id, double time) {
		for (DaliAnomaly anomaly : anomalies) {
			if (anomaly.isBlocking(node_id , time)) {
				return true;
			}
		}
		return false;
	}

////////////////////////////////////////////
// Methods for updates for heat and anomalies. 
///////////////////////////////////////////
	public int getNodes() {
		return this.nodes.keySet().size();
	}
	//Heat in interval [0,1] with 0 - unoccupied and 1 - unpassable
	public void updateHeatMap() {
		for (DaliEdge edge : edges.values()) {
			if (heatNodes.containsKey(edge.dest.id)) {
				edge.heat = heatNodes.get(edge.dest.id);
			}
		}
	}
	
	//public void updateAnomalies(List<DaliAnomaly> newAnomalies, double passedTime) {
	//	anomalies.entrySet().removeIf(e -> e.getValue().timeToLive < passedTime);
	//	anomalies.entrySet().forEach(e -> e.getValue().timeToLive -= passedTime);
	//	newAnomalies.forEach(e -> anomalies.put(e.edgeID, e));
	//}
	
	//public void setStartTime(double time) {
	//	this.startTime = time;
	//}
	
	public void setCheckAnomalies(boolean val) {
		this.checkAnomalies = val;
	}
	
	public void setUsePreferedAreas(boolean val) {
		this.usePreferedAreas = val;
	}

///////////////////////////////////////////	
	
	@Override
	public Path calculate(Node start, Node destination, double vehicleSpeed) {
		return calculate(start, destination, vehicleSpeed, 0);
	}
	
	void clearNodes() {
		for (DaliNode n : this.nodes.values()) {
			n.currentDistance = Double.POSITIVE_INFINITY;
			n.currentEstimation = Double.POSITIVE_INFINITY;
		}
	}
	
	protected Boolean sameDirection(DaliNode next, DaliNode cur) {
		DaliNode prev = cur.previous;
		return (next.lat - cur.lat == cur.lat -prev.lat) && (next.lon - cur.lon == cur.lon -prev.lon);
	}
	
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
				return a.currentDistance < b.currentDistance ? -1 : a.currentDistance == b.currentDistance ? 0 : 1;
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
				double edist = currentDistance + priorityCoeff * e.length / (1-e.heat);
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
					e.dest.previous = current;
					processing.add(e.dest);					
				}			
			}
			distances.put(current, currentDistance);
		}
		clearNodes();
		/* old version
		HashMap<DaliNode, Double> processing = new HashMap<DaliNode, Double>();
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		Path p_result = new Path(start, destination);
		processing.put(source, 0.0);
		while(!processing.isEmpty() || !distances.containsKey(target)) {
			DaliNode current = processing.entrySet().stream().min(Entry.comparingByValue()).get().getKey();
			double currentDistance = processing.get(current);
			processing.remove(current);
			for (DaliEdge e : current.edges) {	
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (checkAnomalies && blockedByAnomalies(e.dest.id, currentDistance / vehicleSpeed + startTime)) continue;
				double priorityCoeff = e.dest.isDesirable ? 1/e.dest.regionIntensity : e.dest.regionIntensity;
				double edist = currentDistance + priorityCoeff * e.length / (1-e.heat) ;
				if (!processing.containsKey(e.dest) || processing.get(e.dest) > edist) {
					processing.put(e.dest, edist);
					e.dest.previous = current;
				}			
			}
			distances.put(current, currentDistance);
		}
		*/
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
				FileWriter fw = new FileWriter(PlannerServiceHandler.logFileDali, true);
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

	public List<Path> calculateSingleSource(Node start, List<Node> destinations, double vehicleSpeed) {
		List<DaliNode> targets = new ArrayList<DaliNode>(); 
		HashMap<DaliNode, Path> p_result = new HashMap<DaliNode,Path>();
		for (Node n : destinations) {
			DaliNode dn = findNearestNode(n.lat, n.lon); 
			targets.add(dn);
			p_result.put(dn, new Path(start, n));
		}
		DaliNode source = findNearestNode(start.lat, start.lon);
		if (targets.stream().anyMatch(s -> s ==null)) {
			System.out.println("At least one target node  not found");
			return null;
		}
		if (source == null) {
			System.out.println("Source node  not found");
			return null;
		}
		PriorityQueue<DaliNode> processing = new PriorityQueue<DaliNode>(new Comparator<DaliNode>() { 
			@Override
		    public int compare(DaliNode a, DaliNode b) {
				return a.currentDistance < b.currentDistance ? -1 : a.currentDistance == b.currentDistance ? 0 : 1;
			}
		});
		HashMap<DaliNode, Double> distances = new HashMap<DaliNode, Double>();
		source.currentDistance = 0;
		processing.add(source);
		while(!processing.isEmpty() && !targets.stream().allMatch(s-> distances.containsKey(s))) {
			DaliNode current = processing.remove();
			double currentDistance = current.currentDistance;
			for (DaliEdge e : current.edges) {
				if (distances.containsKey(e.dest) || e.heat == 1) continue;
				if (checkAnomalies && blockedByAnomalies(e.dest.id, currentDistance / vehicleSpeed)) continue;
				double priorityCoeff = 1;
				if (usePreferedAreas) 
					priorityCoeff = e.dest.isDesirable ? 1/e.dest.regionIntensity : e.dest.regionIntensity;
				double edist = currentDistance + priorityCoeff * e.length / (1-e.heat) ;
				if (!processing.contains(e.dest) || e.dest.currentDistance > edist || 
						(current != source && e.dest.currentDistance == edist && sameDirection(e.dest, current))) {
					processing.remove(e.dest);
					e.dest.currentDistance = edist;
					e.dest.previous = current;
					processing.add(e.dest);					
				}			
			}
			distances.put(current, currentDistance);
		}
		clearNodes();
		for (DaliNode t: targets) {
			if (distances.containsKey(t)) {
				double totalLength = 0;
				List<Node> path = new ArrayList<Node>();
				DaliNode current = t;
				path.add(t);
				while (current.previous != source) {
					DaliEdge e = current.previous.findOutEdge(current);
					totalLength += e.length/(1-e.heat); 
					current = current.previous;
					path.add(0, current);
				}
				DaliEdge e = current.previous.findOutEdge(current);
				totalLength += e.length/(1-e.heat);
				path.add(0, source);
				p_result.get(t).segments = path;
				p_result.get(t).setLength(totalLength);
				System.out.println("Done");
				
				try {
					FileWriter fw = new FileWriter(PlannerServiceHandler.logFileDali, true);
					String log = String.valueOf(distances.size())+ "\n";
					fw.write(log);
					fw.close();
				}catch (Exception ex) {}
			}
			else
			{
				System.out.println("No path found" + start.toString() + t.toString());
			}
		}
		return new ArrayList<Path>(p_result.values());
	}

	
	public boolean pathEntersAnomaly(Path path, double startTime, double speed) {
		int i =0;
		double time = startTime;
		while (i!= path.segments.size() -1) {
			DaliNode cur = (DaliNode)path.segments.get(i);
			DaliNode nxt = (DaliNode)path.segments.get(i+1);
			DaliEdge edge = cur.edges.stream().filter(e -> e.dest == nxt).findFirst().orElse(null);
			if (edge == null) {
				return true;
			}
			time = time + edge.length / (speed * (1-edge.heat));
			if (blockedByAnomalies(nxt.id, time)) {
				return true;
			}
			i++;
		}
		return false;
	}
	
	private List<DaliNode> findLineIntersections(DaliNode start, DaliNode end) {
		ArrayList<DaliNode> intersections = new ArrayList<DaliNode>();
		double movingRight = start.lat < end.lat ? 1 : -1;
		double movingDown = start.lon < end.lon ? 1 : -1;
		double nextv = start.lat + movingRight* NavigationArea.threshold/2;
		double nexth = start.lon + movingDown *NavigationArea.threshold/2;
		boolean atend = ((nextv - end.lat)*movingRight >0) && ((nexth - end.lon)*movingDown >0);
		double coeff = (end.lon-start.lon) /(end.lat-start.lat);
		
		DaliNode lastNode = start;
		while (!atend) {
			double nextvy = (nextv- start.lat)* coeff + start.lon;
			if (nextvy == nexth) { //cross corner
				final DaliNode toadd = findNearestNode(nextv +movingRight* NavigationArea.threshold/2 ,
						nexth+ movingDown* NavigationArea.threshold/2);
				if (toadd == null)
					return null;
				DaliEdge edge = lastNode.edges.stream().filter(e -> e.dest == toadd).findFirst().orElse(null);
				if (edge == null || edge.heat != 0) 
					return null;
				intersections.add(toadd);				
				nextv += movingRight* NavigationArea.threshold;
				nexth += movingDown* NavigationArea.threshold;
				lastNode = toadd;
			}
			else if ((nextvy - nexth) *movingDown > 0) { //cross horizontal
				final DaliNode toadd = findNearestNode(nextv -movingRight* NavigationArea.threshold/2 ,
						nexth+ movingDown* NavigationArea.threshold/2);
				if (toadd == null)
					return null;
				DaliEdge edge = lastNode.edges.stream().filter(e -> e.dest == toadd).findFirst().orElse(null);
				if (edge == null || edge.heat != 0) 
					return null;
				intersections.add(toadd);
				nexth += movingDown* NavigationArea.threshold;
				lastNode = toadd;
			}
			else { //cross vertical
				final DaliNode toadd = findNearestNode(nextv +movingRight* NavigationArea.threshold/2 ,
						nexth- movingDown* NavigationArea.threshold/2);
				if (toadd == null)
					return null;
				DaliEdge edge = lastNode.edges.stream().filter(e -> e.dest == toadd).findFirst().orElse(null);
				if (edge == null || edge.heat != 0) 
					return null;
				intersections.add(toadd);
				nextv += movingRight* NavigationArea.threshold;
				lastNode = toadd;
			}
			atend = ((nextv - end.lat)*movingRight >0) && ((nexth - end.lon)*movingDown >0);
		}
		return intersections;
	}
	
	public List<Node> pathStraightener(Path path, double startTime, double speed) {
		if (path.segments.size() <3) {
			return path.segments;
		}
		int i = 1;
		double time = startTime;
		HashMap<DaliNode, Double> times = new HashMap<DaliNode, Double>();
		times.put((DaliNode)path.segments.get(0), startTime);
		try {
			while (i!= path.segments.size()) {
				final DaliNode tmp = (DaliNode)path.segments.get(i);
				DaliEdge edge = ((DaliNode)path.segments.get(i-1)).edges.stream().filter(e -> e.dest == tmp).findFirst().orElse(null);
				time = time + edge.length / (speed * (1-edge.heat));
				times.put(tmp, time);
				i++;
			}
		} catch (Exception e) { //already straightened
			return path.segments; 
		}
		ArrayList<Node> newSegments = new ArrayList<Node>();
		DaliNode currentSegmentStart = (DaliNode)path.segments.get(0);
		newSegments.add(currentSegmentStart);
		DaliNode currentSegmentEnd =  (DaliNode)path.segments.get(1);
		List<DaliNode> crossedNodes;
		i=2;
		while (i < path.segments.size()) {
			DaliNode next = (DaliNode)path.segments.get(i);
			crossedNodes = findLineIntersections(currentSegmentStart, next);
			boolean canShorten = true;
			if (crossedNodes == null) {
				canShorten = false;
			}
			else {
				for (DaliNode node:crossedNodes) {
					canShorten = canShorten && (!blockedByAnomalies(node.id, times.get(currentSegmentStart) 
							                             + currentSegmentStart.distanceToOther(node)/speed));
				}
			}
			if (!canShorten) {
				newSegments.add(currentSegmentEnd);
				times.put(currentSegmentEnd, times.get(currentSegmentStart) 
                        + currentSegmentStart.distanceToOther(currentSegmentEnd)/speed);
				currentSegmentStart = currentSegmentEnd;
			}
			currentSegmentEnd = next;
			i++;
		}
		newSegments.add(currentSegmentEnd);
		return newSegments;
	}
}
